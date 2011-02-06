package org.j4me.logging;

/**
 * Changes to the original by Mario DE WEERD. Added the possibility to write
 * the log to a specific outputStream (e.g., a file).
 * 
 * @see #setOutputStream Added some extra information to sertain streams.
 */
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Maintains a recording of the application's operation. The <code>Log</code>
 * takes in strings for events that happen during the program's execution.
 * Each event has a <code>Level</code>, or priority, associated with it.
 * <p>
 * The log can be read later to examine problems.
 * <p>
 * The following two examples illustrate how to record things in the log. The
 * first statement shows that the logging level should be checked before
 * logging because it stops the expensive string concatenation when the log
 * level is off. The second shows logging an exception.
 * 
 * <code><pre>
 * if (Log.isDebugEnabled()) {
 *     Log.debug(&quot;X = &quot; + x + &quot; which I only care about when debugging&quot;);
 * }
 * 
 * Log.warn(&quot;Problem with HTTP&quot;, exception);
 * </pre></code>
 * 
 * @see Level
 */
public class Log {
    /**
     * The number of log messages maintained. The log store is a circular
     * buffer of this size. This number should be great enough that if a
     * series of problems occur the conditions leading up to them can be seen.
     * Conversely it should be small enough to not take too much memory (if 10
     * messages are stored averaging 50 characters at 2 bytes per character
     * that would take up 1 KB of heap space).
     */
    private static final int MAX_LOG_MESSAGES = 25;

    /**
     * The log message store. All log messages are recorded in this circular
     * buffer. Once <code>MAX_LOG_MESSAGES</code> more messages are logged
     * the message will be discarded.
     */
    private static LogMessage[] store;

    /**
     * The index into <code>store</code> of the oldest message logged.
     */
    private static int oldestMessageIndex;

    /**
     * The index into <code>store</code> of the last message logged.
     */
    private static int newestMessageIndex;

    /**
     * The level the application is logging at.
     */
    private static Level level = Level.INFO;

    private static PrintStream os;

    /**
     * Initializes the log store.
     */
    static {
        Log.clear();
    }

    /**
     * Returns the lowest level of statements that are logged.
     * 
     * @return The value of the lowest level of log statements written to the
     *         log. It can later be passed to the <code>setLevel</code>
     *         method to reset the logging level.
     */
    public static Level getLogLevel() {
        return Log.level;
    }

    /**
     * Sets the level log statements are evaluated. Anything at
     * <code>level</code> or higher will be logged.
     * <p>
     * The <code>int</code> value <code>level</code> should come from the
     * <code>getLogLevel</code> method.
     * 
     * @param level
     *                is the lowest priority of statements that will be
     *                logged.
     */
    public static void setLevel(final int level) {
        if (level == Level.DEBUG.levelInt) {
            Log.level = Level.DEBUG;
        } else if (level == Level.INFO.levelInt) {
            Log.level = Level.INFO;
        } else if (level == Level.WARN.levelInt) {
            Log.level = Level.WARN;
        } else if (level == Level.ERROR.levelInt) {
            Log.level = Level.ERROR;
        } else if (level == Level.OFF.levelInt) {
            Log.level = Level.OFF;
        } else {
            // If we made it here it isn't a valid log level.
            throw new IllegalArgumentException("getLevel(" + level
                    + ") not a valid level");
        }
    }

    public static void setOutputStream(final OutputStream out) {
        try {
            if (Log.os != null) {
                Log.os.close();
            }
        } catch (final Throwable e) {

        }
        try {
            if (out != null) {
                Log.os = new PrintStream(out);
            } else {
                Log.os = null;
            }
        } catch (final Throwable e) {
            Log.os = null;
            Log.error("Log output stream", e);
        }
    }

    /**
     * Sets the level log statements are evaluated. Anything at
     * <code>level</code> or higher will be logged.
     * 
     * @param level
     *                is the lowest priority of statements that will be
     *                logged.
     */
    public static void setLevel(final Level level) {
        Log.level = level;
    }

    /**
     * Log a message string at the {@link Level#DEBUG DEBUG} Level.
     * <p>
     * This method first checks if this category is <code>DEBUG</code>
     * enabled by comparing the level of this category with
     * {@link Level#DEBUG DEBUG} Level. If the category is <code>DEBUG</code>
     * enabled, then it will log the message.
     * 
     * @param message
     *                is the message to log.
     */
    public static void debug(final String message) {
        if (Log.level.levelInt <= Level.DEBUG.levelInt) {
            Log.addLogMessage(message, Level.DEBUG, null);
        }
    }

