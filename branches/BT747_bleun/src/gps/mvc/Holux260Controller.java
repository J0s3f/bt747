
package gps.mvc;

/**
 * Holux GPSsport 260
 *
 * @author bl
 */
public class Holux260Controller extends HoluxController {

	public Holux260Controller(final GpsController c, final MtkModel m) {
		super(c, m, false);
	}

	@Override
	public boolean reqData(final int dataType) {
		return super.reqData(dataType);
	}
}
