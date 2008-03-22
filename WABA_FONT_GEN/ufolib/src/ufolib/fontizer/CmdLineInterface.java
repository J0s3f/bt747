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

import java.io.*;
import java.util.*;

/**
 * The command line/batch job interface
 *
 */
public class CmdLineInterface implements UserInterface {


    private Controller m_controller;
    private InputStreamReader m_is = null;
    private OutputStreamWriter m_os = null;

    // status
    private boolean m_quiet = false;
    private boolean m_exitOnError = false;


    // **************************************************
    // cmd specific stuff

    /**
     * @return false, if it fails
     */
    public boolean startBatch( String batchfile ) {
	try {
	    m_is = new InputStreamReader( new FileInputStream( batchfile ) );
	    m_os = new OutputStreamWriter( System.out );
	    return processStream();	    
	}
	catch ( FileNotFoundException e ) {
	    return false;
	}
	catch ( IOException e ) {
	    return false;
	}

    }
	

    /**
     * start reading commands; this is the "event loop"
     */
    public boolean startLoop() {
	try {
	    m_is = new InputStreamReader( System.in );
	    m_os = new OutputStreamWriter( System.out );
	    return processStream();
	}
	catch ( IOException e ) {
	    return false;
	}
    }


    /**
     * start reading bach commands; this is the "event loop"
     */
    public boolean processStream() throws IOException {
	String readin;
	String[] cmds;
	
	m_os.write( "! Welcome to ufolib Fontizer (C)2003\n" );
	m_os.write( "! For more information see http://jdict.sf.net\n" );
	m_os.write( "! Type 'help' to get help here\n" );
	m_os.write( "! \n" );

	if ( m_is != null ) {
	    boolean doExit = false;
	    while ( ! doExit ) {
		m_os.write( "% " );
		
		m_os.flush();
		readin = readline();
		//m_os.write( "! echo read: " + readin + "\n" );
	
		cmds = parseRead( readin );
		//m_os.write( "! echo cmds: " );for ( int index= 0; index < cmds.length; index++ ) { m_os.write( " " + index + ":" + cmds[ index ] ); }; m_os.write( "\n" );

		processCmd( cmds );
	    }
	}
	
	return true;
    }

    private String[] parseRead( String ins ) {
	StringTokenizer st = new StringTokenizer( ins );
	String[] ret = new String[ st.countTokens() ];
	for ( int index = 0; index < ret.length; index++ ) {
	    ret[ index ] = st.nextToken();
	}
	return ret;
    }

    private String readline() {
	String ret = null;
	try {
	    int inchar;
	    StringBuffer b = new StringBuffer();
	    while ( ( inchar = m_is.read() ) >= 32 ) {
		b.append( (char) inchar );
	    }
	    ret = b.toString();
	}
	catch ( IOException e ) {
	    // ???
	}
	return ret;
    }


    /**
     * set quiet modus
     */
    public void setQuietModus( boolean quiet ) {
	m_quiet = quiet;
    }


    // **************************************************
    // loop stuff

