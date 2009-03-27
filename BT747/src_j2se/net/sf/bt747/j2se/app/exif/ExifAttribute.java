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

import bt747.sys.Convert;
import bt747.sys.Generic;

/**
 * @author Mario
 * 
 */
public class ExifAttribute {

    private int tag;
    private int type;
    private int count;
    private byte[] value;
    private boolean bigEndian;
    private int valueOrgIdx = 0; // Index where value was found in original file for potential reference.

    public ExifAttribute() {

    }

    public ExifAttribute(final int tag, final int type, final int count, final boolean bigEndian) {
        this.bigEndian = bigEndian;
        setTag(tag);
        setType(type);
        setCount(count);
        newValue(count * getValueUnitSize(type));
    }

    private final void newValue(final int minsize) {
        int size;
        if ((minsize & 0x1) == 0) {
            size = minsize;
        } else {
            size = (minsize & 0xFFFE) + 2;
        }
        value = new byte[size];
    }

    public static final int getValueUnitSize(final int valueType) {
        switch (valueType) {
        case ExifConstants.BYTE:
        case ExifConstants.UNDEFINED:
            return 1;
        case ExifConstants.ASCII:
            return 1;
        case ExifConstants.SHORT:
            return 2;
        case ExifConstants.SSHORT:
            return 2;
        case ExifConstants.SBYTE:
            return 1;
        case ExifConstants.FLOAT:
            return 4;
        case ExifConstants.DOUBLE:
            return 8;
        case ExifConstants.LONG:
        case ExifConstants.SLONG:
            return 4;
        case ExifConstants.SRATIONAL:
        case ExifConstants.RATIONAL:
            return 8;
        default:
            return 0;
        }
    }

