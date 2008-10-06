//********************************************************************
//***                           BT 747                             ***
//***                      April 14, 2007                          ***
//***                  (c)2007 Mario De Weerd                      ***
//***                     m.deweerd@ieee.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER. See the GNU General Public License  ***
//***  for more details.                                           ***
//***  *********************************************************** ***
package gps;

import gps.convert.Conv;
import gps.log.in.WindowedFile;

import bt747.sys.Convert;
import bt747.sys.File;
import bt747.sys.Generic;

final class MTKLogDownloadHandler {

    private final GPSstate gpsState;
    private int logState = C_LOG_NOLOGGING;

    /** File handle for binary log being downloaded. */
    private File logFile = null;
    private int logNextReqAddr;
    private int logNextReadAddr;
    private int logRequestStep;

    public boolean loggingIsActiveBeforeDownload = false;

    // Fields to keep track of logging status
    private int logDownloadStartAddr;

    private int logDownloadEndAddr;

    private int logRequestAhead = 0;

    private byte[] expectedResult;

    public boolean forcedErase = false;

    private boolean getFullLogBlocks = true; // If true, get the entire log

    /**
     * Currently selected file path for download.
     */
    private String logFileName = "";
    /** Card (for Palm) of binary log file. Defaults to last card in device. */
    private int logFileCard = -1;

    // States for log reception state machine.
    private static final int C_LOG_NOLOGGING = 0;

    private static final int C_LOG_CHECK = 1;

    private static final int C_LOG_ACTIVE = 2;

    private static final int C_LOG_RECOVER = 3;

    private static final int C_LOG_ERASE_STATE = 4;

    /** Timeout between log status requests for erase. */
    private static final int C_LOGERASE_TIMEOUT = 2000;

    public MTKLogDownloadHandler(GPSstate gpsState) {
        this.gpsState = gpsState;
    }

