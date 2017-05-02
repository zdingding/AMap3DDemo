package com.amap.map3d.demo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.map3d.demo.tools.GeoToScreenActivity;

import java.util.List;

/** 服务 添加需要的内容
 * Created by yys on 2017/4/26.
 */
public class LocationService extends Service{
    private String TAG = "LocationService";
    private AMapLocationClient mLocationClient;

    private AMapLocationClientOption mLocationOption;
    private final IBinder binder = new MyBinder();
    private LatLng location;
//两点间的距离
    private float distance;
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }



    public class MyBinder extends Binder{
        public LocationService getService(){
            return   LocationService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startLocation();
        return super.onStartCommand(intent, flags, startId);//这里是要做的内容
        /**
         * 1 定位
         *
         * 2 用户距离 线路的距离
         */

    }

    public void jisuan(List<LatLng> latLngs) {

        if(location!=null&&latLngs!=null){
           // 计算距离
            distance  = AMapUtils.calculateLineDistance(latLngs.get(0), location);

            Log.e(TAG,distance+"");
        }
    }

    private void startLocation() {
        stopLocation();

        if (null == mLocationClient) {
            mLocationClient = new AMapLocationClient(this.getApplicationContext());
        }
        mLocationOption = new AMapLocationClientOption();
        // 使用连续
        mLocationOption.setOnceLocation(false);
        mLocationOption.setLocationCacheEnable(false);
        // 每10秒定位一次
        mLocationOption.setInterval(2 * 1000);
        // 地址信息
        mLocationOption.setNeedAddress(true);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.setLocationListener(locationListener);
        mLocationClient.startLocation();
    }
    AMapLocationListener locationListener =new AMapLocationListener(){

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation!=null&&0==aMapLocation.getErrorCode()){
            location = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                }

            sendLocationBroadcast(aMapLocation);
        }
    };
    private void sendLocationBroadcast(AMapLocation aMapLocation) {

        Intent mIntent = new Intent(GeoToScreenActivity.RECEIVER_ACTION);
        mIntent.putExtra("aMapLocation", aMapLocation);
        //发送广播 我的位置
        sendBroadcast(mIntent);
    }

    private void stopLocation() {
        if (null != mLocationClient) {
            mLocationClient.stopLocation();
        }
    }
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
