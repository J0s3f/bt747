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

import ufolib.engine.*;

/**
 * An Unicode encoded fix size font source
 *
 */
public class UFFWriter {

    public static final short UFFMAGICANDVERSION = (short) 12003;
    public static final short UFFFONTTYPE = (short) 0x9010;   // PalmOS default is  0x9000;

    private String m_fontFamily;
    private int m_fontSize;
    private String m_fontShape;
    private int m_extraws;
    private int m_extraleading;

    private boolean m_fontUseSchema = true;
    private String m_fontCustomName;

    private FontRange m_range;
    private UfoUserFont m_ufofont;

    // font source
    private FontSource m_fontsource;

    private String calcSchemaName() {
	String cFam = ( m_fontFamily != null ? m_fontFamily : "genXX" );
	cFam = ( cFam.length() > 20 ? cFam.substring( 0, 20 ) : cFam );
	
	return cFam + Integer.toString( ( m_fontSize > 0 ? m_fontSize : 12 ) ) + ( m_fontShape != null ? m_fontShape : "n" );
    }

    private String actualFontFilename() {
	if ( m_fontUseSchema ) {
	    return calcSchemaName();
	}
	else {
	    return m_fontCustomName;
	}
    }


    // **************************************************************
    // private write UFF

    /**
     * new UfoFont and setting global properties.
     * uses m_fontsource, sets m_ufofont
     */
    private void internInitUff() {

	Controller.mess( 0, "Uffwriter: waba font name scheme, family: " + m_fontFamily );
	Controller.mess( 0, "Uffwriter: waba font name scheme, size: " + m_fontSize );
	Controller.mess( 0, "Uffwriter: waba font name scheme, shape: " + m_fontShape );
	Controller.mess( 0, "Uffwriter: waba use font name scheme?: " + m_fontUseSchema );

	// create Ufofont
	m_ufofont = new UfoUserFont();	

	// header settings
	m_ufofont.magic = UFFMAGICANDVERSION;
	m_ufofont.fcName = m_fontFamily;
	m_ufofont.fcSize = (short) m_fontSize;
	if ( m_fontShape != null ) {
	    m_ufofont.fcSlant = (short) ( ( m_fontShape.indexOf( 'b' ) >= 0 ? 1 : 0 ) + ( m_fontShape.indexOf( 'i' ) >= 0 ? 2 : 0 ) );
	}
	else {
	    m_ufofont.fcSlant = 0;
	}

	if ( m_fontsource != null ) {
	    m_ufofont.comment = m_fontsource.fontComment();
	}
	else {
	    m_ufofont.comment = "";
	}

	m_ufofont.fontType = UFFFONTTYPE;

	if ( m_fontsource != null) {
	    // global font properties
	    m_ufofont.kernMax = 0;  // PalmOS's default
	    m_ufofont.nDescent = 0;   // dummy

	    // setting fRect
	    //m_ufofont.maxWidth = (short) ( m_bdffont.m_fontboundingboxFbbx + m_extraws );
	    //m_ufofont.fRectWidth = (short) ( m_bdffont.m_fontboundingboxFbbx + m_extraws );
	    //m_ufofont.fRectHeight = (short) m_bdffont.m_fontboundingboxFbby;
	    int minW = m_fontsource.fontBoundingBoxSize().width;
	    minW += m_extraws;
	    m_ufofont.maxWidth = (short) minW;
	    m_ufofont.fRectWidth = (short) minW;
	    Controller.mess( 0, "Uffwriter: fRectWidth=" + minW + "." );
	    
	    // ascent & descent 
	    //m_ufofont.descent = (short) ( -1 * m_bdffont.m_fontboundingboxYoff );
	    //m_ufofont.ascent = (short) ( m_ufofont.fRectHeight - m_ufofont.descent );
	    // calculate (max) ascent & descent
	    int asc = 1, des = 0;
	    int curasc, curdes;
	    int noc = m_fontsource.fontNumberOfChars();
	    for ( int index = 0; index < noc; index++ ) {
		curdes = - m_fontsource.fontGlyphDataByIndex( index ).glyphBbYoff();
		curasc = m_fontsource.fontGlyphDataByIndex( index ).glyphBbH() + m_fontsource.fontGlyphDataByIndex( index ).glyphBbYoff();
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
	}

	Controller.mess( 0, "Uffwriter: fRectHeight=" + m_ufofont.fRectHeight );
	Controller.mess( 0, "Uffwriter: ascent=" + m_ufofont.ascent );
	Controller.mess( 0, "Uffwriter: descent=" + m_ufofont.descent  );

	m_ufofont.leading = (short) m_extraleading;

    }

    /**
     * Adjust ranges because of device and UFF format limitations
     * <pre>Pending improvements: 
     * 1. How to estimate range size in bytes? Use max h * max w * nchar?
     * 2. Max size of one range must respect PalmOS max record size 64kB?
     * 3. Because of preformance/memory (only used ranges will be loaded): devide range? when?
     * 4. Combine ranges, if no gap between one and the following in src.
     * 5. Insert dummy chars to make bigger continuous ranges.
     * </pre>
     *
     * in UfoUSerFont it uses the header fields set by internInitUff()
     */   
    private static FontRange decideRanges( FontRange src, UfoUserFont ufofont ) {
	Controller.mess( 1, "Uffwriter, decideRanges start" );

	FontRange ret = new FontRange();
	int curEnc, oldEnc, curIndex;
	int i1, i1orig, i2, iEnd;

	// devide into new ranges
	// estimate ranges
	int maxRangeSize;
	// estimate range size 
	int maxCountEst = 50000 / ( ( ( ufofont.fRectWidth * ufofont.fRectHeight ) / 8 ) + 1 );

	// here we can reduce max range because of performance/memory reasons
	// pending.
	maxCountEst /= 2;

	// the end is:
	maxRangeSize = maxCountEst;

	// instance new ranges
	for ( int index = 0; index < src.size(); index++ ) {
	    i1 = src.getRangeStart( index );
	    i2 = src.getRangeEnd( index );

	    if ( ( i2 - i1 + 1 ) <= maxRangeSize ) {
		// fits!
		ret.addRange( i1, i2 );
		Controller.mess( 7, "Uffwriter, decideRanges: new: (" + i1 + "-" + i2 + ")" );
	    }
	    else {
		// devide
		while ( i1 <= i2 ) {
		    i1orig = i1;
		    i1 = i1 + maxRangeSize - 1;
		    iEnd = ( i1 <= i2 ? i1: i2 );
		    ret.addRange( i1orig, iEnd );
		    Controller.mess( 7, "Uffwriter, decideRanges: new: (" + i1orig + "-" + iEnd + ")" );
		    i1++;
		}
	    }
	}

	Controller.mess( 1, "Uffwriter, decideRanges finish, total " + ret.size() );
	return ret;
    }


    /**
     * init ranges in Ufo font
     * uses m_range, sets m_ufofont
     */
    private void internSetUfofontRanges() {
	int i1, i2;

	m_ufofont.nParts = (short) m_range.size();
	m_ufofont.recNo = new short[ m_ufofont.nParts ];
	m_ufofont.firstChar = new int[ m_ufofont.nParts ];
	m_ufofont.lastChar = new int[ m_ufofont.nParts ];
	m_ufofont.range = new UfoUserFontRange[ m_ufofont.nParts ];

	for ( int index = 0; index < m_ufofont.nParts; index++ ) {
	    i1 = m_range.getRangeStart( index );
	    i2 = m_range.getRangeEnd( index );
	    m_ufofont.recNo[ index ] = (short) ( index + 1 );
	    m_ufofont.firstChar[ index ] = i1;
	    m_ufofont.lastChar[ index ] = i2;
	    Controller.mess( 6, "Uffwriter, Ufofont ranges: range " + index + ": recNo=" + m_ufofont.recNo[ index ] + " (" + m_ufofont.firstChar[ index ] + "-" + m_ufofont.lastChar[ index ] + ")" );
	}

    }


    /**
     * uses m_ufofont, m_fontsource
     */
    private void internFillRanges() {
	// loop over ranges
	for ( int index = 0; index < m_ufofont.nParts; index++ ) {
	    m_ufofont.range[ index ] = new UfoUserFontRange();
	    m_ufofont.range[ index ].numberOfChars = m_ufofont.lastChar[ index ] - m_ufofont.firstChar[ index ] + 1;
	    // table size
	    int sumWidth = 0;
	    for ( int ci = m_ufofont.firstChar[ index ]; ci <= m_ufofont.lastChar[ index ]; ci++ ) {
		sumWidth += m_fontsource.fontGlyphDataByEncoding( ci ).glyphBbW() + m_extraws;
	    }

	    ifrPixel( sumWidth, m_ufofont.range[ index ], m_ufofont.firstChar[ index ], null );
	}   // loop over ranges
    }


    /**
     * uses m_fontsource, m_ufofont
     */
    private void internFillMCS() {
	FontGlyphData curG;

	m_ufofont.recNoMCS = (short) ( m_ufofont.nParts + 1 );
	curG = new MCSGlyphData( m_fontsource );
	m_ufofont.rangeMCS = new UfoUserFontRange();
	m_ufofont.rangeMCS.numberOfChars = 1;
	int sumWidth = curG.glyphBbW() + m_extraws;
	ifrPixel( sumWidth, m_ufofont.rangeMCS, -1, curG );
    }
 

    /**
     * used by internFillRanges() and internFillMCS
     */ 
    private void ifrPixel( int sumWidth, UfoUserFontRange uffr, int uffFirstChar, FontGlyphData constantGlyph ) {
	FontGlyphData curG;
	int curBytePos;
	int offset;
	int effectH, effectW;
	int ands8[] = {0x80,0x40,0x20,0x10,0x08,0x04,0x02,0x01};
	int byteSumWidth = ( ( sumWidth + 7 ) / 8 );

	uffr.rowByte = byteSumWidth;
	uffr.bitmapTableSize = m_ufofont.fRectHeight * byteSumWidth;
	uffr.bitmapTable = new byte[ m_ufofont.fRectHeight * byteSumWidth ];
	uffr.bitIndexTable = new int[ uffr.numberOfChars + 1 ];
	offset = 0;
	// loop over chars of this range
	for ( int ci = 0; ci < uffr.numberOfChars; ci++ ) {
	    // the char we now inspect:
	    if ( constantGlyph == null ) {
		curG = m_fontsource.fontGlyphDataByEncoding( ci + uffFirstChar );
	    }
	    else {
		curG = constantGlyph;
	    }
	    // setting the bit index table:
	    uffr.bitIndexTable[ ci ] = offset; 
	    Controller.mess( 7, "Gly: bitIndex:" + ci + "(" + ( uffFirstChar != -1 ? "+" + uffFirstChar : "MCS" ) + ")" + "  offset " + offset );
	    // setting the bitmap table (some more lines)
	    effectH = m_ufofont.fRectHeight;
	    effectW = curG.glyphBbW() + m_extraws;
	    for ( int y = 0; y < effectH; y++ ) {
		for ( int x = 0; x < effectW; x++ ) {
		    // dest is: line = curG.bbH - y - 1, col = x
		    Controller.mess( 7, "Gly: effectH=" +  effectH + " y=" + y + " byteSumWidth=" + byteSumWidth + " offset=" + offset + " x=" + x + " offset+x/8=" + ( ( offset + x ) / 8 ) );
		    
		    curBytePos = ( ( effectH - y - 1 ) * byteSumWidth ) + ( ( offset + x ) / 8 );
		    Controller.mess( 7, "Gly: range=" + "index?" + " curBytePos=" + curBytePos + ", offset=" + offset + ", x=" + x + ", y=" + y + ", ands=" + ands8[ ( offset + x ) % 8 ] + "." + " m_ufofont.descent=" + m_ufofont.descent ); 
		    if ( curG.isBlack( x + curG.glyphBbXoff(), y - m_ufofont.descent ) ) {
			// set Pixel black
			Controller.mess( 7, "black" );
			uffr.bitmapTable[ curBytePos ] |= ands8[ ( offset + x ) % 8 ];
		    }
		    else {
			// set Pixel white
			Controller.mess( 7, "white" );
			// no need, default is 0 = all pixel white
		    }
		}
	    }
	    offset += effectW;
	}   // loop over chars of this range
	uffr.bitIndexTable[ uffr.numberOfChars ] = offset; 	    
	Controller.mess( 7, "Gly: bitIndex:" + uffr.numberOfChars + " offset " + offset );
    }




    // ************************************************************
    // public

    public UFFWriter() {
	m_range = new FontRange();
    }


    /**
     * @return false, if it fails
     */
    public boolean calculateUffRanges( FontSource fontsource, FontRange fontsourceRange, FontRange profileRange ) {
	Controller.mess( 2, "UFFWriter: start calculateUffRanges" );
	m_fontsource = fontsource;

	FontRange irange, drange;
 
	// 1. intersect provided and desired ranges
	irange = FontRange.intersect( fontsourceRange, profileRange );
	Controller.mess( 6, "UFFWriter: intersect range" + irange.toString() );
	if ( irange != null ) {
	    internInitUff();
	    // 2. fit into Catalog records
	    drange = UFFWriter.decideRanges( irange, m_ufofont );
	    if ( drange != null ) {
		m_range = drange;
		return true;
	    }
	}
	// else:
	return false;
    }	


    /**
     * Get the ranges
     */
    public FontRange range() {
	return m_range;
    }


    /**
     * 
     *
     * @return false, if it fails
     */
    public boolean writeUff( FontSource fontsource, FontRange fsr, FontRange profileRange ) {
	m_fontsource = fontsource;

	// 1. set UFF
	calculateUffRanges( m_fontsource, fsr, profileRange );   // does: new UfoFont(), decideRanges
	internSetUfofontRanges();
	internFillRanges();
	internFillMCS();

	// 2. save UFF
	String acFilename = actualFontFilename();
	Controller.mess( 0, "Uffwriter: actual PDB filename: " + acFilename );
	if ( ( acFilename != null ) && ( ! acFilename.equals( "" ) ) ) {
	    m_ufofont.setFontName( UfoUserFont.UFF_NAMEPREFIX + acFilename );
	    return m_ufofont.savePdb();
	}
	else {
	    return false;
	}
    }


    /**
     * 
     *
     * @return false, if it fails
     */
    public boolean setFamily( String name ) {
	// do some checks ?
	m_fontFamily = name;
	return true;
    }

    public String getFamily() {
	return m_fontFamily;
    }

    /**
     * 
     *
     * @return false, if it fails
     */
    public boolean setSize( int size ) {
	// do some checks ?
	m_fontSize = size;
	return true;
    }

    public int getSize() {
	return m_fontSize;
    }

    /**
     * 
     *
     * @return false, if it fails
     */
    public boolean setShape( String shape ) {
	// do some checks ?
	m_fontShape = shape;
	return true;
    }

    public String getShape() {
	return m_fontShape;
    }

    /**
     * 
     *
     * @return false, if it fails
     */
    public boolean setExtraWS( int extra ) {
	// do some checks ?
	m_extraws = extra;
	return true;
    }

    public int getExtraWS() {
	return m_extraws;
    }

    /**
     * 
     *
     * @return false, if it fails
     */
    public boolean setExtraLeading( int lea ) {
	// do some checks ?
	m_extraleading = lea;
	return true;
    }

    public int getExtraLeading() {
	return m_extraleading;
    }

    /**
     * aka. radio button "use schema name"
     *
     * @return false, if it fails
     */
    public boolean setSchemaName() {
	m_fontUseSchema = true;
	return true;
    }


    /**
     * aka. radio button "custom font name"
     *
     * @return false, if it fails
     */
    public boolean setCustomName( String fontname ) {
	// do some checks ?
	m_fontUseSchema = false;
	m_fontCustomName = fontname;
	return true;
    }

    /**
     * Get Schema/Custom name, whichever is the current.
     */
    public String getSCName() {
	return ( m_fontUseSchema ? getSchemaName() : getCustomName() );
    }

    /**
     * Is Schema or custom name the current
     */
    public boolean isSchemaName() {
	return m_fontUseSchema;
    }

    public String getSchemaName() {
	return calcSchemaName();
    }

    public String getCustomName() {
	return m_fontCustomName;
    }

}


