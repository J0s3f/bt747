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
import java.util.zip.*;
import java.util.*;

/**
 * for font source encoding, read XFree86 Encoding file
 *
 */
public class CharConvertXFreeEncFile extends CharConvertBase {

    
    private int[][] btable1;

    public CharConvertXFreeEncFile( InputStream instream , boolean gz ) {
	try {
	    //	    InputStreamReader is = new InputStreamReader( new GZIPInputStream( new FileInputStream( gzfile ) ) );
	    InputStreamReader is = null;
	    if ( gz ) {
		is = new InputStreamReader( new GZIPInputStream( instream ) );
	    }
	    else {  // ! gz
		is = new InputStreamReader( instream );
	    }

	    boolean cline = false;
	    boolean finish = false;
	    String oneline;
	    int[] conv1;
	    Vector bTableV = new Vector();
	    while ( ! finish ) {
		oneline = readline( is );
		if ( cline ) { // converter line
		    if ( oneline.indexOf( "ENDMAPPING" ) >= 0 ) {
			finish = true;
		    }
		    else {  // normal process
			if ( oneline.indexOf( "UNDEFINE" ) >= 0 ) {
			    // ignore, right?
			}
			else {  // normal process
			    oneline = ( oneline.indexOf( "#" ) >= 0 ? oneline.substring( 0, oneline.indexOf( "#" ) ) : oneline );  // remove comment
			    //System.err.println( "CONV2:" + oneline );
			    oneline = oneline.trim();
			    if ( oneline.length() >= 3 ) {  // only non-empty lines
				conv1 = parseRead( oneline );
				bTableV.add( conv1 );
			    }
			}
		    }

		}
		else {
		    if ( oneline.indexOf( "STARTMAPPING" ) >= 0 ) {
			cline = true;
		    }
		}
	    }
	    btable1 = new int[ bTableV.size() ][];
	    for ( int index = 0; index < bTableV.size(); index++ ) {
		btable1[ index ] = (int[]) bTableV.elementAt( index );
	    }
	}
	catch ( IOException e ) {
	}

	//System.err.println( "CONV:" + toString() );
    }

    private int[] parseRead( String ins ) {
	StringTokenizer st = new StringTokenizer( ins );
	int[] ret = new int[ st.countTokens() ];
	for ( int index = 0; index < ret.length; index++ ) {
	    ret[ index ] = iByS( st.nextToken() );
	    //System.err.println( "CONV3:" + index + "=" + ret[ index ] );
	}
	return ret;
    }

    /**
     * int parameter; never time an error, try the best.
     */
    private int iByS( String cmdx ) {
	String cmd = cmdx.trim();
	int i = 0;
	float f = 0;
	
	try {
	    i = Integer.parseInt( cmd );
	}
	catch ( NumberFormatException e ) {
	    try {
		f = Float.parseFloat( cmd );
		i = (int) f;
	    }
	    catch ( NumberFormatException ee ) {
		try {
		    i = Integer.parseInt( cmd.substring( 2 ), 16 );
		}
		catch ( NumberFormatException eee ) {
		}
	    }
	}

	return i;
    }


    private String readline( InputStreamReader is ) {
	String ret = null;
	try {
	    int inchar;
	    StringBuffer b = new StringBuffer();
	    while ( ( inchar = is.read() ) != '\n' ) {
		b.append( (char) inchar );
	    }
	    ret = b.toString();
	}
	catch ( IOException e ) {
	    // ???
	}
	return ret;
    }


    public String enc() {
	return "gb2312";
    }

    /**
     * unicode encoding for one char
     * return unicode encoding
     */
    public int encToUnicode( int foreignInt ) {
	int ci = 0;

	ci = lookupBTable( foreignInt );

	if ( ci != -1 ) {
	    return ci;
	}
	else {
	    // no conversion found
	    return foreignInt;
	}
    }


    /**
     * is in btable?
     * @return target enc; -1 if not
     */
    private int lookupBTable( int srci ) {
	int[] onec;
	int bsize, bbsize;
	int desti = -1;

	// search for srci
	boolean goout = false;
	bsize = btable1.length;
	for ( int index = 0; index < bsize && ! goout ; index++ ) {
	    onec = btable1[ index ];
	    bbsize = onec.length;

	    if ( bbsize == 2 ) {
		if ( srci == onec[ 0 ] ) {
		    desti = onec[ 1 ];
		    goout = true;
		}
	    }
	    else {  // bbsize == 3
		if ( ( onec[ 0 ] <= srci ) && ( srci <= onec[ 1 ] ) ) {
		    desti = onec[ 2 ] + ( srci - onec[ 0 ] );
		    goout = true;
		}
	    }

	    if ( onec[ 0 ] > srci ) {
		// not found, desti already -1
		goout = true;
	    }
	}

	return desti;
    }


    public String toString() {
	int[] onec;
	int bsize, bbsize;


	StringBuffer b = new StringBuffer();
	b.append( "CharConvertXFreeEncFile[" );
	if ( btable1 != null ) {
	    bsize = btable1.length;
	    b.append( "tablesize=" + bsize );
	    for ( int index = 0; index < bsize; index++ ) {
		onec = btable1[ index ];
		bbsize = onec.length;
		b.append( ", [" + bbsize + "](" );
		if ( bbsize == 2 ) {
		    b.append( "" + onec[ 0 ] + "," + onec[ 1 ] + ")" );
		}
		else {  // bbsize == 3
		    b.append( "" + onec[ 0 ] + "-" + onec[ 1 ] + "," + onec[ 2 ] + ")" );
		}
	    }
	}
	b.append( "]" );
	
	return b.toString();
    }

    
}
