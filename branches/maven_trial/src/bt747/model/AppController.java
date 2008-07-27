package bt747.model;

import gps.BT747Constants;
import gps.log.GPSRecord;

import bt747.Txt;
import bt747.ui.MessageBox;

import moio.util.HashSet;
//import moio.util.Iterator;  Needed later when communicating with views.

public final class AppController extends Controller {

    /**
     * The lower level controller. This should become a separate instance in the
     * future.
     */
    private Controller c;

    /**
     * Reference to the model.
     */
    private Model m;

    /**
     * @param model
     *            The model to associate with this controller.
     */
    public AppController(final Model model) {
        this.m = model;
        c = this; // Temporary solution until application controller methods
        // moved from lower level Controller.
        super.setModel(m);
        // c = new Controller(model);
    }

    // The next methods are to be moved to the application controller.
    /**
     * Convert the log given the provided parameters using other methods.
     * 
     * @param logType
     *            Indicates the type of log that should be written. For example
     *            Model.CSV_LOGTYPE .
     * @see Model#CSV_LOGTYPE
     * @see Model#TRK_LOGTYPE
     * @see Model#KML_LOGTYPE
     * @see Model#PLT_LOGTYPE
     * @see Model#GPX_LOGTYPE
     * @see Model#NMEA_LOGTYPE
     * @see Model#GMAP_LOGTYPE
     */
    public final void convertLog(final int logType) {
        if (doConvertLog(logType) != 0) {
            reportError(c.getLastError(), c.getLastErrorInfo());
        }
    }

    /**
     * Convert the log into an array of trackpoints.
     * 
     * @return Array of selected trackpoints.
     */
    public final GPSRecord[] convertLogToTrackPoints() {
        GPSRecord[] result;
        result = c.doConvertLogToTrackPoints();
        if (result == null) {
            reportError(c.getLastError(), c.getLastErrorInfo());
        }
        return result;
    }

    /** Options for the first warning message. */
    private static final String[] C_ERASE_OR_CANCEL = { Txt.ERASE, Txt.CANCEL };
    /** Options for the first warning message. */
    private static final String[] C_YES_OR_CANCEL = { Txt.YES, Txt.CANCEL };
    /** Options for the second warning message - reverse order on purpose. */
    private static final String[] C_CANCEL_OR_CONFIRM_ERASE = { Txt.CANCEL,
            Txt.CONFIRM_ERASE };

    
    /**
     * A 'recovery Erase' attempts to recover memory that was previously
     * identified as 'bad'.
     */
    public final void recoveryErase() {
        /** Object to open multiple message boxes */
        MessageBox mb;
        mb = new MessageBox(Txt.TITLE_ATTENTION, Txt.C_msgEraseWarning,
                C_ERASE_OR_CANCEL);
        mb.popupBlockingModal();
        if (mb.getPressedButtonIndex() == 0) {
            mb = new MessageBox(Txt.TITLE_ATTENTION, Txt.C_msgEraseWarning2,
                    C_CANCEL_OR_CONFIRM_ERASE);
            mb.popupBlockingModal();
            if (mb.getPressedButtonIndex() == 1) {
                // Erase log
                c.recoveryEraseLog();
            }
        }
    }

    /**
     * (User) request to change the log format. Warns about requirement to erase
     * the log too.
     * 
     * @param logFormat
     *            The logFormat to set upon erase.
     */
    public final void changeLogFormatAndErase(final int logFormat) {
        /** Object to open multiple message boxes */
        MessageBox mb;
        mb = new MessageBox(Txt.TITLE_ATTENTION,
                Txt.C_msgWarningFormatAndErase, C_ERASE_OR_CANCEL);
        mb.popupBlockingModal();
        if (mb.getPressedButtonIndex() == 0) {
            mb = new MessageBox(Txt.TITLE_ATTENTION,
                    Txt.C_msgWarningFormatAndErase2, C_CANCEL_OR_CONFIRM_ERASE);
            mb.popupBlockingModal();
            if (mb.getPressedButtonIndex() == 1) {
                // Set format and reset log
                c.setLogFormat(logFormat);
                c.eraseLog();
            }
        }
    }

    
    /**
     * (User) request to change the log format. The log is not erased and may be
     * incompatible with other applications.
     * 
     * @param logFormat
     *            The new log format to set.
     */
    public final void changeLogFormat(final int logFormat) {
        /** Object to open multiple message boxes */
        MessageBox mb;
        mb = new MessageBox(true, Txt.TITLE_ATTENTION,
                Txt.C_msgWarningFormatIncompatibilityRisk, C_YES_OR_CANCEL);
        mb.popupBlockingModal();
        if (mb.getPressedButtonIndex() == 0) {
            c.setLogFormat(logFormat);
        }
    }

    /**
     * (User) request to change the log format. Warns about requirement to erase
     * the log too.
     */
    public final void eraseLogFormat() {
        /** Object to open multiple message boxes */
        MessageBox mb;
        mb = new MessageBox(Txt.TITLE_ATTENTION, Txt.C_msgEraseWarning,
                C_ERASE_OR_CANCEL);
        mb.popupBlockingModal();
        if (mb.getPressedButtonIndex() == 0) {
            mb = new MessageBox(Txt.TITLE_ATTENTION, Txt.C_msgEraseWarning2,
                    C_CANCEL_OR_CONFIRM_ERASE);
            mb.popupBlockingModal();
            if (mb.getPressedButtonIndex() == 1) {
                // Erase log
                c.eraseLog();
            }
        }
    }

    
    
    /**
     * Report an error.
     * 
     * @param error
     *            The error number.
     * @param errorInfo
     *            A text string related to the error (filename, ...).
     */
    private void reportError(final int error, final String errorInfo) {
        String errorMsg;
        switch (error) {
        case BT747Constants.ERROR_COULD_NOT_OPEN:
            errorMsg = Txt.COULD_NOT_OPEN + errorInfo;
            bt747.sys.Vm.debug(errorMsg);
            new MessageBox(Txt.ERROR, errorMsg).popupBlockingModal();
            break;
        case BT747Constants.ERROR_NO_FILES_WERE_CREATED:
            (new MessageBox(Txt.WARNING, Txt.NO_FILES_WERE_CREATED))
                    .popupBlockingModal();
            break;
        case BT747Constants.ERROR_READING_FILE:
            new MessageBox(Txt.ERROR, Txt.PROBLEM_READING + errorInfo)
                    .popupBlockingModal();
            break;
        default:
            break;
        }
    }

    /**
     * The list of views attached to this controller.
     */
    private HashSet views = new HashSet();

    /**
     * Attach a view to the controller.
     * 
     * @param view
     *            The view that must be attached.
     */
    public final void addView(final BT747View view) {
        views.add(view);
        view.setController(this);
        view.setModel(this.m);
    }

    // protected void postEvent(final int type) {
    // Iterator it = views.iterator();
    // while (it.hasNext()) {
    // BT747View l=(BT747View)it.next();
    // Event e=new Event(l, type, null);
    // l.newEvent(e);
    // }
    // }
}
