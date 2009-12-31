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

import gps.BT747Constants;
import gps.GpsEvent;
import gps.convert.Conv;
import gps.log.in.WindowedFile;
import gps.mvc.commands.GpsLinkExecCommand;
import gps.mvc.commands.GpsLinkNmeaCommand;

import bt747.sys.File;
import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.RAFile;
import bt747.sys.interfaces.BT747Exception;
import bt747.sys.interfaces.BT747Path;

final class MTKLogDownloadHandler {

    private final MTKLogDownloadContext context = new MTKLogDownloadContext();

    /**
     * Data that can be reused across states. [Preparation to implement the
     * State Design Pattern]
     * 
     * @author Mario
     * 
     */
    private final static class MTKLogDownloadContext {
        protected MtkModel mtkM;
        protected MtkController mtkC;

        private int logState = MTKLogDownloadHandler.C_LOG_NOLOGGING;

        /**
         * @param logState
         *            the logState to set
         */
        protected void setLogState(final int logState) {
            this.logState = logState;
            switch (logState) {
            case MTKLogDownloadHandler.C_LOG_NOLOGGING:
            case MTKLogDownloadHandler.C_LOG_ERASE_STATE:
                mtkM.setLogDownloadOngoing(false);
                break;
            default:
                mtkM.setLogDownloadOngoing(true);
            }
        }

        /**
         * @return the logState
         */
        protected int getLogState() {
            return logState;
        }

        private int logDownloadEndAddr;

        /**
         * @param logDownloadEndAddr
         *            the logDownloadEndAddr to set
         */
        protected void setLogDownloadEndAddr(int logDownloadEndAddr) {
            this.logDownloadEndAddr = logDownloadEndAddr;
            mtkM.setEndAddr(logDownloadEndAddr);
        }

        /**
         * @return the logDownloadEndAddr
         */
        protected int getLogDownloadEndAddr() {
            return logDownloadEndAddr;
        }

        private int logNextReadAddr;

        /**
         * @param logNextReadAddr
         *            the logNextReadAddr to set
         */
        protected void setLogNextReadAddr(int logNextReadAddr) {
            this.logNextReadAddr = logNextReadAddr;
            mtkM.setNextReadAddr(logNextReadAddr);
        }

        /**
         * @return the logNextReadAddr
         */
        protected int getLogNextReadAddr() {
            return logNextReadAddr;
        }

        // private final LogFile lf = new LogFile();
        protected RAFile logFile = null;

        /**
         * Currently selected file path for download.
         */
        protected BT747Path logPath = null;

        protected int startAddr; // Parameter
        protected int endAddr; // Parameter

        protected int logNextReqAddr;
        protected int logRequestStep;

        protected boolean isSmart = true;
        protected boolean disableLogging;
        protected boolean loggingIsActiveBeforeDownload = false;

        // Fields to keep track of logging status
        protected int logDownloadStartAddr;

        protected int logRequestAhead = 0;

        protected byte[] expectedResult;

        protected boolean forcedErase = false;

        protected boolean getFullLogBlocks = true; // If true, get the entire
        // log

        protected int usedLogRequestAhead = 0;
        /** buffer used for reading data. */
        protected final byte[] readDataBuffer = new byte[0x800];

    }

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

    protected MTKLogDownloadHandler(final MtkController controller,
            final MtkModel model) {
        context.mtkC = controller;
        context.mtkM = controller.getMtkModel();
    }

    /**
     * Initialise the log download.
     * 
     * @param startAddr
     *            Start address for the log download.
     * @param endAddr
     *            End address for the log download.
     * @param requestStep
     *            Size of data to download with each request (chunk size).
     * @param path
     *            The filename to save to.
     * @param isSmart
     *            When true, perform incremental read.
     * @param disableLogging
     *            Disable logging during download when true.
     */
    protected final void getLogInit(final int startAddr, final int endAddr,
            final int requestStep, final BT747Path path,
            final boolean isSmart, final boolean disableLogging) {
        context.startAddr = startAddr;
        context.endAddr = endAddr;
        // The size of each individual request.
        context.logRequestStep = requestStep;
        context.logPath = path;
        context.isSmart = isSmart;
        context.disableLogging = disableLogging;
        MTKLogDownloadHandler.logInit(context);
    }

