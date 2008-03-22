/*
 * ufolib - Full unicode font range for Superwaba
 * Copyright (C) 2002 Oliver Erdmann
 * http://jdict.sf.net 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License Version 2.1 as published by the Free Software Foundation
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package ufolib.fontizer;

import ufolib.convert.*;

import gnu.getopt.*;
import java.io.*;

import waba.applet.*;

/**
 * Main application
 *
 * functions here are: union of GUI requirements and CmdLine requirements.
 */
public class Controller {

    // ****************************************************
    // meta stuff

    static final int DEBUGLEVEL = 5;
    private static final int UI_GUI = 1;
    private static final int UI_CMD = 2;

    /**
     * desired UI
     * -1 is: not set
     */
    private int m_usedUi = UI_GUI;  // -i is default

    /**
     * the registered UI
     */
    private UserInterface m_ui;

    /**
     * params
     */
    private String[] m_args;

    private boolean m_quiet = false;

    private String m_paramSrcFile = null;
    private String m_paramProFile = null;
    private String m_paramUffFamily = null;
    private String m_paramUffSize = null;
    private String m_paramUffShape = null;
    private String m_paramBatchFile = null;
    private boolean m_saveAndExit = false;

    // ****************************************************
    // font stuff

    /**
     * null if none loaded
     */
    private FontSource m_fontsource = null;
    private int m_requestSize = 0;
    private FontRange m_fontsourceRange = null;
    
    private ProfileManager m_profile = null;
    private String m_profileFilename = null;

    private UFFWriter m_uffwriter = null;

    // ****************************************************
    // main() stuff
    
    public static Controller m_controller;

    /**
     * main
     */
    public static void main( String[] args ) {
	m_controller = new Controller( args );
	m_controller.go();
    }

    public Controller( String[] args ) {
	m_args = args;
    }

    /**
     * Main work.
     */
    public void go() {
	JavaBridge.setNonGUIApp();

	// 1. process args
	processArgs();

	// 2. instance UI
	if ( m_usedUi == UI_GUI ) {
	    m_ui = new GUI_Application();
	}
	else if ( m_usedUi == UI_CMD ) {
	    m_ui = new CmdLineInterface();
	}

	// 3. init UI
	m_ui.preInit( this );

	// 4. instance own fields
	instanceOwnFields();

	// 5. init UI 2
	m_ui.postInit();

	// 6. start main loop
	if ( m_saveAndExit ) {
	    writeUff();
	    friendlyExit();
	}
	else {  // process UI
	    if ( m_usedUi == UI_GUI ) {
		// nothing todo, main loop by Swing event queue
	    }
	    else if ( m_usedUi == UI_CMD ) {
		if ( m_paramBatchFile == null ) {
		    ( (CmdLineInterface) m_ui ).startLoop();
		}
		else {  // batch file
		    ( (CmdLineInterface) m_ui ).startBatch( m_paramBatchFile );
		}
	    }
	}
	
    }

    public void friendlyExit() {
	// 6. exit
	m_ui.exit();
	System.exit( 0 );
    }

    public void unfriendlyExit() {
	// 6. exit
	m_ui.exit();
	System.exit( 1 );
    }



