/**
 * 
 */
package net.sf.bt747.gps.convert.test;

import gps.convert.GeoidIF;
import gps.convert.Geoid;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * @author Mario
 * 
 */
public class GeoidTest extends TestCase {

    /**
     * 
     */
    public GeoidTest() {
        super();
    }

    private static final byte[] geoid_delta = {
    /*
     * 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26
     * 27 28 29 30 31 32 33 34 35 36
     */
    /*
     * -180 -170 -160 -150 -140 -130 -120 -110 -100 -90 -80 -70 -60 -50 -40
     * -30 -20 -10 0 10 20 30 40 50 60 70 80 90 100 110 120 130 140 150 160
     * 170 180
     */
    /* 90S */(byte) -30, (byte) -30, (byte) -30, (byte) -30, (byte) -30,
            (byte) -30, (byte) -30, (byte) -30, (byte) -30, (byte) -30,
            (byte) -30, (byte) -30, (byte) -30, (byte) -30, (byte) -30,
            (byte) -30, (byte) -30, (byte) -30, (byte) -30, (byte) -30,
            (byte) -30, (byte) -30, (byte) -30, (byte) -30, (byte) -30,
            (byte) -30, (byte) -30, (byte) -30, (byte) -30, (byte) -30,
            (byte) -30, (byte) -30, (byte) -30, (byte) -30, (byte) -30,
            (byte) -30, (byte) -30, (byte)
            /* 80S */-53, (byte) -54, (byte) -55, (byte) -52, (byte) -48,
            (byte) -42, (byte) -38, (byte) -38, (byte) -29, (byte) -26,
            (byte) -26, (byte) -24, (byte) -23, (byte) -21, (byte) -19,
            (byte) -16, (byte) -12, (byte) -8, (byte) -4, (byte) -1,
            (byte) 1, (byte) 4, (byte) 4, (byte) 6, (byte) 5, (byte) 4,
            (byte) 2, (byte) -6, (byte) -15, (byte) -24, (byte) -33,
            (byte) -40, (byte) -48, (byte) -50, (byte) -53, (byte) -52,
            (byte) -53, (byte)
            /* 70S */-61, (byte) -60, (byte) -61, (byte) -55, (byte) -49,
            (byte) -44, (byte) -38, (byte) -31, (byte) -25, (byte) -16,
            (byte) -6, (byte) 1, (byte) 4, (byte) 5, (byte) 4, (byte) 2,
            (byte) 6, (byte) 12, (byte) 16, (byte) 16, (byte) 17, (byte) 21,
            (byte) 20, (byte) 26, (byte) 26, (byte) 22, (byte) 16, (byte) 10,
            (byte) -1, (byte) -16, (byte) -29, (byte) -36, (byte) -46,
            (byte) -55, (byte) -54, (byte) -59, (byte) -61, (byte)
            /* 60S */-45, (byte) -43, (byte) -37, (byte) -32, (byte) -30,
            (byte) -26, (byte) -23, (byte) -22, (byte) -16, (byte) -10,
            (byte) -2, (byte) 10, (byte) 20, (byte) 20, (byte) 21, (byte) 24,
            (byte) 22, (byte) 17, (byte) 16, (byte) 19, (byte) 25, (byte) 30,
            (byte) 35, (byte) 35, (byte) 33, (byte) 30, (byte) 27, (byte) 10,
            (byte) -2, (byte) -14, (byte) -23, (byte) -30, (byte) -33,
            (byte) -29, (byte) -35, (byte) -43, (byte) -45, (byte)
            /* 50S */-15, (byte) -18, (byte) -18, (byte) -16, (byte) -17,
            (byte) -15, (byte) -10, (byte) -10, (byte) -8, (byte) -2,
            (byte) 6, (byte) 14, (byte) 13, (byte) 3, (byte) 3, (byte) 10,
            (byte) 20, (byte) 27, (byte) 25, (byte) 26, (byte) 34, (byte) 39,
            (byte) 45, (byte) 45, (byte) 38, (byte) 39, (byte) 28, (byte) 13,
            (byte) -1, (byte) -15, (byte) -22, (byte) -22, (byte) -18,
            (byte) -15, (byte) -14, (byte) -10, (byte) -15, (byte)
            /* 40S */21, (byte) 6, (byte) 1, (byte) -7, (byte) -12,
            (byte) -12, (byte) -12, (byte) -10, (byte) -7, (byte) -1,
            (byte) 8, (byte) 23, (byte) 15, (byte) -2, (byte) -6, (byte) 6,
            (byte) 21, (byte) 24, (byte) 18, (byte) 26, (byte) 31, (byte) 33,
            (byte) 39, (byte) 41, (byte) 30, (byte) 24, (byte) 13, (byte) -2,
            (byte) -20, (byte) -32, (byte) -33, (byte) -27, (byte) -14,
            (byte) -2, (byte) 5, (byte) 20, (byte) 21, (byte)
            /* 30S */46, (byte) 22, (byte) 5, (byte) -2, (byte) -8,
            (byte) -13, (byte) -10, (byte) -7, (byte) -4, (byte) 1, (byte) 9,
            (byte) 32, (byte) 16, (byte) 4, (byte) -8, (byte) 4, (byte) 12,
            (byte) 15, (byte) 22, (byte) 27, (byte) 34, (byte) 29, (byte) 14,
            (byte) 15, (byte) 15, (byte) 7, (byte) -9, (byte) -25,
            (byte) -37, (byte) -39, (byte) -23, (byte) -14, (byte) 15,
            (byte) 33, (byte) 34, (byte) 45, (byte) 46, (byte)
            /* 20S */51, (byte) 27, (byte) 10, (byte) 0, (byte) -9,
            (byte) -11, (byte) -5, (byte) -2, (byte) -3, (byte) -1, (byte) 9,
            (byte) 35, (byte) 20, (byte) -5, (byte) -6, (byte) -5, (byte) 0,
            (byte) 13, (byte) 17, (byte) 23, (byte) 21, (byte) 8, (byte) -9,
            (byte) -10, (byte) -11, (byte) -20, (byte) -40, (byte) -47,
            (byte) -45, (byte) -25, (byte) 5, (byte) 23, (byte) 45,
            (byte) 58, (byte) 57, (byte) 63, (byte) 51, (byte)
            /* 10S */36, (byte) 22, (byte) 11, (byte) 6, (byte) -1,
            (byte) -8, (byte) -10, (byte) -8, (byte) -11, (byte) -9,
            (byte) 1, (byte) 32, (byte) 4, (byte) -18, (byte) -13, (byte) -9,
            (byte) 4, (byte) 14, (byte) 12, (byte) 13, (byte) -2, (byte) -14,
            (byte) -25, (byte) -32, (byte) -38, (byte) -60, (byte) -75,
            (byte) -63, (byte) -26, (byte) 0, (byte) 35, (byte) 52,
            (byte) 68, (byte) 76, (byte) 64, (byte) 52, (byte) 36, (byte)
            /* 00N */22, (byte) 16, (byte) 17, (byte) 13, (byte) 1,
            (byte) -12, (byte) -23, (byte) -20, (byte) -14, (byte) -3,
            (byte) 14, (byte) 10, (byte) -15, (byte) -27, (byte) -18,
            (byte) 3, (byte) 12, (byte) 20, (byte) 18, (byte) 12, (byte) -13,
            (byte) -9, (byte) -28, (byte) -49, (byte) -62, (byte) -89,
            (byte) -102, (byte) -63, (byte) -9, (byte) 33, (byte) 58,
            (byte) 73, (byte) 74, (byte) 63, (byte) 50, (byte) 32, (byte) 22,
            (byte)
            /* 10N */13, (byte) 12, (byte) 11, (byte) 2, (byte) -11,
            (byte) -28, (byte) -38, (byte) -29, (byte) -10, (byte) 3,
            (byte) 1, (byte) -11, (byte) -41, (byte) -42, (byte) -16,
            (byte) 3, (byte) 17, (byte) 33, (byte) 22, (byte) 23, (byte) 2,
            (byte) -3, (byte) -7, (byte) -36, (byte) -59, (byte) -90,
            (byte) -95, (byte) -63, (byte) -24, (byte) 12, (byte) 53,
            (byte) 60, (byte) 58, (byte) 46, (byte) 36, (byte) 26, (byte) 13,
            (byte)
            /* 20N */5, (byte) 10, (byte) 7, (byte) -7, (byte) -23,
            (byte) -39, (byte) -47, (byte) -34, (byte) -9, (byte) -10,
            (byte) -20, (byte) -45, (byte) -48, (byte) -32, (byte) -9,
            (byte) 17, (byte) 25, (byte) 31, (byte) 31, (byte) 26, (byte) 15,
            (byte) 6, (byte) 1, (byte) -29, (byte) -44, (byte) -61,
            (byte) -67, (byte) -59, (byte) -36, (byte) -11, (byte) 21,
            (byte) 39, (byte) 49, (byte) 39, (byte) 22, (byte) 10, (byte) 5,
            (byte)
            /* 30N */-7, (byte) -5, (byte) -8, (byte) -15, (byte) -28,
            (byte) -40, (byte) -42, (byte) -29, (byte) -22, (byte) -26,
            (byte) -32, (byte) -51, (byte) -40, (byte) -17, (byte) 17,
            (byte) 31, (byte) 34, (byte) 44, (byte) 36, (byte) 28, (byte) 29,
            (byte) 17, (byte) 12, (byte) -20, (byte) -15, (byte) -40,
            (byte) -33, (byte) -34, (byte) -34, (byte) -28, (byte) 7,
            (byte) 29, (byte) 43, (byte) 20, (byte) 4, (byte) -6, (byte) -7,
            (byte)
            /* 40N */-12, (byte) -10, (byte) -13, (byte) -20, (byte) -31,
            (byte) -34, (byte) -21, (byte) -16, (byte) -26, (byte) -34,
            (byte) -33, (byte) -35, (byte) -26, (byte) 2, (byte) 33,
            (byte) 59, (byte) 52, (byte) 51, (byte) 52, (byte) 48, (byte) 35,
            (byte) 40, (byte) 33, (byte) -9, (byte) -28, (byte) -39,
            (byte) -48, (byte) -59, (byte) -50, (byte) -28, (byte) 3,
            (byte) 23, (byte) 37, (byte) 18, (byte) -1, (byte) -11,
            (byte) -12, (byte)
            /* 50N */-8, (byte) 8, (byte) 8, (byte) 1, (byte) -11,
            (byte) -19, (byte) -16, (byte) -18, (byte) -22, (byte) -35,
            (byte) -40, (byte) -26, (byte) -12, (byte) 24, (byte) 45,
            (byte) 63, (byte) 62, (byte) 59, (byte) 47, (byte) 48, (byte) 42,
            (byte) 28, (byte) 12, (byte) -10, (byte) -19, (byte) -33,
            (byte) -43, (byte) -42, (byte) -43, (byte) -29, (byte) -2,
            (byte) 17, (byte) 23, (byte) 22, (byte) 6, (byte) 2, (byte) -8,
            (byte)
            /* 60N */2, (byte) 9, (byte) 17, (byte) 10, (byte) 13, (byte) 1,
            (byte) -14, (byte) -30, (byte) -39, (byte) -46, (byte) -42,
            (byte) -21, (byte) 6, (byte) 29, (byte) 49, (byte) 65, (byte) 60,
            (byte) 57, (byte) 47, (byte) 41, (byte) 21, (byte) 18, (byte) 14,
            (byte) 7, (byte) -3, (byte) -22, (byte) -29, (byte) -32,
            (byte) -32, (byte) -26, (byte) -15, (byte) -2, (byte) 13,
            (byte) 17, (byte) 19, (byte) 6, (byte) 2, (byte)
            /* 70N */2, (byte) 2, (byte) 1, (byte) -1, (byte) -3, (byte) -7,
            (byte) -14, (byte) -24, (byte) -27, (byte) -25, (byte) -19,
            (byte) 3, (byte) 24, (byte) 37, (byte) 47, (byte) 60, (byte) 61,
            (byte) 58, (byte) 51, (byte) 43, (byte) 29, (byte) 20, (byte) 12,
            (byte) 5, (byte) -2, (byte) -10, (byte) -14, (byte) -12,
            (byte) -10, (byte) -14, (byte) -12, (byte) -6, (byte) -2,
            (byte) 3, (byte) 6, (byte) 4, (byte) 2, (byte)
            /* 80N */3, (byte) 1, (byte) -2, (byte) -3, (byte) -3,
            (byte) -3, (byte) -1, (byte) 3, (byte) 1, (byte) 5, (byte) 9,
            (byte) 11, (byte) 19, (byte) 27, (byte) 31, (byte) 34, (byte) 33,
            (byte) 34, (byte) 33, (byte) 34, (byte) 28, (byte) 23, (byte) 17,
            (byte) 13, (byte) 9, (byte) 4, (byte) 4, (byte) 1, (byte) -2,
            (byte) -2, (byte) 0, (byte) 2, (byte) 3, (byte) 2, (byte) 1,
            (byte) 1, (byte) 3, (byte)
            /* 90N */13, (byte) 13, (byte) 13, (byte) 13, (byte) 13,
            (byte) 13, (byte) 13, (byte) 13, (byte) 13, (byte) 13, (byte) 13,
            (byte) 13, (byte) 13, (byte) 13, (byte) 13, (byte) 13, (byte) 13,
            (byte) 13, (byte) 13, (byte) 13, (byte) 13, (byte) 13, (byte) 13,
            (byte) 13, (byte) 13, (byte) 13, (byte) 13, (byte) 13, (byte) 13,
            (byte) 13, (byte) 13, (byte) 13, (byte) 13, (byte) 13, (byte) 13,
            (byte) 13, (byte) 13 };

