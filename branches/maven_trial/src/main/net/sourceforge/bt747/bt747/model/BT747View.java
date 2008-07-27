package net.sourceforge.bt747.bt747.model;

public interface BT747View {

    void createMessageBoxModal(
            final String title,
            final String msg,
            final String[] buttonCaptions);
    
    void setController(Controller c);
    
    void setModel(Model m);
    
}