    /**
     * @param startAddr
     * @param endAddr
     * @param requestStep
     * @param fileName
     */
    public final void getLogInit(final int startAddr, final int endAddr,
            final int requestStep, final String fileName, final int card,
            final boolean isIncremental // True if incremental read
    ) {
        try {
            if (logState == C_LOG_NOLOGGING) {
                // Disable device logging while downloading
                loggingIsActiveBeforeDownload = gpsState.isLoggingActive();
                gpsState.stopLog();
                gpsState.reqLogOnOffStatus();
            }

            logDownloadStartAddr = startAddr;
            logDownloadEndAddr = ((endAddr + 0xFFFF) & 0xFFFF0000) - 1;
            logNextReqAddr = logDownloadStartAddr;
            logNextReadAddr = logDownloadStartAddr;
            logRequestStep = requestStep;
            if (logRequestStep > 0x800) {
                usedLogRequestAhead = 0;
            } else {
                usedLogRequestAhead = logRequestAhead;
            }

            if (isIncremental && (new File(fileName)).exists()) {
                // reOpenLogRead(fileName, card);
                closeLog();
                WindowedFile windowedLogFile = new WindowedFile(fileName,
                        File.READ_ONLY, card);
                logFileName = fileName;
                logFileCard = card;
                windowedLogFile.setBufferSize(0x200);
                if (windowedLogFile != null && windowedLogFile.isOpen()) {
                    // There is a file with data.
                    if (windowedLogFile.getSize() >= (C_BLOCKVERIF_START + C_BLOCKVERIF_SIZE)) {
                        // There are enough bytes in the saved file.

                        // Find first incomplete block
                        int blockHeadPos = 0;
                        boolean continueLoop;
                        do {
                            byte[] bytes;
                            bytes = windowedLogFile.fillBuffer(blockHeadPos);
                            continueLoop = (windowedLogFile.getBufferFill() >= 2);
                            if (continueLoop) {
                                // Break the loop if this block was incomplete.
                                continueLoop = !(((bytes[0] & 0xFF) == 0xFF) && ((bytes[1] & 0xFF) == 0xFF));
                            }
                            if (continueLoop) {
                                // This block is fully filled
                                blockHeadPos += 0x10000;
                                continueLoop = (blockHeadPos <= (windowedLogFile
                                        .getSize() & 0xFFFF0000));
                            }
                        } while (continueLoop);

                        if (blockHeadPos > windowedLogFile.getSize()) {
                            // All blocks already had data - continue from end
                            // of
                            // file.
                            logNextReadAddr = windowedLogFile.getSize();
                            logNextReqAddr = logNextReadAddr;
                        } else {
                            // Start just past block header
                            logNextReadAddr = blockHeadPos + 0x200;
                            continueLoop = true;
                            do {
                                // Find a block
                                byte[] rBuffer = windowedLogFile
                                        .fillBuffer(logNextReadAddr);
                                continueLoop = (windowedLogFile.getBufferFill() >= 0x200);

                                if (continueLoop) {
                                    // Check if all FFs in the file.
                                    for (int i = 0; continueLoop && (i < 0x200); i++) {
                                        continueLoop = ((rBuffer[i] & 0xFF) == 0xFF);
                                    }
                                    continueLoop = !continueLoop; // Continue
                                    // if
                                    // something else
                                    // than 0xFF
                                    // found.
                                    if (continueLoop) {
                                        logNextReadAddr += 0x200;
                                    }
                                }
                            } while (continueLoop);
                            logNextReadAddr -= 0x200;
                            logNextReqAddr = logNextReadAddr;

                            // TODO: should read 2 bytes in header once rest of
                            // block was loaded
                            // in order to have precise header information
                            // -> We can not load this value from memory know as
                            // we
                            // might
                            // corrupt the data (0xFFFF present if restarting
                            // download)
                        }

                        expectedResult = new byte[C_BLOCKVERIF_SIZE];
                        byte[] b;
                        b = windowedLogFile.fillBuffer(C_BLOCKVERIF_START);
                        for (int i = 0; i < expectedResult.length; i++) {
                            expectedResult[i] = b[i];
                        }
                        requestCheckBlock();
                        logState = C_LOG_CHECK;
                    }
                }
                gpsState.updateIgnoreNMEA();
                windowedLogFile.close();
            }
            if (!(logState == C_LOG_CHECK)) {
                // File could not be opened or is not incremental.
                openNewLog(fileName, card);
                logState = C_LOG_ACTIVE;
            }
            if (logState != C_LOG_NOLOGGING) {
                gpsState.postEvent(GpsEvent.LOG_DOWNLOAD_STARTED);
            }
        } catch (Exception e) {
            Generic.debug("getLogInit", e);
        }
    }

    protected void openNewLog(final String fileName, final int card) {
        try {
            if (logFile != null && logFile.isOpen()) {
                logFile.close();
            }

            logFile = new File(fileName, bt747.sys.File.DONT_OPEN, card);
            logFileName = fileName;
            logFileCard = card;
            if (logFile.exists()) {
                logFile.delete();
            }

            logFile = new File(fileName, bt747.sys.File.CREATE, card);
            // lastError 10530 = Read only
            logFileName = fileName;
            logFileCard = card;
            logFile.close();
            logFile = new File(fileName, bt747.sys.File.WRITE_ONLY, card);
            logFileName = fileName;
            logFileCard = card;

            if ((logFile == null) || !(logFile.isOpen())) {
                gpsState.postGpsEvent(GpsEvent.COULD_NOT_OPEN_FILE, fileName);
            }
        } catch (Exception e) {
            Generic.debug("openNewLog", e);
        }
    }

    private void reOpenLogWrite(final String fileName, final int card) {
        closeLog();
        try {
            logFile = new File(fileName, File.WRITE_ONLY, card);
            logFileName = fileName;
            logFileCard = card;
        } catch (Exception e) {
            Generic.debug("reOpenLogWrite", e);
        }
    }

