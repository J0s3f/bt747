/**
 * 
 */
package net.sf.bt747.j2se.app.exif.maker;

import net.sf.bt747.j2se.app.exif.ExifAttribute;
import net.sf.bt747.j2se.app.exif.ExifIfdBlock;
import net.sf.bt747.j2se.app.exif.ExifUtils;

/**
 * This decorator is simplified and supposes the value keeps constant size. This
 * is intended to adjust the offsets regarding the new position of the
 * makernotes.
 * 
 * @author Mario De Weerd
 */
public class CanonMakerNotesDecorator extends ExifAttribute {

	// IfdBlock Corresponding to makerNotes
	private ExifIfdBlock makerNotes;

	CanonMakerNotesDecorator(final ExifAttribute makerNote,
			final byte[] buffer, final int tiffHeaderStart,
			final boolean bigEndian) {
		super(makerNote.getTag(), makerNote.getType(), makerNote.getCount(),
				bigEndian);
		makerNotes = new ExifIfdBlock();
		makerNotes.read(buffer, makerNote.getValueIdx(), tiffHeaderStart,
				bigEndian);
	}

	/**
	 * Decorate the Attribute only if needed. See Decorator pattern (Design
	 * Patterns) for information.
	 * 
	 * @param makerNote
	 * @param buffer
	 * @param tiffHeaderStart
	 * @param bigEndian
	 * @return The decorated attribute.
	 */
	public final static ExifAttribute decorate(final ExifAttribute makerNote,
			final byte[] buffer, final int tiffHeaderStart,
			final boolean bigEndian) {
		//byte[] value = makerNote.getValue();

		//if (value[0] == ' ' && value[1] == 0) {
			return new CanonMakerNotesDecorator(makerNote, buffer,
					tiffHeaderStart, bigEndian);
		//} else {
		//	return makerNote;
		//}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.bt747.j2se.app.exif.ExifAttribute#fillBuffer(byte[], int,
	 * boolean, int, int)
	 */
	@Override
	public int fillBuffer(byte[] buffer, int recordOffset, boolean bigEndian,
			int payloadOffset, int tiffHeaderOffset) {
		// makerNotes.
		// public final void fillBuffer(final byte[] buffer,
		// final int tiffHeaderStart, final boolean bigEndian,
		// final int offset, final int nextIfdOffset) {

		ExifUtils
				.addShort2byte(buffer, recordOffset, bigEndian, super.getTag());
		ExifUtils.addShort2byte(buffer, recordOffset + 2, bigEndian, super
				.getType());
		// Count can also be size.
		ExifUtils.addLong4byte(buffer, recordOffset + 4, bigEndian, super
				.getCount());
		ExifUtils.addLong4byte(buffer, recordOffset + 8, bigEndian,
				payloadOffset - tiffHeaderOffset);

		// for (int i = 0; i < 6; i++) {
		// buffer[payloadOffset + i] = getValue()[i];
		// }
		// Copy above does not work - not checking why.
		//buffer[payloadOffset + 0] = ' ';
		//buffer[payloadOffset + 1] = 0;
		// makerNotes is in payload.
		makerNotes.fillBuffer(buffer, tiffHeaderOffset, bigEndian,
				payloadOffset, 0);
		//makerNotes.read(buffer, payloadOffset + 2, tiffHeaderOffset, bigEndian);
		// BT747Hashtable iter;
		// BT747Vector atrs = JavaLibBridge.getVectorInstance();
		// iter = makerNotes.getAttrIterator();
		// // Find list of attributes that are pointers
		// while(iter.hasNext()) {
		// final ExifAttribute a = (ExifAttribute) (iter.get(iter.nextKey()));
		// final int key = a.getTag();
		// if(a.isPointer()) {
		// atrs.addElement(a);
		// }
		// }
		//        
		// for(int i = atrs.size()-1;i>=0;i--) {
		// final int key = ((ExifAttribute)(atrs.elementAt(i))).getTag();
		// ExifAttribute b = makerNotes.get(key);
		// }
		return super.getValue().length; // Used payload
	}
}
