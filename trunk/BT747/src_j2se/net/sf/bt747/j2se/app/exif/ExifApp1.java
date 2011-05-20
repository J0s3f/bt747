// ********************************************************************
// *** BT747 ***
// *** (c)2007-2008 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. ***
// *** ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
package net.sf.bt747.j2se.app.exif;

import net.sf.bt747.j2se.app.exif.maker.CanonMakerNotesDecorator;
import net.sf.bt747.j2se.app.exif.maker.CasioMakerNotesDecorator;
import net.sf.bt747.j2se.app.exif.maker.OlympusMakerNotesDecorator;
import bt747.sys.Generic;

/**
 * @author Mario De Weerd
 * 
 *         Reading and writing the relevant EXIF data should not be too
 *         complicated.
 * 
 *         Strategy is to read the header and the EXIF data. Reading first 64kB
 *         + SOI size bytes is sufficient.
 * 
 *         EXIF data must be stored in a manner that allows reconstruction
 *         later.
 * 
 *         Writing Exif data consists in replacing the APP1 Block and updating
 *         the SOI Marker.
 * 
 *         Specification at http://www.exif.org/Exif2-2.PDF
 */
public class ExifApp1 {

	private ExifIfdBlock Ifd0;
	private ExifIfdBlock exifBlock;
	private ExifIfdBlock gpsBlock;
	private ExifIfdBlock Ifd1;
	private ExifIfdBlock Interoperability;
	private boolean bigEndian;

	public final ExifAttribute newExifAttribute(final int tag, final int type,
			final int count) {
		return new ExifAttribute(tag, type, count, bigEndian);
	}

	private byte[] thumbnail;

