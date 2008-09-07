package bt747.interfaces;

public interface BT747File {

    public abstract int getSize();

    public abstract boolean exists();

    public abstract boolean delete();

    public abstract boolean createDir();

    public abstract boolean close();

    public abstract boolean setPos(int pos);

    public abstract boolean isOpen();

    public abstract int writeBytes(byte[] b, int off, int len);

    public abstract int readBytes(byte[] b, int off, int len);

    public abstract String getPath();
    
    public abstract int getLastError();

}