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

import ufolib.fontizer.*;

/**
 * One char in bdf file
 */
public class BdfGlyph implements FontGlyphData {

    public String m_startchar;
    public int m_encoding;  // unicode encoding
    public int m_encodingRaw;  // encoding in file
    public int m_swidthX;
    public int m_swidthY;
    public int m_dwidthX;
    public int m_dwidthY;
    public int m_bbW;
    public int m_bbH;
    public int m_bbXoff;
    public int m_bbYoff;
    public byte[][] m_bitmap;  // line from top, x

    /**
     * Used while scanning BDF file.
     */
    private int m_currLine;

    /**
     * Used while scanning BDF file.
     */
    public void initBitmap() {
	m_bitmap = new byte[ m_bbH ][ ( m_bbW + 7 ) / 8 ];
	m_currLine = -1;
    }

    /**
     * Used while scanning BDF file.
     */
    public void insertHexline( String hexline ) {
	m_currLine++;

	for ( int index = 0; index < ( hexline.length() / 2 ); index++ ) {
	    m_bitmap[ m_currLine ][ index ] = (byte) parseIntHex( hexline.substring( index * 2, index * 2 + 2) );
	}
    }

    private int parseIntHex( String s ) {
	int r = 0, p = 1;
	int c;

	String xs = s.trim();
	for ( int index = xs.length() - 1; index >= 0; index-- ) {
	    c = (int) xs.charAt( index );
	    if ( ( (int) '0' <= c ) && ( c <= (int) '9' ) ) {
		r += ( c - (int) '0' ) * p;
	    }
	    else if ( ( (int) 'a' <= c ) && ( c <= (int) 'f' ) ) {
		r += ( c - (int) 'a' + 10 ) * p;
	    }
	    else if ( ( (int) 'A' <= c ) && ( c <= (int) 'F' ) ) {
		r += ( c - (int) 'A' + 10 ) * p;
	    }
	    p *= 16;
	}
	return r;
    }		


    /**
     * Get pixel data, origin oriented.
     * 
     */
    public boolean isBlack( int x, int y ) {
	boolean ret; boolean inside;
	int ands8[] = {0x80,0x40,0x20,0x10,0x08,0x04,0x02,0x01};

	// out of pixel range?
	int realX = x - m_bbXoff;
	int realY = y - m_bbYoff;
	if ( ( realX < 0 ) || ( realY < 0 ) || ( realX >= m_bbW ) || ( realY >= m_bbH ) ) {
	    ret = false;  // white <= out of pixel range
	    inside = false;
	}
	else {
	    ret = ( ( ( m_bitmap[ m_bbH - realY - 1 ][ realX / 8 ] ) & ands8[ realX % 8 ] ) != 0 );
	    inside = true;
	}

	//	System.err.println( "BdfGlyph: param(" + x + "," + y + ") inside=" + inside + "/ enc: " + m_encoding  + "(raw: " + m_encodingRaw + ")" realX: " + realX + " realY: " + realY + " realX/8: " + realX/8 + " = return " + ret );
	return ret;
    }
 


    public int glyphEncodingRaw() {
	return m_encodingRaw;
    }

    // **************************************************************************
    // **************************************************************************
    // implementing FontGlyphData

    public int glyphEncoding() {
	return m_encoding;
    }

    public int glyphSwidthX() {
	return m_swidthX;
    }

    public int glyphSwidthY() {
	return m_swidthX;
    }

    public int glyphDwidthX() {
	return m_dwidthX;
    }

    public int glyphDwidthY() {
	return m_dwidthY;
    }

    public int glyphBbW() {
	return m_bbW;
    }

    public int glyphBbH() {
	return m_bbH;
    }

    public int glyphBbXoff() {
	return m_bbXoff;
    }

    public int glyphBbYoff() {
	return m_bbYoff;
    }

    // public boolean isBlack( int x, int y )  // already present

}

