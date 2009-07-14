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
// *** IS ASSUMED BY THE USER. See the GNU General Public License ***
// *** for more details. ***
// *** *********************************************************** ***
package gps.mvc;

import gps.BT747Constants;
import gps.connection.GPSrxtx;
import gps.connection.NMEADecoderState;
import gps.connection.NMEAWriter;
import gps.mvc.commands.GpsRxtxExecCommand;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Semaphore;
import bt747.sys.interfaces.BT747Vector;

/**
 * Refactoring ongoing. Stuff to do:<br> - message type should be generic and
 * a factory can be used to find the right handler.
 * 
 * @author Mario De Weerd
 * 
 */
public final class GPSLinkHandler {
    private GPSrxtx gpsRxTx = null;
    private static final int INITIAL_WAIT = 500;

    /**
     * If currently erasing, this variable is true.
     */
    private boolean eraseOngoing = false;

    private final BT747Vector sentCmds = JavaLibBridge.getVectorInstance(); // List
    // of
    // sent
    // commands

    private static final int C_MAX_SENT_COMMANDS = 10; // Max commands to put
    // in list

    private final BT747Vector toSendCmds = JavaLibBridge.getVectorInstance(); // List
    // of
    // sent
    // commands

    private static final int C_MAX_TOSEND_COMMANDS = 20; // Max commands to
    // put in
    // list

    private static final int C_MAX_CMDS_SENT = 4;

    private static final int C_MIN_TIME_BETWEEN_CMDS = 30;

    /**
     * 
     */
    public GPSLinkHandler() {

    }

    public final void setGPSRxtx(final GPSrxtx gpsRxTx) {
        if (this.gpsRxTx != null) {
            // TODO Remove myself as listener
        }
        this.gpsRxTx = gpsRxTx;
        // TODO: Add myself as listener
    }

    public final GPSrxtx getGPSRxtx() {
        return this.gpsRxTx;
    }

    public final boolean isConnected() {
        return gpsRxTx.isConnected();
    }

    public final void sendCmd(final Object cmd) {
        int cmdsWaiting;
        cmdBuffersAccess.down();
        cmdsWaiting = sentCmds.size();
        cmdBuffersAccess.up();
        if (!isEraseOngoing() && (cmdsWaiting == 0)
                && (Generic.getTimeStamp() > nextCmdSendTime)) {
            // All sent commands were acknowledged, send cmd immediately
            doSendCmd(cmd);
        } else if (cmdsWaiting < GPSLinkHandler.C_MAX_TOSEND_COMMANDS) {
            // Ok to buffer more cmds
            cmdBuffersAccess.down();
            toSendCmds.addElement(cmd);
            if (Generic.isDebug()) {
                Generic.debug("#" + cmd);
            }
            cmdBuffersAccess.up();
        }
    }

    private boolean gpsDecode = true;

    /**
     * @return Returns the gpsDecode.
     */
    public final boolean isGpsDecode() {
        return gpsDecode;
    }

    public final void setGpsDecode(boolean isGpsDecode) {
        gpsDecode = isGpsDecode;
        updateIgnoreNMEA();
    }

    private boolean logDownloadOngoing;

    private final boolean isLogDownloadOngoing() {
        return logDownloadOngoing;
    }

    public final void setLogOrEraseOngoing(final boolean onGoing) {
        logDownloadOngoing = onGoing;
        updateIgnoreNMEA();
    }

    public final void updateIgnoreNMEA() {
        setIgnoreNMEA((!gpsDecode) || isLogDownloadOngoing());
    }

    public final void setIgnoreNMEA(final boolean ignoreNMEA) {
        NMEADecoderState.setIgnoreNMEA(ignoreNMEA);
    }

    private int nextCmdSendTime = 0;

    private final BT747Semaphore cmdBuffersAccess = JavaLibBridge
            .getSemaphoreInstance(1);

