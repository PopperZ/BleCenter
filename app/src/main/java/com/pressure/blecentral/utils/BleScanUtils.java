package com.pressure.blecentral.utils;

import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.pressure.blecentral.ui.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangfeng
 * @data： 23/12/17
 * @description：蓝牙扫描工具类
 */

public class BleScanUtils {
    private static final String TAG="BleScanUtils";

    public static void startBleScan(Context context, String address) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.e(TAG, "start bleScan");
            //获取扫描对象
            ((MainActivity)context).mScanner = ((MainActivity)context).mBleAdapter.getBluetoothLeScanner();
            //扫描结果的集合
            List<ScanFilter> bleScnFilter = new ArrayList<>();
            bleScnFilter.add(new ScanFilter.Builder().setDeviceAddress(address).build());
            //扫描设置
            ScanSettings bleScanSetting = new ScanSettings.Builder().build();
            //开始扫描
            ((MainActivity)context).mScanner.startScan(null, bleScanSetting, ((MainActivity)context).mBleScanCallback);

        } else {
            Log.e(TAG, "build not support LeScan");
        }
    }

    public static void stopScnning(Context context) {
        //应该先判断是否实例化扫描对象，否则会出现空指针异常
        if (MainActivity.mScanner != null) {
            Log.e(TAG, "stop scnning");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                MainActivity.mScanner.stopScan(((MainActivity)context).mBleScanCallback);
            }
        }else {
            Log.e(TAG, "mScanner is null  scan maybe already stop");

        }
    }



}
