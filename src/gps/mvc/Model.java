// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
package gps.mvc;

import gps.GPSListener;
import gps.GpsEvent;
import gps.connection.GPSrxtx;
import gps.log.GPSRecord;
import gps.log.in.CommonIn;
import gps.mvc.commands.GpsLinkExecCommand;
import net.sf.bt747.gps.mtk.MtkBinTransportMessageModel;
import gps.ProtocolConstants;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747HashSet;

/**
 * Refactoring ongoing (split in Model and Controller).
 * 
 * GPSstate maintains a higher level state of communication with the GPS
 * device. It currently contains very specific commands MTK loggers but that
 * could change in the future by extending GPSstate with such features in a
 * derived class.
 * 
 * Must still move stuff to MtkController, move to MtkModel mostly done.
 * 
 * @author Mario De Weerd
 * @see GPSrxtx
 * 
 */
/* Final for the moment */
public class Model implements ProtocolConstants {
    private final GPSLinkHandler handler;
    // For the moment pointing to 'this' for the MtkModel.
    // After refactoring this should be effectively fully delegated.
    private MtkModel mtkModel;

    private int protocol = -1;

    /**
     * Initialiser.
     * 
     * Will also create the model(s) in the lower layers.
     */
    public Model(final GPSrxtx gpsRxTx, final int protocol) {
        handler = new GPSLinkHandler();

        setProtocol(protocol);
        setGPSRxtx(gpsRxTx);
    }

    public final void setProtocol(final int protocol) {
        if (this.protocol != protocol) {
            this.protocol = protocol;
            switch (protocol) {
            case PROTOCOL_SIRFIII:
            case PROTOCOL_MTK:
                mtkModel = new MtkModel(this, handler);
                break;
            case PROTOCOL_WONDEPROUD:
                mtkModel = new DPL700Model(this, handler);
                break;
            case PROTOCOL_HOLUX_PHLX:
                mtkModel = new HoluxModel(this, handler);
                break;
            default:
                // TODO: This should probably be handled through an exception
                mtkModel = null;
                break;
            }
        }
    }

    public final MtkModel getMtkModel() {
        return mtkModel;
    }

    /*************************************************************************
     * Start of code that should be part of the final interface after
     * refactoring.
     */

    public final GPSLinkHandler getHandler() {
        return handler;
    }

    /*************************************************************************
     * Start
     * 
     * @param gpsRxTx
     */
    public final void setGPSRxtx(final GPSrxtx gpsRxTx) {
        handler.setGPSRxtx(gpsRxTx);
    }

    /**
     * Indicates if statistics regarding the link should be fetched. No actual
     * implementation currently.
     */
    private boolean GPS_STATS = false; // (!Settings.onDevice);

    public final void setStats(final boolean stats) {
        GPS_STATS = stats;
    }

    public final boolean getStats() {
        return GPS_STATS;
    }

    /**
     * Invalidates any data previously fetched from the device.
     */
    public void setAllUnavailable() {
        // Delegate to the model(s) in use.
        mtkModel.setAllUnavailable();
    }

    /*************************************************************************
     * End of reviewed code for final interface.
     ************************************************************************/

    /*************************************************************************
     * The code that follows still needs refactoring.
     */

    /**
     * Analyze GPRMC data.
     * 
     * @param sNmea
     * @param gps
     */
    private final void analyzeGPRMC(final String[] sNmea, final GPSRecord gps) {
        if (CommonIn.analyzeGPRMC(sNmea, gps) != 0) {
            postGpsEvent(GpsEvent.GPRMC, gps);
        }
    }

    /**
     * Analyze GPGGA data.
     * 
     * @param sNmea
     * @param gps
     */
    private final void analyzeGPGGA(final String[] sNmea, final GPSRecord gps) {
        if (CommonIn.analyzeGPGGA(sNmea, gps) != 0) {
            gps.height -= gps.geoid; // Default function adds the two
            postGpsEvent(GpsEvent.GPGGA, gps);
        }
    }

    private final GPSRecord gpsPos = GPSRecord.getLogFormatRecord(0);

    final boolean analyseResponse(final Object response) {
        return mtkModel.analyseResponse(response);
    }

