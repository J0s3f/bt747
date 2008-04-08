package bt747.model;

public interface BT747View {

    public void MessageBoxModal(String title, String msg, String[] buttonCaptions);
    
    public void setController(Controller c);
    
    public void setModel(Model m);
    
}
