package com.amap.map3d.demo.location;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.route.WalkStep;
import com.amap.map3d.demo.MyOnMapTouchListener;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.RunRecordingService;
import com.amap.map3d.demo.util.AMapUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * AMapV2地图中介绍定位几种
 */
public class LocationModeSourceActivity extends Activity implements AMap.OnMyLocationChangeListener,OnCheckedChangeListener{
	private AMap aMap;
	private String  TAG="LocationModeSourceActivity";
	private MapView mapView;
	private RadioGroup mGPSModeGroup;
	private MyLocationStyle myLocationStyle;
	private RunRecordingService myservice = null;//绑定的service对象\
	private	ProgressBar	mProgressBar;
	private UiSettings mUiSettings;
	private	List<LatLng> latLngs;
	private  LatLng  myLatLng;
	private  Button  btn_mywey;
	private	ListView lv;
	private MyAdapter mAdapter;
	private MyOnMapTouchListener	 myOnMapTouchListener;
	List list = new ArrayList();//获取距离这些点的所有的距离 看看离那里比较近
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示程序的标题栏
		setContentView(R.layout.locationmodesource_activity);
		mapView = (MapView) findViewById(R.id.map);

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);

		btn_mywey = (Button) findViewById(R.id.btn_mywey);
		 lv = (ListView) findViewById(R.id.lv);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
		btn_mywey.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				latLngs =myOnMapTouchListener.getLatLngs();//所有的点 与我的位置
				if (myLatLng!=null&&latLngs!=null) {
					for(int i=0;i<latLngs.size();i++){
						float distance = AMapUtils.calculateLineDistance(myLatLng, latLngs.get(i));
						list.add(distance);
					}
				}
				//找到最小值          的索引
				mAdapter = new MyAdapter();

				lv.setAdapter(mAdapter);
			}
		});
	}

	class 	MyAdapter extends BaseAdapter{

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(LocationModeSourceActivity.this, R.layout.textitem, null);
			holder.date = (TextView) convertView.findViewById(R.id.data);
			convertView.setTag(holder);
			}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.date.setText(list.get(position)+"");
		return convertView;
	}
		private class ViewHolder {
			TextView date;
		}
}

	/**
	 * 初始化
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			mUiSettings = aMap.getUiSettings();
			setUpMap();
		}
		mGPSModeGroup = (RadioGroup) findViewById(R.id.gps_radio_group);
		mGPSModeGroup.setOnCheckedChangeListener(this);

		//设置SDK 自带定位消息监听
		aMap.setOnMyLocationChangeListener(this);
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
	 * 设置一些amap的属性
	 */
	private void setUpMap() {

		mUiSettings.setScrollGesturesEnabled(false);
		// 如果要设置定位的默认状态，可以在此处进行设置
		myLocationStyle = new MyLocationStyle();
		aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE));
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
	    myOnMapTouchListener =new MyOnMapTouchListener(LocationModeSourceActivity.this,aMap,true);
		aMap.setOnMapTouchListener(myOnMapTouchListener);

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
            case R.id.gps_locate_button:
                aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE));
                break;
            case R.id.gps_follow_button:
                // 设置定位的类型为 跟随模式
                aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW));
				if (myLatLng!=null&&latLngs!=null) {
					float distance = AMapUtils.calculateLineDistance(myLatLng, latLngs.get(0));
				}
                break;
            case R.id.gps_rotate_button:
                // 设置定位的类型为根据地图面向方向旋转
                aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE));
                break;
            case R.id.gps_show_button:
                // 只定位，不进行其他操作
                aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW));
                break;
            case R.id.gps_rotate_location_button:
                // 定位、且将视角移动到地图中心点，定位点依照设备方向旋转，  并且会跟随设备移动。
                aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE));
                break;
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
		unbindService(conn);
	}

	@Override
	public void onMyLocationChange(Location location) {
		// 定位回调监听
		if(location != null) {
			Log.e("amap", "onMyLocationChange 定位成功， lat: " + location.getLatitude() + " lon: " + location.getLongitude());
			Bundle bundle = location.getExtras();
			if(bundle != null) {
				int errorCode = bundle.getInt(MyLocationStyle.ERROR_CODE);
				String errorInfo = bundle.getString(MyLocationStyle.ERROR_INFO);
				// 定位类型，可能为GPS WIFI等，具体可以参考官网的定位SDK介绍
				int locationType = bundle.getInt(MyLocationStyle.LOCATION_TYPE);
				myLatLng=new LatLng(location.getLatitude(),location.getLongitude());
                /*
                errorCode
                errorInfo
                locationType
                */
				Log.e("amap", "定位信息， code: " + errorCode + " errorInfo: " + errorInfo + " locationType: " + locationType );
			} else {
				Log.e("amap", "定位信息， bundle is null ");
			}
		} else {
			Log.e("amap", "定位失败");
		}
	}





}
