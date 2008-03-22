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


import ufolib.fontizer.*;

import java.io.*;
import java.util.Comparator;
import java.util.Arrays;
import java_cup.runtime.*;


/**
 * A BDF font
 */
public class BdfFont implements FontSource {

    public String m_startfont;
    public String m_font;
    public int m_sizePointsize = -1;
    public int m_fontboundingboxFbbx;
    public int m_fontboundingboxFbby;
    public int m_fontboundingboxXoff;
    public int m_fontboundingboxYoff;

    public String[] m_propName;
    public String[] m_propValue;
    public int m_currProp;

    public int m_chars;
    public BdfGlyph[] m_glyphs;
    
    private int m_currGlyphIndex;



    // ************************************************************************
    // basic methods

    public void scanFile( String filename, int debuglevel ) throws IOException, Exception {

	InputStream bdffilehandle;
	bdffilehandle = new FileInputStream( filename );
	BdfScan scanner = new BdfScan( bdffilehandle );
	this.debuglevel = debuglevel;

	/*
	  Symbol i;
	  for ( int index = 0; index < 1000; index++ ) {
	  i = scanner.next_token();
	  debugMess( "X:" + i.sym + " value: " + i.value );
	  }
	*/

	BUParser m_parser = new BUParser( scanner );
	BUParser.setBdfFont( this );
	    
	Symbol parse_tree = null;  
	
	// parse it, fill this
	if ( debuglevel >= 2 ) {
	    parse_tree = m_parser.debug_parse();
	}
	else {
	    parse_tree = m_parser.parse();
	}

    }


    // **************************************************************************
    // useful methods for scanning BDF file

    public void initChars( int t ) {
	mess( 0, "BdfFont, initChars: number=" + t );
	m_chars = t;
	m_glyphs = new BdfGlyph[ t ];
	m_currGlyphIndex = -1;
    }

    public BdfGlyph currGlyph() {
	return m_glyphs[ m_currGlyphIndex ];
    } 

    public void startChar() {
	m_currGlyphIndex++;
	m_glyphs[ m_currGlyphIndex ] = new BdfGlyph();
	mess( 3, "BdfFont, startChar: index=" + m_currGlyphIndex );
    }

    public void initProperties( int c ) {
	m_propName = new String[ c ];
	m_propValue = new String[ c ];
	m_currProp = -1;
	mess( 2, "BdfFont, initProperty: size=" + c );
    }

    public void oneProperty( String p1, String p2 ) {
	m_currProp++;
	m_propName[ m_currProp ] = p1;
	m_propValue[ m_currProp ] = p2;
	mess( 2, "BdfFont, oneProperty: index=" + m_currProp + "(" + p1 + "=" + p2 + ")" );
    }


    // **************************************************************************
    // access to BDF data

    public String getProperty( String name ) {
	if ( m_propName != null ) {
	    for ( int index = 0; index < m_propName.length; index++ ) {
		if ( m_propName[ index ].equals( name ) ) {
		    return m_propValue[ index ];
		}
	    }
	}
	return null;   // else
    }
		  
    public BdfGlyph getGlyphByEnc( int enc ) {
	if ( m_glyphs != null ) {
	    for ( int index = 0; index < m_glyphs.length; index++ ) {
		if ( m_glyphs[ index ].m_encoding == enc ) {
		    return m_glyphs[ index ];
		}
	    }
	}
	return null;
    }


    private int debuglevel = 1;
    public void mess( int level, String s ) {
	if ( level <= debuglevel ) {
	    System.err.println( s );
	}
    }

    // **************************************************************************
    // **************************************************************************
    // implementing FontSource

    private String fontOpenFilename;



