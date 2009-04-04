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
package gps;


import gps.convert.Conv;
import gps.log.in.WindowedFile;

import bt747.sys.File;
import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;

final class MTKLogDownloadHandler {

    private final GPSstate gpsState;
    private int logState = MTKLogDownloadHandler.C_LOG_NOLOGGING;

    /** File handle for binary log being downloaded. */
    private File logFile = null;
    private int logNextReqAddr;
    private int logNextReadAddr;
    private int logRequestStep;

    private boolean loggingIsActiveBeforeDownload = false;

    // Fields to keep track of logging status
    private int logDownloadStartAddr;

    private int logDownloadEndAddr;

    private int logRequestAhead = 0;

    private byte[] expectedResult;

    private boolean forcedErase = false;

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

    /**
     * The log download must start, but we are waiting until all commands are
     * sent.
     */
    private static final int C_LOG_START = 5;

    /**
     * Waiting for a reply from the application concerning the authorisation
     * to overwrite data that is not the same.
     */
    private static final int C_LOG_DATA_NOT_SAME_WAITING_FOR_REPLY = 6;

    /** Timeout between log status requests for erase. */
    private static final int C_LOGERASE_TIMEOUT = 2000;

    protected MTKLogDownloadHandler(final GPSstate state) {
        gpsState = state;
    }

    private boolean incremental = true;

    /**
     * Initialise the log download.
     * 
     * @param startAddr
     *                Start address for the log download.
     * @param endAddr
     *                End address for the log download.
     * @param requestStep
     *                Size of data to download with each request (chunk size).
     * @param fileName
     *                The filename to save to.
     * @param isIncremental
     *                When true, perform incremental read.
     * @param disableLogging
     *                Disable logging during download when true.
     */
    protected final void getLogInit(final int startAddr, final int endAddr,
            final int requestStep, final String fileName, final int card,
            final boolean isIncremental, final boolean disableLogging) {
        if (logState == MTKLogDownloadHandler.C_LOG_NOLOGGING) {
            // Disable device logging while downloading to improve
            // performance.
            loggingIsActiveBeforeDownload = gpsState.isLoggingActive();
            if (disableLogging && loggingIsActiveBeforeDownload) {
                gpsState.stopLog();
                gpsState.reqLogOnOffStatus();
            }
        }
        gpsState.postEvent(GpsEvent.LOG_DOWNLOAD_STARTED);

        if (Generic.isDebug()) {
            Generic.debug((isIncremental ? "Incremental d" : "D")
                    + "ownload request from "
                    + JavaLibBridge.unsigned2hex(startAddr, 8) + " to "
                    + JavaLibBridge.unsigned2hex(endAddr, 8));
        }

        // Start address
        logDownloadStartAddr = startAddr;
        // Round end address to end of block
        logDownloadEndAddr = ((endAddr + 0xFFFF) & 0xFFFF0000) - 1;
        // Next address to request from device is start address.
        logNextReqAddr = logDownloadStartAddr;
        // Next address expected from device is start address.
        logNextReadAddr = logDownloadStartAddr;
        // The size of each individual request.
        logRequestStep = requestStep;

        if (logRequestStep > 0x800) {
            // Not requesting anything ahead of time is individual size is
            // big.
            usedLogRequestAhead = 0;
        } else {
            // The request pipeline is as set by the user.
            usedLogRequestAhead = logRequestAhead;
        }

        logFileName = fileName;
        logFileCard = card;

        incremental = isIncremental;

        logState = MTKLogDownloadHandler.C_LOG_START;
    }

