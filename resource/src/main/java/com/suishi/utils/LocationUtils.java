package com.suishi.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;

import java.util.List;

/**
 * Created by admin on 2018/11/12.
 */
public class LocationUtils {

    private Context mContext;

    private LocationManager locationManager;

    private static LocationUtils instance;

    private LocationListener locationListener;

    private LocationUtils(Context context) {
        this.mContext = context;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public static LocationUtils init(Context context) {
        if (instance == null) {
            instance = new LocationUtils(context);
        }
        return instance;
    }

    public static LocationUtils getInstance() {
        if (instance == null) {
            throw new NullPointerException("请初始化 locationUtils");
        }
        return instance;
    }


    /**
     * 获取经纬度
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public String getLngAndLat(LocationListener locationListener) {
        this.locationListener = locationListener;
        String locationProvider = null;
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);

        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            Intent i = new Intent();
            i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
            return null;
        }

        //获取Location
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (locationListener != null && location != null) {
            locationListener.onLocationChanged(location);
        }

        //监视地理位置变化
        locationManager.requestLocationUpdates(locationProvider, 3000, 0, locationListener);
        return null;
    }


    public void release() {
        if (locationManager != null && locationManager != null) {
            locationManager.removeUpdates(locationListener);
            locationManager = null;
        }
    }
}
