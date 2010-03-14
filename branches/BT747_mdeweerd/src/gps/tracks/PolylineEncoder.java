/**
 * Reimplementation of Mark McClure's Javascript PolylineEncoder All the
 * mathematical logic is more or less copied by McClure
 * 
 * @author Mark Rambow, Mario De Weerd
 * @e-mail markrambow[at]gmail[dot]com
 * @version 0.1
 * 
 */
package gps.tracks;

import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Hashtable;
import bt747.sys.interfaces.BT747Vector;

public final class PolylineEncoder {

    private final int numLevels;

    private final int zoomFactor;

    private final double verySmall;

    private final boolean forceEndpoints;

    private final double[] zoomLevelBreaks;

    private BT747Hashtable bounds;

    // constructor
    public PolylineEncoder(final int pNumLevels, final int pZoomFactor,
            final double pVerySmall, final boolean forceEndpoints) {
        numLevels = pNumLevels;
        zoomFactor = pZoomFactor;
        verySmall = pVerySmall;
        this.forceEndpoints = forceEndpoints;

        zoomLevelBreaks = new double[pNumLevels];

        for (int i = 0; i < pNumLevels; i++) {
            zoomLevelBreaks[i] = pVerySmall
                    * bt747.sys.Generic.pow(zoomFactor, pNumLevels - i - 1);
        }
    }

    public PolylineEncoder() {
        numLevels = 18;
        zoomFactor = 2;
        verySmall = 0.00001;
        forceEndpoints = true;

        zoomLevelBreaks = new double[numLevels];

        for (int i = 0; i < numLevels; i++) {
            zoomLevelBreaks[i] = verySmall
                    * bt747.sys.Generic.pow(zoomFactor, numLevels - i - 1);
        }
    }

    // public static void main(String[] args) {
    //
    // // initialize trackpoints
    // // dirty hack just to show how it works :)
    //
    // Track trk = new Track();
    //
    // trk.addTrackpoint(new Trackpoint((52.29834), (8.94328)));
    // trk.addTrackpoint(new Trackpoint((52.29767), (8.93614)));
    // trk.addTrackpoint(new Trackpoint((52.29322), (8.93301)));
    // trk.addTrackpoint(new Trackpoint((52.28938), (8.93036)));
    // trk.addTrackpoint(new Trackpoint((52.27014), (8.97475)));
    //
    // // encodeSignedNumber(floor1e5(coordinate)));
    // System.out.println(createEncodings(trk, 17, 1));
    //
    // }

    /**
     * Douglas-Peucker algorithm, adapted for encoding
     * 
     * @return [EncodedPoints;EncodedLevels]
     * 
     */
    public final BT747Hashtable dpEncode(final Track track) {
        int i, maxLoc = 0;
        final BT747Vector stack = JavaLibBridge.getVectorInstance();
        final float[] dists = new float[track.getTrackpoints().size()];
        float maxDist, absMaxDist = 0.0f;
        int[] current;
        String encodedPoints, encodedLevels;

        if (track.getTrackpoints().size() > 2) {
            final int[] stackVal = new int[] { 0,
                    (track.getTrackpoints().size() - 1) };
            stack.mypush(stackVal);

            while (stack.size() > 0) {
                current = (int[]) stack.pop();
                maxDist = 0;

                for (i = current[0] + 1; i < current[1]; i++) {
                    final float temp = (float) PolylineEncoder.distance(
                            (Trackpoint) track.getTrackpoints().elementAt(i),
                            (Trackpoint) track.getTrackpoints().elementAt(
                                    current[0]), (Trackpoint) track
                                    .getTrackpoints().elementAt(current[1]));
                    if (temp > maxDist) {
                        maxDist = temp;
                        maxLoc = i;
                        if (maxDist > absMaxDist) {
                            absMaxDist = maxDist;
                        }
                    }
                }
                if (maxDist > verySmall) {
                    dists[maxLoc] = maxDist;
                    final int[] stackValCurMax = { current[0], maxLoc };
                    stack.mypush(stackValCurMax);
                    final int[] stackValMaxCur = { maxLoc, current[1] };
                    stack.mypush(stackValMaxCur);
                }
            }
        }

        // System.out.println("createEncodings(" +
        // track.getTrackpoints().size()
        // + "," + dists.length + ")");
        encodedPoints = createEncodings(track, dists);
        // System.out.println("encodedPoints \t\t: " + encodedPoints);
        // encodedPoints.replace("\\","\\\\");
        encodedPoints = PolylineEncoder.replace(encodedPoints, "\\", "\\\\");
        // System.out.println("encodedPoints slashy?\t\t: " + encodedPoints);

        encodedLevels = encodeLevels(track, dists, absMaxDist);
        // System.out.println("encodedLevels: " + encodedLevels);

        final BT747Hashtable hm = JavaLibBridge.getHashtableInstance(0);
        hm.put("encodedPoints", encodedPoints);
        hm.put("encodedLevels", encodedLevels);
        return hm;

    }