    /**
     * Log a message string with the <code>DEBUG</code> level including the
     * stack trace of the {@link Throwable} <code>t</code> passed as
     * parameter.
     * <p>
     * See {@link #debug(String)} for more detailed information.
     * 
     * @param message
     *                is the message to log.
     * @param t
     *                is the exception to log.
     */
    public static void debug(final String message, final Throwable t) {
        if (Log.level.levelInt <= Level.DEBUG.levelInt) {
            Log.addLogMessage(message, Level.DEBUG, t);
        }
    }

    /**
     * Log a message string with the {@link Level#INFO INFO} Level.
     * <p>
     * This method first checks if this category is <code>INFO</code>
     * enabled by comparing the level of this category with
     * {@link Level#INFO INFO} Level. If the category is <code>INFO</code>
     * enabled, then it will log the message.
     * 
     * @param message
     *                is the message to log.
     */
    public static void info(final String message) {
        if (Log.level.levelInt <= Level.INFO.levelInt) {
            Log.addLogMessage(message, Level.INFO, null);
        }
    }

    /**
     * Log a message string with the <code>INFO</code> level including the
     * stack trace of the {@link Throwable} <code>t</code> passed as
     * parameter.
     * <p>
     * See {@link #info(String)} for more detailed information.
     * 
     * @param message
     *                is the message to log.
     * @param t
     *                is the exception to log.
     */
    public static void info(final String message, final Throwable t) {
        if (Log.level.levelInt <= Level.INFO.levelInt) {
            Log.addLogMessage(message, Level.INFO, t);
        }
    }

    /**
     * Log a message string with the {@link Level#WARN WARN} Level.
     * <p>
     * This method first checks if this category is <code>WARN</code>
     * enabled by comparing the level of this category with
     * {@link Level#WARN WARN} Level. If the category is <code>WARN</code>
     * enabled, then it will log the message.
     * 
     * @param message
     *                is the message to log.
     */
    public static void warn(final String message) {
        if (Log.level.levelInt <= Level.WARN.levelInt) {
            Log.addLogMessage(message, Level.WARN, null);
        }
    }

    /**
     * Log a message string with the <code>WARN</code> level including the
     * stack trace of the {@link Throwable} <code>t</code> passed as
     * parameter.
     * <p>
     * See {@link #warn(String)} for more detailed information.
     * 
     * @param message
     *                is the message to log.
     * @param t
     *                is the exception to log.
     */
    public static void warn(final String message, final Throwable t) {
        if (Log.level.levelInt <= Level.WARN.levelInt) {
            Log.addLogMessage(message, Level.WARN, t);
        }
    }

    /**
     * Log a message string with the {@link Level#ERROR ERROR} Level.
     * <p>
     * This method first checks if this category is <code>ERROR</code>
     * enabled by comparing the level of this category with
     * {@link Level#ERROR ERROR} Level. If the category is <code>ERROR</code>
     * enabled, then it will log the message.
     * 
     * @param message
     *                is the message to log.
     */
    public static void error(final String message) {
        if (Log.level.levelInt <= Level.ERROR.levelInt) {
            Log.addLogMessage(message, Level.ERROR, null);
        }
    }

    /**
     * Log a message string with the <code>ERROR</code> level including the
     * stack trace of the {@link Throwable} <code>t</code> passed as
     * parameter.
     * <p>
     * See {@link #error(String)} for more detailed information.
     * 
     * @param message
     *                is the message to log.
     * @param t
     *                is the exception to log.
     */
    public static void error(final String message, final Throwable t) {
        if (Log.level.levelInt <= Level.ERROR.levelInt) {
            Log.addLogMessage(message, Level.ERROR, t);
        }
    }

    /**
     * Check whether logging at the <code>DEBUG</code> level is enabled.
     * <p>
     * This function is intended to lessen the computational cost of disabled
     * log statements. All debug logs that perform string concatenation should
     * be written as:
     * 
     * <pre>
     * if (Log.isDebugEnabled()) {
     *     Log.debug(&quot;This is entry number: &quot; + i);
     * }
     * </pre>
     * 
     * @return <code>true</code> if debug messages are logged;
     *         <code>false</code> if not.
     */
    public static boolean isDebugEnabled() {
        return Log.level.levelInt <= Level.DEBUG.levelInt;
    }