	public final int read(final byte[] buffer, final int initialIdxInBuffer) {
		int currentIdxInBuffer = initialIdxInBuffer;
		int tiffHeaderStart = currentIdxInBuffer;
		// Next is Tiff header (8 bytes)
		// Byte order8
		if ((buffer[tiffHeaderStart] == 'I')
				&& (buffer[tiffHeaderStart + 1] == 'I')) {
			// little endian (LSB first)
			bigEndian = false;
		} else {
			if ((buffer[tiffHeaderStart] == 'M')
					&& (buffer[tiffHeaderStart + 1] == 'M')) {
				bigEndian = true;
			} else {
				// TODO: bad format
				return -1;
			}
		}
		if (ExifUtils.getShort2byte(buffer, tiffHeaderStart + 2, bigEndian) == 0x002A) {
			// This must be the case
		} else {
			// TODO: bad format
			return -1;
		}
		// TODO: not sure if this value uses defined
		// endianess
		int ifd0Offset = ExifUtils.getLong4byte(buffer, tiffHeaderStart + 4,
				bigEndian);
		// Then IFDs.

		// Go through IF0 to find EXIF information.
		currentIdxInBuffer = ifd0Offset + tiffHeaderStart;

		Ifd0 = new ExifIfdBlock();
		int ifd1Offset;
		ifd1Offset = Ifd0.read(buffer, currentIdxInBuffer, tiffHeaderStart,
				bigEndian);

		// bt747.sys.Generic.debug("EXIF Header");

		ExifAttribute exifAtr;
		exifAtr = Ifd0.get(ExifConstants.TAG_EXIF);
		if (exifAtr != null) {
			exifBlock = new ExifIfdBlock();
			exifBlock.read(buffer, exifAtr.getIntValue(0) + tiffHeaderStart,
					tiffHeaderStart, bigEndian);

			exifAtr = exifBlock
					.get(ExifConstants.TAG_INTEROPERABILITY_IFD_POINTER);
			if (exifAtr != null) {
				Interoperability = new ExifIfdBlock();
				Interoperability.read(buffer, exifAtr.getIntValue(0)
						+ tiffHeaderStart, tiffHeaderStart, bigEndian);
			}
		}

		// bt747.sys.Generic.debug("GPS Header");
		exifAtr = Ifd0.get(ExifConstants.TAG_GPSINFO);
		if (exifAtr != null) {
			gpsBlock = new ExifIfdBlock();
			gpsBlock.read(buffer, exifAtr.getIntValue(0) + tiffHeaderStart,
					tiffHeaderStart, bigEndian);
		}

		if (ifd1Offset != 0) {
			// bt747.sys.Generic.debug("Ifd1 Block");
			Ifd1 = new ExifIfdBlock();
			// int nextOffset;
			// nextOffset =
			Ifd1.read(buffer, ifd1Offset + tiffHeaderStart, tiffHeaderStart,
					bigEndian);

			// EXIF offset is given in IFD0
			// bt747.sys.Generic.debug("EXIF read");
			int jpegAtrOffset;
			ExifAttribute atr;
			atr = Ifd1.get(ExifConstants.TAG_JPEGINTERCHANGEFORMAT);
			if (atr != null) {
				jpegAtrOffset = atr.getIntValue(0);
				atr = Ifd1.get(ExifConstants.TAG_JPEGINTERCHANGEFORMATLENGTH);
				if (atr != null) {
					int len = atr.getIntValue(0);
					if (len != 0) {
						int bufferIdx = tiffHeaderStart + jpegAtrOffset;
						jpegInterchange = new byte[len];
						for (int i = 0; i < jpegInterchange.length; i++) {
							jpegInterchange[i] = buffer[bufferIdx++];
						}
					}
				}
			}
			thumbnail = getThumbnailData(buffer, initialIdxInBuffer);
		}
		// MakerNotes
		// Some maker notes have offsets relative to the TIFF header.
		// IMHO this is not a good methodology, but it is what we have.
		if (exifBlock != null) {
			if (exifBlock.hasTag(ExifConstants.TAG_MAKERNOTE)) {
				ExifAttribute atr;
				atr = exifBlock.get(ExifConstants.TAG_MAKERNOTE);
				if (Ifd0.hasTag(ExifConstants.TAG_MAKE)
						&& Ifd0.get(ExifConstants.TAG_MAKE).getStringValue()
								.equals("Canon\0")) {
					atr = CanonMakerNotesDecorator.decorate(atr, buffer,
							tiffHeaderStart, bigEndian);
				} else {
					atr = CasioMakerNotesDecorator.decorate(atr, buffer,
							tiffHeaderStart, bigEndian);
					atr = OlympusMakerNotesDecorator.decorate(atr, buffer,
							tiffHeaderStart, bigEndian);
				}
				exifBlock.set(atr);
			}
		}
		return 0;
	}

	private byte[] jpegInterchange = null;

	public final int getByteSize() {
		// TODO: Create Ifd0 block if needed
		int size;

		// Header takes 8 bytes
		size = 8;
		// Add IfdBlocks
		if (Ifd0 != null) {
			initIfd0Block();
			size += Ifd0.getByteSize();
		}
		if (exifBlock != null) {
			if (Interoperability != null) {
				size += Interoperability.getByteSize();
				ExifAttribute atr;
				atr = newExifAttribute(
						ExifConstants.TAG_INTEROPERABILITY_IFD_POINTER,
						ExifConstants.LONG, 1);
				atr.setIntValue(0, 0);
				exifBlock.set(atr);
			}
			size += exifBlock.getByteSize();
		}
		if (gpsBlock != null) {
			size += gpsBlock.getByteSize();
		}
		if (Ifd1 != null) {
			size += Ifd1.getByteSize();
		}
		if (jpegInterchange != null) {
			size += jpegInterchange.length;
		}
		return size;
	}

