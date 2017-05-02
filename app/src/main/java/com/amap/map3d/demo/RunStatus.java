package com.amap.map3d.demo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yys on 2017/4/10.跑步状态
 */

public class RunStatus implements Parcelable
{

    private Map<Integer, RunStatus> _map = new HashMap();
    private int intValue;
    private String mp3;
    private String speech;
    private  String[]  strings = new String[]{};
    protected RunStatus(Parcel in) {
    }

    private RunStatus(int paramInt, String paramString1, String paramString2)
    {
        this.intValue = paramInt;
        this.speech = paramString1;
        this.mp3 = paramString2;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RunStatus> CREATOR = new Creator<RunStatus>() {
        @Override
        public RunStatus createFromParcel(Parcel in) {
            return new RunStatus(in);
        }

        @Override
        public RunStatus[] newArray(int size) {
            return new RunStatus[size];
        }
    };
}