    /**
     * Check whether logging at the <code>INFO</code> level is enabled.
     * <p>
     * This function is intended to lessen the computational cost of disabled
     * log statements. All info logs that perform string concatenation should
     * be written as:
     * 
     * <pre>
     * if (Log.isInfoEnabled()) {
     *     Log.info(&quot;This is entry number: &quot; + i);
     * }
     * </pre>
     * 
     * @return <code>true</code> if info messages are logged;
     *         <code>false</code> if not.
     */
    public static boolean isInfoEnabled() {
        return Log.level.levelInt <= Level.INFO.levelInt;
    }

    /**
     * Logs a message into the log store.
     * <p>
     * The log store is a circular buffer. This method maintains it. Once the
     * maximum size is reached, the oldest message logged will be replaced
     * with this one.
     * 
     * @param message
     *                is the text of the log message.
     * @param level
     *                is the severity of the log message.
     * @param throwable
     *                is an exception that caused the log message. This will
     *                be <code>null</code> if no exception caused the
     *                message.
     */
    private static synchronized void addLogMessage(String message,
            final Level level, final Throwable throwable) {
        // Create the log message text.
        if (message == null) {
            message = "";
        }

        String text = message;

        if (throwable != null) {
            text += "\n(" + throwable.toString() + ")";
        }

        // Write the error to the console.
        // On the emulator this will go to the console window.
        // On phones there is no error stream so this will do nothing.
        System.err.print("[");
        System.err.print(level);
        System.err.print("] ");
        System.err.println(text);

        if (throwable != null) {
            throwable.printStackTrace();
        }

        if (Log.os != null) {
            try {
                Log.os.print("[");
                Log.os.print(level);
                Log.os.print("] ");
                Log.os.println(text);
                Log.os.flush();
            } catch (final Exception e) {
                Log.os = null;
            }
        }

        // Store the log message.
        Log.newestMessageIndex = (Log.newestMessageIndex + 1)
                % Log.MAX_LOG_MESSAGES;

        if (Log.newestMessageIndex == Log.oldestMessageIndex) {
            // Replacing the oldest log.
            Log.store[Log.newestMessageIndex].setLogMessage(level, text);
            Log.oldestMessageIndex = (Log.oldestMessageIndex + 1)
                    % Log.MAX_LOG_MESSAGES;
        } else {
            // Create a new slot for the log message.
            Log.store[Log.newestMessageIndex] = new LogMessage(level, text);

            if (Log.oldestMessageIndex < 0) {
                Log.oldestMessageIndex = 0;
            }
        }
    }

    /**
     * Gets all the log messages still in memory. Internally the log messages
     * are kept in a circular buffer and once it fills, the oldest messages
     * will be discarded.
     * <p>
     * The returned array references all of the log messages and does not stop
     * logging from continuing. In other words the returned logs are a
     * snapshot in time.
     * 
     * @return An array of the previously logged messages. The higher the
     *         array index, the more recently it was logged. Therefore
     *         <code>length - 1</code> will be the last message logged. If
     *         no messages have been logged this will return an array of
     *         length zero (i.e. it never returns <code>null</code>).
     */
    public static synchronized LogMessage[] getLogMessages() {
        // Calculate how many log messages are in the circular buffer.
        int numberOfMessages;

        if (Log.newestMessageIndex < 0) {
            numberOfMessages = 0;
        } else if (Log.newestMessageIndex >= Log.oldestMessageIndex) {
            numberOfMessages = Log.newestMessageIndex
                    - Log.oldestMessageIndex + 1;
        } else // The buffer's full
        {
            numberOfMessages = Log.MAX_LOG_MESSAGES;
        }

        // Copy references to the log messages to a new array.
        final LogMessage[] copy = new LogMessage[numberOfMessages];

        for (int i = 0; i < numberOfMessages; i++) {
            int index = Log.newestMessageIndex - i;

            if (index < 0) {
                index = Log.MAX_LOG_MESSAGES + index;
            }

            copy[numberOfMessages - i - 1] = Log.store[index];
        }

        return copy;
    }

    /**
     * Empties the log of all messages.
     */
    public static synchronized void clear() {
        Log.oldestMessageIndex = -1;
        Log.newestMessageIndex = -1;
        Log.store = new LogMessage[Log.MAX_LOG_MESSAGES];
    }
}
