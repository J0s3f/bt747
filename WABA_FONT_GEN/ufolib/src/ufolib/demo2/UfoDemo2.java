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
\u1100\u1101\u1102\u1103\u1104\u1105\u1106\u1107\u1108\u1109\u110A\u110B\u110C\u110D\u110E\u110F\u1110\u1111\u1112\u1161\u1162\u1163\u1164\u1165\u1166\u1167\u1168\u1169\u116A\u116B\u116C\u116D\u116E\u116F\u1170\u1171\u1172\u1173\u1174\u1175\u11A8\u11A9\u11AA\u11AB\u11AC\u11AD\u11AE\u11AF\u11B0\u11B1\u11B2\u11B3\u11B4\u11B5\u11B6\u11B7\u11B8\u11B9\u11BA\u11BB\u11BC\u11BD\u11BE\u11BF\u11C0\u11C1\u11C2\u3129\u3131\u3132\u3133\u3134\u3135\u3136\u3137\u3138\u3139\u313A\u313B\u313C\u313D\u313E\u313F\u3140\u3141\u3142\u3143\u3144\u3145\u3146\u3147\u3148\u3149\u314A\u314B\u314C\u314D\u314E\u314F\u3150\u3151\u3152\u3153\u3154\u3155\u3156\u3157\u3158\u3159\u315A\u315B\u315C\u315D\u315E\u315F\u3160\u3161\u3162\u3163\u3164\u3165\u3166\u3167\u3168\u3169\u316A\u316B\u316C\u316D\u316E\u316F\u3170\u3171\u3172\u3173\u3174\u3175\u3176\u3177\u3178\u3179\u317A\u317B\u317C\u317D\u317E\u317F\u3180\u3181\u3182\u3183\u3184\u3185\u3186\u3187\u3188\u3189\u318A\u318B\u318C\u318D\u318E\u3200\u3201
*/

/*
*/
package ufolib.demo2;

import ufolib.engine.*;

import superwaba.ext.xplat.util.crypto.BinConverter;
import waba.ui.*;
import waba.fx.*;
import waba.sys.*;

/*
 * Main 
 */
public class UfoDemo2 extends MainWindow {

    public void onStart()
    {
	Settings.setPalmOSStyle( true );

	// select one test
	doTest5();
    }

    /**
     * chinese III (GB/wang)
     */
    private void doTest5() {

	Container gc = new Container();
	UfoLabel but2 = new UfoLabel( "X" );
	but2.setUfoFont( new UfoFont( "compl", UfoFont.PLAIN, 16 ) );

	String byI = new String( new char[] { (char) 20196, (char) 20200 } );
	String byU = "\u4ee4\u4ee8";

	String textd = byI + "|" + byU;
        //System.err.println(textd);
	UfoFontMetrics ufm2 = but2.getUfoFontMetrics();

	but2.setText( textd );
	gc.add( but2 );
	but2.setRect( 0, 0, 155, 155 );

	gc.setRect( 0,0, 156, 156 );
	add( gc );
    }


    /**
     * chinese II (wang)
     */
    private void doTest4() {
	//    byte[] lab = (new String("不到长城非好汉")).getBytes();

	//String cnvt=BinConverter.byteArrayToUNCString(lab,1,14);

	//String cnvt ="\ub2bb\ub5bd\ub3a4\ub3c7\ub7c7";
	Container gc = new Container();
	UfoLabel but2 = new UfoLabel( "X" );
	//	but2.setUfoFont( new UfoFont( "compl", UfoFont.PLAIN, 16 ) );
	but2.setUfoFont( new UfoFont( "FUGGER", UfoFont.PLAIN, 17 ) );

	//int[] rstarts = new int[] { 8481,8753,8805,8817,8993,9249,9505,9761,9793,9820,10017,10065,10273,10309,10532,10785,11041,11297,11553,11809,12065,12321,12577,12833,13089,13345,13601,13857,14113,14369,14625,14881,15137,15393,15649,15905,16161,16417,16673,16929,17185,17441,17697,17953,18209,18465,18721,18977,19233,19489,19745,20001,20257,20513,20769,21025,21281,21537,21793,22049,22305,22561,22817,23073,23329,23585,23841,24097,24353,24609,24865,25121,25377,25633,25889,26145,26401,26657,26913,27169,27425,27681,27937,28193,28449,28705,28961,29217,29473,29729,29985,30241,30497,31265,31521,31777,32033,32289 };
	
	int[] rstarts = new int[] { 20196, 20200 };

	StringBuffer rdemo = new StringBuffer();
	for ( int index = 0; index < rstarts.length; index++ ) {
	    rdemo.append( (char) ( rstarts[ index ] ) );
	}



	String textd2 = rdemo.toString();
        //System.err.println(textd2);
	UfoFontMetrics ufm2 = but2.getUfoFontMetrics();

	but2.setText( textd2 );
	gc.add( but2 );
	but2.setRect( 0, 0, 155, 155 );

	gc.setRect( 0,0, 156, 156 );
	add( gc );
    }



