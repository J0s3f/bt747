// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** seesite@bt747.org ***
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

import gps.connection.GPSrxtx;
import gps.connection.NMEADecoderState;
import gps.mvc.commands.CmdVisitor;
import gps.mvc.commands.GpsLinkExecCommand;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Semaphore;
import bt747.sys.interfaces.BT747Vector;

/**
 * Refactoring ongoing. Stuff to do:<br>
 * - message type should be generic and a factory can be used to find the
 * right handler.
 * 
 * @author Mario De Weerd
 * 
 */
public final class GpsLinkHandler {
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
    public GpsLinkHandler() {

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

    public final void sendCmd(final GpsLinkExecCommand cmd) {
        int cmdsWaiting;
        cmdBuffersAccess.down(); // Protecting entire function to avoid
                                 // multiple cmds.
        cmdsWaiting = sentCmds.size();
        if (!isEraseOngoing() && (cmdsWaiting == 0)
                && (Generic.getTimeStamp() > nextCmdSendTime)) {
            // All sent commands were acknowledged, send cmd immediately
            doSendCmdUnprotected(cmd);
        } else if (cmdsWaiting < GpsLinkHandler.C_MAX_TOSEND_COMMANDS) {
            // Ok to buffer more cmds
            toSendCmds.addElement(cmd);
            if (Generic.isDebug()) {
                Generic.debug("#" + cmd);
            }
        }
        cmdBuffersAccess.up();
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
    public void doSendCmd(final GpsLinkExecCommand cmd) {
        cmdBuffersAccess.down();
        doSendCmdUnprotected(cmd);
        cmdBuffersAccess.up();
    }

    private final void doSendCmdUnprotected(final GpsLinkExecCommand cmd) {
        resetLogTimeOut();
        try {
            if(cmd.hasAck()) {
                sentCmds.addElement(cmd);
            }
            cmd.execute(this.getGPSRxtx());
        } catch (final Exception e) {
            Generic.debug("doSendCmd", e);
        }
        nextCmdSendTime = Generic.getTimeStamp()
                + GpsLinkHandler.C_MIN_TIME_BETWEEN_CMDS;
        if (sentCmds.size() > GpsLinkHandler.C_MAX_SENT_COMMANDS) {
            sentCmds.removeElementAt(0);
        }
    }

    private int logTimer = 0;
    private int downloadTimeOut = 3500;

    protected final void initConnected() {
        nextCmdSendTime = Generic.getTimeStamp()
                + GpsLinkHandler.INITIAL_WAIT;
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
                        && (sentCmds.size() < GpsLinkHandler.C_MAX_CMDS_SENT)
                        && (Generic.getTimeStamp() > nextCmdSendTime)) {
                    GpsLinkExecCommand cmd = (GpsLinkExecCommand) toSendCmds
                            .elementAt(0);
                    if (sentCmds.size() == 0 || !cmd.mustBeFirstInQueue()) {
                        // No more commands waiting for acknowledge
                        doSendCmdUnprotected(cmd);
                        toSendCmds.removeElementAt(0);
                    }
                }
            } catch (final Exception e) {
                Generic.debug("checkSendCmdFromQueue", e);
            }
            cmdBuffersAccess.up();
        }
    }

    public final boolean removeFromSentCmds(CmdVisitor visitor) {
        int cmdIdx = -1;
        cmdBuffersAccess.down();
        try {
            for (int i = 0; i < sentCmds.size(); i++) {
                if (visitor.isAcknowledgeOf((GpsLinkExecCommand) (sentCmds
                        .elementAt(i)))) {
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

    // protected final boolean removeFromSentCmds(final String match) {
    // int cmdIdx = -1;
    // cmdBuffersAccess.down();
    // try {
    // for (int i = 0; i < sentCmds.size(); i++) {
    // if (((String) sentCmds.elementAt(i)).startsWith(match)) {
    // cmdIdx = i;
    // break;
    // }
    // }
    // // Remove all cmds up to
    // for (int i = cmdIdx; i >= 0; i--) {
    // // if(GPS_DEBUG) {
    // // debugMsg("Remove:"+(String)sentCmds.items[0]);
    // // }
    // sentCmds.removeElementAt(0);
    // }
    // } catch (final Exception e) {
    // Generic.debug("removeFromSentCmds", e);
    // }
    // cmdBuffersAccess.up();
    // return cmdIdx != -1;
    // }

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
