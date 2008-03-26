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


/**
 * A LogFormat object is a container for the layout-information of the following LogPackets.
 * <br>
 * This class provides some convenience methods.
 * @author Thomas Mohme
 */
public class LogFormat {
	private int format;
	
	LogFormat(int f) {
		this.format = f;
	} // constructor LogFormat(int)
	
	LogFormat(LogFormat other) {
		this.format = other.format;
	} // constructor LogFormat(LogFormat)
	
	public boolean hasUTC() { return ((this.format&(1<<0)) != 0); } 
	public boolean hasFix() { return ((this.format&(1<<1)) != 0); } 
	public boolean hasLatitude() { return ((this.format&(1<<2)) != 0); } 
	public boolean hasLongitude() { return ((this.format&(1<<3)) != 0); } 
	public boolean hasHeight() { return ((this.format&(1<<4)) != 0); } 
	public boolean hasSpeed() { return ((this.format&(1<<5)) != 0); } 
	public boolean hasHeading() { return ((this.format&(1<<6)) != 0); } 
	public boolean hasDSta() { return ((this.format&(1<<7)) != 0); } 
	public boolean hasDAge() { return ((this.format&(1<<8)) != 0); } 
	public boolean hasPDOP() { return ((this.format&(1<<9)) != 0); } 
	public boolean hasHDOP() { return ((this.format&(1<<10)) != 0); } 
	public boolean hasVDOP() { return ((this.format&(1<<11)) != 0); } 
	public boolean hasNSat() { return ((this.format&(1<<12)) != 0); }
	public boolean hasSID() { return ((this.format&(1<<13)) != 0); }
	public boolean hasElevation() { return ((this.format&(1<<14)) != 0); }
	public boolean hasAzimuth() { return ((this.format&(1<<15)) != 0); }
	public boolean hasSNR() { return ((this.format&(1<<16)) != 0); }
	public boolean hasRCR() { return ((this.format&(1<<17)) != 0); } 
	public boolean hasMSec() { return ((this.format&(1<<18)) != 0); } 
	public boolean hasDist() { return ((this.format&(1<<19)) != 0); } 
	public boolean isLowPrecision() { return ((this.format&(1<<31)) != 0); } 
	
	
	public void update(int newFormat) {
		this.format = newFormat;
	}
	
	public int getMinPacketSize(LogDataParser parser) {
		int total = 0;
		total += this.hasUTC()?parser.getUTCSize():0; 
		total += this.hasFix()?parser.getFixSize():0;
		total += this.hasLatitude()?parser.getLatitudeSize():0;
		total += this.hasLongitude()?parser.getLongitudeSize():0;
		total += this.hasHeight()?parser.getHeightSize():0;
		total += this.hasSpeed()?parser.getSpeedSize():0;
		total += this.hasHeading()?parser.getHeadingSize():0;
		total += this.hasDSta()?parser.getDStaSize():0;
		total += this.hasDAge()?parser.getDAgeSize():0;
		total += this.hasPDOP()?parser.getPDOPSize():0;
		total += this.hasHDOP()?parser.getHDOPSize():0;
		total += this.hasVDOP()?parser.getVDOPSize():0;
		total += this.hasNSat()?parser.getNbrSatsSize():0;
		// TODOD A complete calculation has to include the no. of satellite-data records, but excluding them should be accurate enough
		total += this.hasRCR()?parser.getRecordingMethodSize():0;
		total += this.hasMSec()?parser.getMilliSecondSize():0;
		total += this.hasDist()?parser.getDistanceSize():0;
		return total;
	}
	
	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return "0x"+Integer.toHexString(this.format);
	}
} // end of class LogFormat
