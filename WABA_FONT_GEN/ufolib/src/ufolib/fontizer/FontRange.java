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

import waba.util.IntVector;

/**
 * Range manager: _sorted_ ranges
 *
 */
public class FontRange {

    private static final int MAXENCVALUE = 2147483647;

    private IntVector m_rangeFrom;
    private IntVector m_rangeTo;

    public FontRange() {
	m_rangeFrom = new IntVector();
	m_rangeTo = new IntVector();
    }

    // *******************************************************************
    // 

    public String toString() {
	StringBuffer b = new StringBuffer();
	int si = size();
	b.append( "[FontRange[size=" + si + ",ranges=[" );
	for ( int index = 0; index < si; index++ ) {
	    b.append( "(" + getRangeStart( index ) + "-" + getRangeEnd( index ) + ")" );
	}
	b.append( "]]" );

	return b.toString();
    } 

    /**
     * this can change the range indexs!
     *
     * @return false, if it fails
     */
    public boolean addRange( int fromValue, int toValue ) {
	if ( ( fromValue <= toValue ) && ( 0 <= fromValue ) && ( toValue <= MAXENCVALUE ) ) {
	    // 1. search for fitting range
	    int si = size();
	    int beforeindex = -1;
	    if ( si > 0 ) {
		boolean found = false;
		for ( int mindex = 0; ( mindex < si) & ! found; mindex++ ) {
		    if ( getRangeStart( mindex ) > toValue ) {
			// before this index!
			if ( mindex == 0 ) {
			    // before first range
			    beforeindex = mindex;
			    found = true;
			}
			else if ( getRangeEnd( mindex - 1 ) < fromValue ) {
			    // it fits in the place
			    beforeindex = mindex;
			    found = true;
			}
			else {
			    return false;
			}
		    }
		}
		if ( ! found ) {
		    if ( ! ( getRangeEnd( si - 1 ) < fromValue ) ) {
			// does not fit after the last index
			return false;
		    }
		    // else:
		    beforeindex = si;
		}
	    }
	    else {
		beforeindex = 0;
	    }

	    // 2. ok, it fits, then add one range
	    m_rangeFrom.add( -1 );  // value will be set by move
	    m_rangeTo.add( -1 );  // value will be set by move
	    
	    // 3. move 
	    if ( si > 0 ) {
		for ( int mindex = si - 1; mindex >= beforeindex; mindex-- ) {
		    // mindex is source
		    m_rangeFrom.setElementAt( m_rangeFrom.elementAt( mindex ), mindex + 1 );
		    m_rangeTo.setElementAt( m_rangeTo.elementAt( mindex ), mindex + 1 );
		}
	    }
		
	    // 4. set new values
	    m_rangeFrom.setElementAt( fromValue, beforeindex );
	    m_rangeTo.setElementAt( toValue, beforeindex );

	    // ok.
	    return true;
	}	

	// else:
	return false;
    }

    /**
     * @return actual set value; -1 if it fails
     */
    public int setRangeStart( int index, int value ) {
	if ( ( 0 <= index ) && ( index < size() ) ) {
	    if ( value <= getRangeEnd( index ) ) {
		if ( index == 0 ) {
		    // first range
		    if ( value < 0 ) {
			m_rangeFrom.setElementAt( 0, index );
		    }
		    else {
			// value >= 0
			m_rangeFrom.setElementAt( value, index );
		    }
		    return getRangeStart( index );		
		}
		else {
		    // not first range
		    if ( getRangeEnd( index - 1 ) < value ) {
			m_rangeFrom.setElementAt( value, index );
		    }
		    else {
			m_rangeFrom.setElementAt( getRangeEnd( index - 1 ) + 1, index );
		    }
		    return getRangeStart( index );
		}
	    }
	    else {
		// value > getRangeEnd( index )
		m_rangeFrom.setElementAt( getRangeEnd( index ), index );
		return getRangeStart( index );
	    }
	}
	//else:
	return -1;
    }



    
    /**
     * @return actual set value; -1 if it fails
     */
    public int setRangeEnd( int index, int value ) {
	if ( ( 0 <= index ) && ( index < size() ) ) {
	    if ( value >= getRangeStart( index ) ) {
		if ( index == ( size() - 1 ) ) {
		    // last range
		    if ( value > MAXENCVALUE ) {
			m_rangeTo.setElementAt( MAXENCVALUE, index );
		    }
		    else {
			// value <= MAXENCVALUE
			m_rangeTo.setElementAt( value, index );
		    }
		    return getRangeEnd( index );		
		}
		else {
		    // not last range
		    if ( getRangeStart( index + 1 ) > value ) {
			m_rangeTo.setElementAt( value, index );
		    }
		    else {
			m_rangeTo.setElementAt( getRangeStart( index + 1 ) - 1, index );
		    }
		    return getRangeEnd( index );
		}
	    }
	    else {
		// value < getRangeStart( index )
		m_rangeTo.setElementAt( getRangeStart( index ), index );
		return getRangeEnd( index );
	    }
	}
	//else:
	return -1;
    }