    // Called regularly
    private void getNextLogPart() {
        if (logState != C_LOG_NOLOGGING) {
            int z_Step;

            z_Step = logDownloadEndAddr - logNextReqAddr + 1;

            switch (logState) {
            case C_LOG_ACTIVE:
                if (logDownloadEndAddr <= logNextReadAddr) {
                    // Log is completely downloaded
                    endGetLog();
                }
                if (logNextReqAddr > logNextReadAddr + logRequestStep
                        * usedLogRequestAhead) {
                    z_Step = 0;
                }
                break;
            case C_LOG_RECOVER:
                if (logDownloadEndAddr <= logNextReadAddr) {
                    // Log is completely downloaded
                    endGetLog();
                }
                if (logNextReqAddr > logNextReadAddr) {
                    z_Step = 0;
                } else if (z_Step > 0x800) {
                    z_Step = 0x800;
                }
                break;
            default:
                z_Step = 0;
            }

            if (z_Step > 0) {
                if (z_Step > logRequestStep) {
                    z_Step = logRequestStep;
                }
                readLog(logNextReqAddr, z_Step);
                logNextReqAddr += z_Step;
                if (logState == C_LOG_ACTIVE) {
                    getNextLogPart(); // Recursive to get requests 'ahead'
                }
            }
        }
    }

    // Called when no outstanding requests
    private void getLogPartNoOutstandingRequests() {
        switch (logState) {
        case C_LOG_ACTIVE:
        case C_LOG_RECOVER:
            logNextReqAddr = logNextReadAddr; // Recover from timeout.
            getNextLogPart();
            break;
        case C_LOG_CHECK:
            requestCheckBlock();
        default:
            break;
        }
    }

    private void recoverFromLogError() {
        // logNextReqAddr = logNextReadAddr;
        logState = C_LOG_RECOVER; // recover through timeout.
    }

    protected void analyzeLogPart(final int startAddr, final String sData) {
        int dataLength;
        dataLength = Conv.hexStringToBytes(sData, readDataBuffer) / 2; // Fills
        // m_data
        // debugMsg("Got "+p_StartAddr+" "+Convert.toString(p_Data.length())+"):
        // "+Convert.toString(dataLength));
        switch (logState) {
        case C_LOG_ACTIVE:
        case C_LOG_RECOVER:
            if (logNextReadAddr == startAddr) {
                logState = C_LOG_ACTIVE;
                int j = 0;

                // The Palm platform showed problems writing 0x800 blocks.
                // This splits it in smaller blocks and solves that problem.
                if (dataLength != 0x800 && dataLength != logRequestStep
                        && ((logNextReadAddr + dataLength) != logNextReqAddr)) {
                    // Received data is not the right size - transmission error.
                    // Can happen on Palm over BT.
                    logState = C_LOG_RECOVER;
                } else {
                    // Data seems ok
                    for (int i = dataLength; i > 0; i -= C_MAX_FILEBLOCK_WRITE) {
                        int l = i;
                        if (l > C_MAX_FILEBLOCK_WRITE) {
                            l = C_MAX_FILEBLOCK_WRITE;
                        }
                        // debugMsg("Writing("+Convert.toString(p_StartAddr)+"):
                        // "+Convert.toString(j)+" "+Convert.toString(l));

                        try {
                            if ((logFile.writeBytes(readDataBuffer, j, l)) != l) {
                                // debugMsg("Problem during anaLog:
                                // "+Convert.toString(m_logFile.lastError));
                                cancelGetLog();
                                // debugMsg(Convert.toString(q));
                            }
                        } catch (Exception e) {
                            Generic.debug("analyzeLogPart", e);

                            cancelGetLog();
                        }
                        j += l;
                    }
                    logNextReadAddr += dataLength;
                    // m_ProgressBar.repaintNow();
                    if (getFullLogBlocks
                            && (((startAddr - 1 + dataLength) & 0xFFFF0000) >= startAddr)) {
                        // Block boundery (0xX0000) is inside data.
                        int blockStart = 0xFFFF & (0x10000 - (startAddr & 0xFFFF));
                        if (!(((readDataBuffer[blockStart] & 0xFF) == 0xFF) && ((readDataBuffer[blockStart + 1] & 0xFF) == 0xFF))) {
                            // This block is full, next block is still data
                            int minEndAddr;
                            minEndAddr = (startAddr & 0xFFFF0000) + 0x20000 - 1; // This
                            // block
                            // and
                            // next
                            // one.
                            if (minEndAddr > gpsState.getLogMemSize() - 1) {
                                minEndAddr = gpsState.getLogMemSize() - 1;
                            }
                            if (minEndAddr > logDownloadEndAddr) {
                                logDownloadEndAddr = minEndAddr;
                            }
                        }
                    }
                }
                if (logNextReadAddr > logDownloadEndAddr) {
                    gpsState.postEvent(GpsEvent.LOG_DOWNLOAD_SUCCESS);
                    endGetLog();
                } else {
                    getNextLogPart();
                }
            } else {
                Generic.debug("Expected:"
                        + Convert.unsigned2hex(logNextReadAddr, 8) + " Got:"
                        + Convert.unsigned2hex(startAddr, 8), null);
                recoverFromLogError();
            }
            break;
        case C_LOG_CHECK:
            logState = C_LOG_NOLOGGING; // Default.
            if ((startAddr == C_BLOCKVERIF_START)
                    && (dataLength == C_BLOCKVERIF_SIZE)) {
                // The block we got should be the block to check
                // byte[] dataBuffer = new byte[dataLength];
                boolean success;
                success = true;
                for (int i = dataLength - 1; i >= 0; i--) {
                    if (readDataBuffer[i] != expectedResult[i]) {
                        // The log is not the same, data is different
                        success = false;
                        break; // Exit from the loop
                    }
                }

                if (success) {
                    // Downloaded data seems to correspond - start incremental
                    // download
                    reOpenLogWrite(logFileName, logFileCard);
                    try {
                        logFile.setPos(logNextReadAddr);
                    } catch (Exception e) {
                        Generic.debug("C_LOG_CHECK", e);
                    }
                    getNextLogPart();
                    logState = C_LOG_ACTIVE;
                } else {
                    logState = C_LOG_DATA_NOT_SAME_WAITING_FOR_REPLY;
                    gpsState
                            .postEvent(GpsEvent.DOWNLOAD_DATA_NOT_SAME_NEEDS_REPLY);
                }
            }
            break;
        default:
            break;
        } // Switch m_logState
        gpsState.postEvent(GpsEvent.DOWNLOAD_STATE_CHANGE);
    }

