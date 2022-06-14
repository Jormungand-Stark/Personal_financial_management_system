package com.example.activitytest.Util;

import android.app.Application;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

/** 使用百度相关API需要同意隐私协议 */
public class BDAgreePrivacyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 是否同意隐私政策
        SDKInitializer.setAgreePrivacy(getApplicationContext(), true);
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        // 默认本地个性化地图初始化方法
        SDKInitializer.initialize(getApplicationContext());
        // 自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        // 包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }
}
