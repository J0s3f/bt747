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
 * One char in font file
 */
public interface FontGlyphData {

    /**
     * the unicode place = encoding
     */
    public int glyphEncoding();

    public int glyphSwidthX();
    public int glyphSwidthY();
    public int glyphDwidthX();
    public int glyphDwidthY();
    public int glyphBbW();
    public int glyphBbH();
    public int glyphBbXoff();
    public int glyphBbYoff();

    /**
     * Get pixel data, origin oriented.
     */
    public boolean isBlack( int x, int y );
    
}

