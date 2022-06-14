package com.example.activitytest.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.base.LanguageType;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.weather.WeatherDataType;
import com.baidu.mapapi.search.weather.WeatherResult;
import com.baidu.mapapi.search.weather.WeatherSearch;
import com.baidu.mapapi.search.weather.WeatherSearchOption;
import com.baidu.mapapi.search.weather.WeatherSearchRealTime;
import com.baidu.mapapi.search.weather.WeatherServerType;
import com.example.activitytest.Listener.MyOrientationListener;
import com.example.activitytest.R;
import com.example.activitytest.Util.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/** 查询当前位置天气 */
public class WeatherActivity extends BaseActivity {
    private static final String TAG = "WeatherActivity";

    private LocationClient locationClient;
    private WeatherSearch mWeatherSearch;
    private String address;
    private String ADCode;
    private double latitude;
    private double longitude;
    private float radius;
    private boolean isFirstLocation = true;

    private MyOrientationListener myOrientationListener;
    private float mCurrentX;

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private GeoCoder mGeoCoder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate begin");

        // setAgreePrivacy接口需要在LocationClient实例化之前调用
        // 如果setAgreePrivacy接口参数设置为了false，则定位功能不会实现
        // true，表示用户同意隐私合规政策
        // false，表示用户不同意隐私合规政策
        LocationClient.setAgreePrivacy(true);

        // 将多个待申请权限加入List
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(WeatherActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(WeatherActivity.this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(WeatherActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionList.isEmpty()) { // 一次性申请所有需要但未申请的权限
            String[] permissions = permissionList.toArray(new String[0]);
            ActivityCompat
                    .requestPermissions(WeatherActivity.this, permissions, 1);
        }

        // 加载布局
        setContentView(R.layout.weather_layout);

        // 是否开启 GPS 服务
        LocationManager lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        boolean providerEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!providerEnabled) {
            showToast("未开启GPS定位服务");
            finish(); // 退出当前页面
        }

        // 功能实例化
        mWeatherSearch = WeatherSearch.newInstance();
        mGeoCoder = GeoCoder.newInstance();

        // 控件实例化
        initView();

        // 初始化定位选项 并 开始定位
        initLocationOption();

        Log.e(TAG, "onCreate over 主线程ID：" + Thread.currentThread().getId());
    }

    /** 实现定位回调 BDAbstractLocationListener 监听接口，异步获取定位结果 */
    public class MyLocationListener extends BDAbstractLocationListener{
        @Override
        public void onReceiveLocation(BDLocation location){
            // 此处的 BDLocation 为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            // 更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null){
                return;
            }

            latitude = location.getLatitude(); // 获取纬度信息
            longitude = location.getLongitude(); // 获取经度信息
            radius = location.getRadius(); // 获取定位精度
            Log.e(TAG, "onReceiveLocation radius: " + radius);
            float direction = location.getDirection(); // GPS定位时方向角度
            Log.e(TAG, "onReceiveLocation direction: " + direction);
            address = location.getAddrStr();
            Log.e(TAG, "onReceiveLocation address: " + address);
            ADCode = location.getAdCode();

            if (isFirstLocation) {
                ChangeStatus();
                ChangeData();
                ChangeConfiguration();
                initOrientation();
                myOrientationListener.onStart();
                isFirstLocation = false;
            }

