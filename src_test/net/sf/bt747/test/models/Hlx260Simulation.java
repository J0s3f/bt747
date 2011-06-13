
package net.sf.bt747.test.models;

import net.sf.bt747.test.HlxController;
import net.sf.bt747.test.IBlue747Model;

/**
 *
 * @author bl
 */
public class Hlx260Simulation extends HlxController {

	public Hlx260Simulation(final IBlue747Model mtkDeviceModel) {
		super(mtkDeviceModel);

		mtkDeviceModel.mtkData.swVersion = "201";
	}

	@Override
	protected int analyseNMEA(StringBuffer response, int nmeaId, String[] p_nmea) {
		switch (nmeaId) {
		case 829 :
			response.append("PHLX861,");
			response.append(mtkDeviceModel.mtkData.swVersion);
			return 0;
		}
		return super.analyseNMEA(response, nmeaId, p_nmea);
	}
}
