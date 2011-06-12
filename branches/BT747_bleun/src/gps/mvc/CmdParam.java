/**
 * 
 */
package gps.mvc;

import bt747.sys.Generic;

/**
 * May throw exceptions in the future.
 * 
 * @author Mario De Weerd
 * 
 */
public final class CmdParam {
    private final int paramType;
    private final Object param;

    public final static int OTHER = 0;
    public final static int INT = 1;
    public final static int STRING = 2;
    public final static int BOOLEAN = 3;

    /**
     * Create generic parameter for device model/controller method calls.
     */
    public CmdParam(final int paramType, final Object param) {
        this.paramType = paramType;
        this.param = param;
    }

    /**
     * Create int parameter for device model/controller method calls.
     */
    public CmdParam(final int value) {
        this(CmdParam.INT, new myInteger(value));
    }

    /**
     * Create boolean parameter for device model/controller method calls.
     */
    public CmdParam(final boolean value) {
        this(CmdParam.BOOLEAN, new myBoolean(value));
    }

    /**
     * Create string parameter for device model/controller method calls.
     */
    public CmdParam(final String value) {
        this(CmdParam.STRING, value);
    }

    public final int getInt() {
        if (paramType == CmdParam.INT) {
            return ((myInteger) param).getValue();
        } else {
            Generic.debug("Int value expected for " + toString());
            return 0;
        }
    }

    public final boolean getBoolean() {
        if (paramType == CmdParam.BOOLEAN) {
            return ((myBoolean) param).getValue();
        } else {
            Generic.debug("Boolean value expected for " + toString());
            return false;
        }
    }

    public final String getString() {
        if (paramType == CmdParam.STRING) {
            return (String) param;
        } else {
            Generic.debug("Boolean value expected for " + toString());
            return null;
        }
    }

    /**
     * Integer class not available on all platforms - creating a private
     * version.
     * 
     * @author Mario
     * 
     */
    private final static class myInteger {
        int i;

        /**
         * Create new instance with integer value.
         */
        public myInteger(final int i) {
            this.i = i;
        }

        public final int getValue() {
            return i;
        }
    }

    /**
     * Boolean class not available on all platforms - creating a private
     * version.
     * 
     * @author Mario
     * 
     */
    private final static class myBoolean {
        boolean i;

        /**
         * Create new instance with boolean value.
         */
        public myBoolean(final boolean i) {
            this.i = i;
        }

        public final boolean getValue() {
            return i;
        }
    }

}
