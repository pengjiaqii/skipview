package com.jade.testdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

public class LocationActivity extends Activity{

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //异步获取定位结果
    AMapLocationListener mAMapLocationListener = new AMapLocationListener(){
        @Override
        public void onLocationChanged(AMapLocation amapLocation){
            Log.d("DeskClock", "onLocationChanged---" + amapLocation);
            if(amapLocation != null){
                if(amapLocation.getErrorCode() == 0){
                    //解析定位结果
                    String province = amapLocation.getProvince();
                    String city = amapLocation.getCity();
                    String district = amapLocation.getDistrict();
                    android.util.Log.d("DeskClock", "省---" + province);
                    android.util.Log.d("DeskClock", "市---" + city);
                    android.util.Log.d("DeskClock", "区---" + district);
                }
            }
        }
    };
    private AMapLocationClientOption locationOption = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        getAmapLocation();
    }

    private void getAmapLocation(){
        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this, true);
        //初始化定位
        try{
            mLocationClient = new AMapLocationClient(getApplicationContext());
            locationOption = getDefaultOption();
            //设置定位参数
            mLocationClient.setLocationOption(locationOption);
            //设置定位回调监听
            mLocationClient.setLocationListener(mAMapLocationListener);
            //启动定位
            mLocationClient.startLocation();
        } catch(Exception e){
            Log.e("DeskClock", "ExceptionExceptionException---" + e);
            e.printStackTrace();
        }
    }

    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(7200 * 1000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }
}