    public final static String replace(final String s, final String one,
            final String another) {
        // In a string replace one substring with another
        if (s.equals("")) {
            return "";
        }
        StringBuffer res = new StringBuffer();
        int i = s.indexOf(one, 0);
        int lastpos = 0;
        while (i != -1) {
            res.append(s.substring(lastpos, i));
            res.append(another);
            lastpos = i + one.length();
            i = s.indexOf(one, lastpos);
        }
        res.append(s.substring(lastpos)); // the rest
        return res.toString();
    }

    /**
     * distance(p0, p1, p2) computes the distance between the point p0 and the
     * segment [p1,p2]. This could probably be replaced with something that is
     * a bit more numerically stable.
     * 
     * @param p0
     *                Point.
     * @param p1
     *                First point of segment.
     * @param p2
     *                Second point of segment.
     * @return Distance between point p0 and the segment [p1,p2].
     */
    public final static double distance(final Trackpoint p0,
            final Trackpoint p1, final Trackpoint p2) {
        double out = 0.0;

        if ((p1.getLatDouble() == p2.getLatDouble())
                && (p1.getLonDouble() == p2.getLonDouble())) {
            out = Math.sqrt(bt747.sys.Generic.pow(p2.getLatDouble()
                    - p0.getLatDouble(), 2)
                    + bt747.sys.Generic.pow(p2.getLonDouble()
                            - p0.getLonDouble(), 2));
        } else {
            final double u = ((p0.getLatDouble() - p1.getLatDouble())
                    * (p2.getLatDouble() - p1.getLatDouble()) + (p0
                    .getLonDouble() - p1.getLonDouble())
                    * (p2.getLonDouble() - p1.getLonDouble()))
                    / (bt747.sys.Generic.pow(p2.getLatDouble()
                            - p1.getLatDouble(), 2) + bt747.sys.Generic.pow(
                            p2.getLonDouble() - p1.getLonDouble(), 2));

            if (u <= 0) {
                out = Math.sqrt(bt747.sys.Generic.pow(p0.getLatDouble()
                        - p1.getLatDouble(), 2)
                        + bt747.sys.Generic.pow(p0.getLonDouble()
                                - p1.getLonDouble(), 2));
            }
            if (u >= 1) {
                out = Math.sqrt(bt747.sys.Generic.pow(p0.getLatDouble()
                        - p2.getLatDouble(), 2)
                        + bt747.sys.Generic.pow(p0.getLonDouble()
                                - p2.getLonDouble(), 2));
            }
            if ((0 < u) && (u < 1)) {
                out = Math
                        .sqrt(bt747.sys.Generic.pow(p0.getLatDouble()
                                - p1.getLatDouble() - u
                                * (p2.getLatDouble() - p1.getLatDouble()), 2)
                                + bt747.sys.Generic.pow(p0.getLonDouble()
                                        - p1.getLonDouble()
                                        - u
                                        * (p2.getLonDouble() - p1
                                                .getLonDouble()), 2));
            }
        }
        return out;
    }

