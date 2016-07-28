package com.example.leebisheng.studyui;

import android.content.Context;
import android.media.Image;

/**
 * Created by leebisheng on 2016/7/23.
 */
public class Country {
    private int  mCountryFlag;
    private String mCountryName;

    public  Country(int countryFlag,String countryName)
    {
        mCountryFlag=countryFlag;
        mCountryName=countryName;
    }

    public int getmCountryFlag() {
        return mCountryFlag;
    }

    public void setmCountryFlag(int mCountryFlag) {
        this.mCountryFlag = mCountryFlag;
    }

    public String getmCountryName() {
        return mCountryName;
    }

    public void setmCountryName(String mCountryName) {
        this.mCountryName = mCountryName;
    }

}
