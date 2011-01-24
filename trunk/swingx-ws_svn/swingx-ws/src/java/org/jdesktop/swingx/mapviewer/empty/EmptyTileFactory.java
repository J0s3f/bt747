/*
 * EmptyTileFactory.java
 *
 * Created on June 7, 2006, 4:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer.empty;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import java.util.Collection;
import java.util.HashMap;
import org.jdesktop.swingx.mapviewer.Tile;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

/**
 * A null implementation of TileFactory. Draws empty areas.
 * 
 * @author joshy
 */
public class EmptyTileFactory extends TileFactory {

	/** The empty tile image. */
	private BufferedImage emptyTile;

	/** Creates a new instance of EmptyTileFactory */
	public EmptyTileFactory() {
		this(new TileFactoryInfo("EmptyTileFactory 256x256", 1, 15, 17, 256,
				true, true, "", "x", "y", "z"));
	}

	/** Creates a new instance of EmptyTileFactory using the specified info. */
	public EmptyTileFactory(TileFactoryInfo info) {
		super(info);
		int tileSize = info.getTileSize(info.getMinimumZoomLevel());
		emptyTile = new BufferedImage(tileSize, tileSize,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = emptyTile.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, tileSize, tileSize);
		g.setColor(Color.WHITE);
		g.drawOval(10, 10, tileSize - 20, tileSize - 20);
		g.fillOval(70, 50, 20, 20);
		g.fillOval(tileSize - 90, 50, 20, 20);
		g.fillOval(tileSize / 2 - 10, tileSize / 2 - 10, 20, 20);
		g.dispose();
	}


    @Override
    public Tile getTile(String key, String mapName) {
        return requiredTiles.get(mapName).get(key);
    }

    HashMap<String, HashMap<String, Tile>> requiredTiles = new HashMap<String, HashMap<String, Tile>>();

    @Override
    public void setRequiredTiles(Collection<Tile> tiles, String mapName) {
    //NOP
        synchronized (requiredTiles)
        {
            HashMap<String, Tile> requiredTilesForMap = requiredTiles.get(mapName);
            if (requiredTilesForMap == null)
            {
                requiredTilesForMap = new HashMap<String, Tile>();
                requiredTiles.put(mapName, requiredTilesForMap);
            }

            for (Tile tile : tiles)
            {
                if (requiredTilesForMap.containsKey(tile.getKey())) continue;
                tile.setImage(emptyTile);
                requiredTilesForMap.put(tile.getKey(), tile);
            }
            requiredTiles.notifyAll();
        }


    }
    
}