    public boolean openFile( String name ) {
	fontOpenFilename = name;
	try {
	    scanFile( fontOpenFilename, debuglevel );
	    return true;
	}
	catch ( IOException e ) {
	    mess( 0, "BDF Font I/O Error " + e.toString() );
	    e.printStackTrace(System.err);
	}
	catch ( Exception e ) {
	    mess( 0, "BDF Font Error " + e.toString() );
	    e.printStackTrace(System.err);
	} 
	
	// else:
	return false;
    }

    public String fontType() {
	return "BDF";
    }

    public String fontFilename() {
	return fontOpenFilename;
    }

    public String fontCoreFamily() {
	return m_font;  // ??? font is wrong, but where is it?
    }

    public int fontCoreSize() {
	return 0;  // dummy return ???
    }

    public String fontCoreShape() {
	return "n";  // dummy return ???
    }
 
    public String fontComment() {
	return null;  // dummy return ???
    }

    public boolean fontIsFixedSize() {
	return true;
    }

    public boolean fontSetPixelSize( int size ) {
	return false;
    }

    public java.awt.Dimension fontBoundingBoxSize() {
	return new java.awt.Dimension( m_fontboundingboxFbbx, m_fontboundingboxFbby );
    }

    public java.awt.Dimension fontBoundingBoxOffset() {
	return new java.awt.Dimension( m_fontboundingboxXoff, m_fontboundingboxYoff );
    }

    public FontSourcePropertyEntry[] fontProperties() {
	FontSourcePropertyEntry[] ret = new FontSourcePropertyEntry[ m_propName.length ];
	for ( int index = 0; index < m_propName.length; index++ ) {
	    ret[ index ].name = m_propName[ index ];
	    ret[ index ].value = m_propValue[ index ];
	}
	return ret;
    }

    public int fontNumberOfChars() {
	return m_glyphs.length;
    }

    public FontGlyphData fontGlyphDataByIndex( int index ) {
	return m_glyphs[ index ];
    }
	
    public FontGlyphData fontGlyphDataByEncoding( int enc ) {
	return getGlyphByEnc( enc );
    }


    private String m_fontEncoding = null;   // null is default;

    public boolean fontSetEncoding( String enc ) {
	char c;
	int ci, foreignInt;
	byte[] inb;
	InputStreamReader is;

	if ( ( ( m_fontEncoding == null ) && ( enc == null ) ) || ( ( m_fontEncoding != null ) && ( enc != null ) && ( m_fontEncoding.equals( enc ) ) ) ) {
	    // nothing todo
	    return true;
	}

	String saveEnc = m_fontEncoding;  // save old value
	m_fontEncoding = enc;
	// 1. set enc in glyphs
	if ( m_glyphs != null ) {
	    if ( m_fontEncoding != null ) {
		CharConvert cv = null;
		try {
		    cv = new CharConvert( m_fontEncoding );
		}
		catch ( UnsupportedEncodingException e1 ) {
		    m_fontEncoding = saveEnc;
		    return false;
		}
		// now: encoding is ok
		if ( cv != null ) {
		    for ( int index = 0; index < m_glyphs.length; index++ ) {
			foreignInt = m_glyphs[ index ].m_encodingRaw;
			ci = cv.encToUnicode( foreignInt );
			m_glyphs[ index ].m_encoding = ci;
		    }
		}
	    }
	    else {  // m_fontEncoding set to null
		for ( int index = 0; index < m_glyphs.length; index++ ) {
		    m_glyphs[ index ].m_encoding = m_glyphs[ index ].m_encodingRaw;
		}
	    } 
	}

	// 2. re-sort
	Comparator compi = new Comparator() {
		public int compare( Object o1, Object o2 ) {
		    int io1 = ( (BdfGlyph) o1 ).m_encoding;
		    int io2 = ( (BdfGlyph) o2 ).m_encoding;
		    return ( io1 - io2 );
		}
	    };
	
	if ( m_glyphs != null ) {
	    Arrays.sort( m_glyphs, compi );
	}

	// we reached the end:
	return true;
    }

    public String fontEncoding() {
	return m_fontEncoding;
    }


}


