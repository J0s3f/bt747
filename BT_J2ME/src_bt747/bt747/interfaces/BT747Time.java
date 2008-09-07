package bt747.interfaces;

public interface BT747Time {

    public abstract int getHour();
//
//    public abstract void setHour(int hours);
//
    public abstract int getMinute();
//
//    public abstract void setMinute(int hours);
//
    public abstract int getSecond();
//
//    public abstract void setSecond(int hours);
//
//    public abstract void setDay(int date);
//
    public abstract int getYear();
//
//    public abstract void setYear(int year);
//
    public abstract int getMonth();
//
//    public abstract void setMonth(int month);
//
    public abstract int getDay();
//
//    // UTC time in java depends on implementation (leap seconds, ...)
//    // So we need to implement our own function.
    public abstract void setUTCTime(final int utc);

}