    private static final void logInit(final MTKLogDownloadContext context) {
        if (context.getLogState() == MTKLogDownloadHandler.C_LOG_NOLOGGING) {
            // Disable device logging while downloading to improve
            // performance.
            context.loggingIsActiveBeforeDownload = context.mtkM
                    .isLoggingActive();
            if (context.disableLogging
                    && context.loggingIsActiveBeforeDownload) {
                context.mtkC.cmd(MtkController.CMD_STOPLOG);
                context.mtkC.reqData(MtkModel.DATA_LOG_STATUS);
            }
        }
        context.mtkM.postEvent(GpsEvent.LOG_DOWNLOAD_STARTED);

        if (Generic.isDebug()) {
            Generic
                    .debug((context.isSmart ? "Smart d" : "D")
                            + "ownload request from "
                            + JavaLibBridge
                                    .unsigned2hex(context.startAddr, 8)
                            + " to "
                            + JavaLibBridge.unsigned2hex(context.endAddr, 8));
        }

        // Start address
        context.logDownloadStartAddr = context.startAddr;
        // Round end address to end of block
        context
                .setLogDownloadEndAddr(((context.endAddr + 0xFFFF) & 0xFFFF0000) - 1);
        // Next address to request from device is start address.
        context.logNextReqAddr = context.logDownloadStartAddr;
        // Next address expected from device is start address.
        context.setLogNextReadAddr(context.logDownloadStartAddr);

        if (context.logRequestStep > 0x800) {
            // Not requesting anything ahead of time is individual size is
            // big.
            context.usedLogRequestAhead = 0;
        } else {
            // The request pipeline is as set by the user.
            context.usedLogRequestAhead = context.logRequestAhead;
        }

        context.mtkM.getHandler().setLogOrEraseOngoing(true);
        context.setLogState(MTKLogDownloadHandler.C_LOG_START);
    }

    private final void realDownloadStart() throws BT747Exception {
        try {
            if (context.isSmart && (new File(context.logPath)).exists()) {
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
                        context.logPath, File.READ_ONLY);
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
                            context.setLogNextReadAddr(windowedLogFile
                                    .getSize());
                            context.logNextReqAddr = context
                                    .getLogNextReadAddr();
                        } else {
                            // This block is still in the file.
                            // Look for the first location with FFFFFF
                            // Start just past block header
                            context.setLogNextReadAddr(blockHeadPos + 0x200);
                            continueLoop = true;
                            // Find a block
                            do {
                                final byte[] rBuffer = windowedLogFile
                                        .fillBuffer(context
                                                .getLogNextReadAddr());
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
                                        context
                                                .setLogNextReadAddr(context
                                                        .getLogNextReadAddr() + 0x200);
                                    }
                                }
                            } while (continueLoop);
                            // logNextReadAddr points just past FF data.
                            // Make sure it points to valid data.
                            context.setLogNextReadAddr(context
                                    .getLogNextReadAddr() - 0x200);
                            context.logNextReqAddr = context
                                    .getLogNextReadAddr();

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
                        final int potentialEndAddress = ((context
                                .getLogNextReadAddr() + 0xFFFF) & 0xFFFF0000) - 1;
                        if (potentialEndAddress > context
                                .getLogDownloadEndAddr()) {
                            if (Generic.isDebug()) {
                                Generic.debug("Adjusted end address from "
                                        + JavaLibBridge.unsigned2hex(context
                                                .getLogDownloadEndAddr(), 8)
                                        + " to "
                                        + JavaLibBridge.unsigned2hex(
                                                potentialEndAddress, 8));
                            }
                            context
                                    .setLogDownloadEndAddr(potentialEndAddress);
                        }

