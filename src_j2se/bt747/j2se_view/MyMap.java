/**
 * 
 */
package bt747.j2se_view;

import gps.log.GPSRecord;

import javax.swing.JPanel;

import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointPainter;

/**
 * @author Mario
 * 
 */
public class MyMap extends JPanel implements MapViewerInterface {
    JXMapKit map;
    JXMapViewer mapViewer;

    /**
     * 
     */
    public MyMap() {
        // TODO Auto-generated constructor stub
        map = new JXMapKit();
        map.setMiniMapVisible(true);
        map.setDefaultProvider(JXMapKit.DefaultProviders.OpenStreetMaps);
        mapViewer = map.getMainMap();
        add(map);
        org.jdesktop.layout.GroupLayout InfoPanelLayout = new org.jdesktop.layout.GroupLayout(
                this);
        this.setLayout(InfoPanelLayout);
        InfoPanelLayout.setHorizontalGroup(InfoPanelLayout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                InfoPanelLayout.createSequentialGroup()
                // .addContainerGap()
                        .add(map, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                300, Short.MAX_VALUE)
        // .addContainerGap()
                ));
        InfoPanelLayout.setVerticalGroup(InfoPanelLayout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                InfoPanelLayout.createSequentialGroup()
                // .addContainerGap()
                        .add(map, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                312, Short.MAX_VALUE)
        // .addContainerGap()
                ));

//        myPositions.add(new GeoPosition(41.881944, 2.5));
//        myWaypoints.add(new Waypoint(41.88, 2.5));
//        myWaypoints.add(new Waypoint(42.88, 2.56));
//        setWayPoints(myWaypoints);
        // map.setAddressLocation(new GeoPosition(41.881944,2.5));
    }

    /* (non-Javadoc)
     * @see bt747.j2se_view.MapViewerInterface#setWayPoints(gps.log.GPSRecord[])
     */
    public void setWayPoints(GPSRecord[] records) {
        java.util.Set<Waypoint> positions = new java.util.HashSet<Waypoint>();
        java.util.Set<GeoPosition> myPositions = new java.util.HashSet<GeoPosition>();
        
        for (int i = 0; i < records.length; i++) {
            GPSRecord r = records[i];
            if(r.hasLatitude() && r.hasLongitude()) {
                Waypoint w = new Waypoint(r.latitude, r.longitude);
                positions.add(w);
                myPositions.add(w.getPosition());
            }
        }
        mapViewer.calculateZoomFrom(myPositions);
        // Iterator<Waypoint> iter = positions.iterator();
        // while(iter.hasNext()) {
        WaypointPainter<JXMapViewer> p = new WaypointPainter<JXMapViewer>();
        p.setWaypoints(positions);
        mapViewer.setOverlayPainter(p);
        //setGoogleMaps();

    }

    private void setGoogleMaps() {
//http://khm2.google.com/kh?v=33g&x=1042&y=686&z=11&s=Gali
        final int max = 17;
        TileFactoryInfo info = new TileFactoryInfo(1,max-2,max,
                256, true, true, // tile size is 256 and x/y orientation is normal
                //"http://mt2.google.com/mt?n=404&v=w2.21",//5/15/10.png",
                "http://khm2.google.com/kh?v=33",
                "x","y","z") {
            public String getTileUrl(int x, int y, int zoom) {
                zoom = max-zoom;
                String url = this.baseURL +"&x="+x+"&y="+y+"&z="+zoom;
                return url;
            }
            
        };
        TileFactory tf = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tf);

    }
}
