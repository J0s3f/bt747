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

import java.awt.Rectangle;
import java.awt.Dimension;

/**
 * FostSource is an ascending sorted Unicode encoded fixed size font source.
 * If vector font, you can request a size; please provide a default size.
 *
 */
public interface FontSource {


    // *************************************************************
    // access

    /**
     * Open font file. All other set/get methods have to appear afterwards.
     *
     * @param name filename
     * @return false, if it fails
     */
    public boolean openFile( String name );


    // *************************************************************
    // get data 

    /**
     * font original format: BDF, TTF, ...
     */
    public String fontType();

    /**
     * fonts filename
     */
    public String fontFilename();

    /**
     * the core font family name
     */
    public String fontCoreFamily();

    /**
     * the core pixel size
     */
    public int fontCoreSize();

    /**
     * the core font shape
     */
    public String fontCoreShape();

    /**
     * a comment
     */
    public String fontComment();

    /**
     * bitmap font or vector font 
     *
     * @return true, if bitmap font; if false, the fontCoreSize() is probably undefined and you can/must use fontSetPixelSize() 
     */
    public boolean fontIsFixedSize();


    /**
     * set pixel size 
     *
     * @return false, if it fails (i.e. its a bitmap font)
     */
    public boolean fontSetPixelSize( int size ); 


    /**
     * Set encoding. Remember: The Font Source implementation must always provide unicode encoded chars.
     * This is to set the encoding by the user if the file's encoding is not given by the file.
     * So, calling this method forces the implementation to reorganize its chars.
     *
     * @return false, if it fails
     */
    public boolean fontSetEncoding( String enc ); 


    /**
     * Get encoding.
     *
     * @return null, if default
     */
    public String fontEncoding(); 


    /**
     * get the overall font bounding box's size
     */
    public Dimension fontBoundingBoxSize();

    /**
     * get the overall font bounding box's offset
     */
    public Dimension fontBoundingBoxOffset();

    /**
     * the free form properties
     */
    public FontSourcePropertyEntry[] fontProperties();

    /**
     * Number of chars
     */
    public int fontNumberOfChars();

    /**
     * One Glyph (by char index, not by encoding)
     *
     * @return the glyph, or null if index out of range 
     */
    public FontGlyphData fontGlyphDataByIndex( int index );

    /**
     * One Glyph (by unicode place (=encoding) not by index)
     *
     * @return the glyph, or null if the char is not included
     */
    public FontGlyphData fontGlyphDataByEncoding( int enc );

}