    /**
     * @return false, if it fails
     */
    public boolean combineWithSuccessorRange( int index ) {
	return combineRanges( index, index + 1 );

	/*
	int si = size();
	if ( ( si >= 2 ) && ( 0 <= index ) && ( index < ( si - 1 ) ) ) {
	    // 1. combine
	    m_rangeTo.setElementAt( m_rangeTo.elementAt( index + 1 ), index );
	    // 2. move
	    for ( int mindex = index + 1; mindex <= ( si - 2 ); mindex++ ) {
		m_rangeFrom.setElementAt( m_rangeFrom.elementAt( mindex + 1 ), mindex );
		m_rangeTo.setElementAt( m_rangeTo.elementAt( mindex + 1 ), mindex );
	    }
	    // 3. remove last
	    m_rangeFrom.del( si - 1 );
	    m_rangeTo.del( si - 1 );
	    
	    // ok.
	    return true;
	}
	//else:
	return false;
	*/
    }

    /**
     * @return false, if it fails
     */
    public boolean combineRanges( int fromIndex, int toIndex ) {
	int si = size();
	if ( ( fromIndex < toIndex ) && ( 0 <= fromIndex ) && ( toIndex < si ) ) {
	    // 1. combine
	    m_rangeTo.setElementAt( m_rangeTo.elementAt( toIndex ), fromIndex );
	    // 2. move
	    for ( int mindex = fromIndex + 1; mindex <= ( si - ( toIndex - fromIndex + 1) ); mindex++ ) {
		m_rangeFrom.setElementAt( m_rangeFrom.elementAt( mindex + ( toIndex - fromIndex ) ), mindex );
		m_rangeTo.setElementAt( m_rangeTo.elementAt( mindex + ( toIndex - fromIndex ) ), mindex );
	    }
	    // 3. remove last
	    for ( int mindex = si - 1; mindex >= ( si - ( toIndex - fromIndex ) ); mindex-- ) { 
		m_rangeFrom.del( mindex );
		m_rangeTo.del( mindex );
	    }
	    
	    // ok.
	    return true;
	}
	//else:
	return false;
    }


    /**
     * @false, if it fails
     */
    public boolean deleteRange( int index ) {
	int si = size();
	if ( ( si > 0 ) && ( 0 <= index ) && ( index < si ) ) {  // row exists
	    m_rangeFrom.del( index );
	    m_rangeTo.del( index );
	    return true;
	}

	//else:
	return false;
    }


    /**
     * @param value new end value of index's (aka. first of the two) range
     * @return false, if it fails
     */
    public boolean splitRange( int index, int value ) {
	int si = size();
	if ( ( 0 <= index ) && ( index < size() ) ) {
	    if ( ( getRangeStart( index ) <= value ) && ( value < getRangeEnd( index ) ) ) {
		// 1. (move last = add)
		m_rangeFrom.add( -1 );  // value will be set by 2. move
		m_rangeTo.add( -1 );  // value will be set by 2. move
		// 2. move
		for ( int mindex = si - 1; mindex >= index; mindex-- ) {
		    // mindex is source
		    m_rangeFrom.setElementAt( m_rangeFrom.elementAt( mindex ), mindex + 1 );
		    m_rangeTo.setElementAt( m_rangeTo.elementAt( mindex ), mindex + 1 );
		}
		// 3. split
		m_rangeFrom.setElementAt( value + 1, index + 1 );
		m_rangeTo.setElementAt( value, index );
		
		// ok.
		return true;
	    }
	}
	//else:
	return false;
    }


