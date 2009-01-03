/**
 * 
 */
package net.sf.bt747.j2se.app.map;

import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

public class MyTileFactoryInfo extends TileFactoryInfo {
    public String description;
    public String url;

    /**
     * @param name
     * @param minimumZoomLevel
     * @param maximumZoomLevel
     * @param totalMapZoom
     * @param tileSize
     * @param xr2l
     * @param yt2b
     * @param baseURL
     * @param xparam
     * @param yparam
     * @param zparam
     */
    public MyTileFactoryInfo(String name, int minimumZoomLevel,
            int maximumZoomLevel, int totalMapZoom, int tileSize,
            boolean xr2l, boolean yt2b, String baseURL, String xparam,
            String yparam, String zparam, String description, String url) {
        super(name, minimumZoomLevel, maximumZoomLevel, totalMapZoom,
                tileSize, xr2l, yt2b, baseURL, xparam, yparam, zparam);
        this.url = url;
        this.description = description;
    }

}