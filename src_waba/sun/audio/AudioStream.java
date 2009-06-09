/**
 * 
 */
package sun.audio;

/**
 * Overrides class to avoid problem with SuperWaba that does not find the class on Linux.
 * 
 * @author Mario
 * 
 */
public class AudioStream {
    /**
     * 
     */
    public AudioStream(final String path) throws java.io.IOException {
        throw new java.io.IOException("Override");
        // Do nothing
    }
}
