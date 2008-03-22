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

package ufolib.convert;

import java.io.*;
import java_cup.runtime.*;
import java.util.Vector;

import gnu.getopt.*;

import ufolib.engine.*;

//import waba.io.*;
import waba.applet.*;

/**
 * Convert BDF file to UFF
 */
public class Bdfuff {

    public static final short UFFMAGICANDVERSION = (short) 12003;
    public static final short UFFFONTTYPE = (short) 0x9010;   // PalmOS default is  0x9000;

    private static Bdfuff m_bu;
    
    public static void main( String[] args ) {
	m_bu = new Bdfuff();
	m_bu.go( args );
    }

    public static Bdfuff getInstance() {
	return m_bu;
    }


    private int debuglevel = 1;
    public void debugMess( int level, String s ) {
	if ( level <= debuglevel ) {
	    System.err.println( s );
	}
    }


    // ********************************************************************


    private String[] m_args;

    private BdfFont m_bdffont;
    private UfoUserFont m_ufofont;

    // BDF things
    private BUParser m_parser;
    private String m_bdffile;   // the input file's name, (first) passed argument

    // UFF things
    private String m_uffFname = null;   // optional -n
    private String m_uffFslant = null;   // optional -s
    private int m_uffFsize = -1;  // optional -p
    private String m_ufffile = null;   // optional -f
    private final String m_ufffileDefault = "defFont." + UfoUserFont.UFF_CREATORID + "." + UfoUserFont.UFF_TYPEID;   // optional -f

    private int m_extraleading = 0;   // optional -l
    private int m_extraws = 0;   // optional -w

    public BdfFont getBdfFont() {
	return m_bdffont;
    }


    /**
     * main program 
     */
    public void go( String[] args ) {
	JavaBridge.setNonGUIApp();
	m_args = args;
	processArgs();
	// read BDF
	readBdf();
	processArgs2();
	// convert to UFF
	initUff();   // does: new UfoFont()
	decideRanges();
	fillRanges();
	fillMCS();

	// save
	m_ufofont.setFontName( m_ufffile );
	m_ufofont.savePdb();

    }

    private void processArgs2() {
	// the waba font name scheme
	// name in waba font name scheme
	if ( m_uffFname == null ) {
	    m_uffFname = "genXX";   // global default value
	}
	// slant in waba font name scheme
	if ( m_uffFslant == null ) {
	    m_uffFslant = "n";   // global default value
	}
	// point size in waba font name scheme
	if ( m_uffFsize == -1 ) {
            // not set by -p, so see BDF file
	    if ( m_bdffont.m_sizePointsize > 0 ) {
		m_uffFsize = m_bdffont.m_sizePointsize;
	    }
	    else {
		m_uffFsize = 12;   // global default value
	    }
	}


	if ( m_ufffile == null ) {
	    // not set by -f, so use waba font name scheme 
	    m_ufffile = UfoUserFont.UFF_NAMEPREFIX + m_uffFname + m_uffFsize + m_uffFslant;
	    //m_ufffile = m_ufffileDefault;   // global default value
	}
	
    }

