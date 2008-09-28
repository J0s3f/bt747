package bt747.sys.interfaces;

public interface BT747Time {

    int getHour();
//
//    public abstract void setHour(int hours);
//
    int getMinute();
//
//    public abstract void setMinute(int hours);
//
    int getSecond();
//
//    public abstract void setSecond(int hours);
//
//    public abstract void setDay(int date);
//
    int getYear();
//
//    public abstract void setYear(int year);
//
    int getMonth();
//
//    public abstract void setMonth(int month);
//
    int getDay();
//
//    // UTC time in java depends on implementation (leap seconds, ...)
//    // So we need to implement our own function.
    void setUTCTime(final int utc);

}