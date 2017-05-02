package com.amap.map3d.demo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.amap.map3d.demo.weather.LocationLatLng;

/**
 * Created by yys on 2017/4/10.    室外跑的 信息记录
 */

public class RunRecordingService extends Service {

    private final IBinder binder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class MyBinder extends Binder {
        public RunRecordingService getService() {
            return RunRecordingService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
