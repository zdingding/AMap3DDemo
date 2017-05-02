package com.amap.map3d.demo.basic;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.offlinemap.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置显示区域
 */
public class LimitBoundsActivity extends Activity implements AMap.OnMapTouchListener {
//定住屏幕
    private MapView mapView;
    private AMap aMap;

    // 西南坐标
    private LatLng southwestLatLng = new LatLng(39.674949, 115.932873);
    // 东北坐标
    private LatLng northeastLatLng = new LatLng(40.159453, 116.767834);
    int startX;
    int startY;

    private Point mPoint;

    private LatLng mLatlng;
    private List<LatLng> latLngs= new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.limit_bounds_activity);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        init();

        aMap.addMarker(new MarkerOptions().position(southwestLatLng));
        aMap.addMarker(new MarkerOptions().position(northeastLatLng));

        aMap.moveCamera(CameraUpdateFactory.zoomTo(8f));
        aMap.setOnMapTouchListener(this);
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * 设置限制区域
     * @param view
     */
    public void set(View view) {

        LatLngBounds latLngBounds = new LatLngBounds(southwestLatLng, northeastLatLng);
        aMap.setMapStatusLimits(latLngBounds);

    }

    @Override
    public void onTouch(MotionEvent motionEvent) {//保存轨迹点
        ToastUtil.showShortToast(LimitBoundsActivity.this,"触摸事件：屏幕位置" + motionEvent.getX() + " " + motionEvent.getY());
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN: // 手指第一次接触屏幕//起点
                startX = (int) motionEvent.getX();
                startX = (int) motionEvent.getY();
                mPoint = new Point(startX, startX);
                mLatlng = aMap.getProjection().fromScreenLocation(mPoint);//坐标点
                latLngs.add(mLatlng);
                break;
            case MotionEvent.ACTION_MOVE:
                int newX = (int) motionEvent.getX();
                int newY = (int) motionEvent.getY();
                //划出轨迹
                // 重新更新画笔的开始位置
                startX = (int) motionEvent.getX();
                startY = (int) motionEvent.getY();
                mPoint = new Point(startX, startX);
                mLatlng = aMap.getProjection().fromScreenLocation(mPoint);//坐标点
                latLngs.add(mLatlng);
                aMap.addPolyline((new PolylineOptions())
                        .addAll(latLngs)
                        .width(10)
                        .color(Color.argb(255, 1, 1, 1)));
                break;
            case MotionEvent.ACTION_UP: // 手指离开屏幕
                break;
        }
    }
}
