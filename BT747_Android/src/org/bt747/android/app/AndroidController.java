package org.bt747.android.app;

import bt747.Version;
import bt747.model.Controller;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.sys.Generic;
import bt747.sys.interfaces.BT747Exception;
import bt747.sys.interfaces.BT747Path;

public class AndroidController extends Controller {
    /**
     * Reference to the model.
     */
    private Model m;

    /**
     * @param model
     */
    public AndroidController(final Model model) {
        super(model);
        m = model;
    }
    
    public void setModel(final Model m) {
        super.setModel(m);
        this.m = m;
    }

}
