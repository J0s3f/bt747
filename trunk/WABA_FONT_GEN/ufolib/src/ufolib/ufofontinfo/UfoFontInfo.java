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
*/
package ufolib.ufofontinfo;

import ufolib.engine.*;

import waba.ui.*;
import waba.io.*;
import waba.fx.*;
import waba.sys.*;
import waba.util.*;

/*
 * Main 
 */
public class UfoFontInfo extends MainWindow {

    public void onStart()
    {
	Settings.setPalmOSStyle( true );
	doShow();
    }

    Label titlel;
    UfoLabel demol;
    ListBox lb;
    Button butA, but1, but2;

    Vector cl;

    private void doShow() {

	Container gc = new Container();

	add( gc );
	gc.setRect( 0, 0, Settings.screenWidth, Settings.screenHeight );
	
	titlel = new Label( "Ufo Font Overview" );
	gc.add( titlel );
	titlel.setRect( Control.CENTER, Control.TOP, Control.PREFERRED, Control.PREFERRED ); 

	demol = new UfoLabel( "X" );
	gc.add( demol );
	demol.setRect( Control.CENTER, Control.AFTER, Control.PREFERRED, Control.PREFERRED ); 
	
	butA = new Button( "About" );
	but2 = new Button( "Exit" );
	gc.add( butA ); 
	gc.add( but2 );
	butA.setRect( Control.CENTER, Control.AFTER, Control.PREFERRED, Control.PREFERRED ); 
	but2.setRect( Control.AFTER, Control.SAME, Control.PREFERRED, Control.PREFERRED ); 

	lb = new ListBox();
	gc.add( lb );
	cl = new Vector();
	fillCl();
	for ( int index = 0; index < cl.size(); index++ ) {
	    lb.add( (String) cl.get( index ) );
	}
	//lb.setRect( Control.CENTER, Control.AFTER, Control.PREFERRED, Control.PREFERRED );
	lb.setRect( Control.CENTER, Control.AFTER, Settings.screenWidth, Control.PREFERRED );


    }

    String[] fnames;

    private void fillCl() {
	
	// General info
	cl.add( "UFF General Info" );
	fnames = getAllFontsName();
	cl.add( fnames.length + " fonts installed." );
	// Each font short
	for ( int index = 0; index < fnames.length; index++ ) {
	    cl.add( "  " + fnames[ index ] );
	}

	// Each font long
	for ( int index = 0; index < fnames.length; index++ ) {
	    cl.add( "------------------" );
	    cl.add( "Font[" + index + "]=" + fnames[ index ] );
	    addOneFontInfo( fnames[ index ] );
	}
    }	

    private void addOneFontInfo( String name ) {
	UfoUserFont uff = new UfoUserFont( name );
	if ( uff.loaded ) {
	    cl.add( "Number of chars=" + uff.getNumberOfChars() + " (+ MCS)" );
	    cl.add( "Magic is " + uff.magic );
	    cl.add( "Comment:" + uff.comment );
	    cl.add( "Font rectangle=(" + uff.fRectWidth + "x" + uff.fRectHeight+ ")" );
	    cl.add( "Ascent=" + uff.ascent + " Descent=" + uff.descent + " Leading=" + uff.leading );

	    cl.add( "Number of ranges=" + uff.nParts + " (+ MCS )" );
	    for ( int index = 0; index < uff.nParts; index++ ) {
		cl.add( "  Range[" + index + "]=(" + uff.firstChar[ index ] + "-" + uff.lastChar[ index ] + ")" );
	    }
	}
	uff = null;
    }

    private String extractFontName( String catName ) {
	if ( ( catName.substring( 0, 3 ) ).equals( UfoUserFont.UFF_NAMEPREFIX ) ) {
	    String cridtyp = catName.substring( catName.length() - 10 );
	    if ( cridtyp.equals( "." + UfoUserFont.UFF_CREATORID + "." + UfoUserFont.UFF_TYPEID ) ) {
		return catName.substring( 0, catName.length() - 10 );
	    }
	}
	// default
	return null;
    }


    private String[] getAllFontsName() {
	boolean unique;
	String oned, curname;
	String[] allcats = Catalog.listCatalogs();
	Vector collD = new Vector();
	for ( int index = 0; index < allcats.length; index++ ) {
	    oned = extractFontName( allcats[ index ] );
	    if ( oned != null ) {
		// its a font, so
		// check if unique
		unique = true;
		for ( int inindex = 0; ( inindex < collD.size() ) && unique ; inindex++ ) {
		    if ( ( (String) collD.get( inindex ) ).equals( oned ) ) {
			unique = false;
		    }
		}
		if ( unique ) {
		    collD.add( oned );
		}
	    }
	}
	String[] ret = new String[ collD.size() ];
	for ( int index = 0; index < ret.length; index++ ) {
	    ret[ index ] = (String) collD.get( index );
	}
	return ret;
    }

	
    public void onEvent( Event event ) {

	if ( event.type == ControlEvent.PRESSED ) {
	    if ( event.target == butA ) {
		showAbout();
	    }
	    else if ( event.target == but2 ) {
		doExit();
	    }
	}
    }
		

    private void doExit() {
	exit( 0 );
    }

    private void showAbout() {
	MessageBox mb = new MessageBox( "ufolib", UfoUserFont.AboutString );
	mb.setBorderStyle( (byte) 1 );
	popupModal(mb);
    }



    
}



