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
import waba.io.*;

/** 
 * Represents the internal font structure, readed from a pdb file. used internally.
 */

public class UfoUserFont {

    public static final String AboutString = "ufolib|Unicode for Superwaba|(c)2002 Oliver Erdmann|http://jdict.sf.net|LGPL Version 2.1||Parts of the software are|copyright (C) 2000-2002|Guilherme Campos Hazan|<guich@superwaba.org.>|http://www.superwaba.org||This library is free software; you can|redistribute it and/or modify it|under the terms of the GNU Lesser|General Public License Version 2.1 as|published by the|Free Software Foundation.|This library is distributed in the|hope that it will be useful, but| WITHOUT ANY WARRANTY; without|even the implied warranty|of MERCHANTABILITY or FITNESS|FOR A PARTICULAR PURPOSE.|See the GNU Lesser General Public|License for more details.";

    public static final String UFF_NAMEPREFIX = "UFF";
    public static final String UFF_CREATORID = "uffo";
    public static final String UFF_TYPEID = "DATA";

    /**
     *  the font file
     */
    String m_catalogName;

    /**
     *  the font name
     */
    String m_fontname;
    
    /**
     * true, if loding font succeeded
     */
    public boolean loaded = false;

    // ********************************************************************
    // common data of this font

    public short magic;            // magic is 12003
    public String fcName;          // is the short name, e.g. "gnuuni", "myarial"
    public short fcSize;           // is the point size, e.g. 10, 12
    public short fcSlant;          // is 0 for normal font; 1, 2, 3 for bold, italic, bold-italic
    public String comment;         // comment
    public short fontType;         // font type
    public short maxWidth;         // maximum character width
    public short kernMax;          // negative of maximum character kern
    public short nDescent;         // negative of descent
    public short fRectWidth;       // width of font rectangle
    public short fRectHeight;      // height of font rectangle
    public short ascent;           // ascent
    public short descent;          // descent
    public short leading;          // leading

    // the ranges
    public short nParts;           // number of parts following (without MCS)
    public short[] recNo;          // record number, where is the range?
    public int[] firstChar;        // encoding of first char in this range
    public int[] lastChar;         // encoding of last char in this range
    public UfoUserFontRange[] range;   // the bitmap

    // MCS
    public short recNoMCS;         // record number, where to find this char
    public UfoUserFontRange rangeMCS;  // the bitmap
    

    // ********************************************************************
    // init

    /**
     * Do nothing (for converting)
     */
    public UfoUserFont() {
    }


    /**
     * Load the given font
     */
    public UfoUserFont( String fontname ) {
	setFontName( fontname );
	initLoad();
    }

    /**
     * debug messages
     */
    public static void debugMess( String s ) {
	//System.err.println( s );
    }

