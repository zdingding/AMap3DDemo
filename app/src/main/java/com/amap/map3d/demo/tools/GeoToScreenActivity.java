package com.amap.map3d.demo.tools;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.map3d.demo.LocationService;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.offlinemap.ToastUtil;
import com.amap.map3d.demo.util.AMapUtil;

import java.util.ArrayList;
import java.util.List;


public class GeoToScreenActivity extends Activity implements  OnClickListener, AMap.OnMapTouchListener {
	private AMap aMap;
	private MapView mapView;
	private EditText latView, lngView, xView, yView;
	private Button lnglat2pointBtn, point2LatlngBtn;
	private Point mPoint;
	private LatLng mLatlng;
	private LatLng mendLatlng;
	private int x, y;
	private float lat, lng;
	private UiSettings mUiSettings;

	public static final String RECEIVER_ACTION = "location_in_background";
	//開始的
	int startX;
	int startY;
	private List<LatLng> latLngs= new ArrayList<>();//保存的路径集合
	private LocationService myservice = null;//绑定的service对象
	private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
	private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
	private	Button	btn_jisuan;
	//我的定位
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location2screen_activity);
		mapView = (MapView) findViewById(R.id.map);
		btn_jisuan = (Button) findViewById(R.id.btn_jisuan);
		Intent intent = new Intent(this,LocationService.class);
		bindService(intent,conn,Context.BIND_AUTO_CREATE);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
		
	}
	public ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			myservice =((LocationService.MyBinder)service).getService();//得到后台服务
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			myservice = null ;//取消服务
		}
	};
	private void init() {
		CheckBox scrollToggle = (CheckBox) findViewById(R.id.scroll_toggle);
		scrollToggle.setOnClickListener(this);
		if (aMap == null) {
			aMap = mapView.getMap();
			mUiSettings = aMap.getUiSettings();
			setUpMap();
		}
		latView = (EditText)findViewById(R.id.pointLat);
		lngView = (EditText)findViewById(R.id.pointLng);
		xView = (EditText)findViewById(R.id.pointX);
		yView = (EditText)findViewById(R.id.pointY);
		lnglat2pointBtn = (Button)findViewById(R.id.lnglat2pointbtn);
		point2LatlngBtn = (Button)findViewById(R.id.point2Latlngbtn);
		lnglat2pointBtn.setOnClickListener(this);
		point2LatlngBtn.setOnClickListener(this);
		btn_jisuan.setOnClickListener(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(RECEIVER_ACTION);
		registerReceiver(locationChangeBroadcastReceiver, intentFilter);
	}

	private void setUpMap() {
		aMap.setMinZoomLevel(12);//设置缩放级别
		aMap.setMaxZoomLevel(16);//设置最大级别
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		aMap.setOnMapTouchListener(this);
		setupLocationStyle();
	}

	private void setupLocationStyle() {
		// 自定义系统定位蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
		// 自定义定位蓝点图标
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
				fromResource(R.drawable.gps_point));
		// 自定义精度范围的圆形边框颜色
		myLocationStyle.strokeColor(STROKE_COLOR);
		//自定义精度范围的圆形边框宽度
		myLocationStyle.strokeWidth(5);
		// 设置圆形的填充颜色
		myLocationStyle.radiusFillColor(FILL_COLOR);
		// 将自定义的 myLocationStyle 对象添加到地图上
		aMap.setMyLocationStyle(myLocationStyle);
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
		this.unbindService(conn);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_jisuan:
			tojisuan(latLngs);
			break;
			case R.id.lnglat2pointbtn:
			toScreenLocation();
			break;
		case R.id.point2Latlngbtn:
			toGeoLocation();
			break;
			case R.id.scroll_toggle:
				mUiSettings.setScrollGesturesEnabled(((CheckBox) v).isChecked());
				break;
		default:
			break;
		}
		
	}

	private void tojisuan(List<LatLng> latLngs) {
		if(latLngs!=null){
			myservice.jisuan(latLngs);
		}
	}




	private void toGeoLocation() {
		if (AMapUtil.IsEmptyOrNullString(xView.getText().toString()) ||
				AMapUtil.IsEmptyOrNullString(yView.getText().toString())) {
			Toast.makeText(GeoToScreenActivity.this, "x和y为空", Toast.LENGTH_SHORT).show();
		} else {
			x = Integer.parseInt(xView.getText().toString().trim());
			y = Integer.parseInt(yView.getText().toString().trim());
			mPoint = new Point(x, y);
			mLatlng = aMap.getProjection().fromScreenLocation(mPoint);
			if (mLatlng != null) {
				latView.setText(String.valueOf(mLatlng.latitude));
				lngView.setText(String.valueOf(mLatlng.longitude));
			}
		}
		
	}
	private void toScreenLocation() {
		if (AMapUtil.IsEmptyOrNullString(latView.getText().toString()) ||
				AMapUtil.IsEmptyOrNullString(lngView.getText().toString())) {
			Toast.makeText(GeoToScreenActivity.this, "经纬度为空", Toast.LENGTH_SHORT).show();
		} else {
			lat = Float.parseFloat(latView.getText().toString().trim());
			lng = Float.parseFloat(lngView.getText().toString().trim());
			mLatlng = new LatLng(lat, lng);
			mPoint = aMap.getProjection().toScreenLocation(mLatlng);
			if (mPoint != null) {
				xView.setText(String.valueOf(mPoint.x));
				yView.setText(String.valueOf(mPoint.y));
			}
		}
	}


	@Override
	public void onTouch(MotionEvent motionEvent) {

		ToastUtil.showShortToast(GeoToScreenActivity.this,"触摸事件：屏幕位置" + motionEvent.getX() + " " + motionEvent.getY());
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
//	                                    latLngs.clear();
//										设置一下范围
//										LatLngBounds bounds = new LatLngBounds(latLngs.get(0), latLngs.get(latLngs.size() - 2));
//										aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 30));
										break;
								}
	}
	private BroadcastReceiver locationChangeBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(RECEIVER_ACTION)) {
				AMapLocation aMapLocation = intent.getParcelableExtra("aMapLocation");
	if(aMapLocation!=null){
		btn_jisuan.setText("有了");
	}

			}
		}
	};

}