    public final int getValueIdx() {
        return valueOrgIdx;
    }
    public final int read(final byte[] buffer, final int currentIdxInBuffer,
            final int tiffHeaderStart, final boolean bigEndian) {
        this.bigEndian = bigEndian;
        if (currentIdxInBuffer + 12 < buffer.length) {
            setTag(ExifUtils.getShort2byte(buffer, currentIdxInBuffer,
                    bigEndian));
            setType(ExifUtils.getShort2byte(buffer, currentIdxInBuffer + 2,
                    bigEndian));
            setCount(ExifUtils.getLong4byte(buffer, currentIdxInBuffer + 4,
                    bigEndian));
            final int size = getValueUnitSize(getType()) * count;
            int valueIdx;
            boolean usePointer = false;
            // switch (getType()) {
            //
            // case ExifConstants.BYTE:
            // case ExifConstants.SHORT:
            // case ExifConstants.LONG:
            // case ExifConstants.UNDEFINED:
            // break;
            // default:
            // usePointer = true;
            // }

            valueIdx = currentIdxInBuffer + 8;
            if (size > 4) {
                usePointer = true;
            }
            if (usePointer) {
                valueIdx = tiffHeaderStart
                        + ExifUtils.getLong4byte(buffer,
                                currentIdxInBuffer + 8, bigEndian);
                valueOrgIdx = valueIdx;
            }
            newValue(size);
            for (int i = 0; i < size; i++) {
                value[i] = buffer[valueIdx + i];
            }
            return 12;
        } else {
            return 0;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        String s = "";
        StringBuffer sb;
        // TOD: read all values if count !=1
        switch (type) {
        case ExifConstants.BYTE:
            s = " Byte:" + getIntValue(0);
            break;
        case ExifConstants.SHORT:
            s = " Short:" + getIntValue(0);
            break;
        case ExifConstants.LONG:
            s = " Long:" + getIntValue(0);
            break;
        case ExifConstants.ASCII:
            sb = new StringBuffer(value.length + 10);
            sb.append(" ASCII:");
            for (int i = 0; (i < value.length) && (value[i] != 0); i++) {
                sb.append((char) value[i]);
            }
            s = sb.toString();
            break;
        case ExifConstants.UNDEFINED:
            sb = new StringBuffer(3 * value.length + 40);
            sb.append(" UNKNOWN:");
            for (int i = 0; i < value.length; i++) {
                sb.append(Convert.unsigned2hex(value[i], 2));
            }
            sb.append("  (");
            for (int i = 0; (i < value.length) && (value[i] != 0); i++) {
                sb.append((char) value[i]);
            }
            sb.append(')');
            s = sb.toString();
            break;
        case ExifConstants.RATIONAL:
            s = " Rat:" + getFloatValue(0);
            break;
        case ExifConstants.SRATIONAL:
            s = " Srat:" + getFloatValue(0);
            break;
        default:
            break;
        }

        return "Tag:" + Convert.unsigned2hex(getTag(), 4) + " Type:"
                + Convert.unsigned2hex(getType(), 4) + " Count:"
                + Convert.unsigned2hex(getCount(), 8) + s
        // + Convert.unsigned2hex(getValue(), 8)
        ;

    }

    public final int getPayloadSize() {
        if (value.length <= 4) {
            return 0;
        } else {
            return value.length;
        }
    }

    public int fillBuffer(final byte[] buffer, final int recordOffset,
            final boolean bigEndian, final int payloadOffset,
            final int tiffHeaderOffset) {
        ExifUtils.addShort2byte(buffer, recordOffset, bigEndian, tag);
        ExifUtils.addShort2byte(buffer, recordOffset + 2, bigEndian, type);
        ExifUtils.addLong4byte(buffer, recordOffset + 4, bigEndian, count);
        int valueOffset;
        int usedPayload;
        if (value.length <= 4) {
            valueOffset = recordOffset + 8;
            usedPayload = 0;
        } else {
            valueOffset = payloadOffset;
            ExifUtils.addLong4byte(buffer, recordOffset + 8, bigEndian,
                    payloadOffset - tiffHeaderOffset);
            usedPayload = value.length;
        }
        for (int i = 0; i < value.length; i++) {
            buffer[valueOffset + i] = value[i];
        }
        return usedPayload;
    }

    /**
     * @param tag
     *                the tag to set
     */
    public final void setTag(final int tag) {
        this.tag = tag;
    }

    /**
     * @return the tag
     */
    public final int getTag() {
        return tag;
    }

    /**
     * @param type
     *                the type to set
     */
    public final void setType(final int type) {
        this.type = type;
    }

    /**
     * @return the type
     */
    public final int getType() {
        return type;
    }

    /**
     * @param count
     *                the count to set
     */
    public final void setCount(final int count) {
        this.count = count;
    }

    /**
     * @return the count
     */
    public final int getCount() {
        return count;
    }

    /**
     * @param value
     *                the value to set
     */
    public final void setValue(final byte[] value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public final byte[] getValue() {
        return value;
    }

    public final float getFloatValue(final int idx) {
        if ((type == ExifConstants.RATIONAL)
                || (type == ExifConstants.SRATIONAL)) {
            final int offset = 8 * idx;
            if (offset + 8 <= value.length) {
                final int nom = ExifUtils.getLong4byte(value, offset,
                        bigEndian);
                final int denominator = ExifUtils.getLong4byte(value,
                        offset + 4, bigEndian);
                if (denominator == 0) {
                    Generic.debug("Invalid EXIF atribute value");
                    return nom;
                }
                if (((nom < 0) & (denominator < 0))
                        || ((nom >= 0) && (denominator > 0))) {
                    return ((float) nom) / ((float) (denominator));
                } else {
                    return (-(float) nom) / ((denominator));
                }
            }
        } else {
            Generic.debug("Attribute " + type + " is not a float");
        }
        return 0.0f; // TODO: NaN
    }

    public final String getStringValue() {
        final StringBuffer sb = new StringBuffer(value.length);
        for (int i = 0; i < value.length; i++) {
            sb.append((char) value[i]);
        }
        return sb.toString();
    }

    public final int getIntValue(final int idx) {
        final int unitSize = getValueUnitSize(type);
        final int offset = unitSize * idx;
        if (offset + unitSize <= value.length) {
            switch (type) {
            case ExifConstants.BYTE:
                return ExifUtils.getByte(value, offset, bigEndian);
            case ExifConstants.SHORT:
                return ExifUtils.getShort2byte(value, offset, bigEndian);
            case ExifConstants.LONG:
                return ExifUtils.getLong4byte(value, offset, bigEndian);
            default:
                return 0;
            }
        }
        return 0; // TODO: similar to NaN?

    }

    public final void setIntValue(final int idx, final int val) {
        final int unitSize = getValueUnitSize(type);
        final int offset = unitSize * idx;
        if (offset + unitSize <= value.length) {
            switch (type) {
            case ExifConstants.BYTE:
            case ExifConstants.UNDEFINED:
                value[offset] = (byte) val;
                break;
            case ExifConstants.SHORT:
                ExifUtils.addShort2byte(value, offset, bigEndian, val);
                break;
            case ExifConstants.LONG:
                ExifUtils.addLong4byte(value, offset, bigEndian, val);
                break;
            default:
                break;
            }
        }
    }

    public final void setFloatValue(final int idx, final int nom,
            final int den) {
        if ((type == ExifConstants.RATIONAL)
                || (type == ExifConstants.SRATIONAL)) {
            final int offset = 8 * idx;
            if (offset + 8 <= value.length) {
                ExifUtils.addLong4byte(value, offset, bigEndian, nom);
                ExifUtils.addLong4byte(value, offset + 4, bigEndian, den);
            }
        } else {
            Generic.debug("Attribute " + type + " is not a float");
        }
    }

    public final float setGpsFloatValue(final double d) {
        double g;
        if ((type == ExifConstants.RATIONAL) && (count == 3)) {

            if (d < 0) {
                g = -d;
            } else {
                g = d;
            }

            int nom;
            int den;
            den = 1;
            nom = (int) g;
            g -= nom;
            setFloatValue(0, nom, den);

            den = 1;
            g *= 60;
            g *= den;
            nom = (int) g;
            g -= nom;
            setFloatValue(1, nom, den);

            den = 10000;
            g *= 60;
            g *= den;
            nom = (int) g;
            g -= nom;
            setFloatValue(2, nom, den);

        } else {
            Generic.debug("Attribute " + type + " is not a float");
        }
        return 0.0f; // TODO: NaN
    }

    public final void setStringValue(final String s) {
        if (type == ExifConstants.ASCII) {
            if (s.length() + 1 != count) {
                count = s.length() + 1;
                newValue(count);
            }
            final char[] carr = s.toCharArray();
            for (int i = 0; i < carr.length; i++) {
                value[i] = (byte) carr[i];
            }
            value[count - 1] = 0;
        } else {
            Generic.debug("Attribute " + type + " is not ASCII");
        }

    }

}
