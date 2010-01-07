/**
 * 
 */
package sun.audio;

import java.applet.AudioClip;
import java.io.InputStream;

/**
 * Overrides class to avoid problem with SuperWaba that does not find the class on Linux.
 * 
 * @author Mario
 * 
 */
public class AudioStream implements AudioClip {
    /**
     * 
     */
    public AudioStream(final InputStream a) throws java.io.IOException {
        throw new java.io.IOException("Override");
        // Do nothing
    }

    /* (non-Javadoc)
     * @see java.applet.AudioClip#loop()
     */
    public void loop() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.applet.AudioClip#play()
     */
    public void play() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.applet.AudioClip#stop()
     */
    public void stop() {
        // TODO Auto-generated method stub
        
    }

}