    private void processArgs() {
	Getopt g = new Getopt( "Bdfuff", m_args, "-n:s:p:f:l:w:v");
	
	int c;
	String arg;
	int nonoptargcounter = 0;
	while ( ( c = g.getopt() ) != -1 ) {
	    switch ( c ) {
	    case 1:
		nonoptargcounter++;
		switch( nonoptargcounter ) {
		case 1:
		    m_bdffile = g.getOptarg();
		}
		break;
	    case 'n':
		m_uffFname = ( g.getOptarg() ).trim();  // todo: check ws and illegal chars
		break;
	    case 'v':
		debuglevel++;
		break;
	    case 's':
		m_uffFslant = ( g.getOptarg() ).trim();  // todo: check for n b i
		break;
	    case 'p':
		try {
		    m_uffFsize = Integer.parseInt( g.getOptarg() );
		}
		catch ( NumberFormatException e ) {
		    System.err.println( "Given pointsize for -p \"" + g.getOptarg() + "\" can not be recognized as an integer, using default." );
		}
		break;
	    case 'f':
		m_ufffile = ( g.getOptarg() ).trim();
		break;
	    case 'l':
		try {
		    m_extraleading = Integer.parseInt( g.getOptarg() );
		}
		catch ( NumberFormatException e ) {
		    System.err.println( "Given pixel for -l \"" + g.getOptarg() + "\" can not be recognized as an integer, ignored." );
		} 
		break;
	    case 'w':
		try {
		    m_extraws = Integer.parseInt( g.getOptarg() );
		}
		catch ( NumberFormatException e ) {
		    System.err.println( "Given pixel for -w \"" + g.getOptarg() + "\" can not be recognized as an integer, ignored." );
		}
		break;
	    case '?':
		System.err.println( 
				   "-n <name>   the fonts short name, no spaces, eg. \"gnuuni\" \n" +
				   "-s [nbi]    the slant: n normal, b bold, i italic, bi bold italic\n" +
				   "-p <size>   the point size, eg. 12\n" +
				   "-f <file>   the PDB filename\n" + 
				   "-l <pixel>  (extra) leading\n" +
				   "-w <pixel>  extra (white)space between chars\n" + 
				   "-v          verbose output" );
		
		break; // getopt() already printed an error
	    default:
		System.out.print("getopt() returned " + c + "\n");
	    }
	}

	// check argumnets required _before_ opening the bdf
	if ( nonoptargcounter < 1 ) {
	    System.err.println( "Missing argument(s): <bdf-filename>" );
	    System.exit( 1 );
	}

    }    



    private void readBdf() {
	m_bdffont = new BdfFont();

	try {
	    m_bdffont.scanFile( m_bdffile, debuglevel );
	}
	catch ( IOException e ) {
	    debugMess( -1, e.toString() );
	}
	catch ( Exception e ) {
	    /* do cleanup here - - possibly rethrow e */
	    debugMess( -1, e.toString() );
	} 
	finally {
	    /* do close out here */
	}

	debugMess( 0, "BDF File Format Version: " + m_bdffont.m_startfont );
    }


    /**
     * new UfoFont and setting global properties
     */
    private void initUff() {

	debugMess( 0, "Bdfuff: waba font name scheme, name: " + m_uffFname );
	debugMess( 0, "Bdfuff: waba font name scheme, slant: " + m_uffFslant );
	debugMess( 0, "Bdfuff: waba font name scheme, size: " + m_uffFsize );
	debugMess( 0, "Bdfuff: PDB filename: " + m_ufffile );

	// convert to Ufo font
	m_ufofont = new UfoUserFont();	

	m_ufofont.magic = UFFMAGICANDVERSION;
	m_ufofont.fcName = m_uffFname;
	m_ufofont.fcSize = (short) m_uffFsize;
	m_ufofont.fcSlant = (short) ( ( m_uffFslant.indexOf( 'b' ) >= 0 ? 1 : 0 ) + ( m_uffFslant.indexOf( 'i' ) >= 0 ? 2 : 0 ) );

	m_ufofont.comment = m_bdffont.m_font;
	m_ufofont.fontType = UFFFONTTYPE;

	// global font properties
	
	m_ufofont.kernMax = 0;  // PalmOS's default
	m_ufofont.nDescent = 0;   // dummy


	// setting fRect
	//m_ufofont.maxWidth = (short) ( m_bdffont.m_fontboundingboxFbbx + m_extraws );
	//m_ufofont.fRectWidth = (short) ( m_bdffont.m_fontboundingboxFbbx + m_extraws );
	//m_ufofont.fRectHeight = (short) m_bdffont.m_fontboundingboxFbby;
	// calculate minimal fRect
	int minW = 1;
	for ( int index = 0; index < m_bdffont.m_glyphs.length; index++ ) {
	    if ( m_bdffont.m_glyphs[ index ].m_bbW > minW ) {
		minW = m_bdffont.m_glyphs[ index ].m_bbW;
	    }
	}
	minW += m_extraws;
	m_ufofont.maxWidth = (short) minW;
	m_ufofont.fRectWidth = (short) minW;
	debugMess( 0, "Bdfuff: fRectWidth=" + minW + " (BDF said=" + m_bdffont.m_fontboundingboxFbbx + ")" );


	// ascent & descent 
	//m_ufofont.descent = (short) ( -1 * m_bdffont.m_fontboundingboxYoff );
	//m_ufofont.ascent = (short) ( m_ufofont.fRectHeight - m_ufofont.descent );
	// calculate (max) ascent & descent
	int asc = 1, des = 0;
	int curasc, curdes;
	for ( int index = 0; index < m_bdffont.m_glyphs.length; index++ ) {
	    curdes = - m_bdffont.m_glyphs[ index ].m_bbYoff;
	    curasc = m_bdffont.m_glyphs[ index ].m_bbH + m_bdffont.m_glyphs[ index ].m_bbYoff;
	    if ( curdes  > des ) {
		des = curdes;
	    }
	    if ( curasc > asc ) {
		asc = curasc;
	    }
	}
	m_ufofont.descent = (short) des;
	m_ufofont.ascent = (short) asc;
	m_ufofont.fRectHeight = (short) ( des + asc );

	debugMess( 0, "Bdfuff: fRectHeight=" + m_ufofont.fRectHeight + " (BDF said=" + m_bdffont.m_fontboundingboxFbby + ")" );
	debugMess( 0, "Bdfuff: ascent=" + m_ufofont.ascent );
	debugMess( 0, "Bdfuff: descent=" + m_ufofont.descent + " (BDF said=" + ( -1 * m_bdffont.m_fontboundingboxYoff ) + ")" );

	m_ufofont.leading = (short) m_extraleading;

    }


