//********************************************************************
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER. See the GNU General Public License  ***
//***  for more details.                                           ***
//********************************************************************

package net.sf.bt747.j2me.system;

public final class StringTokenizer {

    private String left;
    private char delim;

    public StringTokenizer(final String s, final char delim) {
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