    /**
     * [0] is command, [1..] are parameters
     */
    private void processCmd( String[] cmd )  throws IOException {
	
	if ( cmd.length > 0 ) {
	    String ocmd = cmd[ 0 ];
	
	    // switch by command
	    if ( ocmd.equals( "help" ) ) {
		hereout( "Commands:", HO_OK );
		hereout( "  open_font_file", HO_OK );
		hereout( "  font_info", HO_OK );
		hereout( "  font_set_encoding", HO_OK );
		hereout( "  open_profile_file", HO_OK );
		hereout( "  use_font_as_profile", HO_OK );
		hereout( "  profile_info", HO_OK );
		hereout( "  save_profile_as", HO_OK );
		hereout( "  save_profile", HO_OK );
		hereout( "  calculate_uff_range", HO_OK );
		hereout( "  save_uff_file", HO_OK );
		hereout( "  set_uff_family", HO_OK );
		hereout( "  set_uff_shape", HO_OK );
		hereout( "  set_uff_size", HO_OK );
		hereout( "  list_converters", HO_OK );
		hereout( "  exit", HO_OK );
	    }
	    else if ( ocmd.equals( "open_font_file" ) ) {
		String f = sparam( cmd, 1 );
		if ( f != null ) {
		    boolean suc = m_controller.openSrc( f );
		    if ( ! suc ) {
			hereout( "Open font source failed", HO_ERROR );
		    }
		    else {
			hereout( "Font file loaded: " + f, HO_OK );
			hereoutFontInfo();
		    }
		}
	    }
	    else if ( ocmd.equals( "font_info" ) ) {
		hereoutFontInfo();
	    }
	    else if ( ocmd.equals( "font_set_encoding" ) ) {
		String f = sparam( cmd, 1 );
		if ( f != null ) {
		    boolean suc = m_controller.setSrcDataEncoding( f );
		    if ( ! suc ) {
			hereout( "Setting encoding failed", HO_ERROR );
		    }
		    else {
			hereout( "Font.encoding: " +  m_controller.getSrcDataEncoding(), HO_OK );
			hereoutFontInfo();
		    }
		}
	    }
	    else if ( ocmd.equals( "open_profile_file" ) ) {
		String f = sparam( cmd, 1 );
		if ( f != null ) {
		    boolean suc = m_controller.openProfile( f );
		    if ( ! suc ) {
			hereout( "Open profile file failed", HO_ERROR );
		    }
		    else {
			hereout( "Profile file loaded: " + f, HO_OK );
			hereoutProfileInfo();
		    }
		}
	    }
	    else if ( ocmd.equals( "use_font_as_profile" ) ) {
		FontRange rs, rp;
		boolean suc = false;
		if ( ( rs = m_controller.getSrcDataRange() ) != null ) {
		    if ( ( rp = m_controller.getProfileDataRange() ) != null ) {
			suc = rp.setCompleteRanges( rs );
		    }
		}
		if ( ! suc ) {
		    hereout( "Profile-by-Font setting failed", HO_ERROR );
		}
		else {
		    hereoutProfileInfo();
		}
	    }
	    else if ( ocmd.equals( "profile_info" ) ) {
		hereoutProfileInfo();
	    }
	    else if ( ocmd.equals( "save_profile_as" ) ) {
		String f = sparam( cmd, 1 );
		if ( f != null ) {
		    boolean suc = m_controller.saveProfile( f );
		    if ( ! suc ) {
			hereout( "Profile file saving failed", HO_ERROR );
		    }
		    else {
			hereout( "Profile file " + f + " saved", HO_OK );
		    }
		}
	    }
	    else if ( ocmd.equals( "save_profile" ) ) {
		boolean suc = m_controller.saveProfile( null );
		    if ( ! suc ) {
			hereout( "Profile file saving failed", HO_ERROR );
		    }
		    else {
			hereout( "Profile file saved", HO_OK );
		    }
	    }
	    else if ( ocmd.equals( "calculate_uff_range" ) ) {
		boolean suc = m_controller.calculateUffRanges();
		if ( ! suc ) {
		    hereout( "Calculate UFF ranges failed", HO_ERROR );
		}
		else {
		    hereout( "UFF.range: " + m_controller.getUffDataRange(), HO_OK );
		}
	    }
	    else if ( ocmd.equals( "save_uff_file" ) ) {
		boolean suc = m_controller.writeUff();
		if ( ! suc ) {
		    hereout( "Saving UFF file failed", HO_ERROR );
		}
		else {
		    hereout( "UFF file saving done.", HO_OK );
		}
	    }
	    else if ( ocmd.equals( "set_uff_family" ) ) {
		String f = sparam( cmd, 1 );
		boolean suc = m_controller.setUffDataFamily( f );
		if ( ! suc ) {
		    hereout( "Set UFF family failed", HO_ERROR );
		}
		else {
		    hereout( "Done.", HO_OK );
		}
	    }
	    else if ( ocmd.equals( "set_uff_shape" ) ) {
		String f = sparam( cmd, 1 );
		boolean suc = m_controller.setUffDataShape( f );
		if ( ! suc ) {
		    hereout( "Set UFF shape failed", HO_ERROR );
		}
		else {
		    hereout( "Done.", HO_OK );
		}
	    }
	    else if ( ocmd.equals( "set_uff_size" ) ) {
		int i = iparam( cmd, 1 );
		boolean suc = m_controller.setUffDataSize( i );
		if ( ! suc ) {
		    hereout( "Set UFF size failed", HO_ERROR );
		}
		else {
		    hereout( "Done.", HO_OK );
		}
	    }
	    else if ( ocmd.equals( "list_converters" ) ) {
		String[] allconv = m_controller.getCharConverterList();
		hereout( "List of encoding converters, total: " + allconv.length, HO_OK );
		for ( int index = 0; index < allconv.length; index++ ) {
		    hereout( allconv[ index ], HO_OK );
		}
	    }
	    else if ( ocmd.equals( "exit" ) ) {
		hereout( "Exit.", HO_OK );
		m_controller.friendlyExit();
		// this does not return here
		
	    }
	    else {
		hereout( "Command \"" + ocmd + "\" unknown.", HO_WARNING );
	    }
	    
	}
    }

