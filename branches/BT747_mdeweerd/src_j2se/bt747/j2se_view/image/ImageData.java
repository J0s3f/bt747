//********************************************************************
//***                            BT747                             ***
//***                 (c)2007-2008 Mario De Weerd                  ***
//***                     m.deweerd@ieee.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER.                                     ***
//***                                                              ***
//***  See the GNU General Public License Version 3 for details.   ***
//***  *********************************************************** ***
package bt747.j2se_view.image;

import bt747.j2se_view.exif.ExifAttribute;
import bt747.j2se_view.exif.ExifConstants;
import bt747.j2se_view.exif.ExifJPG;

import gps.log.GPSRecord;

/**
 * @author Mario De Weerd
 * 
 */
public class ImageData {
    private GPSRecord gpsInfo = GPSRecord.getLogFormatRecord(0); // Invalid values.
    private int utc;

    private int width;
    private int height;

    private String path;
    private int card;

    public void setImagePath(final String path, final int card) {
        this.setCard(card);
        setImagePath(path);
    }

    public void setImagePath(final String path) {
        this.setPath(path);
        getImageInfo();
    }

    private void getImageInfo() {
        ExifJPG exifJpg = new ExifJPG();
        if (exifJpg.setPath(getPath())) {
            ExifAttribute atr;
            atr = exifJpg.getExifAttribute(ExifConstants.TAG_IMAGEWIDTH);
            if (atr != null) {
                setWidth(atr.getIntValue());
            }

            atr = exifJpg.getExifAttribute(ExifConstants.TAG_IMAGELENGTH);
            if (atr != null) {
                setHeight(atr.getIntValue());
            }
            atr = exifJpg.getGpsAttribute(ExifConstants.TAG_GPSLATITUDE);
            if (atr != null) {
                gpsInfo.latitude = atr.getFloatValue();
                atr = exifJpg.getGpsAttribute(ExifConstants.TAG_GPSLATITUDEREF);
                if (atr != null) {
                    if (atr.getStringValue().toUpperCase().indexOf('S') >= 0) {
                        gpsInfo.latitude = -gpsInfo.latitude;
                    }
                }
            }
            atr = exifJpg.getGpsAttribute(ExifConstants.TAG_GPSLONGITUDE);
            if (atr != null) {
                gpsInfo.longitude = atr.getFloatValue();
                atr = exifJpg.getGpsAttribute(ExifConstants.TAG_GPSLONGITUDEREF);
                if (atr != null) {
                    if (atr.getStringValue().toUpperCase().indexOf('S') >= 0) {
                        gpsInfo.longitude = -gpsInfo.longitude;
                    }
                }
            }
            atr = exifJpg.getExifAttribute(ExifConstants.TAG_IMAGELENGTH);
            if (atr != null) {
                setHeight(atr.getIntValue());
            }
            atr = exifJpg.getExifAttribute(ExifConstants.TAG_IMAGELENGTH);
            if (atr != null) {
                setHeight(atr.getIntValue());
            }
        }
    }

    /**
     * @param utc the utc to set
     */
    private void setUtc(int utc) {
        this.utc = utc;
    }

    /**
     * @return the utc
     */
    public int getUtc() {
        return utc;
    }

    /**
     * @param width the width to set
     */
    private void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param height the height to set
     */
    private void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return the height
     */
    private int getHeight() {
        return height;
    }

    /**
     * @param path the path to set
     */
    private void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param card the card to set
     */
    private void setCard(int card) {
        this.card = card;
    }

    /**
     * @return the card
     */
    public int getCard() {
        return card;
    }
}
