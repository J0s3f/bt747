/**
 * 
 */
package bt747.j2se_view;

import gps.log.in.GPSLogConvertInterface;
import gps.log.in.GPSInputConversionFactory;

import java.io.File;
import java.io.IOException;

import bt747.j2se_view.model.ImageData;
import bt747.model.Controller;
import bt747.model.Model;

/**
 * @author Mario
 *
 */
public class J2SEController extends Controller {

    /**
     * Reference to the model.
     */
    private Model m;

    /**
     * @param model
     */
    public J2SEController(Model model) {
        super(model);
        m = model;
    }

    /**
     * 
     */
    public J2SEController() {
        super();
    }
    
    /**
     * 
     */
    public void setModel(Model m) {
        super.setModel(m);
        this.m = m;
    }


    private static final class GPXHandlerFactory extends GPSInputConversionFactory {
        /* (non-Javadoc)
         * @see bt747.model.GPSOutputFactory#getInputConversionInstance(java.lang.String)
         */
        @Override
        public final GPSLogConvertInterface getInputConversionInstance(
                final String logFile) {
            final String logFileLC = logFile.toLowerCase();
            if (logFileLC.endsWith(".gpx")) {
                return new GPXLogConvert();   
            } else {
                return super.getInputConversionInstance(logFile);
            }
        }
    }
    
    static {
        GPSInputConversionFactory.addHandler(new GPXHandlerFactory());
    }

    /**
     * @param fpf
     * @param w
     * @throws IOException
     */
    public final static void convertImage(final TaggedFilePathFactory fpf, final ImageData img)
            throws IOException {
        final String p = img.getPath();
        final String newPath = fpf.getTaggedFilePath(p, img);

        String f1 = (new File(newPath)).getCanonicalPath();
        String f2 = (new File(p)).getCanonicalPath();
        String orgPath = p;
        if (f1.equals(f2)) {
            // Target path and origin path are the same.
            // We may need to move the original file.
            orgPath = fpf.getOrgFilePath(p, img);
            File d = new File(orgPath);
            if (!d.exists()) {
                (new File(p)).renameTo(d);
            }
        }
        // now convert from orgPath to newPath.
        img.writeImage(orgPath, newPath, 0);
    }

}