    private final void realDownloadStart() {
        try {
            if (incremental && (new File(logFileName)).exists()) {
                /**
                 * File already exists and incremental download requested.
                 * Checking if the content is the same.
                 */
                // Make sure the log is closed from any previous download
                // handling.
                closeLog();

                // Storing the file specifics - needed in subsequent opens.
                // Opening the existing log file in read only mode.
                final WindowedFile windowedLogFile = new WindowedFile(
                        logFileName, File.READ_ONLY, logFileCard);
                windowedLogFile.setBufferSize(0x200);
                if ((windowedLogFile != null) && windowedLogFile.isOpen()) {
                    // There is a file with data.
                    if (windowedLogFile.getSize() >= (MTKLogDownloadHandler.C_BLOCKVERIF_START + MTKLogDownloadHandler.C_BLOCKVERIF_SIZE)) {
                        // There are enough bytes in the saved file.

                        int blockHeadPos = 0;
                        boolean continueLoop;
                        // Skip blocks that look complete
                        do {
                            byte[] bytes;
                            bytes = windowedLogFile.fillBuffer(blockHeadPos);
                            // We need at least the first 2 bytes of the
                            // header.
                            continueLoop = (windowedLogFile.getBufferFill() >= 2);
                            if (continueLoop) {
                                // If the first two bytes are FFFF, the block
                                // is
                                // possibly incomplete.
                                continueLoop = !((bytes[0] == (byte) 0xFF) && (bytes[1] == (byte) 0xFF));
                            }
                            if (continueLoop) {
                                // This block is fully filled
                                blockHeadPos += 0x10000;
                                continueLoop = (blockHeadPos <= (windowedLogFile
                                        .getSize() & 0xFFFF0000));
                            }
                        } while (continueLoop);

                        if (blockHeadPos > windowedLogFile.getSize()) {
                            // The start of the next block that is potentially
                            // not complete is past the file end.
                            // Therefore, start from the end of the existing
                            // file for the download.
                            logNextReadAddr = windowedLogFile.getSize();
                            logNextReqAddr = logNextReadAddr;
                        } else {
                            // This block is still in the file.
                            // Look for the first location with FFFFFF
                            // Start just past block header
                            logNextReadAddr = blockHeadPos + 0x200;
                            continueLoop = true;
                            // Find a block
                            do {
                                final byte[] rBuffer = windowedLogFile
                                        .fillBuffer(logNextReadAddr);
                                continueLoop = (windowedLogFile
                                        .getBufferFill() >= 0x200);

                                if (continueLoop) {
                                    // Check if all FFs in the file.
                                    for (int i = 0; continueLoop
                                            && (i < 0x200); i++) {
                                        continueLoop = (rBuffer[i] == (byte) 0xFF);
                                    }
                                    continueLoop = !continueLoop; // Continue
                                    // 'continueLoop' now true when data was
                                    // not
                                    // all FF.
                                    if (continueLoop) {
                                        logNextReadAddr += 0x200;
                                    }
                                }
                            } while (continueLoop);
                            // logNextReadAddr points just past FF data.
                            // Make sure it points to valid data.
                            logNextReadAddr -= 0x200;
                            logNextReqAddr = logNextReadAddr;

                            // TODO: should read 2 bytes in header once rest
                            // of
                            // block was loaded in order to have precise
                            // header
                            // information
                            // -> We can not load this value from memory know
                            // as
                            // we might corrupt the data (0xFFFF present if
                            // restarting download)
                        }

                        // Adjust the end address.
                        final int potentialEndAddress = ((logNextReadAddr + 0xFFFF) & 0xFFFF0000) - 1;
                        if (potentialEndAddress > logDownloadEndAddr) {
                            if (Generic.isDebug()) {
                                Generic.debug("Adjusted end address from "
                                        + JavaLibBridge.unsigned2hex(
                                                logDownloadEndAddr, 8)
                                        + " to "
                                        + JavaLibBridge.unsigned2hex(
                                                potentialEndAddress, 8));
                            }
                            logDownloadEndAddr = potentialEndAddress;
                        }

                        expectedResult = new byte[MTKLogDownloadHandler.C_BLOCKVERIF_SIZE];
                        byte[] b;
                        b = windowedLogFile
                                .fillBuffer(MTKLogDownloadHandler.C_BLOCKVERIF_START);
                        for (int i = expectedResult.length - 1; i >= 0; i--) {
                            expectedResult[i] = b[i];
                        }
                        logState = MTKLogDownloadHandler.C_LOG_CHECK;
                        gpsState.resetLogTimeOut();
                        requestCheckBlock();
                    }
                }
                gpsState.updateIgnoreNMEA();
                windowedLogFile.close();
            }
            if (!(logState == MTKLogDownloadHandler.C_LOG_CHECK)) {
                // File could not be opened or is not incremental.
                openNewLog(logFileName, logFileCard);
                if (Generic.isDebug()) {
                    Generic.debug("Starting download from "
                            + JavaLibBridge.unsigned2hex(logNextReadAddr, 8)
                            + " to "
                            + JavaLibBridge.unsigned2hex(logDownloadEndAddr, 8));
                }
                logState = MTKLogDownloadHandler.C_LOG_ACTIVE;
            }
            if (logState == MTKLogDownloadHandler.C_LOG_NOLOGGING) {
                gpsState.postEvent(GpsEvent.LOG_DOWNLOAD_DONE);
            }
        } catch (final Exception e) {
            Generic.debug("getLogInit", e);
        }
    }

