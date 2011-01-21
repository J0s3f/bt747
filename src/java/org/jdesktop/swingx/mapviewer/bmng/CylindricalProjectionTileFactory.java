package org.jdesktop.swingx.mapviewer.bmng;

import org.jdesktop.swingx.mapviewer.*;

public class CylindricalProjectionTileFactory extends DefaultTileFactory {

    public CylindricalProjectionTileFactory() {
        this(new SLMapServerInfo());
    }
    public CylindricalProjectionTileFactory(SLMapServerInfo info) {
        super(info);
    }

    /*
    x = lat * ppd + fact
    x - fact = lat * ppd
    (x - fact)/ppd = lat
    y = -lat*ppd + fact
    -(y-fact)/ppd = lat
    */
}