    // /**
    // * @param points
    // * set the points that should be encoded all points have to be in
    // * the following form: Latitude, longitude\n
    // */
    // public static Track pointsToTrack(String points) {
    // Track trk = new Track();
    //
    // StringTokenizer st = new StringTokenizer(points, "\n");
    // while (st.hasMoreTokens()) {
    // String[] pointStrings = st.nextToken().split(", ");
    // trk.addTrackpoint(new Trackpoint((pointStrings[0]),
    // (pointStrings[1])));
    // }
    // return trk;
    // }

    // /**
    // * @param LineString
    // * set the points that should be encoded all points have to be in
    // * the following form: Longitude,Latitude,Altitude"_"...
    // */
    // public static Track kmlLineStringToTrack(String points) {
    // Track trk = new Track();
    // StringTokenizer st = new StringTokenizer(points, " ");
    //		
    // while (st.hasMoreTokens()) {
    // String[] pointStrings = st.nextToken().split(",");
    // trk.addTrackpoint(new Trackpoint((pointStrings[1]),
    // (pointStrings[0]), (pointStrings[2])));
    // }
    // return trk;
    // }

    // /**
    // * Goolge cant show Altitude, but its in some GPS/GPX Files
    // * Altitude will be ignored here so far
    // * @param points
    // * @return
    // */
    // public static Track pointsAndAltitudeToTrack(String points) {
    // System.out.println("pointsAndAltitudeToTrack");
    // Track trk = new Track();
    // StringTokenizer st = new StringTokenizer(points, "\n");
    // while (st.hasMoreTokens()) {
    // String[] pointStrings = st.nextToken().split(",");
    // trk.addTrackpoint(new Trackpoint((pointStrings[1]),
    // (pointStrings[0])));
    // System.out.println((pointStrings[1]).toString() + ", "
    // + (pointStrings[0]).toString());
    // }
    // return trk;
    // }

    private final static int floor1e5(final double coordinate) {
        return (int) Math.floor(coordinate * 1e5);
    }

    private final static String encodeSignedNumber(final int num) {
        int sgnNum = num << 1;
        if (num < 0) {
            sgnNum = ~(sgnNum);
        }
        return (PolylineEncoder.encodeNumber(sgnNum));
    }

    private final static String encodeNumber(final int num) {
        final StringBuffer encodeString = new StringBuffer();
        int value = num;
        int nextValue;

        while (value >= 0x20) {
            nextValue = (0x20 | (value & 0x1f)) + 63;
            encodeString.append((char) (nextValue));
            value >>= 5;
        }

        value += 63;
        encodeString.append((char) (value));

        return encodeString.toString();
    }

    /**
     * Now we can use the previous function to march down the list of points
     * and encode the levels. Like createEncodings, we ignore points whose
     * distance (in dists) is undefined.
     */
    private final String encodeLevels(final Track points,
            final float[] dists, final float absMaxDist) {
        int i;
        final StringBuffer encodedLevels = new StringBuffer();

        if (forceEndpoints) {
            encodedLevels.append(PolylineEncoder.encodeNumber(numLevels - 1));
        } else {
            encodedLevels.append(PolylineEncoder.encodeNumber(numLevels
                    - computeLevel(absMaxDist) - 1));
        }
        for (i = 1; i < points.size() - 1; i++) {
            if (dists[i] != 0) {
                encodedLevels.append(PolylineEncoder.encodeNumber(numLevels
                        - computeLevel(dists[i]) - 1));
            }
        }
        if ((points.size() > 1)) {
            if (forceEndpoints) {
                encodedLevels.append(PolylineEncoder
                        .encodeNumber(numLevels - 1));
            } else {
                encodedLevels.append(PolylineEncoder.encodeNumber(numLevels
                        - computeLevel(absMaxDist) - 1));
            }
        }
        // System.out.println("encodedLevels: " + encoded_levels);
        return encodedLevels.toString();
    }

