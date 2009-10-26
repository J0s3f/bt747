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
    public MyTileFactoryInfo(final String name, final int minimumZoomLevel,
            final int maximumZoomLevel, final int totalMapZoom,
            final int tileSize, final boolean xr2l, final boolean yt2b,
            final String baseURL, final String xparam, final String yparam,
            final String zparam, final String description, final String url) {
        super(name, minimumZoomLevel, maximumZoomLevel, totalMapZoom,
                tileSize, xr2l, yt2b, baseURL, xparam, yparam, zparam);
        this.url = url;
        this.description = description;
    }

}
