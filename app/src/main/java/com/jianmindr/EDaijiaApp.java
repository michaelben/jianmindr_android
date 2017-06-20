package com.jianmindr;

import android.app.Application;
import android.content.Context;

import com.baidu.location.service.LocationService;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

public class EDaijiaApp extends Application {
    private static EDaijiaApp instance;
    public static Context applicationContext;
    public LocationService locationService;

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();

        final Context mContext = this;
        new Runnable() {
            @Override
            public void run() {
                applicationContext = mContext;

                /***
                 * 初始化定位sdk，建议在Application中创建
                 */
                locationService = new LocationService(applicationContext);
                // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
                SDKInitializer.initialize(applicationContext);
            }
        }.run();
    }
}
