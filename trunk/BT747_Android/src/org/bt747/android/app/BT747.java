package org.bt747.android.app;

import android.app.Activity;
import android.os.Bundle;
import bt747.model.Model;

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
					new BT747cmd(m, c, null /* options */);
				}
			};

			r.run();
			// java.awt.EventQueue.invokeLater(r);
		}
    }
}