/**
 * Adapted from http://javaxden.blogspot.com/2007/08/creatin-thumbnail.html .
 * 
 * Using MediaTracker looks like a good idea.
 * 
 *Creating thumbnails with Java
 * 
 * This programm loads an image via java.awt.Toolkit, scales it down to a
 * user-defined resolution and saves it as a Developer required format.
 * 
 * This code is a works as a modeule to crate thumbnail for the given image
 * with the given width and height.So you
 * 
 * can call this method by passwing the original image, file name to save as,
 * width and height.
 * 
 * To use this program do the following:
 * 
 * Insert the code in any Class Create the object for the class Call the
 * method by passing the required parameters mentiond above The file image.jpg
 * must exist already, thumbnail.jpg will be created (and any existing file of
 * that name overwritten).
 * 
 * Now let's see how this program works.
 * 
 * First the input image is loaded via Toolkit and MediaTracker. The third and
 * fourth program argument contain the maximum size of the thumbnail to be
 * created. The actual size of the thumbnail will be computed from that
 * maximum size and the actual size of the image (all sizes are given as
 * pixels). The code that does this is not really very readable, and also not
 * essential to loading and saving image files. But it is necessary to create
 * a thumbnail that is scaled correctly.
 * 
 * As an example, if the two arguments for the maximum thumbnail size are both
 * 100 and the image that was loaded is 400 times 200 pixels large, we want
 * the thumbnail to be 100 times 50 pixels large, not 100 times 100, because
 * the original image is twice as wide as it is high. A 100 times 100 pixel
 * thumbnail would contain a very skewed version of the original image.
 * 
 * Now that we have determined the size of the thumbnail we create a
 * BufferedImage of that size, named thumbImage. We ask for a Graphics2D
 * object for that new thumbnail image and call its drawImage method to draw
 * the original image on that new image. The call to drawImage does the actual
 * scaling. The rendering hints for bilinear interpolation can be left out
 * (remove the line with graphics2D.setRenderingHint) if high quality is not
 * required and speed more important. Note that embedded color profiles can
 * make scaling with bilinear interpolation very slow with certain versions of
 * the JDK; this supposedly gets better with JDK 6. If you can't rule out that
 * you are dealing with such JPEGs, make sure to not use the interpolation
 * hint or thumbnail creation will take forever (well, two minutes on a modern
 * system on a 6M image). For nicer results (at least in some cases) try
 * RenderingHints.VALUE_INTERPOLATION_BICUBIC instead of
 * RenderingHints.VALUE_INTERPOLATION_BILINEAR. Same warning as above.
 * 
 * In order to save the scaled-down image to a JPEG file, we create a buffered
 * FileOutputStream with the second argument as name and initialize the
 * necessary objects from the com.sun.image.codec.jpeg package.
 */
package net.sf.bt747.j2se.app.list;

import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

// import java.io.BufferedOutputStream;
// import java.io.FileOutputStream;
//
// import com.sun.image.codec.jpeg.JPEGCodec;
// import com.sun.image.codec.jpeg.JPEGEncodeParam;
// import com.sun.image.codec.jpeg.JPEGImageEncoder;

public final class MyThumbNail {
    // public static void main(String[] args) throws Exception {
    // new MyThumbNail().createThumbnail("C:/personal/a.jpg",
    // "C:/personal/thumb.jpg", 50, 30);
    // }

    public final static BufferedImage createThumbnail(final MediaTracker mt,
            final String imgFilePath, int thumbWidth, int thumbHeight)
            throws Exception {

        Image image = Toolkit.getDefaultToolkit().getImage(imgFilePath);
        MediaTracker mediaTracker = mt;
        if(mt==null) {
            mediaTracker = new MediaTracker(new Container());
        }
        mediaTracker.addImage(image, 0);
        mediaTracker.waitForID(0);
        final double thumbRatio = (double) thumbWidth / (double) thumbHeight;
        final int imageWidth = image.getWidth(null);
        final int imageHeight = image.getHeight(null);
        final double imageRatio = (double) imageWidth / (double) imageHeight;
        if (thumbRatio < imageRatio) {
            thumbHeight = (int) (thumbWidth / imageRatio);
        } else {
            thumbWidth = (int) (thumbHeight * imageRatio);
        }
        final BufferedImage thumbImage = new BufferedImage(thumbWidth,
                thumbHeight, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = thumbImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);

        return thumbImage;
        // BufferedOutputStream out = new BufferedOutputStream(
        // new FileOutputStream(thumbPath));
        // JPEGCodec.
        // JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        // JPEGEncodeParam param =
        // encoder.getDefaultJPEGEncodeParam(thumbImage);
        // int quality = 100;
        // param.setQuality((float) quality / 100.0f, false);
        // encoder.setJPEGEncodeParam(param);
        // encoder.encode(thumbImage);
        // out.close();
    }
}