    private void processArgs() {
	Getopt g = new Getopt( "Fontizer Controller", m_args, "-f:p:u:v:w:icb:qx");
	
	// parse args
	int c;
	String arg;
	while ( ( c = g.getopt() ) != -1 ) {
	    switch ( c ) {
	    case 'f':
		m_paramSrcFile = ( g.getOptarg() ).trim();  // todo: check ws and illegal chars
		break;
	    case 'p':
		m_paramProFile  = ( g.getOptarg() ).trim();  // todo:  check ws and illegal chars
		break;
	    case 'u':
		m_paramUffFamily  = ( g.getOptarg() ).trim();  // todo:  check ws and illegal chars
		break;
	    case 'v':
		m_paramUffSize  = ( g.getOptarg() ).trim();  // todo:  check ws and illegal chars
		break;
	    case 'w':
		m_paramUffShape  = ( g.getOptarg() ).trim();  // todo:  check ws and illegal chars
		break;
	    case 'q':
		m_quiet = true;
		break;
	    case 'i':
		// default = GUI
		break;
	    case 'c':
		// (not default) CMD UI
		m_usedUi = UI_CMD;
		break;
	    case 'b':
		m_paramBatchFile  = ( g.getOptarg() ).trim();  // todo:  check ws and illegal chars
		break;
	    case 'x':
		m_saveAndExit = true;
		break;
	    case '?':
		System.err.println( 
				   "-i          start interactive gui (this is the default), vs. -c/-b \n" +
				   "-c <file>   start command line interface, vs. -i/-b\n" +
				   "-b <file>   start batch job, commands given in <file>, vs. -i/-c\n" +
				   "-f <file>   load this source font on startup\n" + 
				   "-p <file>   load this profile on startup\n" +
				   "-u <fam>    use this as UFF family name\n" +
				   "-v <size>   use this as UFF pixelsize\n" +
				   "-w <nbi>    use this as UFF shape\n" +
				   "-x          save UFF and exit, use with -f -p -u -v -w \n" +
				   "-q          be quiet\n" );
		break; // getopt() already printed an error
	    default:
		System.out.print("getopt() returned " + c + "\n");
	    }
	}

	// process args
	if ( m_paramBatchFile != null ) {
	    // non-interactive CMD UI
	    m_usedUi = UI_CMD;
	}
	
    }
    
    private void instanceOwnFields() {

	// param src file
	if ( m_paramSrcFile != null ) {
	    setSrcFile( m_paramSrcFile );
	}

	m_profile = new ProfileManager();
	// param profile
	if ( m_paramProFile != null ) {
	    setProFile( m_paramProFile );
	}

	m_uffwriter = new UFFWriter();

	if ( m_paramUffFamily != null ) {
	    setUffDataFamily( m_paramUffFamily );
	}
	if ( m_paramUffSize != null ) {
	    try {
		int i = Integer.parseInt( m_paramUffSize );
		setUffDataSize( i );
	    }
	    catch ( NumberFormatException e ) {
	    }
	}
	if ( m_paramUffShape != null ) {
	    setUffDataShape( m_paramUffShape );
	}
    }


    /**
     * @return false, if it fails
     */
    private boolean setSrcFile( String filename ) {
	FontSource fs = null;
	boolean success = false;

	// 1. check file
	File fsf = new File( filename );
	mess( 2, "open font file: " + filename );
	if ( fsf.canRead() ) {
	    mess( 2, "open font file readable" );
	    // 2.  check file type and open suitable FontSource implementation
	    if ( filename.endsWith( ".bdf" ) || filename.endsWith( ".BDF" ) ) {
		mess( 2, "detected as BDF file" );
		// its a BDF file
		fs = new BdfFont();
	    }

	    // 3. open file
	    if ( fs != null ) {
		mess( 2, "font source open ..." );
		success = fs.openFile( filename );
		if ( success ) {
		mess( 2, "font source open ok" );
		    // all ok, so
		    // 1. set this as font src
		    m_fontsource = fs;
		    // 2. set range
		    fsCalcRange();
		    // 3. set size
		    m_requestSize = m_fontsource.fontCoreSize();
		    // 3. inform gui?
		    // ???
		}
	    }
	}
	mess( 2, "finish open src is " + success );
	return success;
    }