    /**
     * Immediate sending of cmd regardless of type.
     * 
     * This allows to change mode on the fly. The code here will reflect that.
     * 
     * Some commands can have execution method with context. TODO: Implement
     * that.
     * 
     * @param cmd
     */
    public void doSendCmd(final Object cmd) {
        resetLogTimeOut();
        cmdBuffersAccess.down();
        try {
            if (cmd instanceof String) {
                final String nmeaCmd = (String) cmd;
                if (nmeaCmd.startsWith("PMTK")) {
                    sentCmds.addElement(cmd);
                }
                NMEAWriter.sendPacket(gpsRxTx, nmeaCmd);
            } else if (cmd instanceof GpsRxtxExecCommand) {
                GpsRxtxExecCommand linkCmd = (GpsRxtxExecCommand) cmd;
                linkCmd.execute(this.getGPSRxtx());
            }
        } catch (final Exception e) {
            Generic.debug("doSendCmd", e);
        }
        nextCmdSendTime = Generic.getTimeStamp()
                + GPSLinkHandler.C_MIN_TIME_BETWEEN_CMDS;
        if (sentCmds.size() > GPSLinkHandler.C_MAX_SENT_COMMANDS) {
            sentCmds.removeElementAt(0);
        }
        cmdBuffersAccess.up();
    }

    private int logTimer = 0;
    private int downloadTimeOut = 3500;

    protected final void initConnected() {
        nextCmdSendTime = Generic.getTimeStamp()
                + GPSLinkHandler.INITIAL_WAIT;
    }

    protected final void checkSendCmdFromQueue() {
        final int cTime = Generic.getTimeStamp();
        if (!isEraseOngoing()) {
            cmdBuffersAccess.down();
            try {
                if ((sentCmds.size() != 0)
                        && ((cTime - logTimer) >= downloadTimeOut)) {
                    // TimeOut!!
                    if (Generic.isDebug()) {
                        Generic.debug("Timeout: " + cTime + "-" + logTimer
                                + ">" + downloadTimeOut, null);
                        for (int i = 0; i < sentCmds.size(); i++) {
                            Generic.debug("No ack:" + sentCmds.elementAt(i));
                        }
                    }
                    // sentCmds.removeElementAt(0); // Previous cleaning
                    // Since the last command that was sent is a timeout ago,
                    // we
                    // suppose that all the subsequent ones are forfeit too.
                    sentCmds.removeAllElements();
                    logTimer = cTime;
                }
                if ((toSendCmds.size() != 0)
                        && (sentCmds.size() < GPSLinkHandler.C_MAX_CMDS_SENT)
                        && (Generic.getTimeStamp() > nextCmdSendTime)) {
                    // No more commands waiting for acknowledge
                    cmdBuffersAccess.up();
                    doSendCmd(toSendCmds.elementAt(0));
                    cmdBuffersAccess.down();
                    toSendCmds.removeElementAt(0);
                }
            } catch (final Exception e) {
                Generic.debug("checkSendCmdFromQueue", e);
            }
            cmdBuffersAccess.up();
        }
    }

    protected final boolean removeFromSentCmds(final String match) {
        int cmdIdx = -1;
        cmdBuffersAccess.down();
        try {
            for (int i = 0; i < sentCmds.size(); i++) {
                if (((String) sentCmds.elementAt(i)).startsWith(match)) {
                    cmdIdx = i;
                    break;
                }
            }
            // Remove all cmds up to
            for (int i = cmdIdx; i >= 0; i--) {
                // if(GPS_DEBUG) {
                // debugMsg("Remove:"+(String)sentCmds.items[0]);
                // }
                sentCmds.removeElementAt(0);
            }
        } catch (final Exception e) {
            Generic.debug("removeFromSentCmds", e);
        }
        cmdBuffersAccess.up();
        return cmdIdx != -1;
    }

    public final int getOutStandingCmdsCount() {
        int total;
        cmdBuffersAccess.down();
        total = sentCmds.size() + toSendCmds.size();
        cmdBuffersAccess.up();
        // Generic.debug("sent:"+sentCmds.size()+"
        // tosend:"+toSendCmds.size());
        // if(toSendCmds.size()>0) {
        // Generic.debug(""+toSendCmds.elementAt(0));
        // }
        return total;
    }

    protected final Object getResponse() {
        return gpsRxTx.getResponse();
    }

    private final boolean isEraseOngoing() {
        return eraseOngoing;
    }

    protected final void setEraseOngoing(final boolean eraseOngoing) {
        this.eraseOngoing = eraseOngoing;
    }

    protected final void setDownloadTimeOut(final int downloadTimeOut) {
        this.downloadTimeOut = downloadTimeOut;
    }

    /**
     * Resets the condition to determine if a log request timeout occurs.
     */
    protected final void resetLogTimeOut() {
        logTimer = Generic.getTimeStamp();
    }

    protected final int timeSinceLastStamp() {
        return (Generic.getTimeStamp() - logTimer);
    }

}