	// Unfinished!
	public final void fillBuffer(final byte[] buffer, final int tiffHeaderStart) {
		int currentIdx = tiffHeaderStart;
		// Next is Tiff header (8 bytes)
		if (bigEndian) {
			buffer[currentIdx++] = 'M';
			buffer[currentIdx++] = 'M';
		} else {
			buffer[currentIdx++] = 'I';
			buffer[currentIdx++] = 'I';
		}
		currentIdx += ExifUtils.addShort2byte(buffer, currentIdx, bigEndian,
				0x002A);

		// ifd0Offset - is next position: 8 !

		currentIdx += ExifUtils.addLong4byte(buffer, currentIdx, bigEndian, 8);

		// Ifd0 has to exist. TODO: check this ?!
		int nextBlock;
		int ifd0size;
		int ifd1size = 0;
		int ifd1offset = 0;
		int interopOffset = 0;
		int exifBlockSize = 0;
		int interopBlockSize = 0;
		int exifOffset = 0;
		int gpsBlockSize = 0;
		int gpsOffset = 0;
		int jpgInterChangeOffset = 0;
		ifd0size = Ifd0.getByteSize();
		nextBlock = ifd0size;
		nextBlock += currentIdx - tiffHeaderStart;
		if (exifBlock != null) {
			ExifAttribute atr;
			if (Interoperability != null) {
				// Placeholder
				atr = newExifAttribute(
						ExifConstants.TAG_INTEROPERABILITY_IFD_POINTER,
						ExifConstants.LONG, 1);
				atr.setIntValue(0, interopOffset);
				exifBlock.set(atr);
			}
			exifBlockSize = exifBlock.getByteSize();
			exifOffset = nextBlock;
			nextBlock += exifBlockSize;
			if (Interoperability != null) {
				interopBlockSize = Interoperability.getByteSize();
				interopOffset = nextBlock;
				nextBlock += interopBlockSize;
			}
			if (gpsBlock != null) {
				gpsBlockSize = gpsBlock.getByteSize();
				gpsOffset = nextBlock;
				nextBlock += gpsBlockSize;
			}
			if (Ifd1 != null) {
				ifd1size = Ifd1.getByteSize();
				ifd1offset = nextBlock;
				nextBlock += ifd1size;
			}
		}
		if (jpegInterchange != null) {
			jpgInterChangeOffset = nextBlock;
			nextBlock += jpegInterchange.length;
		}

		// TODO: nextBlock may need to point to current block.
		Ifd0.setNextIfdBlockOffset(ifd1offset);

		ExifAttribute atr;
		if (exifOffset != 0) {
			atr = newExifAttribute(ExifConstants.TAG_EXIF, ExifConstants.LONG,
					1);
			atr.setIntValue(0, exifOffset);
			Ifd0.set(atr);
		}
		if (gpsOffset != 0) {
			atr = newExifAttribute(ExifConstants.TAG_GPSINFO,
					ExifConstants.LONG, 1);
			atr.setIntValue(0, gpsOffset);
			Ifd0.set(atr);
		}

		atr = Ifd0.get(ExifConstants.TAG_STRIPOFFSETS);
		if (atr != null) {
			// TODO adjust offset
			Generic.debug("Has stripoffsets - potential problem.");
			// atr.setIntValue(0, val);
		}
		Ifd0.fillBuffer(buffer, tiffHeaderStart, bigEndian, currentIdx,
				ifd1offset);

		currentIdx += ifd0size;

		if (exifBlock != null) {
			int size;
			if (Interoperability != null) {
				// Actual value
				atr = newExifAttribute(
						ExifConstants.TAG_INTEROPERABILITY_IFD_POINTER,
						ExifConstants.LONG, 1);
				atr.setIntValue(0, interopOffset);
				exifBlock.set(atr);
			}
			size = exifBlock.getByteSize();
			nextBlock = size;
			nextBlock += currentIdx - tiffHeaderStart;
			if (exifOffset != currentIdx - tiffHeaderStart) {
				Generic.debug("Problem in offset Exif");
			}
			// TODO Need to adjust MakerNotes.
			// if(makerNotes!=null) {
			// byte[] makerValue;
			// makerNotes.fillBuffer(makerValue, tiffHeaderStart, bigEndian, 6,
			// 0);
			// }
			exifBlock.fillBuffer(buffer, tiffHeaderStart, bigEndian,
					currentIdx, 0);
			currentIdx += size;
		}

		if (Interoperability != null) {
			int size;
			size = Interoperability.getByteSize();
			nextBlock = size;
			nextBlock += currentIdx - tiffHeaderStart;
			if (interopOffset != currentIdx - tiffHeaderStart) {
				Generic.debug("Problem in offset Interop");
			}
			Interoperability.fillBuffer(buffer, tiffHeaderStart, bigEndian,
					currentIdx, 0);
			currentIdx += size;
		}
		if (gpsBlock != null) {
			int size;
			size = gpsBlock.getByteSize();
			nextBlock = size;
			nextBlock += currentIdx - tiffHeaderStart;
			if (gpsOffset != currentIdx - tiffHeaderStart) {
				Generic.debug("Problem in offset Gps");
			}
			gpsBlock.fillBuffer(buffer, tiffHeaderStart, bigEndian, currentIdx,
					0);
			currentIdx += size;
		}
		if (Ifd1 != null) {
			int size;
			if (jpegInterchange != null) {
				atr = newExifAttribute(ExifConstants.TAG_JPEGINTERCHANGEFORMAT,
						ExifConstants.LONG, 1);
				atr.setIntValue(0, jpgInterChangeOffset);
				Ifd1.set(atr);
				atr = newExifAttribute(
						ExifConstants.TAG_JPEGINTERCHANGEFORMATLENGTH,
						ExifConstants.LONG, 1);
				atr.setIntValue(0, jpegInterchange.length);
				Ifd1.set(atr);
			}
			atr = Ifd1.get(ExifConstants.TAG_STRIPOFFSETS);
			if (atr != null) {
				// TODO adjust offset
				// atr.setIntValue(0, val);
				Generic.debug("Strip offset adjustment not implemented");
			}
			size = Ifd1.getByteSize();
			nextBlock = Ifd1.getByteSize();
			nextBlock += currentIdx - tiffHeaderStart;
			if (ifd1offset != currentIdx - tiffHeaderStart) {
				Generic.debug("Problem in offset Ifd1");
			}
			Ifd1.fillBuffer(buffer, tiffHeaderStart, bigEndian, currentIdx, 0);
			currentIdx += size;
		}
		if (jpegInterchange != null) {
			// TODO: adjust pointer in previous structure.
			for (int i = 0; i < jpegInterchange.length; i++) {
				buffer[currentIdx++] = jpegInterchange[i];
			}
		}
		// Write jpeginterchange
	}

