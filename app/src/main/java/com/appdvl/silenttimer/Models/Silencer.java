package com.appdvl.silenttimer.Models;

public class Silencer
{
    private String silencerName;
    private String startTime;
    private String endTime;
    private boolean sunday;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean isChecked;

    public String getSilencerName() {return silencerName; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public boolean getSunday() { return sunday; }
    public boolean getMonday() { return monday; }
    public boolean getTuesday() { return tuesday; }
    public boolean getWednesday() { return wednesday; }
    public boolean getThursday() { return thursday; }
    public boolean getFriday() { return friday; }
    public boolean getSaturday() { return saturday; }
    public boolean getIsChecked() { return isChecked; }

    public Silencer(String mSilencerName, String mStartTime, String mEndTime, boolean mSunday, boolean mMonday,
                    boolean mTuesday, boolean mWednesday, boolean mThursday, boolean mFriday, boolean mSaturday,
                    boolean mIsChecked)
    {
        silencerName = mSilencerName;
        startTime = mStartTime;
        endTime = mEndTime;
        sunday = mSunday;
        monday = mMonday;
        tuesday = mTuesday;
        wednesday = mWednesday;
        thursday = mThursday;
        friday = mFriday;
        saturday = mSaturday;
        isChecked = mIsChecked;
    }
}