    /**
     * the FontSource's range is not managed by the FontRange, we do it here.
     */
    private boolean fsCalcRange() {
	int oldEnc, curEnc, startV, endV;

	m_fontsourceRange = new FontRange();
	int glyphslength = m_fontsource.fontNumberOfChars();
	Controller.mess( 2, "Controller, fontsourceRange: scan for range, number of chars " + glyphslength + ", start" );

	if ( glyphslength > 0 ) {
	    oldEnc = m_fontsource.fontGlyphDataByIndex( 0 ).glyphEncoding();
	    startV = oldEnc;
	    for ( int index = 1; index < glyphslength; index++ ) {
		curEnc = m_fontsource.fontGlyphDataByIndex( index ).glyphEncoding();
		//Controller.mess( 2, "Controller, fontsourceRange: inspec enc=" + curEnc + " (at index=" + index + "/" + glyphslength + ")" );

		// new range?
		if ( ( oldEnc + 1 ) != curEnc ) {
		    // new range!
		    endV = oldEnc;
		    Controller.mess( 9, "Controller, fontsourceRange: middle (" + startV + "-" + endV + ") ..." );
		    m_fontsourceRange.addRange( startV, endV );
		    Controller.mess( 8, "Controller, fontsourceRange: middle (" + startV + "-" + endV + ") done" );
		    startV = curEnc;
		}
		oldEnc = curEnc;
	    }
	    endV = oldEnc;
	    Controller.mess( 9, "Controller, fontsourceRange: last (" + startV + "-" + endV + ") ..." );
	    m_fontsourceRange.addRange( startV, endV );
	    Controller.mess( 8, "Controller, fontsourceRange: last (" + startV + "-" + endV + ") done" );
	}

	Controller.mess( 2, "Controller, fontsourceRange: total " + m_fontsourceRange.size() + ", finished" );
	return true;
    }

    /**
     * 
     * @return false, if it fails
     */
    private boolean setProFile( String filename ) {
	boolean success = false;

	// 1. check file
	File fsf = new File( filename );
	if ( fsf.canRead() ) {
	    success = m_profile.openFile( filename );
	    if ( success ) {
		// inform gui?
		// ???
	    }
	}
	return success;
    }


    // ****************************************************
    // Src stuff

    /**
     * open font source
     *
     * @return false, if it fails
     */
    public boolean openSrc( String filename ) {
	mess( 1, "Src file open " + filename );
	return setSrcFile( filename );
    }

    public String getSrcDataCoreFamily() {
	if ( m_fontsource != null ) {
	    return m_fontsource.fontCoreFamily();
	}
	return null;
    }

    public int getSrcDataCoreSize() {
	if ( m_fontsource != null ) {
	    return m_fontsource.fontCoreSize();
	}
	// else:
	return 0;  
    }

    public int getSrcDataRequestSize() {
	return m_requestSize;
    }

    public String getSrcDataCoreShape() {
	if ( m_fontsource != null ) {
	    return m_fontsource.fontCoreShape();
	}
	return null;
    }

    public String getSrcDataFilename() {
	if ( m_fontsource != null ) {
	    return m_fontsource.fontFilename();
	}
	return null;
    }

    public String getSrcDataType() {
	if ( m_fontsource != null ) {
	    return m_fontsource.fontType();
	}
	return null;
    }

    public String getSrcDataComment() {
	if ( m_fontsource != null ) {
	    return m_fontsource.fontComment();
	}
	return null;
    }

    public boolean getSrcDataIsFixedSize() {
	if ( m_fontsource != null ) {
	    return m_fontsource.fontIsFixedSize();
	}
	// else:
	return true;
    }

    
    /**
     * Number of Ranges
     */
    public int getSrcDataRanges() {
	if ( m_fontsourceRange != null ) {
	    return m_fontsourceRange.size();
	}
	return 0;
    }


    /**
     * Ranges
     */
    public FontRange getSrcDataRange() {
	if ( m_fontsourceRange != null ) {
	    return m_fontsourceRange;
	}
	return null;
    }

    /**
     * Total number of chars
     */
    public int getSrcDataChars() {
	if ( m_fontsourceRange != null ) {
	    return m_fontsource.fontNumberOfChars();  // == m_fontsourceRange.sum()
	}
	return 0;
    }

    public int getSrcDataRangeStart( int index ) {
	if ( m_fontsourceRange != null ) {
	    return m_fontsourceRange.getRangeStart( index );
	}
	return 0;
    }

    public int getSrcDataRangeEnd( int index ) {
	if ( m_fontsourceRange != null ) {
	    return m_fontsourceRange.getRangeEnd( index );
	}
	return 0;
    }

