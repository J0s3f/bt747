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
import java.util.Date;

/**
 * A LogDataParser knows the technical representation of individual data items on the stream.
 * <br>
 * This class is used to parse "standard" logs.
 * @author Thomas Mohme
 */
public class LogDataParser {
	protected DataInputStream dis;
	
	/**
	 * This constructor gets used when some "external force" dictates it's use.
	 * <br>
	 * Attention: The resulting object isn't operational until the DataInputStream gets set!
	 */
	public LogDataParser() {
		// just to enable inheritance
	}
	
	/**
	 * This constructor gets used by the factory.
	 * @param aDIS The stream the parser gets the raw bytes from.
	 */
	LogDataParser(DataInputStream aDIS){
		this.dis = aDIS;
	}
	
	public void peek(int l) {
		try {
			dis.mark(l);
			for (int i = 0; i < l; i++) {
				System.out.printf("%02x ", dis.readByte()); //$NON-NLS-1$
			}
			System.out.println();
			dis.reset();
		} catch (IOException e) {
			// ignored
		}
	}
	
	public void setStream(DataInputStream aDIS) {
		this.dis = aDIS;
	}
	
	
	public Date getUTC() throws IOException {
		return new Date((long)Integer.reverseBytes(this.dis.readInt()) * 1000); // Important: Force a "long" result to avoid int truncation!
	}
	public int getUTCSize() { return 4; }
	
	public Short getFix() throws IOException {
		return Short.reverseBytes(this.dis.readShort());
	}
	public int getFixSize() { return 2; }
	
	public Double getLatitude() throws IOException {
		long l = Long.reverseBytes(this.dis.readLong());
		return Double.longBitsToDouble(l);
	}
	public int getLatitudeSize() { return 8; }
	
	public Double getLongitude() throws IOException {
		long l = Long.reverseBytes(this.dis.readLong());
		return Double.longBitsToDouble(l);
	}
	public int getLongitudeSize() { return 8; }
	
	public Float getHeight() throws IOException {
		int i = Integer.reverseBytes(this.dis.readInt());
		return Float.intBitsToFloat(i);
	}
	public int getHeightSize() { return 4; }
	
	public Float getSpeed() throws IOException {
		int i = Integer.reverseBytes(this.dis.readInt());
		return Float.intBitsToFloat(i);
	}
	public int getSpeedSize() { return 4; }
	
	public Float getHeading() throws IOException {
		int i = Integer.reverseBytes(this.dis.readInt());
		return Float.intBitsToFloat(i);
	}
	public int getHeadingSize() { return 4; }
	
	public Short getDSta() throws IOException {
		// TODO Test
		return Short.reverseBytes(this.dis.readShort());
	}
	public int getDStaSize() { return 4; }
	
	public Integer getDAge() throws IOException {
		// TODO Test
		return Integer.reverseBytes(this.dis.readInt());
	}
	public int getDAgeSize() { return 4; }
	
	public Short getPDOP() throws IOException {
		return Short.reverseBytes(this.dis.readShort());
	}
	public int getPDOPSize() { return 2; }
	
	public Short getHDOP() throws IOException {
		return Short.reverseBytes(this.dis.readShort());
	}
	public int getHDOPSize() { return 2; }
	
	public Short getVDOP() throws IOException {
		return Short.reverseBytes(this.dis.readShort());
	}
	public int getVDOPSize() { return 2; }
	
	public Byte getNSatInUse() throws IOException {
		return new Byte(this.dis.readByte());
	}
	public int getNSatInUseSize() { return 1; }
	
	public Byte getNSatInView() throws IOException {
		return new Byte(this.dis.readByte());
	}
	public int getNSatInViewSize() { return 1; }
	
	public byte getSID() throws IOException {
		return this.dis.readByte();
	}
	public int getSIDSize() { return 1; }
	
	public byte getSatIsInUse() throws IOException {
		return this.dis.readByte();
	}
	public int getSatIsInUseSize() { return 1; }
	
	public Short getNbrSats() throws IOException {
		return Short.reverseBytes(this.dis.readShort());
	}
	public int getNbrSatsSize() { return 2; }
	
	public Short getElevation() throws IOException {
		return Short.reverseBytes(this.dis.readShort());
	}
	public int getElevationSize() { return 2; }
	
	public Short getAzimuth() throws IOException {
		return Short.reverseBytes(this.dis.readShort());
	}
	public int getAzimuthSize() { return 2; }
	
	public Short getSNR() throws IOException {
		return Short.reverseBytes(this.dis.readShort());
	}
	public int getSNRSize() { return 2; }
	
	public Short getRecordingMethod() throws IOException {
		return Short.reverseBytes(this.dis.readShort());
	}
	public int getRecordingMethodSize() { return 2; }
	
	public Short getMilliSecond() throws IOException {
		return Short.reverseBytes(this.dis.readShort());
	}
	public int getMilliSecondSize() { return 2; }
	
	public Double getDistance() throws IOException {
		long l = Long.reverseBytes(this.dis.readLong());
		return Double.longBitsToDouble(l);
	}
	public int getDistanceSize() { return 8; }
	
	public byte getChecksum() throws IOException {
		byte delimiter = this.dis.readByte();
		assert(delimiter == 0x2a); // '*'
		return this.dis.readByte();
	}
	public int getChecksumSize() { return 2; }
	
} // end of class LogDataParser
