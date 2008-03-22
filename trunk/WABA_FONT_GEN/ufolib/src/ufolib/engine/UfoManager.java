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

/*
 * Parts are:
 *  Copyright (C) 2000-2002 Guilherme Campos Hazan <guich@superwaba.org.>
 */
package ufolib.engine;

import waba.sys.*;
import waba.fx.*;
import waba.util.Hashtable;

/** 
 * Manage Ufo Fonts.
 *
 * Parts are parts of waba.applet.JavaBridge adopted for UfoFont.
 * Parts are parts of waba.fx.Graphics adopted for UfoFont.
 */
public class UfoManager
{

    /**
     * The overall default Ufo font
     */
    public static final UfoFont DEFAULTFONT = new UfoFont( "genXX", UfoFont.PLAIN, 12 );

    // ************************************************************
    // 

    /**
     * List of loaded Ufo fonts
     */
    private static Hashtable htLoadedFonts = new Hashtable( 10 );

    
    /**
     * The current default font
     */
    private static UfoFont m_currentFont = DEFAULTFONT;


    // ************************************************************
    // 

    public static UfoFont getCurrentFont() {
	return m_currentFont;
    }

    public static void setCurrentFont( UfoFont font ) {
	m_currentFont = font;
    }


    /**
     * the UFF naming scheme for PDBs
     */
    public static String uffName( UfoFont f ) {
	String sf = UfoUserFont.UFF_NAMEPREFIX + f.getName() + f.getSize() + f.getStyle();
	return sf;
    }
    
    
    private static UfoUserFont tryFont( UfoFont f ) {
	UfoUserFont uf;
	if ( f != null ) {
	    // verify if its in the cache.
	    String sf = uffName( f );
	    if ( ( uf = (UfoUserFont) htLoadedFonts.get( sf ) ) != null ) {
		return uf;
	    }
	    else {
		// not in chache, so load:
		uf = new UfoUserFont( sf );
		if ( uf.loaded ) {
		    htLoadedFonts.put( sf, uf );
		    return uf;
		}
		else {
		    return null;
		}
	    }
	}
	else {
	    return null;
	}
    }


    public static UfoUserFont getFont( UfoFont f ) {
	UfoUserFont uf = null;
	// 1. try given font
	uf = tryFont( f );
	if ( uf != null ) {
	    return uf;
	}
	else {
	    // 2. try current font
	    uf = tryFont( m_currentFont );
	    if ( uf != null ) {
		return uf;
	    }
	    else {
		// 3. try overall default font
		uf = tryFont( DEFAULTFONT );
		if ( uf != null ) {
		    return uf;
		}
		else {
		    // 4. use non Ufo font
		    // ???
		    return null; // dummy
		}
	    }
	}
    }


    /** called by FontMetrics */
    /*
      public static UserFont getFontMetrics(waba.fx.Font f)
   {
      return getFont(f); // the UserFont has all methods that are needed
   }
    */


    /** 
     * Draws a text with the current font and the current foreground color.
     * @param text the text to be drawn
     * @param x x coordinate of the text
     * @param y y coordinate of the text
     */
    public static void drawText( Graphics g, UfoFont f, String text, int x, int y ) {

	int ands8[] = {0x80,0x40,0x20,0x10,0x08,0x04,0x02,0x01};
	int offset,index;
	int width,w;
	int start,current;
	int startBit,currentBit;
	int ands;
	int rowWIB;
	// speedup
	UfoUserFont font = UfoManager.getFont( f );
	int height = (byte)font.fRectHeight;
	int h;
	
	if ( text == null || text.length() == 0 ) return;
	char []chars = text.toCharArray();
	byte[] bitmapTable;
	int[] bitIndexTable;
	  
	for ( int i = 0; i < chars.length; i++ ) {   // draw each char
	    char ch = chars[ i ];
	    //UfoUserFont.debugMess( "UfoGraphics: String index=" + i + " char=" + ( (int) ch ) );
	    bitmapTable = font.bitmapTable( ch ); 
	    bitIndexTable = font.bitIndexTable( ch );
	    
	    index = font.index( ch );  // MCS is considered
	    // Get the source x coordinate and width of the character
	    offset    = bitIndexTable[index];
	    width     = (byte) ( font.charWidth( ch ) );
	    start     = ( offset >> 3 );
	    startBit  = offset & 7;
	    //UfoUserFont.debugMess( "UfoGraphics: NEW CHAR index=" + index + " offset=" + offset + " width=" + width + " start=" + start + " startBit=" + startBit );

	    // draws the char
	    	    
	    rowWIB = font.rowWidthInBytes( ch );
	    for ( h = height; h-- > 0; start += rowWIB ) {
		//UfoUserFont.debugMess( "UfoGraphics: h=" + h + " start= " + start );
		// draw each row
		current = start;
		currentBit = startBit;
		ands = currentBit;
		for ( w = width; w-- > 0; x++ ) {  // draw each pixel
		    //UfoUserFont.debugMess( "UfoGraphics: x=" + x + ", y=" + y + "__ w=" + width +" h=" + height );
		    if ( ( bitmapTable[ current ] & ands8[ ands++ ] ) != 0 ) { 
			g.setPixel( x, y );
		    }
		    if (++currentBit == 8) { // finished this byte?
			currentBit = 0; // reset counter
			ands = 0;   // reset test bit pointer
			current++;      // inc current byte
		    }
		}
		x -= width;
		y++;
	    }  // end: draw each row 
	    x += width;
	    y -= height;

	}  // end: draw each char
	
    }


}