	public final ExifAttribute getIfd0Attribute(final int tag) {
		if (Ifd0 != null) {
			return Ifd0.get(tag);
		} else {
			return null;
		}
	}

	public final ExifAttribute getExifAttribute(final int tag) {
		if (exifBlock != null) {
			return exifBlock.get(tag);
		} else {
			return null;
		}
	}

	public final ExifAttribute getGpsAttribute(final int tag) {
		if (gpsBlock != null) {
			return gpsBlock.get(tag);
		} else {
			return null;
		}
	}

	private final void initIfd0Block() {
		if (Ifd0 == null) {
			Ifd0 = new ExifIfdBlock();
			ExifAttribute v;
			if (exifBlock != null) {
				v = newExifAttribute(ExifConstants.TAG_EXIF,
						ExifConstants.LONG, 1);
				Ifd0.set(v);
			}
			if (gpsBlock != null) {
				v = newExifAttribute(ExifConstants.TAG_GPSINFO,
						ExifConstants.LONG, 1);
				Ifd0.set(v);
			}
			if (Ifd1 != null) {
				v = newExifAttribute(
						ExifConstants.TAG_INTEROPERABILITY_IFD_POINTER,
						ExifConstants.LONG, 1);
				Ifd0.set(v);
			}

		}
	}