    public final boolean analyseNMEA(final String[] sNmea) {
        // final int cmd;
        boolean result;
        result = false;
        try {
            // if(GPS_DEBUG&&!p_nmea[0].startsWith("G")) {
            // waba.sys.debugMsg("ANA:"+p_nmea[0]+","+p_nmea[1]);}
            if (sNmea.length == 0) {
                // Should not happen, problem in program
                Generic.debug("Problem - report NMEA is 0 length");
            } else if (handler.isGpsDecode()
                    && !isLogDownloadOnGoing() // Not
                    // during
                    // log
                    // download for
                    // performance.
                    && (sNmea[0].length() != 0)
                    && (sNmea[0].charAt(0) == 'G')) {
                // Commented - not interpreted.
                // Generic.debug("Before"+sNmea[0]+(new
                // java.util.Date(gpsPos.utc*1000L)).toString()+"("+gpsPos.utc+")");
                if (sNmea[0].startsWith("GPGGA")) {
                    analyzeGPGGA(sNmea, gpsPos);
                } else if (sNmea[0].startsWith("GPRMC")) {

                    analyzeGPRMC(sNmea, gpsPos);
                }
                // Generic.debug("After"+sNmea[0]+(new
                // java.util.Date(gpsPos.utc*1000L)+"("+gpsPos.utc+")").toString());
                // else if(p_nmea[0].startsWith("GPZDA")) {
                // // GPZDA,$time,$msec,$DD,$MO,$YYYY,03,00
                // } else if(p_nmea[0].startsWith("GPRMC")) {
                // //
                // GPRMC,$time,$fix,$latf1,$ns,$lonf1,$ew,$knots,$bear,$date,$magnvar,$magnew,$magnfix
                // } else if(p_nmea[0].startsWith("GPSTPV")) {
                // //
                // GPSTPV,$epoch.$msec,?,$lat,$lon,,$alt,,$speed,,$bear,,,,A
                // }
                result = true; // Success in decoding
            }
        } catch (final Exception e) {
            Generic.debug("AnalyzeNMEA", e);
        }
        return result;
    } // End method

    /*************************************************************************
     * LOGGING FUNCTIONALITY
     ************************************************************************/

    /*************************************************************************
     * Getters and Setters
     * 
     */

    /**
     * @return Returns the gpsDecode.
     */
    public final boolean isGpsDecode() {
        return handler.isGpsDecode();
    }

    /**
     * @param gpsDecode
     *            Activate gps decoding if true, do not decode if false. This
     *            may improve performance.
     */
    public final void setGpsDecode(final boolean gpsDecode) {
        handler.setGpsDecode(gpsDecode);
    }

    protected void postGpsEvent(final int type, final Object o) {
        postEvent(new GpsEvent(type, o));
    }

    public final GPSRecord getGpsRecord() {
        return gpsPos;
    }

    private final BT747HashSet listeners = JavaLibBridge.getHashSetInstance();

    /** add a listener to event thrown by this class */
    public final void addListener(final GPSListener l) {
        listeners.add(l);
    }

    public final void removeListener(final GPSListener l) {
        listeners.remove(l);
    }

    protected void postEvent(final int event) {
        postEvent(new GpsEvent(event));
    }

    protected final void postEvent(final GpsEvent e) {
        final BT747HashSet it = listeners.iterator();
        while (it.hasNext()) {
            final GPSListener l = (GPSListener) it.next();
            l.gpsEvent(e);
        }
    }

    /**
     * Send an NMEA string to the link. The parameter should not include the
     * checksum - this is added by the method.
     * 
     * @param s
     *            NMEA string to send.
     */
    public final void sendCmd(final GpsLinkExecCommand s) {
        handler.sendCmd(s);
    }

    /**
     * Immediate string sending.
     * 
     * @param s
     */
    protected final void doSendCmd(final Object s) {
        handler.doSendCmd(s);
    }

    /**
     * Get the number of Cmds that are still waiting to be sent and/or waiting
     * for acknowledgement.
     * 
     * @return
     */
    public final int getOutStandingCmdsCount() {
        return handler.getOutStandingCmdsCount();
    }

    protected final int timeSinceLastStamp() {
        return handler.timeSinceLastStamp();
    }

    protected final void resetLogTimeOut() {
        handler.resetLogTimeOut();
    }

    protected final boolean isConnected() {
        return handler.isConnected();
    }

    /**
     * Get the start address for the log download. To be used for the download
     * progress bar.
     * 
     * @return the startAddr
     */
    public final int getStartAddr() {
        return mtkModel.getStartAddr();
    }

    /**
     * Get the end address for the log download. To be used for the download
     * progress bar.
     * 
     * @return the endAddr
     */
    public final int getEndAddr() {
        return mtkModel.getEndAddr();
    }

    /**
     * Get 'download ongoing' status.
     * 
     * @return true if the download is currently ongoing. This is usefull for
     *         the download progress bar.
     */
    public final boolean isLogDownloadOnGoing() {
        return mtkModel.isLogDownloadOngoing();
    }

    /**
     * Get the log address that we are now expecting to receive data for. This
     * is usefull for the download progress bar.
     * 
     * @return the nextReadAddr
     */
    public final int getNextReadAddr() {
        return mtkModel.getNextReadAddr();
    }
}
