//********************************************************************
//***                           BT747                             ***
//***                 (c)2007-2008 Mario De Weerd                  ***
//***                     m.deweerd@ieee.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER.                                     ***
//***                                                              ***
//***  See the GNU General Public License Version 3 for details.   ***
//***  *********************************************************** ***
package bt747.j2se_view.exif;

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
    private int denominator;
    private boolean endian;

    public ExifAttribute() {

    }

    public ExifAttribute(final int tag, final int type, final int count) {
        setTag(tag);
        setType(type);
        setCount(count);
        value = new byte[count * getValueUnitSize(type)];
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

    public final int read(final byte[] buffer, final int currentIdxInBuffer,
            final int tiffHeaderStart, final boolean bigEndian) {
        endian = bigEndian;
        if (currentIdxInBuffer + 12 < buffer.length) {
            setTag(ExifUtils.getShort2byte(buffer, currentIdxInBuffer,
                    bigEndian));
            setType(ExifUtils.getShort2byte(buffer, currentIdxInBuffer + 2,
                    bigEndian));
            setCount(ExifUtils.getLong4byte(buffer, currentIdxInBuffer + 4,
                    bigEndian));
            int size = getValueUnitSize(getType()) * count;
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
            }
            value = new byte[size];
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
        switch (type) {
        case ExifConstants.BYTE:
            s = " Byte:" + getIntValue();
            break;
        case ExifConstants.SHORT:
            s = " Short:" + getIntValue();
            break;
        case ExifConstants.LONG:
            s = " Long:" + getIntValue();
            break;
        case ExifConstants.ASCII:
            sb = new StringBuffer(value.length + 10);
            sb.append(" ASCII:");
            for (int i = 0; i < value.length && value[i] != 0; i++) {
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
            for (int i = 0; i < value.length && value[i] != 0; i++) {
                sb.append((char) value[i]);
            }
            sb.append(')');
            s = sb.toString();
            break;
        case ExifConstants.RATIONAL:
            s = " Rat:" + getFloatValue();
            break;
        case ExifConstants.SRATIONAL:
            s = " Srat:" + getFloatValue();
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

    /**
     * @param tag
     *            the tag to set
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
     *            the type to set
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
     *            the count to set
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
     *            the value to set
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

    public final float getFloatValue() {
        int nom = ExifUtils.getLong4byte(value, 0, endian);
        int denominator = ExifUtils.getLong4byte(value, 4, endian);
        if (denominator == 0) {
            Generic.debug("Invalid EXIF atribute value");
            return (float) nom;
        }
        if ((nom < 0 & denominator < 0) || (nom >= 0 && denominator > 0)) {
            return ((float) nom) / ((float) (denominator));
        } else {
            return (-(float) nom) / ((float) (denominator));
        }
    }

    public final String getStringValue() {
        StringBuffer sb = new StringBuffer(value.length);
        for (int i = 0; i < value.length; i++) {
            sb.append(Convert.unsigned2hex(value[i], 2));
        }
        return sb.toString();
    }

    public final int getIntValue() {
        switch (type) {
        case ExifConstants.BYTE:
            return ExifUtils.getByte(value, 0, endian);
        case ExifConstants.SHORT:
            return ExifUtils.getShort2byte(value, 0, endian);
        case ExifConstants.LONG:
            return ExifUtils.getLong4byte(value, 0, endian);
        default:
            return 0;
        }

    }

    /**
     * @return the denominator
     */
    public final int getDenominator() {
        return this.denominator;
    }

    /**
     * @param denominator
     *            the denominator to set
     */
    public final void setDenominator(final int denominator) {
        this.denominator = denominator;
    }
}