    /**
     * Load common font data
     */
    public void initLoad() {

	Catalog c = new Catalog( m_catalogName, Catalog.READ_ONLY );

	if ( c.isOpen() ) {
	    DataStream ds = new DataStream( c );

	    // load header record
	    c.setRecordPos( 0 );
	    magic = ds.readShort();
	    debugMess( "UfoUserFont,initLoad: magic " + magic );
	    fcName = ds.readString();
	    debugMess( "UfoUserFont,initLoad: fcName " + fcName );
	    fcSize = ds.readShort();
	    debugMess( "UfoUserFont,initLoad: fcSize " + fcSize );
	    fcSlant = ds.readShort();
	    debugMess( "UfoUserFont,initLoad: fcSlant " + fcSlant );
	    comment = ds.readString();
	    debugMess( "UfoUserFont,initLoad: comment " + comment );
	    fontType = ds.readShort();
	    debugMess( "UfoUserFont,initLoad: fontType " + fontType );
	    maxWidth = ds.readShort();
	    debugMess( "UfoUserFont,initLoad: " + maxWidth );
	    kernMax = ds.readShort();
	    debugMess( "UfoUserFont,initLoad: " + kernMax );
	    nDescent = ds.readShort();
	    debugMess( "UfoUserFont,initLoad: " + nDescent );
	    fRectWidth  = ds.readShort();
	    debugMess( "UfoUserFont,initLoad: fRectWidth " + fRectWidth );
	    fRectHeight = ds.readShort();
	    debugMess( "UfoUserFont,initLoad: fRectHeight " + fRectHeight );
	    ascent = ds.readShort();
	    debugMess( "UfoUserFont,initLoad: " + ascent );
	    descent = ds.readShort();
	    debugMess( "UfoUserFont,initLoad: " + descent );
	    leading = ds.readShort();
	    debugMess( "UfoUserFont,initLoad: " + leading );

	    nParts = ds.readShort();
	    debugMess( "UfoUserFont,initLoad: nParts " + nParts );
	    recNo = new short[ nParts ];
	    firstChar = new int[ nParts ];
	    lastChar = new int[ nParts ];
	    range = new UfoUserFontRange[ nParts ];
	    for ( int index = 0; index < nParts; index++ ) {
		recNo[ index ] = ds.readShort(); 
		firstChar[ index ] = ds.readInt();
		lastChar[ index ] = ds.readInt();
		debugMess( "UfoUserFont,initLoad: range " + index + ": recNo=" + recNo[ index ] + " (" + firstChar[ index ] + "-" + lastChar[ index ] + ")" );
	    }
	    recNoMCS = ds.readShort();
	    debugMess( "UfoUserFont,initLoad: recNoMCS=" + recNoMCS );
	    
	    // (pre)load MCS record
	    c.setRecordPos( recNoMCS );
	    rangeMCS = new UfoUserFontRange();
	    rangeMCS.numberOfChars = 1;
	    rangeMCS.rowByte = ds.readInt();
	    debugMess( "UfoUserFont,initLoad: rangeMCS.rowByte=" +  rangeMCS.rowByte );
	    rangeMCS.bitmapTableSize = rangeMCS.rowByte * (int) fRectHeight;
	    rangeMCS.bitmapTable = new byte[ rangeMCS.bitmapTableSize ];
	    ds.readBytes( rangeMCS.bitmapTable );
	    debugMess( "UfoUserFont,initLoad: rangeMCS.bitmapTableSize=" +  rangeMCS.bitmapTableSize );
	    rangeMCS.bitIndexTable = new int[ rangeMCS.numberOfChars + 1 ];
	    for ( int index = 0; index < rangeMCS.bitIndexTable.length; index++ ) {
		rangeMCS.bitIndexTable[ index ] = ds.readInt(); 
		debugMess( "UfoUserFont,initLoad: rangeMCS.bitmapIndexTable=offset=" + rangeMCS.bitIndexTable[ index ] );
	    }

	    c.close();
	    loaded = true;
	}
    }
    

    // **************************************************************************
    // For converting from BDF
    
    
    public void setFontName( String fName ) {
	m_fontname = fName;
	m_catalogName = m_fontname + "." + UFF_CREATORID + "." + UFF_TYPEID;
    }


