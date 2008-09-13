package gps.parser;

/*
 (c)2008 Thomas Mohme
 tmohme at sourceforge.net

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.DataInputStream;
import java.io.IOException;

/**
 * A LogBlockHeader primarily provides the engine with an initial LogFormat.
 * 
 * @author Thomas Mohme
 */
public class LogBlockHeader {
    public final static int LOG_BLOCK_HEADER_SIZE = 20 // header_info
    + 32 // sector_status
    + 454 // 0xFF
    + 6; // 0x2AFFBBBBBBBB

    private Short count = null;
    private LogFormat format = null;
    private Short fix = null;
    private Short period = null;
    private Short distance = null;
    private Short speed = null;

    LogBlockHeader(DataInputStream dis) throws IOException {
        // System.out.println("Analyzing Header");

        dis.mark(LOG_BLOCK_HEADER_SIZE);
        short n = Short.reverseBytes(dis.readShort());
        if (n != 0xffff) {
            this.count = n;
        }
        this.format = new LogFormat(Integer.reverseBytes(dis.readInt()));
        this.fix = Short.reverseBytes(dis.readShort());
        this.period = Short.reverseBytes(dis.readShort());
        this.distance = Short.reverseBytes(dis.readShort());
        this.speed = Short.reverseBytes(dis.readShort());

        dis.reset();
        dis.skip(LOG_BLOCK_HEADER_SIZE);

    } // constructor LogBlockHeader(byte[])

    protected LogFormat getFormat() {
        return this.format;
    } // LogFormat getFormat()

    @SuppressWarnings("nls")
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(this.getClass().getName());
        if (this.count != null) {
            sb.append(String.format(": %6d", this.count));
        } else {
            sb.append(String.format(":Unknown number of"));
        }
        sb.append(String.format(" records in format " + this.format + " ("
                + this.fix + "), p=" + (this.period / 10.0) + "s, d="
                + (this.distance / 10.0) + "m, s=" + (this.speed / 10.0)
                + "km/h"));

        return sb.toString();
    }
} // end of class LogBlockHeader