    /**
     * Set request size, in case of vector font
     *
     * @return actual set size
     */
    public int setSrcDataRequestSize( int rq ) {
	if ( m_fontsource != null ) {
	    if ( m_fontsource.fontIsFixedSize() ) {
		// bitmap font
		m_requestSize = m_fontsource.fontCoreSize();
	    }
	    else {
		m_requestSize = rq;
		m_fontsource.fontSetPixelSize( m_requestSize );
	    }
	}
	return m_requestSize;
    }


    /**
     * Set encoding at font source
     *
     * @return null, if default
     */
    public boolean setSrcDataEncoding( String enc ) {
	if ( m_fontsource != null ) {
	    if ( m_fontsource.fontSetEncoding( enc ) ) {
		return fsCalcRange();
	    }
	}
	
	// else:
	return false;
    }

    /**
     * Get encoding from font source
     *
     * @return FontSource returns null, if default
     */
    public String getSrcDataEncoding() {
	if ( m_fontsource != null ) {
	    return m_fontsource.fontEncoding();
	}
	
	// else:
	return null;
    }


    /**
     * Get list of converter-encodings
     */
    public String[] getCharConverterList() {
	return CharConvert.providedConverters();
    }


    // ****************************************************
    // Profile stuff
    
    /**
     * 
     *
     * @return false, if it fails
     */
    public boolean openProfile( String filename ) {
	if ( ( filename != null ) && ( m_profile != null ) ) {
	    m_profileFilename = filename;
	    return m_profile.openFile( m_profileFilename );
	}

	// else:
	return false;
    }

    /**
     * 
     * @param filename save this file; null if openProfile's filename should be used
     * @return false, if it fails
     */
    public boolean saveProfile( String filename ) {
	String ufilename = ( filename == null ? m_profileFilename : filename );

	// do some checks
	// ???

	if ( ( m_profile != null ) && ( ufilename != null ) ) {
	    return m_profile.saveFile( ufilename );
	}
	
	// else:
	return false;
    }

    public String getProfileDataCoreFamily() {
	return null;  // dummy return ???
    }

    public int getProfileDataCoreSize() {
	return 0;  // dummy return ???
    }

    public String getProfileDataCoreShape() {
	return null;  // dummy return ???
    }


    public String getProfileDataComment() {
	if ( m_profile != null ) {
	    return m_profile.getProfileComment();
	}
	// else:
	return null;
    }

    public void setProfileDataComment( String comm ) {
	if ( m_profile != null ) {
	    mess( 3, "to profile: setting profile comment: " + comm );
	    m_profile.setProfileComment( comm );
	}
    }

    /**
     * 
     */
    public FontRange getProfileDataRange() {
	if ( m_profile != null ) {
	    return m_profile.range();
	}
	//else:
	return null;
    }

    public boolean setProfileRangesByUff( String filename ) {
        return false;   // not implemented yet
    }

    // ****************************************************
    // UFF stuff

    /**
     * 
     *
     * @return false, if it fails
     */
    public boolean writeUff() {
	if ( ( m_uffwriter != null ) && ( m_fontsource != null ) && ( m_profile != null ) ) {
	    return m_uffwriter.writeUff( m_fontsource, m_fontsourceRange, m_profile.range() );
	}
	// else:
	return false;
    }


    /**
     * 
     *
     * @return false, if it fails
     */
    public boolean calculateUffRanges() {
	mess( 6, "Contoller calculateUffRange: " + m_uffwriter + " : " + m_fontsource + " : " + m_fontsourceRange + " : " + m_profile + "." );
	if ( ( m_uffwriter != null ) && ( m_fontsource != null ) && ( m_fontsourceRange != null ) && ( m_profile != null ) ) {
	    if ( m_fontsourceRange.size() > 0 ) {
		return m_uffwriter.calculateUffRanges( m_fontsource, m_fontsourceRange, m_profile.range() );
	    }
	    else {
		mess( 2, "calculateUffRanges, but Font Source range is empty" );
		return true;
	    }
	}
	// else:
	return true;
    }


    /**
     * 
     *
     * @return false, if it fails
     */
    public boolean setUffDataFamily( String name ) {
	if ( m_uffwriter != null ) {
	    return m_uffwriter.setFamily( name );
	}
	// else:
	return false;
    }