    /**
     * This computes the appropriate zoom level of a point in terms of it's
     * distance from the relevant segment in the DP algorithm. Could be done
     * in terms of a logarithm, but this approach makes it a bit easier to
     * ensure that the level is not too large.
     */
    private int computeLevel(final double absMaxDist) {
        int lev = 0;
        if (absMaxDist > verySmall) {
            lev = 0;
            while (absMaxDist < zoomLevelBreaks[lev]) {
                lev++;
            }
        }
        return lev;
    }

    private String createEncodings(final Track points, final float[] dists) {
        final StringBuffer encodedPoints = new StringBuffer();

        double maxlat = 0, minlat = 0, maxlon = 0, minlon = 0;

        int plat = 0;
        int plng = 0;

        for (int i = 0; i < points.size(); i++) {

            // determin bounds (max/min lat/lon)
            if (i == 0) {
                maxlat = minlat = points.get(i).getLatDouble();
                maxlon = minlon = points.get(i).getLonDouble();
            } else {
                if (points.get(i).getLatDouble() > maxlat) {
                    maxlat = points.get(i).getLatDouble();
                } else if (points.get(i).getLatDouble() < minlat) {
                    minlat = points.get(i).getLatDouble();
                } else if (points.get(i).getLonDouble() > maxlon) {
                    maxlon = points.get(i).getLonDouble();
                } else if (points.get(i).getLonDouble() < minlon) {
                    minlon = points.get(i).getLonDouble();
                }
            }

            if ((dists[i] != 0) || (i == 0) || (i == points.size() - 1)) {
                final Trackpoint point = points.get(i);

                final int late5 = PolylineEncoder.floor1e5(point
                        .getLatDouble());
                final int lnge5 = PolylineEncoder.floor1e5(point
                        .getLonDouble());

                final int dlat = late5 - plat;
                final int dlng = lnge5 - plng;

                plat = late5;
                plng = lnge5;

                encodedPoints
                        .append(PolylineEncoder.encodeSignedNumber(dlat));
                encodedPoints
                        .append(PolylineEncoder.encodeSignedNumber(dlng));

            }
        }

        final BT747Hashtable lbounds = JavaLibBridge.getHashtableInstance(0);
        lbounds.put("maxlat", JavaLibBridge.toString(maxlat));
        lbounds.put("minlat", JavaLibBridge.toString(minlat));
        lbounds.put("maxlon", JavaLibBridge.toString(maxlon));
        lbounds.put("minlon", JavaLibBridge.toString(minlon));

        setBounds(lbounds);
        return encodedPoints.toString();
    }

    private final void setBounds(final BT747Hashtable pbounds) {
        bounds = pbounds;
    }

    public final BT747Hashtable createEncodings(final Track track,
            final int level, final int step) {

        final BT747Hashtable resultMap = JavaLibBridge.getHashtableInstance(0);
        final StringBuffer encodedPoints = new StringBuffer();
        final StringBuffer encodedLevels = new StringBuffer();

        int plat = 0;
        int plng = 0;
        int counter = 0;

        final int listSize = track.size();

        Trackpoint trackpoint;

        for (int i = 0; i < listSize; i += step) {
            counter++;
            trackpoint = track.get(i);

            final int late5 = PolylineEncoder.floor1e5(trackpoint
                    .getLatDouble());
            final int lnge5 = PolylineEncoder.floor1e5(trackpoint
                    .getLonDouble());

            final int dlat = late5 - plat;
            final int dlng = lnge5 - plng;

            plat = late5;
            plng = lnge5;

            encodedPoints.append(PolylineEncoder.encodeSignedNumber(dlat))
                    .append(PolylineEncoder.encodeSignedNumber(dlng));
            encodedLevels.append(PolylineEncoder.encodeNumber(level));

        }

        // System.out.println("listSize: " + listSize + " step: " + step
        // + " counter: " + counter);

        resultMap.put("encodedPoints", PolylineEncoder.replace(encodedPoints
                .toString(), "\\", "\\\\"));
        resultMap.put("encodedLevels", encodedLevels.toString());

        return resultMap;
    }

    public final BT747Hashtable getBounds() {
        return bounds;
    }
}
