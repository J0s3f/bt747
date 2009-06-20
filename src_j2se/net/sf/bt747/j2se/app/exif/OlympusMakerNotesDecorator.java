/**
 * 
 */
package net.sf.bt747.j2se.app.exif;

import bt747.sys.Generic;

/**
 * This decorator is simplified and supposes the value keeps constant size.
 * This is intended to adjust the offsets regarding the new position of the
 * makernotes.
 * 
 * This Decorator is currently not of much use.  Converting the offsets
 * can not be done in this context.
 * 
 * @author Mario De Weerd
 */
public class OlympusMakerNotesDecorator extends ExifAttribute {

    private final static int OLYMPUS_CAMERASETTINGS2 = 0x2020;

    private final static int CAMERASETTINGS2_PREVIEWIMAGESTART = 0x0101;
    private final static int CAMERASETTINGS2_PREVIEWIMAGELENGTH = 0x0102;

    // IfdBlock Corresponding to makerNotes
    private ExifIfdBlock makerNotes;
    private ExifIfdBlock cameraSettings2;

    OlympusMakerNotesDecorator(final ExifAttribute makerNote,
            final byte[] buffer, final int tiffHeaderStart,
            final boolean bigEndian) {
        super(makerNote.getTag(), makerNote.getType(), makerNote.getCount(),
                bigEndian);
        makerNotes = new ExifIfdBlock();
        makerNotes.read(buffer, makerNote.getValueIdx() + header.length,
                tiffHeaderStart, bigEndian);
        
        // The maker notes seem to be in itself an EXIF block starting with OLYMPUS and followed with II\3\0.
        // So this looks pretty much like an ExifAPP1 marker.
        if (makerNotes.hasTag(OLYMPUS_CAMERASETTINGS2)) {
            cameraSettings2 = new ExifIfdBlock();
            cameraSettings2.read(buffer, makerNotes.get(
                    OLYMPUS_CAMERASETTINGS2).getValueIdx() + makerNote.getValueIdx() - header.length,
                    makerNote.getValueIdx() - header.length,
                    bigEndian);
            if (cameraSettings2.hasTag(CAMERASETTINGS2_PREVIEWIMAGESTART)) {
                ExifAttribute previewStart = cameraSettings2
                        .get(CAMERASETTINGS2_PREVIEWIMAGESTART);
                //Generic.debug(previewStart.toString());
            }
        }
        // Generic.debug(makerNotes.toString());
    }

    private final static char[] header = "OLYMPUS\0II\3\0".toCharArray();

    /**
     * Decorate the Attribute only if needed.
     * 
     * @param makerNote
     * @param buffer
     * @param tiffHeaderStart
     * @param bigEndian
     * @return
     */
    public final static ExifAttribute decorate(final ExifAttribute makerNote,
            final byte[] buffer, final int tiffHeaderStart,
            final boolean bigEndian) {
        final byte[] value = makerNote.getValue();

        for (int i = 0; i < header.length; i++) {
            if (value[i] != (byte) header[i]) {
                return makerNote;
            }
        }

        try {
            return new OlympusMakerNotesDecorator(makerNote, buffer,
                    tiffHeaderStart, bigEndian);
        } catch (Exception e) {
            Generic.debug("Problem reading OlympusMakerNotes",e);
            return makerNote;
        }
    }

    // Should correct the offset for PREVIEWIMAGESTART but that can not be done in this context.
    // Should add a central offset conversion table/functionality.
}