    protected final void openNewLog(final String fileName, final int card) {
        try {
            if ((logFile != null) && logFile.isOpen()) {
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
        } catch (final Exception e) {
            Generic.debug("openNewLog", e);
        }
    }

    private void reOpenLogWrite(final String fileName, final int card) {
        closeLog();
        try {
            logFile = new File(fileName, File.WRITE_ONLY, card);
            logFileName = fileName;
            logFileCard = card;
        } catch (final Exception e) {
            Generic.debug("reOpenLogWrite", e);
        }
    }

    // Called regularly
    private void getNextLogPart() {
        if (logState != MTKLogDownloadHandler.C_LOG_NOLOGGING) {
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
                final int stepUntilBoundary = ((logNextReqAddr + 0x10000) & ~0xFFFF)
                        - logNextReqAddr;
                if (z_Step > stepUntilBoundary) {
                    // Avoid crossing 0x10000 boundaries that seem to result
                    // in download trouble on some devices.
                    z_Step = stepUntilBoundary;
                }
                readLog(logNextReqAddr, z_Step);
                logNextReqAddr += z_Step;
                if (logState == MTKLogDownloadHandler.C_LOG_ACTIVE) {
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
            break;
        case C_LOG_START:
            realDownloadStart();
            break;
        default:
            break;
        }
    }

    private void recoverFromLogError() {
        // TODO: Make sure that is not called in C_LOG_CHECK mode.
        // logNextReqAddr = logNextReadAddr;
        logState = MTKLogDownloadHandler.C_LOG_RECOVER; // recover through
        // timeout.
    }

    protected final void analyzeLogPart(final int startAddr,
            final String sData) {
        int dataLength;
        // Convert hex data to bytes
        dataLength = Conv.hexStringToBytes(sData, readDataBuffer) / 2;
        // debugMsg("Got "+startAddr+" "+JavaLibBridge.toString(sData.length())+"):
        // "+JavaLibBridge.toString(dataLength));
        switch (logState) {
        case C_LOG_ACTIVE:
        case C_LOG_RECOVER:
            if (logNextReadAddr == startAddr) {
                logState = MTKLogDownloadHandler.C_LOG_ACTIVE;
                int j = 0;

                // The Palm platform showed problems writing 0x800 blocks.
                // This splits it in smaller blocks and solves that problem.
                if ((dataLength != 0x800)
                        && (dataLength != logRequestStep)
                        && ((logNextReadAddr + dataLength) != logNextReqAddr)
                        // Datalength is up to 0x10000 boundary
                        && (dataLength != ((logNextReadAddr + 0x10000) & ~0xFFFF)
                                - logNextReadAddr)) {
                    // Received data is not the right size - transmission
                    // error.
                    // Can happen on Palm over BT.
                    if (Generic.isDebug()) {
                        Generic.debug("Unexpected datalength: "
                                + JavaLibBridge.unsigned2hex(dataLength, 8));
                    }
                    logState = MTKLogDownloadHandler.C_LOG_RECOVER;
                } else {
                    // Data seems ok
                    for (int i = dataLength; i > 0; i -= MTKLogDownloadHandler.C_MAX_FILEBLOCK_WRITE) {
                        int l = i;
                        if (l > MTKLogDownloadHandler.C_MAX_FILEBLOCK_WRITE) {
                            l = MTKLogDownloadHandler.C_MAX_FILEBLOCK_WRITE;
                        }
                        // debugMsg("Writing("+JavaLibBridge.toString(p_StartAddr)+"):
                        // "+JavaLibBridge.toString(j)+" "+JavaLibBridge.toString(l));

                        try {
                            if ((logFile.writeBytes(readDataBuffer, j, l)) != l) {
                                // debugMsg("Problem during anaLog:
                                // "+JavaLibBridge.toString(m_logFile.lastError));
                                cancelGetLog();
                                // debugMsg(JavaLibBridge.toString(q));
                            }
                        } catch (final Exception e) {
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
                        final int blockStart = 0xFFFF & (0x10000 - (startAddr & 0xFFFF));
                        if (!(((readDataBuffer[blockStart] & 0xFF) == 0xFF) && ((readDataBuffer[blockStart + 1] & 0xFF) == 0xFF))) {
                            // This block is full, next block is still data
                            int minEndAddr;
                            // This block and next one.
                            minEndAddr = (startAddr & 0xFFFF0000) + 0x20000 - 1;
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
                        + JavaLibBridge.unsigned2hex(logNextReadAddr, 8) + " Got:"
                        + JavaLibBridge.unsigned2hex(startAddr, 8) + " ("
                        + JavaLibBridge.unsigned2hex(dataLength, 8) + ")", null);
                recoverFromLogError();
            }
            break;
        case C_LOG_CHECK:
            logState = MTKLogDownloadHandler.C_LOG_NOLOGGING; // Default.
            if ((startAddr == MTKLogDownloadHandler.C_BLOCKVERIF_START)
                    && (dataLength == MTKLogDownloadHandler.C_BLOCKVERIF_SIZE)) {
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
                    } catch (final Exception e) {
                        Generic.debug("C_LOG_CHECK", e);
                    }
                    if (Generic.isDebug()) {
                        Generic
                                .debug("Starting incremental download from "
                                        + JavaLibBridge.unsigned2hex(
                                                logNextReadAddr, 8)
                                        + " to "
                                        + JavaLibBridge.unsigned2hex(
                                                logDownloadEndAddr, 8));
                    }
                    logState = MTKLogDownloadHandler.C_LOG_ACTIVE;
                    getNextLogPart();
                } else {
                    logState = MTKLogDownloadHandler.C_LOG_DATA_NOT_SAME_WAITING_FOR_REPLY;
                    if (Generic.isDebug()) {
                        Generic
                                .debug("Different data - requesting overwrite confirmation");
                    }
                    gpsState
                            .postEvent(GpsEvent.DOWNLOAD_DATA_NOT_SAME_NEEDS_REPLY);
                }
            } else {
                if (Generic.isDebug()) {
                    Generic
                            .debug(
                                    "Expected:"
                                            + JavaLibBridge
                                                    .unsigned2hex(
                                                            MTKLogDownloadHandler.C_BLOCKVERIF_START,
                                                            8)
                                            + " Got:"
                                            + JavaLibBridge.unsigned2hex(startAddr,
                                                    8)
                                            + " ("
                                            + JavaLibBridge.unsigned2hex(
                                                    dataLength, 8) + ")",
                                    null);
                }
                logState = MTKLogDownloadHandler.C_LOG_CHECK;
                // recoverFromLogError();
            }
            break;
        default:
            break;
        } // Switch m_logState
        gpsState.postEvent(GpsEvent.DOWNLOAD_STATE_CHANGE);
    }

    /**
     * Response from the application indicating if overwrite of log is ok nor
     * not.
     * 
     * @param overwrite
     */
    protected final void replyToOkToOverwrite(final boolean overwrite) {
        if (logState == MTKLogDownloadHandler.C_LOG_DATA_NOT_SAME_WAITING_FOR_REPLY) {
            if (overwrite) {
                openNewLog(logFileName, logFileCard);
                logNextReadAddr = 0;
                logNextReadAddr = 0;
                logState = MTKLogDownloadHandler.C_LOG_ACTIVE;
            } else {
                endGetLog();
            }
        }
    }

    private int usedLogRequestAhead = 0;

    /**
     * Start of block position to verify if log in device corresponds to log
     * in file.
     */
    private static final int C_BLOCKVERIF_START = 0x200;

    /** Size of block to validate that log in device is log in file. */
    private static final int C_BLOCKVERIF_SIZE = 0x200;

    private static final int C_MAX_FILEBLOCK_WRITE = 0x800;

    /** buffer used for reading data. */
    private final byte[] readDataBuffer = new byte[0x800];

    /**
     * Request the block to validate that log in device is log in file.
     */
    private void requestCheckBlock() {
        // Read 200 bytes, just past header.
        readLog(MTKLogDownloadHandler.C_BLOCKVERIF_START,
                MTKLogDownloadHandler.C_BLOCKVERIF_SIZE);
    }

    private void closeLog() {
        try {
            if (logFile != null) {
                if (logFile.isOpen()) {
                    logFile.close();
                    logFile = null;
                }
            }
        } catch (final Exception e) {
            Generic.debug("CloseLog", e);
        }
    }

    private void endGetLog() {
        logState = MTKLogDownloadHandler.C_LOG_NOLOGGING;
        closeLog();

        if (loggingIsActiveBeforeDownload) {
            gpsState.startLog();
            gpsState.reqLogOnOffStatus();
        }
        gpsState.postEvent(GpsEvent.LOG_DOWNLOAD_DONE);
    }

    protected final boolean isLogDownloadOnGoing() {
        return (logState != MTKLogDownloadHandler.C_LOG_NOLOGGING)
                && (logState != MTKLogDownloadHandler.C_LOG_ERASE_STATE);
    }

    protected final int getStartAddr() {
        return logDownloadStartAddr;
    }

    /**
     * @return the endAddr
     */
    protected final int getEndAddr() {
        return logDownloadEndAddr;
    }

    protected final int getNextReadAddr() {
        return logNextReadAddr;
    }

    protected final void cancelGetLog() {
        endGetLog();
    }

    /**
     * erase the log - takes a while.<br>
     * TODO: Find out a way to follow up on erasal (status) (check response on
     * cmd)
     */

    protected final void eraseLog() {
        if (gpsState.isConnected()) {
            gpsState.sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                    + BT747Constants.PMTK_LOG_ERASE + ","
                    + BT747Constants.PMTK_LOG_ERASE_YES_STR);
            waitEraseDone();
        }
    }

    protected final void recoveryEraseLog() {
        // Get some information (when debug mode active)
        gpsState.stopLog(); // Stop logging for this operation
        gpsState.reqLogStatus(); // Check status
        gpsState.reqLogFlashSectorStatus(); // Get flash sector information
        // from
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
        gpsState.reqLogFlashSectorStatus(); // Get flash sector information
        // from
        // device

        gpsState.sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_INIT);

        gpsState.reqLogFlashSectorStatus(); // Get flash sector information
        // from
        // device
        gpsState.reqLogStatus();
    }

    private void waitEraseDone() {
        gpsState.setEraseOngoing(true);
        logState = MTKLogDownloadHandler.C_LOG_ERASE_STATE;
        gpsState.resetLogTimeOut();
        gpsState.postEvent(GpsEvent.ERASE_ONGOING_NEED_POPUP);
        // readLogFlashStatus(); - Will be done after timeout
    }

    private void signalEraseDone() {
        logState = MTKLogDownloadHandler.C_LOG_NOLOGGING;
        gpsState.setEraseOngoing(false);
        gpsState.postEvent(GpsEvent.ERASE_DONE_REMOVE_POPUP);
    }

    protected final void stopErase() {
        if (gpsState.isEraseOngoing()
                && (logState == MTKLogDownloadHandler.C_LOG_ERASE_STATE)) {
            gpsState.updateIgnoreNMEA();
            signalEraseDone();
        }
    }

    /**
     * Called from within run of GPSstate (regularly called).
     */
    protected void notifyRun() {
        if ((gpsState.getOutStandingCmdsCount() == 0)
                && (logState != MTKLogDownloadHandler.C_LOG_NOLOGGING)
                && (logState != MTKLogDownloadHandler.C_LOG_ERASE_STATE)) {
            // Sending command on next timer adds some delay after
            // the end of the previous command (reception)
            getLogPartNoOutstandingRequests();
        } else if (logState == MTKLogDownloadHandler.C_LOG_ACTIVE) {
            getNextLogPart();
        } else if (logState == MTKLogDownloadHandler.C_LOG_ERASE_STATE) {
            if (gpsState.timeSinceLastStamp() > MTKLogDownloadHandler.C_LOGERASE_TIMEOUT) {
                gpsState.reqLogFlashStatus();
            }
        }
    }

    protected final void notifyDisconnected() {
        if (logState != MTKLogDownloadHandler.C_LOG_NOLOGGING) {
            endGetLog();
        }
    }

    protected final void handleLogFlashStatReply(final String s) {
        if (logState == MTKLogDownloadHandler.C_LOG_ERASE_STATE) {
            switch (JavaLibBridge.toInt(s)) {
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
     *                start address of the data range requested
     * @param size
     *                size of the data range requested
     */
    protected final void readLog(final int startAddr, final int size) {
        gpsState.sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_Q_LOG + ","
                + JavaLibBridge.unsigned2hex(startAddr, 8) + ","
                + JavaLibBridge.unsigned2hex(size, 8));
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
