/*
 * TileCache.java
 *
 * Created on January 2, 2007, 7:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.jdesktop.swingx.mapviewer;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

/**
 * An implementation only class for now. For internal use only.
 * 
 * @author joshua.marinacci@sun.com
 */
public class TileCache {
    private static final Logger LOG = Logger.getLogger(TileCache.class.getName());


    static {
        LOG.setLevel(Level.SEVERE);
    }
    private final Object mutex = new Object();
    private File diskCacheDir = null;
    private final Map<String, UnCompressed> uncompressedCache = new HashMap<String, UnCompressed>();
    private final LinkedList<String> uncompressedCacheAccessQueue = new LinkedList<String>();
    private int uncompressedCacheSize = 0;
    private final Map<String, Compressed> compressedCache = new HashMap<String, Compressed>();
    private final LinkedList<String> compressedCacheAccessQueue = new LinkedList<String>();
    private int compressedCacheSize = 0;
    private final Map<String, Long> imageDate = new HashMap<String, Long>();
    private int maxSizeCompressedCache = 1000 * 1000 * 25;
    private int maxSizeUncompressedCache = 1000 * 1000 * 50;
    private final int MINCACHESIZE = 1000 * 1000 * 10;

    public TileCache() {
    }

    /**
     * Set the disk image cache directory.
     * 
     * @param cacheDir
     *            the base directory to be used as a disk image cache.
     */
    public void setDiskCacheDir(File cacheDir) {
        this.diskCacheDir = cacheDir;
    }

    private File getCacheFile(String key) {
        if(diskCacheDir!=null) {
           return new File(diskCacheDir, key);
        }
        return null;
    }

    /**
     * Put a tile image into the cache. This puts both a buffered image and array of bytes that make up the compressed image.
     * 
     * @param uri
     *            URI of image that is being stored in the cache
     * @param bimg
     *            bytes of the compressed image, ie: the image file that was loaded over the network
     * @param img
     *            image to store in the cache
     */
    public void put(String key, byte[] bimg, BufferedImage img) {
        synchronized (mutex) {
            addToCompressedCache(key, bimg);
            addToUncompressedCache(key, img);
            imageDate.put(key, System.currentTimeMillis());
        }
        addToDiskCache(key, bimg);
    }

