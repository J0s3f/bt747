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
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;

import bt747.j2se_view.model.ImageData;
import bt747.sys.interfaces.BT747Path;

// import java.io.BufferedOutputStream;
// import java.io.FileOutputStream;
//
// import com.sun.image.codec.jpeg.JPEGCodec;
// import com.sun.image.codec.jpeg.JPEGEncodeParam;
// import com.sun.image.codec.jpeg.JPEGImageEncoder;

public final class MyThumbNail {
	private static MediaTracker mediaTracker;
	private static int lastId = 0;

	public final static BufferedImage createThumbnail(final MediaTracker mt,
			final String imgFilePath, int thumbWidth, int thumbHeight)
			throws Exception {
		BufferedImage img = null;
		// if (bt747.Version.VERSION_NUMBER.equals("2.dev") && hasSanselan()) {
		// return createThumbnailSanselan(mt, imgFilePath, thumbWidth,
		// thumbHeight);
		// }

		try {
			img = createThumbnailFromJPGThumbNail(mt, imgFilePath, thumbWidth,
					thumbHeight);
		} catch (Exception e) {
			// TODO: handle exception
		}

		if (img == null) {
			img = createThumbnailSingleStep(mt, imgFilePath, thumbWidth,
					thumbHeight);
		}
		return img;
	}

	private static synchronized int getNextId() {
		return ++lastId;
	}

	private final static MediaTracker getMediaTracker(final MediaTracker mt) {
		if (mt != null) {
			return mt;
		} else {
			if (mediaTracker == null) {
				mediaTracker = new MediaTracker(new Container());
			}
		}
		return mediaTracker;
	}

	public final static BufferedImage createThumbnailFromJPGThumbNail(
			final MediaTracker mt, final String imgFilePath, int thumbWidth,
			int thumbHeight) throws Exception {
		ImageData i = ImageData.getInstance(new BT747Path(imgFilePath));
		byte[] t = i.getThumbnailData();
		final Image image = Toolkit.getDefaultToolkit().createImage(t);
		final int id = getNextId();

		mediaTracker = getMediaTracker(mt);

		mediaTracker.addImage(image, id);
		mediaTracker.waitForID(id);

		try {
			if ((Boolean) image.getClass().getMethod("hasError").invoke(image)) {
				return null;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		final BufferedImage result = getThumbnail(thumbWidth, thumbHeight,
				image);
		image.flush(); // Not sure this helps, but trying.
		return result;
	}

	// public static void main(String[] args) throws Exception {
	// new MyThumbNail().createThumbnail("C:/personal/a.jpg",
	// "C:/personal/thumb.jpg", 50, 30);
	// }

	public final static BufferedImage createThumbnailSingleStep(
			final MediaTracker mt, final String imgFilePath, int thumbWidth,
			int thumbHeight) throws Exception {
		// final Image image =
		// Toolkit.getDefaultToolkit().getImage(imgFilePath);
		final Image image = Toolkit.getDefaultToolkit()
				.createImage(imgFilePath);
		final int id = getNextId();

		mediaTracker = getMediaTracker(mt);

		mediaTracker.addImage(image, id);
		mediaTracker.waitForID(id);

		final BufferedImage result = getThumbnail(thumbWidth, thumbHeight,
				image);
		image.flush(); // Not sure this helps, but trying.
		return result;
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

	/**
	 * @param thumbWidth
	 * @param thumbHeight
	 * @param image
	 * @return
	 */
	private static BufferedImage getThumbnail(int thumbWidth, int thumbHeight,
			final Image image) {
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
		graphics2D.dispose();
		return thumbImage;
	}

	// Following function taken from the public domain at:
	// http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html?page=last#thread
	/**
	 * Convenience method that returns a scaled instance of the provided {@code
	 * BufferedImage}.
	 * 
	 * @param img
	 *            the original image to be scaled
	 * @param targetWidth
	 *            the desired width of the scaled instance, in pixels
	 * @param targetHeight
	 *            the desired height of the scaled instance, in pixels
	 * @param hint
	 *            one of the rendering hints that corresponds to {@code
	 *            RenderingHints.KEY_INTERPOLATION} (e.g. {@code
	 *            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR}, {@code
	 *            RenderingHints.VALUE_INTERPOLATION_BILINEAR}, {@code
	 *            RenderingHints.VALUE_INTERPOLATION_BICUBIC})
	 * @param higherQuality
	 *            if true, this method will use a multi-step scaling technique
	 *            that provides higher quality than the usual one-step technique
	 *            (only useful in downscaling cases, where {@code targetWidth}
	 *            or {@code targetHeight} is smaller than the original
	 *            dimensions, and generally only when the {@code BILINEAR} hint
	 *            is specified)
	 * @return a scaled version of the original {@code BufferedImage}
	 */
	public static BufferedImage getScaledInstanceMultiStep(
			final BufferedImage img, final int targetWidth,
			final int targetHeight, final Object hint,
			final boolean higherQuality) {
		int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
				: BufferedImage.TYPE_INT_ARGB;
		BufferedImage ret = (BufferedImage) img;
		int w, h;
		if (higherQuality) {
			// Use multi-step technique: start with original size, then
			// scale down in multiple passes with drawImage()
			// until the target size is reached
			w = img.getWidth();
			h = img.getHeight();
		} else {
			// Use one-step technique: scale directly from original
			// size to target size with a single drawImage() call
			w = targetWidth;
			h = targetHeight;
		}

		do {
			if (higherQuality && w > targetWidth) {
				w /= 2;
				if (w < targetWidth) {
					w = targetWidth;
				}
			}

			if (higherQuality && h > targetHeight) {
				h /= 2;
				if (h < targetHeight) {
					h = targetHeight;
				}
			}

			BufferedImage tmp = new BufferedImage(w, h, type);
			Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();

			ret = tmp;
		} while (w != targetWidth || h != targetHeight);

		return ret;
	}

	private static int hasSanselanState = 0;

	private static boolean hasSanselan() {
		if (hasSanselanState == 0) {
			try {
				if (Class.forName("org.apache.sanselan.Sanselan") != null)
					hasSanselanState = 1;
			} catch (ClassNotFoundException e) {
				hasSanselanState = -1;
			}
		}
		return hasSanselanState == 1;
	}

	public final static BufferedImage createThumbnailSanselan(
			final MediaTracker mt, final String imgFilePath, int thumbWidth,
			int thumbHeight) throws Exception {
		final IImageMetadata metadata = Sanselan.getMetadata(new File(
				imgFilePath));
		BufferedImage thumb = null;
		if (metadata instanceof JpegImageMetadata) {
			JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

			try {
				thumb = jpegMetadata.getEXIFThumbnail();
			} catch (ImageReadException e) {
				// This error is not an issue
			}
		}

		if (thumb != null) {
			thumb.getWidth();
			thumb = getThumbnail(thumbWidth, thumbHeight, thumb);
		} else {
			thumb = createThumbnailSingleStep(mt, imgFilePath, thumbWidth,
					thumbHeight);
		}
		return thumb;
	}
}