    /**
     * Save pdb
     *
     * @return false, if it fails
     */
    public boolean savePdb( ) {

	Catalog catalog = null;
	ResizeStream resizeCatalog = null;
	DataStream dataCatalog = null;
	
	// 1. open catalog file
	catalog = new Catalog( m_catalogName, Catalog.READ_WRITE );
	if ( ! catalog.isOpen() ) {
	    catalog = new Catalog( m_catalogName, Catalog.CREATE );
	}
	resizeCatalog = new ResizeStream( catalog, 1024 );
	dataCatalog = new DataStream( resizeCatalog );
	// delete old data
	int maxR = catalog.getRecordCount();
	for ( int index = maxR - 1; index >= 0; index-- ) {
	    if ( catalog.setRecordPos( index ) ) {
		catalog.deleteRecord();
	    }
	}

	// 2. write records
	// 2.1. write header record
	resizeCatalog.startRecord();
	dataCatalog.writeShort( magic );
	dataCatalog.writeString( ( fcName == null ? "" : fcName ) );
	dataCatalog.writeShort( fcSize );
	dataCatalog.writeShort( fcSlant );
	dataCatalog.writeString( ( comment == null ? "" : comment ) );
	dataCatalog.writeShort( fontType );
	dataCatalog.writeShort( maxWidth );
	dataCatalog.writeShort( kernMax );
	dataCatalog.writeShort( nDescent );
	dataCatalog.writeShort( fRectWidth );
	dataCatalog.writeShort( fRectHeight );
	dataCatalog.writeShort( ascent );
	dataCatalog.writeShort( descent );
	dataCatalog.writeShort( leading );

	debugMess( "UfoUserFont,save: magic " + magic );
	debugMess( "UfoUserFont,save: comment " + comment );
	debugMess( "UfoUserFont,save: fontType " + fontType );
	debugMess( "UfoUserFont,save: " + maxWidth );
	debugMess( "UfoUserFont,save: " + kernMax );
	debugMess( "UfoUserFont,save: " + nDescent );
	debugMess( "UfoUserFont,save: fRectWidth " + fRectWidth );
	debugMess( "UfoUserFont,save: fRectHeight " + fRectHeight );
	debugMess( "UfoUserFont,save: " + ascent );
	debugMess( "UfoUserFont,save: " + descent );
	debugMess( "UfoUserFont,save: " + leading );

	// the ranges
	dataCatalog.writeShort( nParts );
	debugMess( "UfoUserFont,save: nParts " + nParts );

	for ( int index = 0; index < nParts; index++ ) {
	    dataCatalog.writeShort( recNo[ index ] );
	    dataCatalog.writeInt( firstChar[ index ] );
	    dataCatalog.writeInt( lastChar[ index ] );
	    debugMess( "UfoUserFont,save: range " + index + ": recNo=" + recNo[ index ] + " (" + firstChar[ index ] + "-" + lastChar[ index ] + ")" );
	}
	// the MCS  
	dataCatalog.writeShort( recNoMCS );
	resizeCatalog.endRecord();

	// 2.2. the range records
	int countBytes;
	for ( int index = 0; index < nParts; index++ ) {
	    debugMess( "UfoUserFont,save: starting record range " + index + ": recNo=" + recNo[ index ] );
	    resizeCatalog.startRecord();
	    
	    dataCatalog.writeInt( range[ index ].rowByte );

	    countBytes = range[ index ].rowByte * fRectHeight;
	    dataCatalog.writeBytes( range[ index ].bitmapTable, 0, countBytes );
	    
	    for ( int ci = 0; ci < ( range[ index ].numberOfChars + 1 ); ci++ ) {
		dataCatalog.writeInt( range[ index ].bitIndexTable[ ci ] );
	    }

	    resizeCatalog.endRecord();
	}

	// 2.3 the MCS
	debugMess( "UfoUserFont,save: starting record range MCS recNo=" + recNoMCS );
	resizeCatalog.startRecord();
	dataCatalog.writeInt( rangeMCS.rowByte );
	countBytes = rangeMCS.rowByte * fRectHeight;
	dataCatalog.writeBytes( rangeMCS.bitmapTable, 0, countBytes );
	debugMess( "UfoUserFont,save: MCS bitindextable " +  rangeMCS.bitIndexTable[ 0 ] + ", " + rangeMCS.bitIndexTable[ 1 ] );
	dataCatalog.writeInt( rangeMCS.bitIndexTable[ 0 ] );
	dataCatalog.writeInt( rangeMCS.bitIndexTable[ 1 ] );
	resizeCatalog.endRecord();
	
	// 3. close catalog file
	catalog.close();

	return true;
    }


    // **************************************************************************
    // Manage char ranges


    /**
     * Find range. Best would be a binary tree search.
     *
     * @return index in range arrays, or -1 if MCS
     */
    private int indexRange( char ch ) {
	int enc = (int) ch;
	for ( int index = 0; index < nParts; index++) {
	    if ( ( firstChar[ index ] <= enc ) && ( enc <= lastChar[ index ] ) ) {
		return index;
	    }
	}

	// not found:
	return -1;
    }


