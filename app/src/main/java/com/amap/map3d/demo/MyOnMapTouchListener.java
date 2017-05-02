package com.amap.map3d.demo;

import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**得到路线的集合
 * Created by yys on 2017/4/27.
 */

public class MyOnMapTouchListener implements AMap.OnMapTouchListener {
    private  boolean isDraw;
    private AMap aMap;
    private Context context;
    private LatLng mendLatlng;
    public  MyOnMapTouchListener(Context context, AMap aMap,boolean isDraw){
        this.context =context;
        this.aMap =aMap;
        this.isDraw =isDraw;
    }
    private Point mPoint;
    //開始的
    int startX;
    int startY;
    private LatLng mLatlng;
    private List<LatLng> latLngs= new ArrayList<>();//保存的路径集合

    public  List<LatLng>  getLatLngs(){
        return  latLngs;
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        if(!isDraw){
            return;
        }
        switch (motionEvent.getAction()){

            case MotionEvent.ACTION_DOWN:
                    // 获取手按下时的坐标
                    startX = (int) motionEvent.getX();
                    startY = (int) motionEvent.getY();
                    mPoint = new Point(startX, startY);
                    mLatlng = aMap.getProjection().fromScreenLocation(mPoint);
                    latLngs.add(mLatlng);
                break;
            case MotionEvent.ACTION_MOVE:
                    // 获取手移动后的坐标
                    int endX = (int) motionEvent.getX();
                    int endY = (int) motionEvent.getY();
                    // 刷新开始坐标
                    startX = (int) motionEvent.getX();
                    startY = (int) motionEvent.getY();
                    // 在开始和结束坐标间画一条线
                    mPoint = new Point(endX, endY);
                    mendLatlng = aMap.getProjection().fromScreenLocation(mPoint);
                    latLngs.add(mendLatlng);
                break;
            case MotionEvent.ACTION_UP: // 手指离开屏幕
                aMap.addPolyline((new PolylineOptions().setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.custtexture)))
                        .addAll(latLngs)
                        .width(18));
                break;

        }
    }
}
