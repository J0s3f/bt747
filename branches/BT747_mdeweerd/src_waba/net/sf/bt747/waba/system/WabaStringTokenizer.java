package net.sf.bt747.waba.system;

import moio.util.StringTokenizer;

import bt747.sys.interfaces.BT747StringTokenizer;

public final class WabaStringTokenizer extends StringTokenizer implements
        BT747StringTokenizer {
    public WabaStringTokenizer(final String a, final char b) {
        super(a, "" + b);
    }
}
