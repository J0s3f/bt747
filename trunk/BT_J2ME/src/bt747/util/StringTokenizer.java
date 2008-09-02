package bt747.util;

public class StringTokenizer {

    private String left;
    private char delim;

    public StringTokenizer(String s, char delim) {
        this.left = s;
        if (this.left == null) {
            this.left = "";
        }
        this.delim = delim;
    }

    public boolean hasMoreTokens() {
        int i = left.length();
        if (i > 0) {
            return true;
        } else {
            return false;
        }
    }

    public String nextToken() {
        StringBuffer s = new StringBuffer();
        boolean done = false;
        int pos = 0;
        boolean end = false;
        while (!done) {
            if (left.length() > pos) {
                char c = left.charAt(pos);
                if (c != delim) {
                    s.append(c);
                } else if (!done && s.length() > 0) {
                    done = true;
                } else if ((pos + 1) == left.length()) {
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
}