    /**
     * load and cache used ranges. pending: unload unused ranges
     *
     * @return index in range arrays, or -1 if MCS
     */
    private int ensureRange( char ch ) {
	int acLoaded;

	int rindex = indexRange( ch );
	if ( rindex >= 0 ) {
	    if ( range[ rindex  ] != null ) {
		//debugMess( "UfoUserFont,ensureRange: char " + ( (int) ch ) + " in range " + rindex + " CACHED" );
		// even loaded
		// do nothing
	    }
	    else {
		// load
		debugMess( "UfoUserFont,ensureRange: char " + ( (int) ch ) + " is not loaded yet..." );
		Catalog c = new Catalog( m_catalogName, Catalog.READ_ONLY );
		if ( c.isOpen() ) {
		    DataStream ds = new DataStream( c );
		    // load record
		    debugMess( "UfoUserFont,ensureRange: try recPos " + recNo[ rindex ] );
		    c.setRecordPos( recNo[ rindex ] );
		    range[ rindex ] = new UfoUserFontRange();
		    range[ rindex ].numberOfChars = lastChar[ rindex ] - firstChar[ rindex ] + 1;
		    debugMess( "UfoUserFont,ensureRange: numberOfChars=" + range[ rindex ].numberOfChars );
		    range[ rindex ].rowByte = ds.readInt();
		    debugMess( "UfoUserFont,ensureRange: rowByte=" + range[ rindex ].rowByte );
		    range[ rindex ].bitmapTableSize = range[ rindex].rowByte * (int) fRectHeight;
		    debugMess( "UfoUserFont,ensureRange: bitmapTableSize=" + range[ rindex ].bitmapTableSize );
		    range[ rindex ].bitmapTable = new byte[ range[ rindex ].bitmapTableSize ];
		    acLoaded = ds.readBytes( range[ rindex ].bitmapTable );
		    debugMess( "UfoUserFont,ensureRange: bitmapTableSize=" + range[ rindex ].bitmapTableSize + ", loaded=" + acLoaded );
		    range[ rindex ].bitIndexTable = new int[ range[ rindex ].numberOfChars + 1 ];
		    for ( int index = 0; index < range[ rindex ].bitIndexTable.length; index++ ) {
			range[ rindex ].bitIndexTable[ index ] = ds.readInt(); 
		    }
		    debugMess( "UfoUserFont,ensureRange: loaded range " + rindex + ": number=" + range[ rindex ].numberOfChars + ", rowByte=" + range[ rindex ].rowByte + ", bitmapTableSize=" + range[ rindex ].bitmapTableSize );
		    c.close();
		}
		else {
		    // open catalog failed, so use MCS instead
		    debugMess( "UfoUserFont,ensureRange: open catalog for range "+ rindex + "failed, so using MCS instead" );
		    rindex = -1;
		}
	    }
	}
	else {
	    // MCS
	    // do nothing, MCS "range" is loaded on startup
	    //debugMess( "UfoUserFont,ensureRange: an MCS char " + ( (int) ch ) + " is auto-CACHED" );
	}
	return rindex;
    }


    // **************************************************************************
    // Access to char
    // In difference to normal font, the bitmap etc. _depends_ on the
    // char you want

    public boolean isMCS( char ch ) {
	return ( indexRange( ch ) == -1 ) ;
    }

    public byte[] bitmapTable( char ch ) {
	if ( ! isMCS( ch ) ) {
	    // ensure that range is loaded
	    int cindex = ensureRange( ch );
	    return range[ cindex ].bitmapTable;
	}
	else {
	    return rangeMCS.bitmapTable;
	}
    }

    public int[] bitIndexTable( char ch ) {
	if ( ! isMCS( ch ) ) {
	    // ensure that range is loaded
	    int cindex = ensureRange( ch );
	    return range[ cindex ].bitIndexTable;
	}
	else {
	    return rangeMCS.bitIndexTable;
	}
    }  


    /**
     * The index of the char inside its bitmapTable/bitIndexTable
     */
    public int index( char ch ) {
	if ( ! isMCS( ch ) ) {
	    // ensure that range is loaded
	    int cindex = ensureRange( ch );
	    return ( (int) ch ) - firstChar[ cindex ];
	}
	else {
	    return 0;
	}
    }  
	

    public int rowWidthInBytes( char ch ) {
	if ( ! isMCS( ch ) ) {
	    // ensure that range is loaded
	    int cindex = ensureRange( ch );
	    return range[ cindex ].rowByte;
	}
	else {
	    return rangeMCS.rowByte;
	}
    }  


    public int charWidth( char ch ) {
	// ensure that range is loaded
	int cindex = ensureRange( ch );
	if ( ! isMCS( ch ) ) {
	    UfoUserFontRange crange = range[ cindex ];
	    int index = ch - firstChar[ cindex ];
	    // Get the source x coordinate and width of the character
	    int offset = crange.bitIndexTable[ index ];
	    return (byte) ( crange.bitIndexTable[ index + 1 ] - offset);
	}
	else {
	    return (byte) ( rangeMCS.bitIndexTable[ 1 ]);
	}	    
    }


    public int stringWidth(String s) {
	char []ac = s.toCharArray();
	int sum = 0;
	for ( int i =0; i < ac.length; i++ )
	    sum += charWidth( ac[ i ] );
	return sum;
    }


    public int getAscent() {
	return ascent;
    }
    

    public int getDescent() {
	return descent;
    }


    public int getLeading() {
	return leading;
    }


    /**
     * This is _without_ MCS
     */
    public int getNumberOfChars() {
	int num = 0;
	for ( int index=0; index < nParts; index++ ) {
	    num += ( lastChar[ index ] - firstChar[ index ] + 1 );
	}
	return num;
    }

}