    /**
     * Get ranges of original BDF and decide new ranges in UFF.
     * 1. How to estimate range size in bytes? Use max h * max w * nchar?
     * 2. Max size of one range must respect PalmOS max record size 64kB
     * 3. Because of preformance/memory (only used ranges will be loaded): devide?
     */   
    private void decideRanges() {
	Vector startV, endV;
	int curEnc, oldEnc, curIndex;
	int i1, i2;

	// 1. get original BDF ranges
	startV = new Vector();
	endV = new Vector();
	BdfGlyph[] glyphs = m_bdffont.m_glyphs; 

	if ( glyphs.length > 0 ) {
	    oldEnc = glyphs[ 0 ].m_encoding;

	    startV.add( new Integer( oldEnc ) );
	    debugMess( 1, "Bdfuff,decideRanges: bdf start:" + oldEnc );

	    for ( int index = 1; index < glyphs.length; index++ ) {
		curEnc = glyphs[ index ].m_encoding;
		
		// new range?
		if ( ( oldEnc + 1 ) != curEnc ) {
		    // new range!
		    endV.add( new Integer( oldEnc ) );
		    debugMess( 1, "Bdfuff,decideRanges: bdf end:  " + oldEnc );
		    startV.add( new Integer( curEnc ) );
		    debugMess( 1, "Bdfuff,decideRanges: bdf start:" + curEnc );
		}

		oldEnc = curEnc;
	    }
	    
	    endV.add( new Integer( oldEnc ) );
	    debugMess( 1, "Bdfuff,decideRanges: bdf end:  " + oldEnc );
	}
    
	// 2. devide into new ranges
	Vector startNew, endNew;

	startNew = new Vector();
	endNew = new Vector();
	    
	// estimate ranges
	int maxRangeSize;
	// estimate range size 
	int maxCountEst = 50000 / ( ( ( m_ufofont.fRectWidth * m_ufofont.fRectHeight ) / 8 ) + 1 );

	// here we can reduce max range because of performance/memory reasons
	// pending.
	maxCountEst /= 2;

	// the end is:
	maxRangeSize = maxCountEst;

	// 2.2 instance new ranges
	for ( int index = 0; index < startV.size(); index++ ) {
	    i1 = ( (Integer) startV.get( index ) ).intValue();
	    i2 = ( (Integer) endV.get( index ) ).intValue();

	    if ( ( i2 - i1 + 1 ) <= maxRangeSize ) {
		// fits!
		startNew.add( startV.get( index ) );
		debugMess( 1, "Bdfuff,decideRanges: new start:" + ( (Integer) startV.get( index ) ).intValue() );
		endNew.add( endV.get( index ) );
		debugMess( 1, "Bdfuff,decideRanges: new end:  " + ( (Integer) endV.get( index ) ).intValue() );
	    }
	    else {
		// devide
		while ( i1 <= i2 ) {
		    startNew.add( new Integer( i1 ) );
		    debugMess( 1, "Bdfuff,decideRanges: new start:" + i1 );
		    i1 = i1 + maxRangeSize - 1;
		    if ( i1 <= i2 ) {
			endNew.add( new Integer( i1 ) );
			debugMess( 1, "Bdfuff,decideRanges: new end:  " + i1 );
		    }
		    else {
			endNew.add( new Integer( i2 ) );
			debugMess( 1, "Bdfuff,decideRanges: new end:  " + i2 );
		    }
		    i1++;
		}
	    }
	}

	// 2.3 init ranges in Ufo font
	m_ufofont.nParts = (short) startNew.size();
	m_ufofont.recNo = new short[ m_ufofont.nParts ];
	m_ufofont.firstChar = new int[ m_ufofont.nParts ];
	m_ufofont.lastChar = new int[ m_ufofont.nParts ];
	m_ufofont.range = new UfoUserFontRange[ m_ufofont.nParts ];

	for ( int index = 0; index < m_ufofont.nParts; index++ ) {
	    i1 = ( (Integer) startNew.get( index ) ).intValue();
	    i2 = ( (Integer) endNew.get( index ) ).intValue();
	    m_ufofont.recNo[ index ] = (short) ( index + 1 );
	    m_ufofont.firstChar[ index ] = i1;
	    m_ufofont.lastChar[ index ] = i2;
	    debugMess( 0, "Bdfuff,decideRanges: range " + index + ": recNo=" + m_ufofont.recNo[ index ] + " (" + m_ufofont.firstChar[ index ] + "-" + m_ufofont.lastChar[ index ] + ")" );
	}
    }


