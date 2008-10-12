package bt747.sys;

public final class StringTokenizer {

    private final bt747.sys.interfaces.BT747StringTokenizer tk;

    public StringTokenizer(final String a, final char b) {
        tk = Interface.tr.getStringTokenizer(a, b);
    }
    
    public final boolean hasMoreTokens() {
        return tk.hasMoreTokens();
    }
    
    public final int countTokens() {
        return tk.countTokens();
    }
    
    public final String nextToken() {
        return tk.nextToken();
    }
}
