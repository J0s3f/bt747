package net.sf.bt747.j2me.system;

import bt747.sys.interfaces.BT747StringTokenizer;

public final class J2MEStringTokenizer implements BT747StringTokenizer{
    
    private String left;
    private char delim;

    public J2MEStringTokenizer(final String s, final char delim) {
        this.left = s;
        if (this.left == null) {
            this.left = "";
        }
        this.delim = delim;
    }

    public final boolean hasMoreTokens() {
        int i = left.length();
        if (i > 0) {
            return true;
        } else {
            return false;
        }
    }

    public final String nextToken() {
        StringBuffer s = new StringBuffer();
        boolean done = false;
        int pos = 0;
        boolean end = false;
        while (!done) {
            if (left.length() > pos) {
                char c = left.charAt(pos);
                if (c != delim) {
                    s.append(c);
                } else{
                    done = true;
                }
            } else {
                done = true;
                end = true;
            }
            pos++;
        }
        String token = s.toString();
        if (end) {
            left = "";
        } else {
            left = left.substring(pos);
        }
        return token;
    }
    
    public final int countTokens() {
        if(left.length()==0) {
            return 0;
        } else {
            int count = 1;
            int index = left.length()-1;
            while(index >=0) {
                if(left.charAt(index)==delim) {
                    count++;
                }
                index--;
            }
            return count;
        }
    }
}