    private void fillRanges() {
	UfoUserFontRange curR;
	int ands8[] = {0x80,0x40,0x20,0x10,0x08,0x04,0x02,0x01};
	BdfGlyph curG;
	int curBytePos;
	int offset;
	int effectH, effectW;

	// loop over ranges
	for ( int index = 0; index < m_ufofont.nParts; index++ ) {
	    m_ufofont.range[ index ] = new UfoUserFontRange();
	    m_ufofont.range[ index ].numberOfChars = m_ufofont.lastChar[ index ] - m_ufofont.firstChar[ index ] + 1;
	    // table size
	    int sumWidth = 0;
	    for ( int ci = m_ufofont.firstChar[ index ]; ci <= m_ufofont.lastChar[ index ]; ci++ ) {
		sumWidth += m_bdffont.getGlyphByEnc( ci ).m_bbW + m_extraws;
	    }
	    int byteSumWidth = ( ( sumWidth + 7 ) / 8 );

	    m_ufofont.range[ index ].rowByte = byteSumWidth;
	    m_ufofont.range[ index ].bitmapTableSize = m_ufofont.fRectHeight * byteSumWidth;
	    m_ufofont.range[ index ].bitmapTable = new byte[ m_ufofont.fRectHeight * byteSumWidth ];
	    m_ufofont.range[ index ].bitIndexTable = new int[ m_ufofont.range[ index ].numberOfChars + 1 ];
	    offset = 0;
	    // loop over chars of this range
	    for ( int ci = 0; ci < m_ufofont.range[ index ].numberOfChars; ci++ ) {
		// the char we now inspect:
		curG = m_bdffont.getGlyphByEnc( ci + m_ufofont.firstChar[ index ] );
		// setting the bit index table:
		m_ufofont.range[ index ].bitIndexTable[ ci ] = offset; 
		debugMess( 1, "bitIndex:" + ci + " offset " + offset );
		// setting the bitmap table (some more lines)
		effectH = m_ufofont.fRectHeight;
		effectW = curG.m_bbW + m_extraws;
		for ( int y = 0; y < effectH; y++ ) {
		    for ( int x = 0; x < effectW; x++ ) {
			// dest is: line = curG.m_bbH - y - 1, col = x
			debugMess( 1, "Bdfuff:  effectH=" +  effectH + " y=" + y + " byteSumWidth=" + byteSumWidth + " offset=" + offset + " x=" + x + " offset+x/8=" + ( ( offset + x ) / 8 ) );

			curBytePos = ( ( effectH - y - 1 ) * byteSumWidth ) + ( ( offset + x ) / 8 );
			debugMess( 1, "Bdfuff: range=" + index + " curBytePos=" + curBytePos + ", offset=" + offset + ", x=" + x + ", y=" + y + ", ands=" + ands8[ ( offset + x ) % 8 ] + "." + " m_ufofont.descent=" + m_ufofont.descent ); 
			if ( curG.isBlack( x + curG.m_bbXoff, y - m_ufofont.descent ) ) {
			    // set Pixel black
			    debugMess( 1, "black" );
			    m_ufofont.range[ index ].bitmapTable[ curBytePos ] |= ands8[ ( offset + x ) % 8 ];
			}
			else {
			    // set Pixel white
			    debugMess( 1, "white" );
			    // no need, default is 0 = all pixel white
			}
		    }
		}
		offset += effectW;
	    }   // loop over chars of this range
	    m_ufofont.range[ index ].bitIndexTable[ m_ufofont.range[ index ].numberOfChars ] = offset; 	    
	    debugMess( 1, "bitIndex:" + m_ufofont.range[ index ].numberOfChars + " offset " + offset );
	}   // loop over ranges
    }
		


