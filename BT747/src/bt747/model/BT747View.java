package bt747.model;

public interface BT747View {

    void createMessageBoxModal(
            final String title,
            final String msg,
            final String[] buttonCaptions);
    
    void setController(AppController c);
    
    void setModel(Model m);
    
}
