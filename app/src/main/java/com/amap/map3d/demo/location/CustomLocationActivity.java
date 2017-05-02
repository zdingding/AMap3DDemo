package com.amap.map3d.demo.location;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.RunRecordingService;

/**
 * AMapV2地图中介绍自定义定位小蓝点
 */
public class CustomLocationActivity extends Activity {
	private AMap aMap;
	private MapView mapView;
	private RadioGroup mGPSModeGroup;
	
	private TextView mLocationErrText;
	private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
	private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
	private RunRecordingService myservice = null;//绑定的service对象
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示程序的标题栏
		setContentView(R.layout.locationmodesource_activity);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
		Intent localIntent = new Intent(this, RunRecordingService.class);
		bindService(localIntent, conn, Context.BIND_AUTO_CREATE);
	}
	public ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			myservice= ((RunRecordingService.MyBinder)service).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			myservice =null ;
		}
	};
	/**
	 * 初始化
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
		mGPSModeGroup = (RadioGroup) findViewById(R.id.gps_radio_group);
		mGPSModeGroup.setVisibility(View.GONE);
		mLocationErrText = (TextView)findViewById(R.id.location_errInfo_text);
		mLocationErrText.setVisibility(View.GONE);
	}

	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		aMap.setMinZoomLevel(12);//设置缩放级别
		aMap.setMaxZoomLevel(16);//设置最大级别
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

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
	
}
