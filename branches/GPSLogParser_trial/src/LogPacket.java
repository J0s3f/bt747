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


import java.io.IOException;
import java.util.Date;

/**
 * A LogPacket represents a single set of semantically linked data values.
 * <br>
 * A LogPacket knows it's internal structure on the next lower layer (with assistance of the LogFormat).
 * @author Thomas Mohme
 */
public class LogPacket {
	public Date utc;
	public Short fix;
	public Double latitude;
	public Double longitude;
	public Float height;
	public Float speed;
	public Float heading;
	public Short dsta;
	public Integer dage;
	public Short pdop;
	public Short hdop;
	public Short vdop;
	public Byte nSatInUse;
	public Byte nSatInView;
	public SatelliteData[] sats;
	public Short milliseconds;
	public Short recordingMethod;
	public Double distance;

	/**
	 * Construction of LogPacket, using LogFormat's knowledge, which values to except, and LogDataParser's 
	 * knowledge of the technical representation of the individual values and his ability to access these values.
	 * @param parser The parser used to get the individual values.
	 * @param lf The LogFormat, describing which values to expect.
	 * @throws IOException
	 */
	public LogPacket(LogDataParser parser, LogFormat lf) throws IOException {
//		System.out.print("constructing LogPacket from: "); //$NON-NLS-1$
//		parser.peek(100);
		
		// Important: The sequence is meaningful!
		if (lf.hasUTC()) {
			this.utc = parser.getUTC();
		}
		
		if (lf.hasFix()) {
			this.fix = parser.getFix();
		}
		
		if (lf.hasLatitude()) {
			this.latitude = parser.getLatitude();
			assert(this.latitude <= 90.0);
			assert(this.latitude >= -90.0);
		}
		
		if (lf.hasLongitude()) {
			this.longitude = parser.getLongitude();
			assert(this.longitude <= 180.0);
			assert(this.longitude >= -180.0);
		}
		
		if (lf.hasHeight()) {
			this.height = parser.getHeight();
			assert(this.height >= -1000.0);
			assert(this.height <= 36000.0);
			// TODO Correction on "noGeoid" . . . 
		}
		
		if (lf.hasSpeed()) {
			this.speed = parser.getSpeed();
			assert(this.speed >= 0.0);
		}
		
		if (lf.hasHeading()) {
			this.heading = parser.getHeading();
			assert(this.heading >= 0.0);
			assert(this.heading <= 360.0);
		}
		
		if (lf.hasDSta()) {
			this.dsta = parser.getDSta();
		}
		
		if (lf.hasDAge()) {
			this.dage = parser.getDAge();
		}
		
		if (lf.hasPDOP()) {
			this.pdop = parser.getPDOP();
		}
		
		if (lf.hasHDOP()) {
			this.hdop = parser.getHDOP();
		}
		
		if (lf.hasVDOP()) {
			this.vdop = parser.getVDOP();
		}
		
		assert(lf.hasNSat() == lf.hasSID());
		if (lf.hasNSat()) {
			this.nSatInUse = parser.getNSatInUse();
			this.nSatInView = parser.getNSatInView();
			
			this.sats = new SatelliteData[this.nSatInUse];
			for (int i=0; i<this.nSatInUse; i++) {
				// . . . assuming lf.hasSID() . . . 
				sats[i] = new SatelliteData();
				sats[i].id = parser.getSID();
				sats[i].isInUse = (parser.getSatIsInUse() == 0x01);
				sats[i].nbrSats = parser.getNbrSats();
				
				// TODO Clarify SID-influences on other fields . . .
				
				if (lf.hasElevation()) {
					sats[i].elevation = parser.getElevation();
				}
				
				if (lf.hasAzimuth()) {
					sats[i].azimuth = parser.getAzimuth();
				}
				
				if (lf.hasSNR()) {
					sats[i].SNR = parser.getSNR();
				}
			} // for
		}  // if lf.hasNSat()
		
		if (lf.hasRCR()) {
			this.recordingMethod = parser.getRecordingMethod();
		}
		
		if (lf.hasMSec()) {
			this.milliseconds = parser.getMilliSecond();
		}
		
		if (lf.hasDist()) {
			this.distance = parser.getDistance();
		}
		
		byte checksum = parser.getChecksum();
		// TODO verify checksum
	} // constructor LogPacket(byte[])
	
	
	@SuppressWarnings("nls")
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(200);
		if (this.utc != null) sb.append(String.format("%tF %tT (%d)", this.utc, this.utc, this.utc.getTime()));
		if (this.fix != null) sb.append(String.format(", fix=%02x", this.fix));
		if (this.latitude != null) sb.append(String.format(", lat=%f", this.latitude));
		if (this.longitude != null) sb.append(String.format(", long=%f", this.longitude));
		if (this.height != null) sb.append(String.format(", height=%f", this.height));
		if (this.speed != null) sb.append(String.format(", speed=%f", this.speed));
		if (this.heading != null) sb.append(String.format(", heading=%f", this.heading));
		if (this.dsta != null) sb.append(String.format(", DSta=%d", this.dsta));
		if (this.dage != null) sb.append(String.format(", DAge=%d", this.dage));
		if (this.pdop != null) sb.append(String.format(", PDOP=%d", this.pdop));
		if (this.hdop != null) sb.append(String.format(", HDOP=%d", this.hdop));
		if (this.vdop != null) sb.append(String.format(", VDOP=%d", this.vdop));
		if (this.sats.length > 0) {
			sb.append(String.format(", %d/%d[", this.nSatInUse, this.nSatInView));
			for (SatelliteData sat : this.sats) {
				sb.append(String.format("(id=%d, used=%b, n=%d", sat.id, sat.isInUse, sat.nbrSats));
				if (sat.elevation != null) sb.append(String.format(", elev=%d", sat.elevation));
				if (sat.azimuth != null) sb.append(String.format(", azimv=%d", sat.azimuth));
				if (sat.SNR != null) sb.append(String.format(", SNR=%d", sat.SNR));
				sb.append(')');
			}
			sb.append(']');
		}
		if (this.recordingMethod != null) sb.append(String.format(", RCR=%d", this.recordingMethod));
		if (this.milliseconds != null) sb.append(String.format(", ms=%d", this.milliseconds));
		if (this.distance != null) sb.append(String.format(", distance=%f", this.distance));
		return sb.toString();
	}
} // end of class LogPacket
