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
 * for font source encoding
 *
 */
public class CharConvert {

    private String m_inenc = null;

    private CharConvertBase m_realConverter = null;


    // ****************************************************************
    // List of converters

    // 0. all
    private static String[] m_providedConvertersString = null;

    // 1. format xfree
    private static CVEntry[] m_providedConverters1 = null;


    static final class CVEntry {
	public String name;
	public String filename;
	public CVEntry( String name, String filename ) {
	    this.name = name;
	    this.filename = filename;
	}
    }

    static {
	int i;
	String[] sp;
	InputStreamReader is;

	// A. Creating list of converters
	// 1. format xfree
	//InputStream iss = ClassLoader.getSystemResourceAsStream("/ufolib/fontizer/encodings/format-xfree/encodings.dir");
	InputStream iss = new Object().getClass().getResourceAsStream("/ufolib/fontizer/encodings/format-xfree/encodings.dir");
	//System.out.println( "iss:" + iss );
	is = new InputStreamReader( iss );
	int count = s2i( readline( is ) );
	m_providedConverters1 = new CVEntry[ count ];
	for ( int index = 0; index < count; index++ ) {
	    sp = parseRead( readline( is ) );
	    m_providedConverters1[ index ] = new CVEntry( sp[ 0 ] + "__XFree", sp [ 1 ] );
	} 
	// n. All: Create "name" list: Attention: NO SPACES in name!
	int allc = 2 + m_providedConverters1.length;  // + 2.length + ...
	int alli = 0;
	m_providedConvertersString = new String[ allc ];
	// n.0. = default
	m_providedConvertersString[ alli++ ] = "Unicode__default";
	m_providedConvertersString[ alli++ ] = "<default>__default";
	// n.1. format xfree
	for ( int index = 0; index < m_providedConverters1.length; index++ ) {
	    m_providedConvertersString[ alli++ ] = m_providedConverters1[ index ].name;
	}

    }


    /**
     * List of converters 
     */
    public static String[] providedConverters() {
	return m_providedConvertersString;
    }


    private static String[] parseRead( String ins ) {
	StringTokenizer st = new StringTokenizer( ins );
	String[] ret = new String[ st.countTokens() ];
	for ( int index = 0; index < ret.length; index++ ) {
	    ret[ index ] = st.nextToken();
	}
	return ret;
    }

    private static String readline( InputStreamReader is ) {
	String ret = null;
	try {
	    int inchar;
	    StringBuffer b = new StringBuffer();
	    while ( ( inchar = is.read() ) >= 32 ) {
		b.append( (char) inchar );
	    }
	    ret = b.toString();
	}
	catch ( IOException e ) {
	    // ???
	}
	return ret;
    }

    private static int s2i( String s ) {
        int ret = 0;
        if ( s != null ) {
            try {
                ret = Integer.parseInt( s );
            }
            catch ( NumberFormatException e ) {
                ret = 0;
            }
        }
        return ret;
    }


    // ****************************************************************
    // provide _one_ converter

    /**
     * @param enc encoding string; null generates "identity" converter
     */
    public CharConvert( String enc ) throws UnsupportedEncodingException {
	boolean found;

	m_inenc = enc;
	if ( m_inenc == null ) {
	    m_inenc = m_providedConvertersString[ 0 ];
	}
	if ( m_inenc.trim().equals( "" ) ) {
	    m_inenc = m_providedConvertersString[ 0 ];
	}


	// search for enc
	found = false;

	// 0.
	if ( m_inenc.equals( m_providedConvertersString[ 0 ] ) || m_inenc.equals( m_providedConvertersString[ 1 ] ) ) {
	    m_realConverter = null;  // = identity 
	    found = true;
	}
	
	// 1. format xfree?
	if ( ! found ) {
	    for ( int index = 0; ( index < m_providedConverters1.length ) && ! found; index++ ) {
		if ( m_inenc.equals( m_providedConverters1[ index ].name ) ) {
		    String ffname = "/ufolib/fontizer/encodings/format-xfree/" + m_providedConverters1[ index ].filename;
		    //System.out.println( "ffname:" + ffname );
		    InputStream iss = new Object().getClass().getResourceAsStream( ffname );
		    //System.out.println( "iss:" + iss );
		    boolean isgz = m_providedConverters1[ index ].filename.endsWith( ".gz" );
		    //System.out.println( "isgz:" + isgz );
		    m_realConverter = new CharConvertXFreeEncFile( iss , isgz );
		    //System.out.println( "btable:" + m_realConverter.toString() );
		    found = true;
		}
	    }
	}
	    
	// 2. if ! found  ... etc.
	
	// n.
	if ( ! found ) {
	    throw new UnsupportedEncodingException( "Encoding " + m_inenc + " not defined in CharConvert." );
	}
	
    }

    public String enc() {
	return m_inenc;
    }

    /**
     * unicode encoding for one char.
     * return unicode encoding
     */
    public int encToUnicode( int foreignInt ) {
	if ( m_realConverter != null ) {
	    return m_realConverter.encToUnicode( foreignInt );
	}
	else {
	    return foreignInt;
	}
    }


	/*
	byte[] inb;
	InputStreamReader is;
	int ci = 0;

	String hereenc = m_inenc;
	if ( hereenc != null ) {
	    try {
		// foreign int to byte: LE/BE? depending on enc? 1,2,4 byte ...
		//inb = new byte[] { (byte) ( foreignInt / 256 ), (byte) ( foreignInt % 256 ) };
		inb = new byte[] { (byte) ( foreignInt % 256 ), (byte) ( foreignInt / 256 ) };

		is = new InputStreamReader( new ByteArrayInputStream( inb ), hereenc );
		ci = is.read();
	    }
	    catch ( UnsupportedEncodingException e1 ) {
		// ?
	    }
	    catch ( IOException e2 ) {
		// ?
	    }
	}

	return ci;
	*/


}

