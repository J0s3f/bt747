package net.sf.bt747.iphone.system;

import bt747.sys.interfaces.BT747StringTokenizer;

public final class IphoneStringTokenizer implements BT747StringTokenizer {
    private String left;
    private char delim;

    public IphoneStringTokenizer(final String s, final char delim) {
        left = s;
        this.delim = delim;
    }

    public final boolean hasMoreTokens() {
        return (left != null);
    }

    public final String nextToken() {
        if (left == null) {
            return null;
        }
        final StringBuffer s = new StringBuffer();
        boolean done = false;
        int pos = 0;
        boolean end = false;
        while (!done) {
            if (left.length() > pos) {
                final char c = left.charAt(pos);
                if (c != delim) {
                    s.append(c);
                } else {
                    done = true;
                }
            } else {
                done = true;
                end = true;
            }
            pos++;
        }
        final String token = s.toString();
        if (end) {
            left = null;
        } else {
            left = left.substring(pos);
        }
        return token;
    }

    public final int countTokens() {
        if (left == null) {
            return 0;
        } else {
            int count = 1;
            int index = left.length() - 1;
            while (index >= 0) {
                if (left.charAt(index) == delim) {
                    count++;
                }
                index--;
            }
            return count;
        }
    }
}
