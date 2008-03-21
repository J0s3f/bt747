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
 * A LogDataParser knows the technical representation of individual data items on the stream.
 * <br>
 * This class is used to parse holux logs.
 * @author Thomas Mohme
 */
public class LogDataParserHolux extends LogDataParser{

	public LogDataParserHolux() {
		super();
	}

	LogDataParserHolux(DataInputStream aDIS) {
		super(aDIS);
	}
	
	@Override
	public Double getLatitude() throws IOException {
		int i = Integer.reverseBytes(super.dis.readInt());
		return (double)Float.intBitsToFloat(i);
	}
	@Override
	public int getLatitudeSize() { return 4; }
	
	@Override
	public Double getLongitude() throws IOException {
		int i = Integer.reverseBytes(super.dis.readInt());
		return (double)Float.intBitsToFloat(i);
	}
	@Override
	public int getLongitudeSize() { return 4; }
	
	@Override
	public Float getHeight() throws IOException {
		byte[] bytes = new byte[3];
		bytes[2] = super.dis.readByte();
		bytes[1] = super.dis.readByte();
		bytes[0] = super.dis.readByte();
        int i = (0xFF&bytes[2])<<8
        		|(0xFF&bytes[1])<<16
        		|(0xFF&bytes[0])<<24;
		return Float.intBitsToFloat(i);
	}
	@Override
	public int getHeightSize() { return 3; }
	
	
	@Override
	public byte getChecksum() throws IOException {
		return this.dis.readByte();
	}
	@Override
	public int getChecksumSize() { return 1; }
	
} // end of class LogDataParser