    private final static byte[] opttable;
    private final static String opttableS = ( // Table in string format
    "\342\342\342\342\342\342\342\342\342\342" // 0
            + "\342\342\342\342\342\342\342\342\342\342" // 10
            + "\342\342\342\342\342\342\342\342\342\342" // 20
            + "\342\342\342\342\342\342\342\313\312\311" // 30
            + "\314\320\326\332\332\343\346\346\350\351" // 40
            + "\353\355\360\364\370\374\377\001\004\004" // 50
            + "\006\005\004\002\372\361\350\337\330\320" // 60
            + "\316\313\314\313\303\304\303\311\317\324" // 70
            + "\332\341\347\360\372\001\004\005\004\002" // 80
            + "\006\014\020\020\021\025\024\032\032\026" // 90
            + "\020\012\377\360\343\334\322\311\312\305" // 100
            + "\303\323\325\333\340\342\346\351\352\360" // 110
            + "\366\376\012\024\024\025\030\026\021\020" // 120
            + "\023\031\036\043\043\041\036\033\012\376" // 130
            + "\362\351\342\337\343\335\325\323\361\356" // 140
            + "\356\360\357\361\366\366\370\376\006\016" // 150
            + "\015\003\003\012\024\033\031\032\042\047" // 160
            + "\055\055\046\047\034\015\377\361\352\352" // 170
            + "\356\361\362\366\361\025\006\001\371\364" // 180
            + "\364\364\366\371\377\010\027\017\376\372" // 190
            + "\006\025\030\022\032\037\041\047\051\036" // 200
            + "\030\015\376\354\340\337\345\362\376\005" // 210
            + "\024\025\056\026\005\376\370\363\366\371" // 220
            + "\374\001\011\040\020\004\370\004\014\017" // 230
            + "\026\033\042\035\016\017\017\007\367\347" // 240
            + "\333\331\351\362\017\041\042\055\056\063" // 250
            + "\033\012\000\367\365\373\376\375\377\011" // 260
            + "\043\024\373\372\373\000\015\021\027\025" // 270
            + "\010\367\366\365\354\330\321\323\347\005" // 280
            + "\027\055\072\071\077\063\044\026\013\006" // 290
            + "\377\370\366\370\365\367\001\040\004\356" // 300
            + "\363\367\004\016\014\015\376\362\347\340" // 310
            + "\332\304\265\301\346\000\043\064\104\114" // 320
            + "\100\064\044\026\020\021\015\001\364\351" // 330
            + "\354\362\375\016\012\361\345\356\003\014" // 340
            + "\024\022\014\363\367\344\317\302\247\232" // 350 XX 232
            + "\301\367\041\072\111\112\077\062\040\026" // 360
            + "\015\014\013\002\365\344\332\343\366\003" // 370
            + "\001\365\327\326\360\003\021\041\026\027" // 380
            + "\002\375\371\334\305\246\241\301\350\014" // 390
            + "\065\074\072\056\044\032\015\005\012\007" // 400
            + "\371\351\331\321\336\367\366\354\323\320" // 410
            + "\340\367\021\031\037\037\032\017\006\001" // 420
            + "\343\324\303\275\305\334\365\025\047\061" // 430
            + "\047\026\012\005\371\373\370\361\344\330" // 440
            + "\326\343\352\346\340\315\330\357\021\037" // 450
            + "\042\054\044\034\035\021\014\354\361\330" // 460
            + "\337\336\336\344\007\035\053\024\004\372" // 470
            + "\371\364\366\363\354\341\336\353\360\346" // 480
            + "\336\337\335\346\002\041\073\064\063\064" // 490
            + "\060\043\050\041\367\344\331\320\305\316" // 500
            + "\344\003\027\045\022\377\365\364\370\010" // 510
            + "\010\001\365\355\360\356\352\335\330\346" // 520
            + "\364\030\055\077\076\073\057\060\052\034" // 530
            + "\014\366\355\337\325\326\325\343\376\021" // 540
            + "\027\026\006\002\370\002\011\021\012\015" // 550
            + "\001\362\342\331\322\326\353\006\035\061" // 560
            + "\101\074\071\057\051\025\022\016\007\375" // 570
            + "\352\343\340\340\346\361\376\015\021\023" // 580
            + "\006\002\002\002\001\377\375\371\362\350" // 590
            + "\345\347\355\003\030\045\057\074\075\072" // 600
            + "\063\053\035\024\014\005\376\366\362\364" // 610
            + "\366\362\364\372\376\003\006\004\002\003" // 620
            + "\001\376\375\375\375\377\003\001\005\011" // 630
            + "\013\023\033\037\042\041\042\041\042\034" // 640
            + "\027\021\015\011\004\004\001\376\376\000" // 650
            + "\002\003\002\001\001\003\015\015\015\015" // 660
            + "\015\015\015\015\015\015\015\015\015\015" // 670
            + "\015\015\015\015\015\015\015\015\015\015" // 680
            + "\015\015\015\015\015\015\015\015\015\015" // 690
            + "\015\015\015"); // Getbytes may use character encoding so not useable :-(

