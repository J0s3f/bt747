/**
 * 
 */
package gps.convert;

/**
 * @author Mario
 * 
 */
public final class Geoid implements GeoidIF {
    public final static GeoidIF getInstance() {
        return new Geoid();
    }

    private Geoid() {

    }

    private static final int GEOID_ROW = 19;
    private static final int GEOID_COL = 37;
    private static final byte[] geoid_delta;
    /* String has smaller foot print than initialising with { 1,2,6,5, ..} */
    private final static String geoid_deltaS = ( // Table in string format
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
        geoid_delta = new byte[geoid_deltaS.length()];
        for (int i = 0; i < geoid_delta.length; i++) {
            geoid_delta[i] = (byte) geoid_deltaS.charAt(i);
        }
    }

    /*
     * return geoid separation (MSL - WGS84) in meters, given a lat/lot in
     * degrees
     */
    /* @ +charint @ */
    public final double wgs84Separation(final double lat, final double lon) {
        /* @ -charint @ */
        int ilat, ilon;
        int ilat1, ilat2, ilon1, ilon2;

        ilat = (int) Math.floor((90. + lat) / 10);
        ilon = (int) Math.floor((180. + lon) / 10);

        ilat1 = ilat;
        ilon1 = ilon;
        ilat2 = (ilat < GEOID_ROW - 1) ? (ilat + 1) : ilat;
        ilon2 = (ilon < GEOID_COL - 1) ? (ilon + 1) : ilon;

        try {
            return Conv.bilinear((ilon1 * 10.) - 180., (ilat1 * 10.) - 90.,
                    (ilon2 * 10.) - 180., (ilat2 * 10.) - 90., lon, lat,
                    geoid_delta[ilon1 + (ilat1 * GEOID_COL)],
                    geoid_delta[ilon2 + (ilat1 * GEOID_COL)],
                    geoid_delta[ilon1 + (ilat2 * GEOID_COL)],
                    geoid_delta[ilon2 + (ilat2 * GEOID_COL)]);
        } catch (final Exception e) {
            return -999;
        }
    }

}