package net.sf.bt747.j2se.system;

import java.util.StringTokenizer;

import bt747.sys.interfaces.BT747StringTokenizer;

public final class J2SEStringTokenizer extends StringTokenizer implements
        BT747StringTokenizer {
    public J2SEStringTokenizer(final String a, final char b) {
        super(a, "" + b);
    }
}