    private void fillMCS() {
	BdfGlyph curP,curG;
	int effectH, effectW;

	m_ufofont.recNoMCS = (short) ( m_ufofont.nParts + 1 );

	// MCS code? 
	//curP = m_bdffont.m_glyphs[ 0 ];
	curP = m_bdffont.getGlyphByEnc( 32 );
	if ( curP == null ) {
	    curP = m_bdffont.m_glyphs[ 0 ];
	}
 
	curG = new BdfGlyph();
	curG.m_swidthX = curP.m_swidthX;
	curG.m_swidthY = curP.m_swidthY;
	curG.m_dwidthX = curP.m_dwidthX;
	curG.m_dwidthY = curP.m_dwidthY;
	curG.m_bbW = 8;
	curG.m_bbH = curP.m_bbH;
	curG.m_bbXoff = curP.m_bbXoff;
	curG.m_bbYoff = curP.m_bbYoff;
	curG.initBitmap();
	for ( int index = 0; index < curG.m_bbH; index++ ) {
	    curG.insertHexline( ( index % 2 ) == 0 ? "FE" : "01" );
	}

	// set ufo MCS
	m_ufofont.rangeMCS = new UfoUserFontRange();
	m_ufofont.rangeMCS.numberOfChars = 1;
	int sumWidth = curG.m_bbW + m_extraws;
	int byteSumWidth = ( ( sumWidth + 7 ) / 8 );

	m_ufofont.rangeMCS.rowByte = byteSumWidth;
	m_ufofont.rangeMCS.bitmapTableSize = m_ufofont.fRectHeight * byteSumWidth;
	m_ufofont.rangeMCS.bitmapTable = new byte[ m_ufofont.fRectHeight * byteSumWidth ];
	m_ufofont.rangeMCS.bitIndexTable = new int[ m_ufofont.rangeMCS.numberOfChars + 1 ];
    
	int ands8[] = {0x80,0x40,0x20,0x10,0x08,0x04,0x02,0x01};
	int offset = 0;
	int curBytePos;

	m_ufofont.rangeMCS.bitIndexTable[ 0 ] = offset;
	debugMess( 1, "bitIndex MCS: noc " + m_ufofont.rangeMCS.numberOfChars + " offset " + offset );
	effectH = m_ufofont.fRectHeight;
	effectW = curG.m_bbW + m_extraws;
	for ( int y = 0; y < effectH; y++ ) {
	    for ( int x = 0; x < effectW; x++ ) {
		// dest is: line = curG.m_bbH - y - 1, col = x
		curBytePos = ( ( effectH - y - 1 ) * byteSumWidth ) + ( ( offset + x ) / 8 );
		debugMess( 1, "Bdfuff: MCS: " + curBytePos + ", offset=" + offset + ", x=" + x + ", y=" + y + ", ands=" + ands8[ ( offset + x ) % 8 ] + "." ); 
		if ( curG.isBlack( x + curG.m_bbXoff, y - m_ufofont.descent ) ) {
		    // set Pixel black
		    m_ufofont.rangeMCS.bitmapTable[ curBytePos ] |= ands8[ ( offset + x ) % 8 ];
		}
		else {
		    // set Pixel white
		    // no need, default is 0 = all pixel white
		}
	    }
	}
	offset += effectW;
	m_ufofont.rangeMCS.bitIndexTable[ 1 ] = offset; 	    	    
	debugMess( 1, "bitIndex MCS: noc " + m_ufofont.rangeMCS.numberOfChars + " offset " + offset );
    }
 


}
