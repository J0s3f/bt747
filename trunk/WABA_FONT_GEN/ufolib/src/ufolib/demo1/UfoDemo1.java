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
package ufolib.demo1;

import ufolib.engine.*;

import waba.ui.*;
import waba.fx.*;
import waba.sys.*;

/*
 * Main 
 */
public class UfoDemo1 extends MainWindow {

    public void onStart()
    {
	//Settings.setPalmOSStyle( true );

	// select one test
	doTest2();
    }

    private void doTest3() {
	Container gc = new Container();

	Label l1 = new Label( "spain çãáéíóú" );
	gc.add( l1 );
	l1.setRect( 0, 21, 150, 20 );

	Label l3 = new Label( "spainU \u00E3\u00E7\u00ED" );
	gc.add( l3 );
	l3.setRect( 0, 41, 150, 20 );

	Label l2 = new Label( "german öäüÖÄÜß" );
	gc.add( l2 );
	l2.setRect( 0, 61, 150, 20 );

	gc.setRect( 0,10, 150,140 );
	add( gc );
    }


    /**
     * Test two fonts
     */
    private void doTest2() {

	Container gc = new Container();
	UfoLabel but1 = new UfoLabel( "X" );
	UfoLabel but2 = new UfoLabel( "X" );
	but1.setUfoFont( new UfoFont( "genXX", UfoFont.PLAIN, 12 ) );

	but2.setUfoFont( new UfoFont( "genXX", UfoFont.PLAIN, 17 ) );
	//but2.setUfoFont( new UfoFont( "japanese",UfoFont.PLAIN, 18) );		
	
	String textd = "ii iiiaaa\u09F2bbbcccü!\u00fc! Test two different UFF fonts, if it runs and to see the difference and usage.";  
	//textd = "\u7FFD";	//32765
	//textd = textd + "\u8005";	//32773


	UfoFontMetrics ufm1 = but1.getUfoFontMetrics();
	UfoFontMetrics ufm2 = but2.getUfoFontMetrics();

	//String textd1 = UfoConvert.insertLineBreak( 120, '|', ufm1, textd );
	//String textd2 = UfoConvert.insertLineBreak( 120, '|', ufm2, textd );
	String textd1 = textd; 
	String textd2 = textd; 

	but1.setText( textd1 );
	gc.add( but1 );
	but1.setRect( 0, 0, 150, 20 );

	but2.setText( textd2 );
	gc.add( but2 );
	but2.setRect( 0, 21, 150, 20 );


	Label l1 = new Label( "çãáéíóú" );
	gc.add( l1 );
	l1.setRect( 0, 41, 150, 20 );

	Label l2 = new Label( "Hallo" );
	gc.add( l2 );
	l2.setRect( 0, 61, 150, 20 );

	gc.setRect( 0,10, 150,140 );
	add( gc );
    }



    /**
     * Test internal coding of literal given Strings
     */
    private void doTest1() {

	Container gc = new Container();
	UfoLabel but2 = new UfoLabel( "X" );
	
	// one more ü = 0x00fc (german umlaut u)
	int ucsValue = 2546;
	String genu = ( (char) 2546 ) + "a\u09F2ü\u00fc!" + ( (char) ucsValue ) + "!";  

	String h = "|";
	for ( int index = 0; index < genu.length(); index++ ) {
	    h = h + "," + index + ":" + ( (int) genu.charAt( index ) );
	}

	String textform = genu + h;

	//System.err.println( "EE:" + textform );

	but2.setText( textform );

	gc.add( but2 );
	but2.setRect( 0, 0, 150, 60 );

	Label l = new Label( textform );
	gc.add( l );
	l.setRect( 0, 60, 150, 60 );
	
	gc.setRect( 0,10, 150,140 );
	add( gc );

	//but2.scroll( true );
	
    }

    
}