                        context.expectedResult = new byte[MTKLogDownloadHandler.C_BLOCKVERIF_SIZE];
                        byte[] b;
                        b = windowedLogFile
                                .fillBuffer(MTKLogDownloadHandler.C_BLOCKVERIF_START);
                        for (int i = context.expectedResult.length - 1; i >= 0; i--) {
                            context.expectedResult[i] = b[i];
                        }
                        context
                                .setLogState(MTKLogDownloadHandler.C_LOG_CHECK);
                        context.mtkM.getHandler().resetLogTimeOut();
                        requestCheckBlock();
                    }
                }
                context.mtkM.getHandler().updateIgnoreNMEA();
                windowedLogFile.close();
            }
            if (!(context.getLogState() == MTKLogDownloadHandler.C_LOG_CHECK)) {
                // File could not be opened or is not incremental.
                openNewLog(context.logPath);
                if (Generic.isDebug()) {
                    Generic.debug("Starting download from "
                            + JavaLibBridge.unsigned2hex(context
                                    .getLogNextReadAddr(), 8)
                            + " to "
                            + JavaLibBridge.unsigned2hex(context
                                    .getLogDownloadEndAddr(), 8));
                }
                context.setLogState(MTKLogDownloadHandler.C_LOG_ACTIVE);
            }
            if (context.getLogState() == MTKLogDownloadHandler.C_LOG_NOLOGGING) {
                context.mtkM.postEvent(GpsEvent.LOG_DOWNLOAD_DONE);
            }
        } catch (final BT747Exception e) {
            context.setLogState(MTKLogDownloadHandler.C_LOG_NOLOGGING);
            context.mtkM.postEvent(GpsEvent.LOG_DOWNLOAD_DONE);
                throw e;
        } catch (final Exception e) {
            context.setLogState(MTKLogDownloadHandler.C_LOG_NOLOGGING);
            context.mtkM.postEvent(GpsEvent.LOG_DOWNLOAD_DONE);
            Generic.debug("getLogInit", e);
        }
    }

    protected final void openNewLog(final BT747Path path)
            throws BT747Exception {
        try {
            if ((context.logFile != null) && context.logFile.isOpen()) {
                context.logFile.close();
            }

            context.logFile = new RAFile(path, bt747.sys.File.DONT_OPEN);
            context.logPath = path;
            if (context.logFile.exists()) {
                context.logFile.delete();
            }

            context.logFile = new RAFile(path, bt747.sys.File.CREATE);
            // lastError 10530 = Read only
            context.logPath = path;
            context.logFile.close();
            context.logFile = new RAFile(path, bt747.sys.File.WRITE_ONLY);
            context.logPath = path;

            if ((context.logFile == null) || !(context.logFile.isOpen())) {
                throw new BT747Exception(BT747Exception.ERR_COULD_NOT_OPEN,
                        new Throwable(path.toString()));
            }
        } catch (BT747Exception e) {
            throw e;
        } catch (Exception e) {
            Generic.debug("openNewLog", e);
            throw new BT747Exception("open", new Throwable(path.toString()));
        }

    }

    private void reOpenLogWrite(final BT747Path path) {
        closeLog();
        try {
            context.logFile = new RAFile(path, File.WRITE_ONLY);
            context.logPath = path;
        } catch (final Exception e) {
            Generic.debug("reOpenLogWrite", e);
        }
    }

    // Called regularly
    private void getNextLogPart() {
        if (context.getLogState() != MTKLogDownloadHandler.C_LOG_NOLOGGING) {
            int z_Step;

            z_Step = context.getLogDownloadEndAddr() - context.logNextReqAddr
                    + 1;

            switch (context.getLogState()) {
            case C_LOG_ACTIVE:
                if (context.getLogDownloadEndAddr() <= context
                        .getLogNextReadAddr()) {
                    // Log is completely downloaded
                    endGetLog();
                }
                if (context.logNextReqAddr > context.getLogNextReadAddr()
                        + context.logRequestStep
                        * context.usedLogRequestAhead) {
                    z_Step = 0;
                }
                break;
            case C_LOG_RECOVER:
                if (context.getLogDownloadEndAddr() <= context
                        .getLogNextReadAddr()) {
                    // Log is completely downloaded
                    endGetLog();
                }
                if (context.logNextReqAddr > context.getLogNextReadAddr()) {
                    z_Step = 0;
                } else if (z_Step > 0x800) {
                    z_Step = 0x800;
                }
                break;
            default:
                z_Step = 0;
            }

            if (z_Step > 0) {
                if (z_Step > context.logRequestStep) {
                    z_Step = context.logRequestStep;
                }
                final int stepUntilBoundary = ((context.logNextReqAddr + 0x10000) & ~0xFFFF)
                        - context.logNextReqAddr;
                if (z_Step > stepUntilBoundary) {
                    // Avoid crossing 0x10000 boundaries that seem to result
                    // in download trouble on some devices.
                    z_Step = stepUntilBoundary;
                }
                readLog(context.logNextReqAddr, z_Step);
                context.logNextReqAddr += z_Step;
                if (context.getLogState() == MTKLogDownloadHandler.C_LOG_ACTIVE) {
                    getNextLogPart(); // Recursive to get requests 'ahead'
                }
            }
        }
    }

    // Called when no outstanding requests
    private void getLogPartNoOutstandingRequests() throws BT747Exception {
        switch (context.getLogState()) {
        case C_LOG_ACTIVE:
        case C_LOG_RECOVER:
            context.logNextReqAddr = context.getLogNextReadAddr(); // Recover
            // from
            // timeout.
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
        context.setLogState(MTKLogDownloadHandler.C_LOG_RECOVER); // recover
        // through
        // timeout.
    }

    protected final void analyzeLogPart(final int startAddr,
            final String sData) {
        int dataLength;
        // Convert hex data to bytes
        dataLength = Conv.hexStringToBytes(sData, context.readDataBuffer) / 2;
        // debugMsg("Got "+startAddr+"
        // "+JavaLibBridge.toString(sData.length())+"):
        // "+JavaLibBridge.toString(dataLength));
        switch (context.getLogState()) {
        case C_LOG_ACTIVE:
        case C_LOG_RECOVER:
            if (context.getLogNextReadAddr() == startAddr) {
                context.setLogState(MTKLogDownloadHandler.C_LOG_ACTIVE);
                int j = 0;

                // The Palm platform showed problems writing 0x800 blocks.
                // This splits it in smaller blocks and solves that problem.
                if ((dataLength != 0x800)
                        && (dataLength != context.logRequestStep)
                        && ((context.getLogNextReadAddr() + dataLength) != context.logNextReqAddr)
                        // Datalength is up to 0x10000 boundary
                        && (dataLength != ((context.getLogNextReadAddr() + 0x10000) & ~0xFFFF)
                                - context.getLogNextReadAddr())) {
                    // Received data is not the right size - transmission
                    // error.
                    // Can happen on Palm over BT.
                    if (Generic.isDebug()) {
                        Generic.debug("Unexpected datalength: "
                                + JavaLibBridge.unsigned2hex(dataLength, 8));
                    }
                    context.setLogState(MTKLogDownloadHandler.C_LOG_RECOVER);
                } else {
                    // Data seems ok
                    for (int i = dataLength; i > 0; i -= MTKLogDownloadHandler.C_MAX_FILEBLOCK_WRITE) {
                        int l = i;
                        if (l > MTKLogDownloadHandler.C_MAX_FILEBLOCK_WRITE) {
                            l = MTKLogDownloadHandler.C_MAX_FILEBLOCK_WRITE;
                        }
                        // debugMsg("Writing("+JavaLibBridge.toString(p_StartAddr)+"):
                        // "+JavaLibBridge.toString(j)+"
                        // "+JavaLibBridge.toString(l));

                        try {
                            if ((context.logFile.writeBytes(
                                    context.readDataBuffer, j, l)) != l) {
                                // debugMsg("Problem during anaLog:
                                // "+JavaLibBridge.toString(m_context.logFile.lastError));
                                cancelGetLog();
                                // debugMsg(JavaLibBridge.toString(q));
                            }
                        } catch (final Exception e) {
                            Generic.debug("analyzeLogPart", e);
                            cancelGetLog();
                        }
                        j += l;
                    }
                    context.setLogNextReadAddr(context.getLogNextReadAddr()
                            + dataLength);
                    // m_ProgressBar.repaintNow();
                    if (context.getFullLogBlocks
                            && (((startAddr - 1 + dataLength) & 0xFFFF0000) >= startAddr)) {
                        // Block boundary (0xX0000) is inside data.
                        final int blockStart = 0xFFFF & (0x10000 - (startAddr & 0xFFFF));
                        if (!(((context.readDataBuffer[blockStart] & 0xFF) == 0xFF) && ((context.readDataBuffer[blockStart + 1] & 0xFF) == 0xFF))) {
                            // This block is full, next block is still data
                            int minEndAddr;
                            // This block and next one.
                            minEndAddr = (startAddr & 0xFFFF0000) + 0x20000 - 1;
                            if (minEndAddr > context.mtkM.getLogMemSize() - 1) {
                                minEndAddr = context.mtkM.getLogMemSize() - 1;
                            }
                            if (minEndAddr > context.getLogDownloadEndAddr()) {
                                context.setLogDownloadEndAddr(minEndAddr);
                            }
                        }
                    }
                }
                if (context.getLogNextReadAddr() > context
                        .getLogDownloadEndAddr()) {
                    context.mtkM.postEvent(GpsEvent.LOG_DOWNLOAD_SUCCESS);
                    endGetLog();
                } else {
                    getNextLogPart();
                }
            } else {
                Generic.debug("Expected:"
                        + JavaLibBridge.unsigned2hex(context
                                .getLogNextReadAddr(), 8) + " Got:"
                        + JavaLibBridge.unsigned2hex(startAddr, 8) + " ("
                        + JavaLibBridge.unsigned2hex(dataLength, 8) + ")",
                        null);
                recoverFromLogError();
            }
            break;
        case C_LOG_CHECK:
            context.setLogState(MTKLogDownloadHandler.C_LOG_NOLOGGING); // Default.
            if ((startAddr == MTKLogDownloadHandler.C_BLOCKVERIF_START)
                    && (dataLength == MTKLogDownloadHandler.C_BLOCKVERIF_SIZE)) {
                // The block we got should be the block to check
                // byte[] dataBuffer = new byte[dataLength];
                boolean success;
                success = true;
                for (int i = dataLength - 1; i >= 0; i--) {
                    if (context.readDataBuffer[i] != context.expectedResult[i]) {
                        // The log is not the same, data is different
                        success = false;
                        break; // Exit from the loop
                    }
                }

                if (success) {
                    // Downloaded data seems to correspond - start incremental
                    // download
                    reOpenLogWrite(context.logPath);
                    try {
                        context.logFile.setPos(context.getLogNextReadAddr());
                    } catch (final Exception e) {
                        Generic.debug("C_LOG_CHECK", e);
                    }
                    if (Generic.isDebug()) {
                        Generic.debug("Starting incremental download from "
                                + JavaLibBridge.unsigned2hex(context
                                        .getLogNextReadAddr(), 8)
                                + " to "
                                + JavaLibBridge.unsigned2hex(context
                                        .getLogDownloadEndAddr(), 8));
                    }
                    context.setLogState(MTKLogDownloadHandler.C_LOG_ACTIVE);
                    getNextLogPart();
                } else {
                    context
                            .setLogState(MTKLogDownloadHandler.C_LOG_DATA_NOT_SAME_WAITING_FOR_REPLY);
                    if (Generic.isDebug()) {
                        Generic
                                .debug("Different data - requesting overwrite confirmation");
                    }
                    context.mtkM
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
                                            + JavaLibBridge.unsigned2hex(
                                                    startAddr, 8)
                                            + " ("
                                            + JavaLibBridge.unsigned2hex(
                                                    dataLength, 8) + ")",
                                    null);
                }
                context.setLogState(MTKLogDownloadHandler.C_LOG_CHECK);
                // recoverFromLogError();
            }
            break;
        default:
            break;
        } // Switch m_context.logState
        if (context.getLogState() == C_LOG_NOLOGGING) {
            context.mtkM.getHandler().setLogOrEraseOngoing(false);
        }
        context.mtkM.postEvent(GpsEvent.DOWNLOAD_STATE_CHANGE);
    }

    /**
     * Response from the application indicating if overwrite of log is ok nor
     * not.
     * 
     * @param overwrite
     * @throws BT747Exception
     */
    protected final void replyToOkToOverwrite(final boolean overwrite)
            throws BT747Exception {
        if (context.getLogState() == MTKLogDownloadHandler.C_LOG_DATA_NOT_SAME_WAITING_FOR_REPLY) {
            if (overwrite) {
                openNewLog(context.logPath);
                context.setLogNextReadAddr(0);
                context.setLogNextReadAddr(0);
                context.setLogState(MTKLogDownloadHandler.C_LOG_ACTIVE);
            } else {
                endGetLog();
            }
        }
    }

    /**
     * Start of block position to verify if log in device corresponds to log
     * in file.
     */
    private static final int C_BLOCKVERIF_START = 0x200;

    /** Size of block to validate that log in device is log in file. */
    private static final int C_BLOCKVERIF_SIZE = 0x200;

    private static final int C_MAX_FILEBLOCK_WRITE = 0x800;

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
            if (context.logFile != null) {
                if (context.logFile.isOpen()) {
                    context.logFile.close();
                    context.logFile = null;
                }
            }
        } catch (final Exception e) {
            Generic.debug("CloseLog", e);
        }
    }

    private void endGetLog() {
        context.setLogState(MTKLogDownloadHandler.C_LOG_NOLOGGING);
        closeLog();

        if (context.loggingIsActiveBeforeDownload) {
            context.mtkC.cmd(MtkController.CMD_STARTLOG);
            context.mtkC.reqData(MtkModel.DATA_LOG_STATUS);
        }
        context.mtkM.postEvent(GpsEvent.LOG_DOWNLOAD_DONE);
    }

    protected final int getStartAddr() {
        return context.logDownloadStartAddr;
    }

    protected final int getNextReadAddr() {
        return context.getLogNextReadAddr();
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
        if (context.mtkM.getHandler().isConnected()) {
            context.mtkM.getHandler().setEraseOngoing(true);
            context.mtkC.doSendCmd(new GpsLinkNmeaCommand("PMTK"
                    + BT747Constants.PMTK_CMD_LOG_STR + ","
                    + BT747Constants.PMTK_LOG_ERASE + ","
                    + BT747Constants.PMTK_LOG_ERASE_YES_STR));
            waitEraseDone();
        }
    }

    protected final void recoveryEraseLog() {
        // Get some information (when debug mode active)
        context.mtkC.cmd(MtkController.CMD_STOPLOG); // Stop logging for
        // this operation
        context.mtkC.reqData(MtkModel.DATA_LOG_STATUS); // Check status
        context.mtkC.reqData(MtkModel.DATA_LOG_FLASH_SECTOR_STATUS); // Get
        // flash
        // sector
        // information
        // from
        // device
        // TODO: Handle flash sector information.
        // Ignore acknowledges in controller - handled here.
        context.mtkC.sendCmd(new GpsLinkNmeaCommand("PMTK"
                + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_ENABLE, false));

        context.mtkC.reqData(MtkModel.DATA_LOG_STATUS); // Check status

        context.forcedErase = true;
        eraseLog();

    }

    private void postRecoveryEraseLog() {
        context.mtkC.reqData(MtkModel.DATA_LOG_STATUS);
        context.mtkC.reqData(MtkModel.DATA_LOG_FLASH_SECTOR_STATUS); // Get
        // flash
        // sector
        // information
        // from
        // device

        context.mtkC.sendCmd("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_INIT);

        context.mtkC.reqData(MtkModel.DATA_LOG_FLASH_SECTOR_STATUS); // Get
        // flash
        // sector
        // information
        // from
        // device
        context.mtkC.reqData(MtkModel.DATA_LOG_STATUS);
    }

    private void waitEraseDone() {
        context.setLogState(MTKLogDownloadHandler.C_LOG_ERASE_STATE);
        context.mtkM.getHandler().setLogOrEraseOngoing(true);
        context.mtkM.getHandler().resetLogTimeOut();
        // readLogFlashStatus(); - Will be done after timeout
    }

    protected void signalEraseDone() {
        context.setLogState(MTKLogDownloadHandler.C_LOG_NOLOGGING);
        context.mtkM.getHandler().setLogOrEraseOngoing(true);
        context.mtkM.setEraseOngoing(false);
    }

    protected final void stopErase() {
        if (context.mtkM.isEraseOngoing()
                && (context.getLogState() == MTKLogDownloadHandler.C_LOG_ERASE_STATE)) {
            context.mtkM.getHandler().updateIgnoreNMEA();

            signalEraseDone();
        } else {
            // Not changing state.
            context.mtkM.setEraseOngoing(false);
        }
    }

    /**
     * Called from within run of GPSstate (regularly called).
     * 
     * @throws BT747Exception
     */
    protected void notifyRun() throws BT747Exception {
        if ((context.mtkM.getHandler().getOutStandingCmdsCount() == 0)
                && (context.getLogState() != MTKLogDownloadHandler.C_LOG_NOLOGGING)
                && (context.getLogState() != MTKLogDownloadHandler.C_LOG_ERASE_STATE)) {
            // Sending command on next timer adds some delay after
            // the end of the previous command (reception)
            getLogPartNoOutstandingRequests();
        } else if (context.getLogState() == MTKLogDownloadHandler.C_LOG_ACTIVE) {
            getNextLogPart();
        } else if (context.getLogState() == MTKLogDownloadHandler.C_LOG_ERASE_STATE) {
            if (context.mtkM.getHandler().timeSinceLastStamp() > MTKLogDownloadHandler.C_LOGERASE_TIMEOUT) {
                context.mtkC.reqData(MtkModel.DATA_LOG_FLASH_STATUS);
            }
        }
    }

    protected final void notifyDisconnected() {
        if (context.getLogState() != MTKLogDownloadHandler.C_LOG_NOLOGGING) {
            endGetLog();
        }
    }

    protected final void handleLogFlashStatReply(final String s) {
        if (context.getLogState() == MTKLogDownloadHandler.C_LOG_ERASE_STATE) {
            switch (JavaLibBridge.toInt(s)) {
            case 1:
                if (context.mtkM.isEraseOngoing()) {
                    signalEraseDone();
                }
                if (context.forcedErase) {
                    context.forcedErase = false;
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
    protected final void readLog(final int startAddr, final int size) {
        context.mtkC.sendCmd("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_Q_LOG + ","
                + JavaLibBridge.unsigned2hex(startAddr, 8) + ","
                + JavaLibBridge.unsigned2hex(size, 8));
    }

    protected final void setLogRequestAhead(final int logRequestAhead) {
        context.logRequestAhead = logRequestAhead;
    }

    /**
     * Temporary for Wonde Proud
     */
    protected final File getLogFile() {
        return context.logFile;
    }

}