    private static final int HO_ERROR = 1;
    private static final int HO_WARNING = 2;
    private static final int HO_OK = 3;

    private void hereout( String mess, int kind ) throws IOException {
	if ( kind == HO_OK ) {
	    if ( ! m_quiet ) {
		m_os.write( "!" + mess + "\n" );
	    }
	}
	else if ( kind == HO_ERROR ) {
	    m_os.write( "!" + "ERROR: " + mess + "\n" );
	    if ( m_exitOnError ) {
		m_controller.unfriendlyExit();
		// this does not return here
	    }
	}
	else if ( kind == HO_WARNING ) {
	    m_os.write( "!" + "WARNING: " + mess + "\n" );
	}

    }
	
	

    /**
     * String parameter
     */
    private String sparam( String[] cmd, int index ) throws IOException {
	if ( cmd.length > index ) {
	    return cmd[ index ];
	}
	else {
	    hereout( "Missing parameter no. " + index, HO_ERROR );
	    return null;
	}
    }

    /**
     * int parameter
     */
    private int iparam( String[] cmd, int index ) throws IOException {
	int i = 0;
	if ( cmd.length > index ) {
	    try {
		i = Integer.parseInt( cmd[ index ] );
	    }
	    catch ( NumberFormatException e ) {
		hereout( "Parameter no. " + index + " can not be recognized as an integer, using default.", HO_ERROR );
	    }
	}
	else {
	    hereout( "Missing parameter no. " + index, HO_ERROR );
	}
	return i;
    }

    private void hereoutFontInfo() throws IOException {
	hereout( "Font.comment: " + m_controller.getSrcDataComment(), HO_OK );
	hereout( "Font.family: " + m_controller.getSrcDataCoreFamily(), HO_OK );
	hereout( "Font.size: " + m_controller.getSrcDataCoreSize(), HO_OK );
	hereout( "Font.shape: " + m_controller.getSrcDataCoreShape(), HO_OK );
	hereout( "Font.type: " + m_controller.getSrcDataType(), HO_OK );
	hereout( "Font.encoding: " +  m_controller.getSrcDataEncoding(), HO_OK );
	hereout( "Font.isFixedSize: " + m_controller.getSrcDataIsFixedSize(), HO_OK );
	hereout( "Font.range: " + m_controller.getSrcDataRange(), HO_OK );
    }


    private void hereoutProfileInfo() throws IOException {
	hereout( "Profile.comment: " + m_controller.getProfileDataComment(), HO_OK );
	hereout( "Profile.family: " + m_controller.getProfileDataCoreFamily(), HO_OK );
	hereout( "Profile.size: " + m_controller.getProfileDataCoreSize(), HO_OK );
	hereout( "Profile.shape: " + m_controller.getProfileDataCoreShape(), HO_OK );
	hereout( "Profile.range: " + m_controller.getProfileDataRange(), HO_OK );
    }


    // **************************************************
    // implementing UserInterface

    public void preInit( Controller controller ) {
	m_controller = controller;
    }

    public void postInit() {
    }

    public void exit() {
    }

    public void uffRangeShow( int index ) {
	// nothing, as cmd line
    }



}

