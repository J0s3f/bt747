/**
 * 
 */
package net.sf.bt747.j2se.app.exif;

/**
 * This decorator is simplified and supposes the value keeps constant size.
 * This is intended to adjust the offsets regarding the new position of the
 * makernotes.
 * 
 * @author Mario De Weerd
 */
public class CasioMakerNotesDecorator extends ExifAttribute {

    // IfdBlock Corresponding to makerNotes
    private ExifIfdBlock makerNotes;

    CasioMakerNotesDecorator(final ExifAttribute makerNote,
            final byte[] buffer, final int tiffHeaderStart,
            final boolean bigEndian) {
        super(makerNote.getTag(), makerNote.getType(), makerNote.getCount(),
                bigEndian);
        makerNotes = new ExifIfdBlock();
        makerNotes.read(buffer, makerNote.getValueIdx() + 6, tiffHeaderStart,
                bigEndian);
    }

    /**
     * Decorate the Attribute only if needed. Se Decorator pattern (Design
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
        byte[] value = makerNote.getValue();

        if (value[0] == 'Q' && value[1] == 'V' && value[2] == 'C'
                && value[3] == 0 && value[4] == 0 && value[5] == 0) {
            return new CasioMakerNotesDecorator(makerNote, buffer,
                    tiffHeaderStart, bigEndian);
        } else {
            return makerNote;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.bt747.j2se.app.exif.ExifAttribute#fillBuffer(byte[], int,
     *      boolean, int, int)
     */
    @Override
    public int fillBuffer(byte[] buffer, int recordOffset, boolean bigEndian,
            int payloadOffset, int tiffHeaderOffset) {
        // makerNotes.
        // public final void fillBuffer(final byte[] buffer,
        // final int tiffHeaderStart, final boolean bigEndian,
        // final int offset, final int nextIfdOffset) {

        ExifUtils.addShort2byte(buffer, recordOffset, bigEndian, super
                .getTag());
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
        buffer[payloadOffset + 0] = 'Q';
        buffer[payloadOffset + 1] = 'V';
        buffer[payloadOffset + 2] = 'C';
        buffer[payloadOffset + 3] = 0;
        buffer[payloadOffset + 4] = 0;
        buffer[payloadOffset + 5] = 0;
        // makerNotes is in payload.
        makerNotes.fillBuffer(buffer, tiffHeaderOffset, bigEndian,
                payloadOffset + 6, 0);
        makerNotes.read(buffer, payloadOffset + 6, tiffHeaderOffset,
                bigEndian);
        if (makerNotes.hasTag(0x2000) && makerNotes.hasTag(0x0004)) {
            ExifAttribute a = makerNotes.get(0x0004); // PreviewImageStart
            ExifAttribute b = makerNotes.get(0x2000); // PreviewImageOffset.
            // b.getValueIndex is the original offset.
            a.setIntValue(0, b.getValueIdx() - tiffHeaderOffset);
            makerNotes.set(a);
            makerNotes.fillBuffer(buffer, tiffHeaderOffset, bigEndian,
                    payloadOffset + 6, 0);
        }
        return super.getValue().length; // Used payload
    }
}
