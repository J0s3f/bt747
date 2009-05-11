package net.sf.bt747.j4me.app.maps;

/*
 * Taken from http://wiki.forum.nokia.com/index.php/J2ME_Google_Maps_API =>
 * Public Domain
 * 
 * Can not be used without an Enterprise license from Google.
 * 
 * Kept in project for future reference (OpenStreetMap). Might also consider
 * http://www.nutiteq.com/content/developer-guides .
 * 
 * 
 * To use this class, you firstly instantiate it with your API key:
 * 
 * GoogleMaps gMap = new GoogleMaps("API_KEY");
 * 
 * To geocode an address, you can use the geocodeAddress() method:
 * 
 * double[] lanLng = gMap.geocodeAddress("Leicester Square, London");
 * 
 * To retrieve a map image:
 * 
 * Image map = gMap.retrieveStaticImage(320, 240, 51.510605, -0.130728, 8,
 * "png32");
 */

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Image;

public class GoogleMaps {
    private static final String URL_UNRESERVED = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "abcdefghijklmnopqrstuvwxyz" + "0123456789-_.~";
    private static final char[] HEX = "0123456789ABCDEF".toCharArray();

    // these 2 properties will be used with map scrolling methods. You can
    // remove them if not needed
    public static final int offset = 268435456;
    public static final double radius = GoogleMaps.offset / Math.PI;

    private String apiKey = null;

    public GoogleMaps(final String key) {
        apiKey = key;
    }

    public double[] geocodeAddress(final String address) throws Exception {
        final byte[] res = GoogleMaps.loadHttpFile(getGeocodeUrl(address));
        final String[] data = GoogleMaps.split(
                new String(res, 0, res.length), ',');

        if (data[0].compareTo("200") != 0) {
            final int errorCode = Integer.parseInt(data[0]);
            throw new Exception("Google Maps Exception: "
                    + GoogleMaps.getGeocodeError(errorCode));
        }

        return new double[] { Double.parseDouble(data[2]),
                Double.parseDouble(data[3]) };
    }

    public Image retrieveStaticImage(final int width, final int height,
            final double lat, final double lng, final int zoom,
            final String format) throws IOException {
        final byte[] imageData = GoogleMaps.loadHttpFile(getMapUrl(width,
                height, lng, lat, zoom, format));

        return Image.createImage(imageData, 0, imageData.length);
    }

    private static String getGeocodeError(final int errorCode) {
        switch (errorCode) {
        case 400:
            return "Bad request";
        case 500:
            return "Server error";
        case 601:
            return "Missing query";
        case 602:
            return "Unknown address";
        case 603:
            return "Unavailable address";
        case 604:
            return "Unknown directions";
        case 610:
            return "Bad API key";
        case 620:
            return "Too many queries";
        default:
            return "Generic error";
        }
    }

    private String getGeocodeUrl(final String address) {
        return "http://maps.google.com/maps/geo?q="
                + GoogleMaps.urlEncode(address) + "&output=csv&key=" + apiKey;
    }

    private String getMapUrl(final int width, final int height,
            final double lng, final double lat, final int zoom,
            final String format) {
        return "http://maps.google.com/staticmap?center=" + lat + "," + lng
                + "&format=" + format + "&zoom=" + zoom + "&size=" + width
                + "x" + height + "&key=" + apiKey;
    }

    private static String urlEncode(final String str) {
        final StringBuffer buf = new StringBuffer();
        byte[] bytes = null;
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final DataOutputStream dos = new DataOutputStream(bos);
            dos.writeUTF(str);
            bytes = bos.toByteArray();
        } catch (final IOException e) {
            // ignore
        }
        for (int i = 2; i < bytes.length; i++) {
            final byte b = bytes[i];
            if (GoogleMaps.URL_UNRESERVED.indexOf(b) >= 0) {
                buf.append((char) b);
            } else {
                buf.append('%').append(GoogleMaps.HEX[(b >> 4) & 0x0f])
                        .append(GoogleMaps.HEX[b & 0x0f]);
            }
        }
        return buf.toString();
    }

    private static byte[] loadHttpFile(final String url) throws IOException {
        byte[] byteBuffer;

        final HttpConnection hc = (HttpConnection) Connector.open(url);
        try {
            hc.setRequestMethod(HttpConnection.GET);
            final InputStream is = hc.openInputStream();
            try {
                final int len = (int) hc.getLength();
                if (len > 0) {
                    byteBuffer = new byte[len];
                    int done = 0;
                    while (done < len) {
                        done += is.read(byteBuffer, done, len - done);
                    }
                } else {
                    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    final byte[] buffer = new byte[512];
                    int count;
                    while ((count = is.read(buffer)) >= 0) {
                        bos.write(buffer, 0, count);
                    }
                    byteBuffer = bos.toByteArray();
                }
            } finally {
                is.close();
            }
        } finally {
            hc.close();
        }

        return byteBuffer;
    }

    private static String[] split(final String s, final int chr) {
        final Vector res = new Vector();

        int curr;
        int prev = 0;

        while ((curr = s.indexOf(chr, prev)) >= 0) {
            res.addElement(s.substring(prev, curr));
            prev = curr + 1;
        }
        res.addElement(s.substring(prev));

        final String[] splitted = new String[res.size()];
        res.copyInto(splitted);

        return splitted;
    }

    // Needs Microfloat (http://www.dclausen.net/projects/microfloat/)
    // public double[] adjust(double lat, double lng, int deltaX, int deltaY,
    // int z)
    // {
    // return new double[]{
    // XToL(LToX(lng) + (deltaX<<(21-z))),
    // YToL(LToY(lat) + (deltaY<<(21-z)))
    // };
    // }
    // double LToX(double x)
    // {
    // return round(offset + radius * x * Math.PI / 180);
    // }
    //     
    // double LToY(double y)
    // {
    // return round(
    // offset - radius *
    // Double.longBitsToDouble(MicroDouble.log(
    // Double.doubleToLongBits(
    // (1 + Math.sin(y * Math.PI / 180))
    // /
    // (1 - Math.sin(y * Math.PI / 180))
    // )
    // )) / 2);
    // }
    //     
    // double XToL(double x)
    // {
    // return ((round(x) - offset) / radius) * 180 / Math.PI;
    // }
    //     
    // double YToL(double y)
    // {
    // return (Math.PI / 2 - 2 * Double.longBitsToDouble(
    // MicroDouble.atan(
    // MicroDouble.exp(Double.doubleToLongBits((round(y)-offset)/radius))
    // )
    // )) * 180 / Math.PI;
    // }
    // double round(double num)
    // {
    // double floor = Math.floor(num);
    //            
    // if(num - floor >= 0.5)
    // return Math.ceil(num);
    // else
    // return floor;
    // }
}