    /**
     * 
     *
     * @return false, if it fails
     */
    public boolean setUffDataSize( int size ) {
	if ( m_uffwriter != null ) {
	    return m_uffwriter.setSize( size );
	}
	// else:
	return false;
    }

    /**
     * 
     *
     * @return false, if it fails
     */
    public boolean setUffDataShape( String shape ) {
	if ( m_uffwriter != null ) {
	    return m_uffwriter.setShape( shape );
	}
	// else:
	return false;
    }


    /**
     * 
     *
     */
    public String getUffDataFamily() {
	if ( m_uffwriter != null ) {
	    return m_uffwriter.getFamily();
	}
	// else:
	return null;
    }

    /**
     * 
     *
     */
    public int getUffDataSize() {
	if ( m_uffwriter != null ) {
	    return m_uffwriter.getSize();
	}
	// else:
	return 0;
    }

    /**
     * 
     *
     */
    public String getUffDataShape() {
	if ( m_uffwriter != null ) {
	    return m_uffwriter.getShape();
	}
	// else:
	return null;
    }

    /**
     * 
     *
     * @return false, if it fails
     */
    public boolean setUffDataExtraWS( int v ) {
	if ( m_uffwriter != null ) {
	    return m_uffwriter.setExtraWS( v );
	}
	// else:
	return false;
    }


    /**
     * 
     *
     */
    public int getUffDataExtraWS() {
	if ( m_uffwriter != null ) {
	    return m_uffwriter.getExtraWS();
	}
	// else:
	return 0;
    }

    /**
     * 
     *
     * @return false, if it fails
     */
    public boolean setUffDataExtraLeading( int v ) {
	if ( m_uffwriter != null ) {
	    return m_uffwriter.setExtraLeading( v );
	}
	// else:
	return false;
    }


    /**
     * 
     *
     */
    public int getUffDataExtraLeading() {
	if ( m_uffwriter != null ) {
	    return m_uffwriter.getExtraLeading();
	}
	// else:
	return 0;
    }


    /**
     * aka. radio button "use schema name"
     *
     * @return false, if it fails
     */
    public boolean setUffDataSchemaName() {
	if ( m_uffwriter != null ) {
	    return m_uffwriter.setSchemaName();
	}
	// else:
	return false;
    }


    /**
     * aka. radio button "custom font name"
     *
     * @return false, if it fails
     */
    public boolean setUffDataCustomName( String fontname ) {
	if ( m_uffwriter != null ) {
	    return m_uffwriter.setCustomName( fontname );
	}
	// else:
	return false;
    }

    public String getUffDataSchemaName() {
	if ( m_uffwriter != null ) {
	    return m_uffwriter.getSchemaName();
	}
	// else:
	return null;
    }

    public String getUffDataCustomName() {
	if ( m_uffwriter != null ) {
	    return m_uffwriter.getCustomName();
	}
	// else:
	return null;
    }

    public String getUffDataSCName() {
	if ( m_uffwriter != null ) {
	    return m_uffwriter.getSCName();
	}
	// else:
	return null;
    }


    /**
     * Is Schema or custom name the current
     */
    public boolean getUffDataIsSchemaName() {
	if ( m_uffwriter != null ) {
	    return m_uffwriter.isSchemaName();
	}
	// else:
	return true;
    }


    /**
     *  When written to PDB there will be one more (the MCS).
     */
    public FontRange getUffDataRange() {
	if ( m_uffwriter != null ) {
	    return m_uffwriter.range();
	}
	//else:
	return null;
    }

    // ****************************************************
    // misc stuff


    /**
     * if an error happens, get the description of the last error here.
     *
     * @return a string; null if no error
     */
    public String errorMess() {
	return null;  // dummy return ???
    }

    
    // ****************************************************
    // intern

    static void mess( int level , String s ) {
	if ( level <= DEBUGLEVEL ) {
	    System.err.println( "(via) fontizer.Controller: " + s );
	}
    }
}