            // 设置地图状态更改侦听器
            mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
                @Override
                public void onMapStatusChangeStart(MapStatus status) {
                    Log.e(TAG, "onMapStatusChangeStart: over");
                }
                @Override
                public void onMapStatusChangeStart(MapStatus status, int reason) {
                    Log.e(TAG, "onMapStatusChangeStart双参数: over");
                }
                @Override
                public void onMapStatusChange(MapStatus status) {
                    Log.e(TAG, "onMapStatusChange: over");
                }
                @Override
                public void onMapStatusChangeFinish(MapStatus status) {
                    LatLng center = status.target;
                    ReverseGeoCodeOption rgcOption =
                            new ReverseGeoCodeOption().location(center).radius(500);
                    // 发起反地理编码请求(经纬度->地址信息)，调用 setOnGetGeoCodeResultListener
                    mGeoCoder.reverseGeoCode(rgcOption);

                    Point point = mBaiduMap.getProjection().toScreenLocation(center);
                    BitmapDescriptor bitmapDescriptor =
                            BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
                    MarkerOptions markerOptions = new MarkerOptions()
                            .icon(bitmapDescriptor).position(center).fixedScreenPosition(point);
                    mBaiduMap.addOverlay(markerOptions);
                    Log.e(TAG, "onMapStatusChangeFinish over");
                }
            });

            mGeoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener(){
                /**
                 * 地理编码查询结果回调函数
                 *
                 * @param result 地理编码查询结果
                 */
                @Override
                public void onGetGeoCodeResult(GeoCodeResult result) {
                }
                /**
                 * 反地理编码查询结果回调函数
                 *
                 * @param result 反地理编码查询结果
                 */
                @Override
                public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                    ADCode = String.valueOf(result.getAdcode());
                    address = result.getAddress();
                    Log.e(TAG, "onGetReverseGeoCodeResult address: " + address);
                }
            });
            Log.e(TAG, "onReceiveLocation: over");
        }
    }

    /** 权限申请回调 */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "必须同意所有权限才能使用本程序",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            } else {
                Toast.makeText(this, "申请权限时发生未知错误", Toast.LENGTH_LONG)
                        .show();
                Log.e(TAG, "onRequestPermissionsResult:待申请权限个数<=0");
                finish();
            }
        }
    }

    /** 控件实例化 */
    @SuppressLint("ClickableViewAccessibility")
    private void initView(){
        mMapView = findViewById(R.id.bmapView);
        mMapView.removeViewAt(1); // 不显示百度地图Logo

        mBaiduMap = mMapView.getMap();
        Log.e(TAG, "initView mBaiduMap: " + mBaiduMap);
        mBaiduMap.setMyLocationEnabled(true);

        ImageButton exit_weather = findViewById(R.id.exit_weather);
        exit_weather.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                view.setBackgroundResource(R.color.white_20);
            }
            else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                view.performClick(); // 触发点击事件
            }
            return true;
        });
        exit_weather.setOnClickListener(v -> finish());

        Button mBtnSearch = findViewById(R.id.btn_search_weather);
        mBtnSearch.setOnClickListener(v -> {
            Log.e(TAG, "检索天气按钮: " + Thread.currentThread().getId());
            WeatherSearchOption weatherSearchOption = new WeatherSearchOption()
                    .weatherDataType(WeatherDataType.WEATHER_DATA_TYPE_ALL)
                    .districtID(ADCode)
                    .languageType(LanguageType.LanguageTypeChinese)
                    .serverType(WeatherServerType.WEATHER_SERVER_TYPE_DEFAULT);
            // 设置天气请求结果监听器
            mWeatherSearch.setWeatherSearchResultListener(weatherResult -> runOnUiThread
                    (() -> popupWeatherDialog(weatherResult)));
            mWeatherSearch.request(weatherSearchOption); // 发起天气检索请求
        });

        ImageButton my_location = findViewById(R.id.my_location);
        my_location.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                view.setBackgroundResource(R.color.white_20);
            }
            else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                view.performClick();
                view.setBackgroundResource(R.color.dark_gray);
            }
            return true;
        });
        my_location.setOnClickListener(v -> {
            mBaiduMap.clear();
            ChangeStatus();
        });
        Log.e(TAG, "initView 完成");
    }

    /** 初始化方向传感器 */
    private void initOrientation() {
        Log.e(TAG, "开始初始化方向传感器");
        myOrientationListener = new MyOrientationListener(this);
        myOrientationListener.setOnOrientationListener(x -> {
            mCurrentX = x;
            // Log.e(TAG, "initOrientation mCurrentX: " + mCurrentX);
            ChangeData();
        });
        Log.e(TAG, "方向传感器初始化完成");
    }

    /** 改变 MapStatus 信息，使地图比例尺恰当、并将当前位置作为地图中心 */
    private void ChangeStatus(){
        LatLng center = new LatLng(latitude, longitude);
        MapStatus.Builder builder = new MapStatus.Builder();
        MapStatus mMapStatus = builder.zoom(18).target(center).build();
        MapStatusUpdate msu = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(msu); // 直接到中间
        // mBaiduMap.animateMapStatus(mMapStatusUpdate); // 动画的形式到中间
        Log.e(TAG, "current_location MapStatus: " + mBaiduMap.getMapStatus());
    }

    /** 改变 MyLocationData 信息并传回给 mBaiduMap */
    private void ChangeData(){
        MyLocationData data = new MyLocationData.Builder()
                .accuracy(radius)
                .direction(mCurrentX)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        mBaiduMap.setMyLocationData(data);
    }

    /** 改变 MyLocationConfiguration 信息并传回给 mBaiduMap，以改变图层状态 */
    private void ChangeConfiguration(){
        MyLocationConfiguration configuration = new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, // 定位图层显示方式：普通态
                true, // 是否允许显示方向信息
                null // 设置用户自定义定位图标
        );
        mBaiduMap.setMyLocationConfiguration(configuration);
    }

    /** 初始化定位参数配置 */
    private void initLocationOption() {
        try {
            // 定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动。
            locationClient = new LocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, "locationClient初始化完成: " + locationClient);

        LocationClientOption locationOption = new LocationClientOption(); // 配置定位参数

        // Hight_Accuracy 高精度模式（默认模式），优先使用GPS定位吗，无法接受GPS信号时使用网络定位。
        // Battery_Saving 节电模式，只会使用网络定位。
        // Device_Sensors 传感器模式，只会使用GPS定位。
        // Fuzzy_Locating 模糊定位模式，可以降低 API 的调用频率，但同时也会降低定位精度；
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        // 国内有三种经纬度类型可选：GCJ02（国测局坐标）、BD09（百度墨卡托坐标）和BD09ll（百度经纬度坐标）
        // 若想将定位 SDK 获得的经纬度直接在百度地图上标注，请选择坐标类型 BD09ll
        // 海外地区定位，无需设置坐标类型，统一返回 WGS84 类型坐标
        locationOption.setCoorType("BD09ll");

        // 可选，设置发起定位请求的间隔，int类型，单位ms
        // 如果设置为0，则代表单次定位，即仅定位一次，默认为0
        // 如果设置非0，需设置1000ms以上才有效
        locationOption.setScanSpan(2000);

        // 可选，是否需要地址信息，默认为不需要，即参数为false
        // 如果开发者需要获得当前点的地址信息，此处必须为true
        locationOption.setIsNeedAddress(true);

        // 可选，是否需要位置描述信息，默认为不需要，即参数为false
        // 如果开发者需要获得当前点的位置信息，此处必须为true
        locationOption.setIsNeedLocationDescribe(true);

        // 可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false);

        // 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setLocationNotify(true);

        // 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，
        // 设置是否在stop的时候杀死这个进程，默认为 true，表不杀死
        locationOption.setIgnoreKillProcess(true);

        // 可选，默认false，设置是否需要位置语义化结果
        // 可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true);

        // 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);

        // 可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false);

        // 可选，设置是否使用 GPS，默认false
        // 使用 Hight_Accuracy 和 Device_Sensors 两种定位模式的，参数必须设置为true
        locationOption.setOpenGps(true);

        // 可选，V7.2版本新增能力
        // 首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位
        locationOption.setWifiCacheTimeOut(5*60*1000);

        // 可选，设置是否需要过滤 GPS 仿真结果，默认需要，即参数为false
        locationOption.setEnableSimulateGps(false);

        // 需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        locationClient.setLocOption(locationOption);

        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000,1,
                LocationClientOption.LOC_SENSITIVITY_HIGHT);

        // 不同意隐私政策可能导致 locationClient 为空
        if (locationClient != null){
            locationClient.registerLocationListener(new MyLocationListener()); // 注册监听器
            locationClient.start(); // 开始定位
        }

        Log.e(TAG, "locationClient已启动");
    }

    /** 弹出天气对话框 */
    private void popupWeatherDialog(WeatherResult weatherResult){
        if (null == weatherResult) {
            return;
        }

        // 天气实况数据-WeatherSearchRealTime
        WeatherSearchRealTime weatherSearchRealTime = weatherResult.getRealTimeWeather();

        // 未获得天气实况数据
        if (null == weatherSearchRealTime) {
            return;
        }

        // 加载弹窗视图
        final AlertDialog.Builder weatherDialog = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.weather_item, null);
        // 加载失败
        if (null == view) {
            return;
        }

        TextView txtTemp = view.findViewById(R.id.txtTemp);
        String temp = "温度：" + weatherSearchRealTime.getTemperature() + "℃";
        txtTemp.setText(temp);

        TextView txtSensoryTemp = view.findViewById(R.id.txtSensoryTemp);
        String sensoryTemp = "体感温度：" + weatherSearchRealTime.getSensoryTemp() + "℃";
        txtSensoryTemp.setText(sensoryTemp);

        TextView txtRelativeHumdidity = view.findViewById(R.id.txtRelativeHumidity);
        String relativeHumidity =
                "相对湿度：" + weatherSearchRealTime.getRelativeHumidity() + "%";
        txtRelativeHumdidity.setText(relativeHumidity);

        TextView txtPhenomenon = view.findViewById(R.id.txtPhenomenon);
        String phenomenon = "天气现象："+ weatherSearchRealTime.getPhenomenon();
        txtPhenomenon.setText(phenomenon);

        TextView txtWindDirection = view.findViewById(R.id.txtWindDirection);
        String windDirection = "风向：" + weatherSearchRealTime.getWindDirection();
        txtWindDirection.setText(windDirection);

        TextView txtWindPower = view.findViewById(R.id.txtWindPower);
        String windPower = "风力：" + weatherSearchRealTime.getWindPower();
        txtWindPower.setText(windPower);

        TextView txtAir = view.findViewById(R.id.txtAir);
        String Air = "当前位置：" + address;
        txtAir.setText(Air);

        weatherDialog.setTitle("实时天气").setView(view).create();
        weatherDialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop begin");
        myOrientationListener.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 在activity执行onResume时必须调用mMapView. onResume ()
        if (null != mMapView) {
            mMapView.onResume();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        // 在activity执行onPause时必须调用mMapView. onPause ()
        if(null != mMapView) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (null != mWeatherSearch) {
            mWeatherSearch.destroy();
            Log.e(TAG, "onDestroy mWeatherSearch");
        }

        if (null != mGeoCoder) {
            mGeoCoder.destroy();
            Log.e(TAG, "mGeoCoder onDestroy");
        }

        if (null != mMapView) {
            mMapView.onDestroy();
            mMapView = null;
            Log.e(TAG, "mMapView onDestroy");
        }

        if (null != locationClient) {
            locationClient.stop();
            Log.e(TAG, "locationClient onDestroy");
        }

        if (null != mBaiduMap) {
            mBaiduMap.clear();
            mBaiduMap.setMyLocationEnabled(false);
            Log.e(TAG, "mBaiduMap onDestroy");
        }
    }
}