    /**
     * chinese?
     */
    private void doTest3() {

	Container gc = new Container();
	UfoLabel but2 = new UfoLabel( "X" );
	but2.setUfoFont( new UfoFont( "compl", UfoFont.PLAIN, 16 ) );

	int[] rstarts = new int[] { 8481,8753,8805,8817,8993,9249,9505,9761,9793,9820,10017,10065,10273,10309,10532,10785,11041,11297,11553,11809,12065,12321,12577,12833,13089,13345,13601,13857,14113,14369,14625,14881,15137,15393,15649,15905,16161,16417,16673,16929,17185,17441,17697,17953,18209,18465,18721,18977,19233,19489,19745,20001,20257,20513,20769,21025,21281,21537,21793,22049,22305,22561,22817,23073,23329,23585,23841,24097,24353,24609,24865,25121,25377,25633,25889,26145,26401,26657,26913,27169,27425,27681,27937,28193,28449,28705,28961,29217,29473,29729,29985,30241,30497,31265,31521,31777,32033,32289 };

	StringBuffer rdemo = new StringBuffer();
	for ( int index = 0; index < rstarts.length; index++ ) {
	    rdemo.append( (char) ( rstarts[ index ] + 1 ) );
	    if ( ( index % 9 ) == 0 ) {
		rdemo.append( '|' );
	    }
	}

	String textd2 = rdemo.toString();

	UfoFontMetrics ufm2 = but2.getUfoFontMetrics();

	but2.setText( textd2 );
	gc.add( but2 );
	but2.setRect( 0, 0, 155, 155 );

	gc.setRect( 0,0, 156, 156 );
	add( gc );
    }


    /**
     * korean?
     */
    private void doTest2() {

	Container gc = new Container();
	UfoLabel but2 = new UfoLabel( "X" );
	but2.setUfoFont( new UfoFont( "gnui1k", UfoFont.PLAIN, 16 ) );
	

	String textd = "korean? \u1100\u1101\u1102\u1103\u1104\u1105\u1106|" +
"\u1107\u1108\u1109\u110A\u110B\u110C\u110D\u110E\u110F\u1110\u1111\u1112\u1161\u1162\u1163\u1164\u1165|" +
"\u1166\u1167\u1168\u1169\u116A\u116B\u116C\u116D\u116E\u116F\u1170\u1171\u1172\u1173\u1174\u1175\u11A8|" +
"\u11A9\u11AA\u11AB\u11AC\u11AD\u11AE\u11AF\u11B0\u11B1\u11B2\u11B3\u11B4\u11B5\u11B6\u11B7\u11B8\u11B9|" +
"\u11BA\u11BB\u11BC\u11BD\u11BE\u11BF\u11C0\u11C1\u11C2\u3129\u3131\u3132\u3133\u3134\u3135\u3136\u3137|" +
"\u3138\u3139\u313A\u313B\u313C\u313D\u313E\u313F\u3140\u3141\u3142\u3143\u3144\u3145\u3146\u3147\u3148|" +
"\u3149\u314A\u314B\u314C\u314D\u314E\u314F\u3150\u3151\u3152\u3153\u3154\u3155\u3156\u3157\u3158\u3159|" +
"\u315A\u315B\u315C\u315D\u315E\u315F\u3160\u3161\u3162\u3163\u3164\u3165\u3166\u3167\u3168\u3169\u316A|" +
"\u316B\u316C\u316D\u316E\u316F\u3170\u3171\u3172\u3173\u3174\u3175\u3176\u3177\u3178\u3179\u317A\u317B|" +
"\u317C\u317D\u317E\u317F\u3180\u3181\u3182\u3183\u3184\u3185\u3186\u3187\u3188\u3189\u318A\u318B\u318C|" +
	    "\u318D\u318E\u3200\u3201";

	UfoFontMetrics ufm2 = but2.getUfoFontMetrics();

	//String textd2 = UfoConvert.insertLineBreak( 120, '|', ufm2, textd );
	String textd2 = textd;

	but2.setText( textd2 );
	gc.add( but2 );
	but2.setRect( 0, 0, 155, 155 );

	gc.setRect( 0,0, 156, 156 );
	add( gc );
    }

}