    public final void replyToOkToOverwrite(final boolean overwrite) {
        if (logState == C_LOG_DATA_NOT_SAME_WAITING_FOR_REPLY) {
            if (overwrite) {
                openNewLog(logFileName, logFileCard);
                logNextReadAddr = 0;
                logNextReadAddr = 0;
                logState = C_LOG_ACTIVE;
            } else {
                endGetLog();
            }
        }
    }

    /**
     * Waiting for a reply from the application concerning the authorisation to
     * overwrite data that is not the same.
     */
    private static final int C_LOG_DATA_NOT_SAME_WAITING_FOR_REPLY = 5;
    private int usedLogRequestAhead = 0;

    /**
     * Start of block position to verify if log in device corresponds to log in
     * file.
     */
    private static final int C_BLOCKVERIF_START = 0x200;

    /** Size of block to validate that log in device is log in file. */
    private static final int C_BLOCKVERIF_SIZE = 0x200;

    private static final int C_MAX_FILEBLOCK_WRITE = 0x800;

    private final byte[] readDataBuffer = new byte[0x800]; // buffer used for

    // reading data.

    /**
     * Request the block to validate that log in device is log in file.
     */
    private void requestCheckBlock() {
        readLog(C_BLOCKVERIF_START, C_BLOCKVERIF_SIZE); // Read 200 bytes, just
        // past header.
    }

    private void closeLog() {
        try {
            if (logFile != null) {
                if (logFile.isOpen()) {
                    logFile.close();
                    logFile = null;
                }
            }
        } catch (Exception e) {
            Generic.debug("CloseLog", e);
        }
    }

    private void endGetLog() {
        logState = C_LOG_NOLOGGING;
        closeLog();

        if (loggingIsActiveBeforeDownload) {
            gpsState.startLog();
            gpsState.reqLogOnOffStatus();
        }
        gpsState.postEvent(GpsEvent.LOG_DOWNLOAD_DONE);
    }

    protected final boolean isLogDownloadOnGoing() {
        return (logState != C_LOG_NOLOGGING) && (logState != C_LOG_ERASE_STATE);
    }

    public final int getStartAddr() {
        return logDownloadStartAddr;
    }

    /**
     * @return the endAddr
     */
    public final int getEndAddr() {
        return logDownloadEndAddr;
    }

