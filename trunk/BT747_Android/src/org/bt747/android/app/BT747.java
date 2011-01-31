package org.bt747.android.app;

import android.app.Activity;
import android.os.Bundle;
import bt747.model.AppSettings;
import bt747.model.Model;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747FileName;
import bt747.sys.interfaces.BT747Path;
import bt747.sys.interfaces.BT747Time;

public class BT747 extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		boolean success = true;
		if (success) {
			final Runnable r = new Runnable() {

				Model m = new Model();
				AndroidController c = new AndroidController(m);

				public void run() {
					/** For test / convert an input file to GPX. */

					/** Set the input file. */
					c.setStringOpt(Model.LOGFILEPATH, "BT747_log.bin");
					
					/** Set the method to compute the output filename. */
					c.setFileNameBuilder(new BT747FileName() {
						public BT747Path getOutputFileName(
								final BT747Path baseName,
								final int utcTimeSeconds,
								final String proposedExtension,
								final String proposedTimeSpec) {
							BT747Time t = JavaLibBridge.getTimeInstance();
							t.setUTCTime(utcTimeSeconds);
							// String base = Conv.expandDate(baseName.getPath(),
							// t);
							String base = "acb";
							boolean addTimeSpec;
							addTimeSpec = (baseName.getPath().indexOf('%') < 0);
							switch (m
									.getIntOpt(AppSettings.OUTPUTFILESPLITTYPE)) {
							case 0:
								addTimeSpec &= false;
							default:
								addTimeSpec &= true;
							}

							if (!addTimeSpec) {
								return new BT747Path(base + "_trk"
										+ proposedExtension);
							} else {
								return new BT747Path(base + proposedTimeSpec
										+ "_trk" + proposedExtension);
							}
						}
					});

					final int error = c.doConvertLog(Model.GPX_LOGTYPE);;

					// new BT747cmd(m, c, null /* options */);
				}
			};

			r.run();
			// java.awt.EventQueue.invokeLater(r);
		}
	}
}