package com.amap.map3d.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by yys on 2017/4/10.传递消息的
 */

public class ServiceGuardianReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String paramIntent = intent.getAction();
        switch (paramIntent){

            case "com.letsrun.intent.action.RUN_SERVICE_DESTROY":
            break;
        }
    }
}