    public final int getNextReadAddr() {
        return logNextReadAddr;
    }

    public final void cancelGetLog() {
        endGetLog();
    }

    /**
     * erase the log - takes a while.<br>
     * TODO: Find out a way to follow up on erasal (status) (check response on
     * cmd)
     */

    public final void eraseLog() {
        if (gpsState.isConnected()) {
            gpsState.sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                    + BT747Constants.PMTK_LOG_ERASE + ","
                    + BT747Constants.PMTK_LOG_ERASE_YES_STR);
            waitEraseDone();
        }
    }

    public final void recoveryEraseLog() {
        // Get some information (when debug mode active)
        gpsState.stopLog(); // Stop logging for this operation
        gpsState.reqLogStatus(); // Check status
        gpsState.reqLogFlashSectorStatus(); // Get flash sector information from
                                            // device
        // TODO:
        gpsState.sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_ENABLE);

        gpsState.reqLogStatus(); // Check status

        forcedErase = true;
        eraseLog();

    }

    private void postRecoveryEraseLog() {
        gpsState.reqLogStatus();
        gpsState.reqLogFlashSectorStatus(); // Get flash sector information from
                                            // device

        gpsState.sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_INIT);

        gpsState.reqLogFlashSectorStatus(); // Get flash sector information from
                                            // device
        gpsState.reqLogStatus();
    }

    private void waitEraseDone() {
        gpsState.postEvent(GpsEvent.ERASE_ONGOING_NEED_POPUP);
        gpsState.setEraseOngoing(true);
        logState = C_LOG_ERASE_STATE;
        gpsState.resetLogTimeOut();
        // readLogFlashStatus(); - Will be done after timeout
    }

    private void signalEraseDone() {
        logState = C_LOG_NOLOGGING;
        gpsState.setEraseOngoing(false);
        gpsState.postEvent(GpsEvent.ERASE_DONE_REMOVE_POPUP);
    }

    public final void stopErase() {
        if (gpsState.isEraseOngoing() && (logState == C_LOG_ERASE_STATE)) {
            gpsState.updateIgnoreNMEA();
            signalEraseDone();
        }
    }

    /**
     * Called from within run of GPSstate (regurarly called).
     */
    protected void notifyRun() {
        if ((gpsState.getOutStandingCmdsCount() == 0)
                && (logState != C_LOG_NOLOGGING)
                && (logState != C_LOG_ERASE_STATE)) {
            // Sending command on next timer adds some delay after
            // the end of the previous command (reception)
            getLogPartNoOutstandingRequests();
        } else if (logState == C_LOG_ACTIVE) {
            getNextLogPart();
        } else if (logState == C_LOG_ERASE_STATE) {
            if (gpsState.timeSinceLastStamp() > C_LOGERASE_TIMEOUT) {
                gpsState.reqLogFlashStatus();
            }
        }
    }

    protected void notifyDisconnected() {
        if (logState != C_LOG_NOLOGGING) {
            endGetLog();
        }
    }

    protected void handleLogFlashStatReply(final String s) {
        if (logState == C_LOG_ERASE_STATE) {
            switch (Convert.toInt(s)) {
            case 1:
                if (gpsState.isEraseOngoing()) {
                    signalEraseDone();
                }
                if (forcedErase) {
                    forcedErase = false;
                    postRecoveryEraseLog();
                }
                break;
            default:
                break;
            }
        }
    }

    // public static final int PMTK_LOG_ENABLE = 10;
    // public static final int PMTK_LOG_DISABLE = 11;

    /**
     * A single request to get information from the device's log.
     * 
     * @param startAddr
     *            start address of the data range requested
     * @param size
     *            size of the data range requested
     */
    public final void readLog(final int startAddr, final int size) {
        gpsState.sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_REQ_DATA_STR + ","
                + Convert.unsigned2hex(startAddr, 8) + ","
                + Convert.unsigned2hex(size, 8));
    }

    protected final void setLogRequestAhead(final int logRequestAhead) {
        this.logRequestAhead = logRequestAhead;
    }

    /**
     * Temporary for DPL700
     */
    protected final File getLogFile() {
        return logFile;
    }

}
