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


/**
 * The MCS char
 */
public class MCSGlyphData implements FontGlyphData {


    private FontGlyphData srcG;

    public MCSGlyphData( FontSource fs ) {

	// MCS template? 
	//curP = m_bdffont.m_glyphs[ 0 ];
	srcG = fs.fontGlyphDataByEncoding( 32 );
	if ( srcG == null ) {
	    srcG = fs.fontGlyphDataByIndex( 0 );
	}

    }

    /**
     * MCS = no encoding!
     */
    public int glyphEncoding() {
	return -1;
    }

    public int glyphSwidthX() {
	return srcG.glyphSwidthX();
    }

    public int glyphSwidthY() {
	return srcG.glyphSwidthY();
    }

    public int glyphDwidthX() {
	return srcG.glyphDwidthY();
    }

    public int glyphDwidthY() {
	return srcG.glyphDwidthY();
    }

    public int glyphBbW() {
	return 8;
    }

    public int glyphBbH() {
	return srcG.glyphBbH();
    }

    public int glyphBbXoff() {
	return srcG.glyphBbXoff();
    }

    public int glyphBbYoff() {
	return srcG.glyphBbYoff();
    }

    public boolean isBlack( int x, int y ) {
	// the MCS, how should it look like?
	return ( ( x % 2 ) == 0 ); 
    }

}


