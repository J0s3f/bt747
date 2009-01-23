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
package gps;

import gps.connection.GPSrxtx;

import bt747.sys.Convert;
import bt747.sys.Generic;
import bt747.sys.Interface;
import bt747.sys.interfaces.BT747Semaphore;
import bt747.sys.interfaces.BT747Vector;

final class GPSLinkHandler {
    private GPSrxtx gpsRxTx = null;
    private static final int INITIAL_WAIT = 500;

    /**
     * If currently erasing, this variable is true.
     */
    private boolean eraseOngoing = false;

    private final BT747Vector sentCmds = Interface.getVectorInstance(); // List
    // of
    // sent
    // commands

    private static final int C_MAX_SENT_COMMANDS = 10; // Max commands to put
    // in list

    private final BT747Vector toSendCmds = Interface.getVectorInstance(); // List
    // of
    // sent
    // commands

    private static final int C_MAX_TOSEND_COMMANDS = 20; // Max commands to
    // put in
    // list

    private static final int C_MAX_CMDS_SENT = 4;

    private static final int C_MIN_TIME_BETWEEN_CMDS = 30;

    public final void setGPSRxtx(final GPSrxtx gpsRxTx) {
        if (this.gpsRxTx != null) {
            // TODO Remove myself as listener
        }
        this.gpsRxTx = gpsRxTx;
        // TODO: Add myself as listener
    }

    public final boolean isConnected() {
        return gpsRxTx.isConnected();
    }

    public final void sendNMEA(final String cmd) {
        int cmdsWaiting;
        cmdBuffersAccess.down();
        cmdsWaiting = sentCmds.size();
        cmdBuffersAccess.up();
        if (!isEraseOngoing() && (cmdsWaiting == 0)
                && (Generic.getTimeStamp() > nextCmdSendTime)) {
            // All sent commands were acknowledged, send cmd immediately
            doSendNMEA(cmd);
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

    public final void setIgnoreNMEA(final boolean ignoreNMEA) {
        gpsRxTx.setIgnoreNMEA(ignoreNMEA);
    }

    private int nextCmdSendTime = 0;

    private final BT747Semaphore cmdBuffersAccess = Interface
            .getSemaphoreInstance(1);

    protected final void doSendNMEA(final String cmd) {
        resetLogTimeOut();
        cmdBuffersAccess.down();
        try {
            if (cmd.startsWith("PMTK")) {
                sentCmds.addElement(cmd);
            }

            gpsRxTx.sendPacket(cmd);
            if (Generic.isDebug()) {
                Generic.debug(">" + cmd + " " + gpsRxTx.isConnected());
            }
            nextCmdSendTime = Generic.getTimeStamp()
                    + GPSLinkHandler.C_MIN_TIME_BETWEEN_CMDS;
            if (sentCmds.size() > GPSLinkHandler.C_MAX_SENT_COMMANDS) {
                sentCmds.removeElementAt(0);
            }
        } catch (final Exception e) {
            Generic.debug("doSendNMEA", e);
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
                    doSendNMEA((String) toSendCmds.elementAt(0));
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

    // TODO: When acknowledge is missing for some commands, take appropriate
    // action.
    protected int analyseMTK_Ack(final String[] sNmea) {
        // PMTK001,Cmd,Flag
        int flag;
        int result = -1;
        // if(GPS_DEBUG) { waba.sys.debugMsg(p_nmea[0]+","+p_nmea[1]+"\n");}

        if (sNmea.length >= 3) {
            String sMatch;
            flag = Convert.toInt(sNmea[sNmea.length - 1]); // Last
            // parameter
            sMatch = "PMTK" + sNmea[1];
            for (int i = 2; i < sNmea.length - 1; i++) {
                // ACK is variable length, can have parameters of cmd.
                sMatch += "," + sNmea[i];
            }
            // if(GPS_DEBUG) {
            // debugMsg("Before:"+sentCmds.size()+" "+z_MatchString);
            // }

            removeFromSentCmds(sMatch);
            // if(GPS_DEBUG) {
            // debugMsg("After:"+sentCmds.size());
            // }
            // sentCmds.find(z_MatchString);
            // if(GPS_DEBUG) {
            // waba.sys.debugMsg("IDX:"+Convert.toString(z_CmdIdx)+"\n");}
            // if(GPS_DEBUG) {
            // waba.sys.debugMsg("FLAG:"+Convert.toString(z_Flag)+"\n");}
            switch (flag) {
            case BT747Constants.PMTK_ACK_INVALID:
                // 0: Invalid cmd or packet
                result = 0;
                break;
            case BT747Constants.PMTK_ACK_UNSUPPORTED:
                // 1: Unsupported cmd or packet
                result = 0;
                break;
            case BT747Constants.PMTK_ACK_FAILED:
                // 2: Valid cmd or packet but action failed
                result = 0;
                break;
            case BT747Constants.PMTK_ACK_SUCCEEDED:
                // 3: Valid cmd or packat but action succeeded
                result = 0;
                break;
            default:
                result = -1;
                break;
            }
        }
        return result;
    }

    public final int getOutStandingCmdsCount() {
        int total;
        cmdBuffersAccess.down();
        total = sentCmds.size() + toSendCmds.size();
        cmdBuffersAccess.up();
        return total;
    }

    protected final String[] getResponse() {
        return gpsRxTx.getResponse();
    }

    protected final void sendCmdAndGetDPL700Response(final int cmd,
            final int buffer_size) {
        gpsRxTx.sendCmdAndGetDPL700Response(cmd, buffer_size);
    }

    protected final void sendCmdAndGetDPL700Response(final String cmd,
            final int buffer_size) {
        gpsRxTx.sendCmdAndGetDPL700Response(cmd, buffer_size);
    }

    protected final void sendDPL700Cmd(final String cmd) {
        gpsRxTx.sendDPL700Cmd(cmd);
    }

    protected final byte[] getDPL700_buffer() {
        return gpsRxTx.getDPL700_buffer();
    }

    protected final int getDPL700_buffer_idx() {
        return gpsRxTx.getDPL700_buffer_idx();
    }

    protected final boolean isEraseOngoing() {
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