    /**
     * Returns a buffered image for the requested URI from the cache. This method must return null if the image is not in the cache. If the
     * image is unavailable but it's compressed version *is* available, then the compressed version will be expanded and returned.
     * 
     * @param uri
     *            URI of the image previously put in the cache
     * @return the image matching the requested key, or null if not available
     * @throws java.io.IOException
     */
    public BufferedImage get(String key) {
        try {
            byte[] b = null;
            synchronized (mutex) {
                // preferentially get the image from the uncompressed cache, then from the compressed cache, then from the disk cache.
                if (uncompressedCache.containsKey(key)) {
                    uncompressedCacheAccessQueue.remove(key);
                    uncompressedCacheAccessQueue.addLast(key);
                    UnCompressed img = uncompressedCache.get(key);
                    if (img != null) {
                        BufferedImage i = img.getImage();
                        if (i != null) {
                            return i;
                        }
                        checkEntries();
                    }
                }
                if (compressedCache.containsKey(key)) {
                    compressedCacheAccessQueue.remove(key);
                    compressedCacheAccessQueue.addLast(key);
                    b = compressedCache.get(key).getBytes();
                    if (b == null) {
                        checkEntries();
                    }
                }
            }
            if (b != null) {
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(b));
                if(img != null) {
                    synchronized (mutex) {
                        addToUncompressedCache(key, img);
                    }
                    return img;
                }
            }

            File cacheFile = getCacheFile(key);
            if (cacheFile != null) {
                if (!cacheFile.exists()) {
                    return null;
                }
                FileInputStream fis = new FileInputStream(cacheFile);
                byte[] bimg = new byte[(int) cacheFile.length()];
                fis.read(bimg);
                fis.close();
                fis = null;
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(bimg));
                if (img != null) {
                    synchronized (mutex) {
                        addToCompressedCache(key, bimg);
                        addToUncompressedCache(key, img);
                    }
                    imageDate.put(key, cacheFile.lastModified());
                    return img;
                }
            }
        } catch (Exception e) {
            LOG.log(Level.INFO, "Couldn't load cached image: " + key, e);
        }
        LOG.log(Level.INFO, "Couldn't find cached image: " + key);
        return null;
    }

    public Long getImageDate(String key) {
        synchronized (mutex) {
            return imageDate.get(key);
        }
    }

    /**
     * Add the given image to the uncompressed image cache. Must be externally synchronised.
     * 
     * @param key
     * @param img
     */
    private void addToUncompressedCache(String key, final BufferedImage img) {
        if(img == null)
            return;
        synchronized (mutex) {
            while (uncompressedCacheSize > maxSizeUncompressedCache) {
                String oldKey = uncompressedCacheAccessQueue.removeFirst();
                UnCompressed oldimg = uncompressedCache.remove(oldKey);
                if (oldimg == null) {
                    LOG.severe("TileCache.addToImageCache: couldn't find oldimg for key: " + oldKey);
                    continue;
                }
                uncompressedCacheSize -= oldimg.size;
            }
            /* allow for replacement of the old cached image with a new one. */
            UnCompressed oldimgr = uncompressedCache.get(key);
            if (oldimgr != null) {
                uncompressedCacheSize -= oldimgr.size;
                oldimgr = null;
            }
            UnCompressed newimgr = new UnCompressed(img);
            uncompressedCache.put(key, newimgr);
            uncompressedCacheSize += newimgr.size;
            uncompressedCacheAccessQueue.remove(key);
            uncompressedCacheAccessQueue.addLast(key);
        }
    }

    /**
     * Add the given image to the compressed image cache. Must be externally synchronised.
     * 
     * @param key
     * @param bimg
     */
    private void addToCompressedCache(String key, byte[] bimg) {
        if(bimg == null)
            return;
        synchronized (mutex) {
            while (compressedCacheSize > maxSizeCompressedCache) {
                String oldKey = compressedCacheAccessQueue.removeFirst();
                Compressed oldbimg = compressedCache.remove(oldKey);
                compressedCacheSize -= oldbimg.size;
            }
            /* allow for replacement of the old cached image with a new one. */
            Compressed oldbimg = compressedCache.get(key);
            if (oldbimg != null && oldbimg.getBytes() != null) {
                compressedCacheSize -= oldbimg.size;
                oldbimg = null;
            }
            compressedCache.put(key, new Compressed(bimg));
            compressedCacheSize += bimg.length;
            compressedCacheAccessQueue.remove(key);
            compressedCacheAccessQueue.addLast(key);
        }
    }

    /**
     * Add the given image to the disk image cache. Must be externally synchronised.
     * 
     * @param key
     * @param bimg
     */
    private void addToDiskCache(String key, byte[] bimg) {
        // if application hasn't set the cacheDir, don't write to disk cache.
        final File cacheFile = getCacheFile(key);
        if (cacheFile != null) {
            try {
                File dir = new File(cacheFile.getParent());
                if(!dir.exists()) {
                    dir.mkdirs();
                }
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(cacheFile));
                bos.write(bimg);
                bos.flush();
                bos.close();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "TileCache.put: Couldn't write image to file: " + cacheFile.getAbsolutePath(), e);
            }
        }
    }

    private void adjustCacheSize() {
        synchronized (mutex) {
            int total = compressedCacheSize + uncompressedCacheSize;
            if (total < MINCACHESIZE) {
                total = MINCACHESIZE;
            }
            maxSizeCompressedCache = total / 3;
            maxSizeUncompressedCache = total - maxSizeCompressedCache;
        }
    }

    private void checkEntries() {
        try {
            synchronized (mutex) {
                for (String key : compressedCacheAccessQueue) {
                    Compressed oldbimg = compressedCache.get(key);
                    if (oldbimg.getBytes() == null) {
                        compressedCacheAccessQueue.remove(key);
                        compressedCache.remove(key);
                        compressedCacheSize -= oldbimg.size;
                        adjustCacheSize();
                    }
                }

                for (String key : uncompressedCacheAccessQueue) {
                    UnCompressed oldimg = uncompressedCache.get(key);
                    if (oldimg.getImage() == null) {
                        uncompressedCacheAccessQueue.remove(key);
                        uncompressedCache.remove(key);
                        uncompressedCacheSize -= oldimg.size;
                        adjustCacheSize();
                    }
                }
            }
        } catch (Exception e) {
            //LOG.log(Level.SEVERE, "Problem sanitizing tile image cache", e);
        }
    }

    private static class Compressed {

        int size;
        SoftReference<byte[]> b;

        public Compressed(byte[] by) {
            size = by.length;
            b = new SoftReference<byte[]>(by);
        }
        ;

        public byte[] getBytes() {
            return b.get();
        }
    }

    private static class UnCompressed {

        int size;
        SoftReference<BufferedImage> b;

        public UnCompressed(BufferedImage by) {
            size = by.getWidth() * by.getHeight() * 4;
            b = new SoftReference<BufferedImage>(by);
        }
        ;

        public BufferedImage getImage() {
            return b.get();
        }
    }
}