    /**
     * @return false, if it fails
     */
    public boolean removeAllRanges() {
	m_rangeFrom.clear();
	m_rangeTo.clear();
	return true;
    }


    /**
     * @return flase, if it fails
     */
    public boolean setCompleteRanges( FontRange rin ) {
	removeAllRanges();
	int si = rin.size();
	for ( int index = 0; index < si; index++ ) {
	    addRange( rin.getRangeStart( index ), rin.getRangeEnd( index ) );
	}
	return true;
    }

    public int getRangeStart( int index ) {
	return m_rangeFrom.elementAt( index );
    }

    public int getRangeEnd( int index ) {
	return m_rangeTo.elementAt( index );
    }

    /**
     * number of ranges
     */
    public int size() {
	return m_rangeFrom.size();
    }
	
    /**
     * total number of chars 
     */
    public int sum() {
	int summ = 0;
	int si = m_rangeFrom.size();
	for ( int index = 0; index < si; index++ ) {
	    summ += getRangeEnd( index ) - getRangeStart( index ) + 1;
	}
	return summ;
    }


    // *******************************************************************
    // 

    /**
     * intersect with another range
     */
    public static FontRange intersect( FontRange rangeA, FontRange rangeB ) {
	kout( "intersect " + rangeA + " AND " + rangeB );

	int fromValue, toValue, fromRange, toRange, higherOne;
	FontRange ret = new FontRange();

	FontRange[] range = new FontRange[ 2 ];
	int[] index = new int[ 2 ];
	int[] size = new int[ 2 ];
	boolean[] high = new boolean[ 2 ];

	range[ 0 ] = rangeA; range[ 1 ] = rangeB; 
	index[ 0 ] = 0; index[ 1 ] = 0; 
	size[ 0 ] = range[ 0 ].size(); size[ 1 ] = range[ 1 ].size();
	high[ 1 ] = false; high[ 1 ] = false;

	toValue = -1;
	fromValue = -1;
	kout( "start " );
	while ( ( index[ 0 ] < size[ 0 ] ) && ( index[ 1 ] < size[ 1 ] ) ) {
	    if ( ( ! high[ 0 ] ) && ( ! high[ 1 ] ) ) {
		// search next start: the bigger start
		fromRange = ( range[ 0 ].getRangeStart( index[ 0 ] ) >  range[ 1 ].getRangeStart( index[ 1 ] ) ? 0 : 1 );
		fromValue = range[ fromRange ].getRangeStart( index[ fromRange ] );
		kout( "ll fromValue " + fromValue );
		high[ 1 - fromRange ] = true;
	    }
	    else if ( ( high[ 0 ] ) && ( high[ 1 ] ) ) {
		// both high
		toRange =  ( range[ 0 ].getRangeEnd( index[ 0 ] ) <  range[ 1 ].getRangeEnd( index[ 1 ] ) ? 0 : 1 );
		toValue = range[ toRange ].getRangeEnd( index[ toRange ] );
		kout( "hh toValue " + toValue );
		// finish range
		kout( "add (" + fromValue + " , " + toValue + ")" );
		ret.addRange( fromValue, toValue );
		// finish this
		high[ toRange ] = false;
		index[ toRange ]++;
		if ( ! ( index[ toRange ] < size[ toRange ] ) ) {
		    // while guard fails, so finish loop
		}
		else {
		    // search next start: the bigger start
		    fromRange = ( range[ 0 ].getRangeStart( index[ 0 ] ) >  range[ 1 ].getRangeStart( index[ 1 ] ) ? 0 : 1 );
		    fromValue = range[ fromRange ].getRangeStart( index[ fromRange ] );
		    kout( "hh fromValue " + fromValue );
		    high[ 1 - fromRange ] = true;
		}
	    }
	    else {
		// one is high, one is low
		higherOne = ( high[ 0 ] ? 0 : 1 );  
		if ( range[ higherOne ].getRangeEnd( index[ higherOne ] ) < fromValue ) {
		    // no intersect, so: skip
		    high[ higherOne ] = false;
		    index[ higherOne ]++;
		}
		else {
		    // intersect start at fromValue
		    high[ 1 - higherOne ] = true;
		}
	    }
	}
	
	kout( "intersect result " + ret );
	return ret;
    }


    private static void kout( String s ) {
	//System.out.println( "intersect:" + s );
    }
	

}