	private final void initIfd1Block() {
		if (Ifd1 == null) {
			Ifd1 = new ExifIfdBlock();
			ExifAttribute v;
			v = newExifAttribute(
					ExifConstants.TAG_INTEROPERABILITY_IFD_POINTER,
					ExifConstants.LONG, 1);
			Ifd0.set(v);
		}
	}

	public final byte[] getThumbnailData() {
		return thumbnail;
	}

	private static byte[] copyOfRange(byte[] buffer, int startIdx, int endIdx) {
		byte[] result = new byte[endIdx - startIdx + 1];
		for (int idx = startIdx, tgtIdx = 0; idx < endIdx; idx++, tgtIdx++) {
			result[tgtIdx] = buffer[idx];
		}
		return result;
	}

	private final byte[] getThumbnailData(final byte[] buffer,
			final int initialIdxInBuffer) {
		byte[] ret;
		ret = null; // Default value

		if (Ifd1 != null) {
			if (Ifd1.hasTag(ExifConstants.TAG_JPEGINTERCHANGEFORMAT)
					&& Ifd1
							.hasTag(ExifConstants.TAG_JPEGINTERCHANGEFORMATLENGTH)) {
				int idx = Ifd1.get(ExifConstants.TAG_JPEGINTERCHANGEFORMAT)
						.getIntValue(0);
				int len = Ifd1.get(
						ExifConstants.TAG_JPEGINTERCHANGEFORMATLENGTH)
						.getIntValue(0);

				byte[] b = ExifApp1.copyOfRange(buffer, initialIdxInBuffer
						+ idx, initialIdxInBuffer + idx + len);
				ret = b;

			}
		}

		return ret;
	}

	public final void setIfd0Attribute(final ExifAttribute atr) {
		if (Ifd0 == null) {
			Ifd0 = new ExifIfdBlock();
			ExifAttribute v;
			v = newExifAttribute(ExifConstants.TAG_EXIFVERSION,
					ExifConstants.UNDEFINED, 4);
			v.setIntValue(0, '0');
			v.setIntValue(1, '2');
			v.setIntValue(2, '2');
			v.setIntValue(3, '0');
			Ifd0.set(v);
		}
		Ifd0.set(atr);
	}

	public final void setExifAttribute(final ExifAttribute atr) {
		if (exifBlock == null) {
			exifBlock = new ExifIfdBlock();
			ExifAttribute v;
			v = newExifAttribute(ExifConstants.TAG_EXIFVERSION,
					ExifConstants.UNDEFINED, 4);
			v.setIntValue(0, '0');
			v.setIntValue(1, '2');
			v.setIntValue(2, '2');
			v.setIntValue(3, '0');
			exifBlock.set(v);
			if (Ifd0 == null) {
				initIfd0Block();
			}
			v = newExifAttribute(ExifConstants.TAG_EXIF, ExifConstants.LONG, 1);
			Ifd0.set(v);
		}
		exifBlock.set(atr);
	}

	public final void setGpsAttribute(final ExifAttribute atr) {
		if (gpsBlock == null) {
			gpsBlock = new ExifIfdBlock();
			ExifAttribute v;
			v = newExifAttribute(ExifConstants.TAG_GPSVERSIONID,
					ExifConstants.BYTE, 4);
			v.setIntValue(0, 0x02);
			v.setIntValue(1, 0x02);
			v.setIntValue(2, 0x00);
			v.setIntValue(3, 0x00);
			gpsBlock.set(v);
			if (Ifd0 == null) {
				initIfd0Block();
			}
			v = newExifAttribute(ExifConstants.TAG_GPSINFO, ExifConstants.LONG,
					1);
			Ifd0.set(v);
		}
		gpsBlock.set(atr);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String r = "";
		if (Ifd0 != null) {
			r += Ifd0.toString();
		}
		if (exifBlock != null) {
			r += exifBlock.toString();
		}
		if (gpsBlock != null) {
			r += gpsBlock.toString();
		}
		if (Ifd1 != null) {
			r += Ifd1.toString();
		}
		if (Interoperability != null) {
			r += Interoperability.toString();
		}
		return r;
	}
}
