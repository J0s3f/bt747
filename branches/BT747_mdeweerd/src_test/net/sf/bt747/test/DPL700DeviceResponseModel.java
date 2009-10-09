/**
 * 
 */
package net.sf.bt747.test;

/**
 * @author Mario De Weerd
 *
 */
public final class DPL700DeviceResponseModel {
    private String responseType;
    private byte[] responseBuffer;
    private int responseSize;
    
    public final int getResponseSize() {
        return this.responseSize;
    }
    protected final void setResponseSize(int responseSize) {
        this.responseSize = responseSize;
    }
    public final String getResponseType() {
        return this.responseType;
    }
    protected final void setResponseType(String responseType) {
        this.responseType = responseType;
    }
    public final byte[] getResponseBuffer() {
        return this.responseBuffer;
    }
    protected final void setResponseBuffer(byte[] responseBuffer) {
        this.responseBuffer = responseBuffer;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String result = super.toString();
        if(responseType!=null) {
            result = responseType;
        }
        return result;
    }
}
