/**
 * 
 */
package net.sf.bt747.j2se.app.trackgraph;

import gps.log.GPSRecord;

import java.awt.event.MouseEvent;

import javax.swing.JFrame;

/**
 * @author Mario
 * 
 */
public class GraphUtils {

    public static void showGraphFrame(final GPSRecordDataProvider dp) {
        // Set up the Gui
        // the main panel
        final TrackDisplay trackdisplay = new TrackDisplay();

        // hand over data iterators
        trackdisplay.showHeightTrack(dp.getHeightIter());
        trackdisplay.showSpeedTrack(dp.getSpeedIter());

        // add a TrackDisplayMouseListener with custom left-click behavior
        trackdisplay.addMouseListener(new TrackDisplayMouseListener(
                trackdisplay) {
            @Override
            protected void leftClick(MouseEvent ev) {
                // resolve x-axis coordinate that is the time stamp of the
                // record
                double timestamp = trackdisplay.translateMousePosition(ev)
                        .getX();

                // let the CoordinateResolver do it's job
                GPSRecord rec = dp.getRecordOfTimestamp(timestamp);

                System.out.println("appropriate coordinate: " + rec);
            }
        });

        // create the window
        JFrame frame = new JFrame("Height and Speed");
        frame.getContentPane().add(trackdisplay);
        frame.setSize(800, 600);

        // frame.addWindowListener(new WindowAdapter() {
        // public void windowClosing(WindowEvent e) {
        // System.exit(0);
        // }
        // });
        frame.setVisible(true);
    }

}