    static {
        opttable = new byte[opttableS.length()];
        for (int i = 0; i < opttable.length; i++) {
            opttable[i] = (byte) opttableS.charAt(i);
        }
    }

    /**
     * Checks the Wgs84Seperation function.
     * 
     * @throws Exception
     */
    @Test
    public void testWgs84Separation() throws Exception {
        GeoidIF gIF = Geoid.getInstance();
        assertEquals("Some value", -10.0, gIF.wgs84Separation(50., 50.));
        assertEquals("Boundary -90,-180", -30.0, gIF.wgs84Separation(-90.,
                -180.));
        assertEquals("Boundary 90,-180", 13.0, gIF
                .wgs84Separation(90., -180.));
        assertEquals("Boundary -90,180", -30.0, gIF.wgs84Separation(-90.,
                180.));
        assertEquals("Boundary 90,180", 13.0, gIF.wgs84Separation(90., 180.));
        assertEquals("Boundary -56,-10", 21.0, gIF
                .wgs84Separation(-56., -10.));
    }

    @Test
    public void testTAlbe() throws Exception {
        // recreates a table that can be pasted
        byteTableToString(geoid_delta);
        for (int i = 0; i < geoid_delta.length; i++) {
            if (geoid_delta[i] != opttable[i]) {
                System.err.println(String.format(
                        "Index %d: %d %d (%o %o) %c %c", i, geoid_delta[i],
                        opttable[i], geoid_delta[i], opttable[i],
                        (char) geoid_delta[i], (char) opttable[i]));
            }
            assertEquals("Index " + i, geoid_delta[i], opttable[i]);
        }
        for (int i = 0; i < geoid_delta.length; i++) {
            assertEquals("Index " + i, geoid_delta[i], opttable[i]);
        }

    }

    private String byteTableToString(byte[] table) {
        StringBuffer sb = new StringBuffer(table.length * 4);
        int nextlen = 40;
        int lastidx = 0;
        int idx = 0;
        sb.append(" \"");
        for (byte b : table) {
            sb.append("\\");
            sb.append(String.format("%03o", (b + 256) % 256));
            idx++;
            if (sb.length() > nextlen) {
                sb.append("\" // " + lastidx + "\n+\"");
                nextlen = sb.length() + 38;
                lastidx = idx;
            }

        }
        sb.append("\"\n");
        System.out.println(sb.toString());
        return sb.toString();
    }
}
