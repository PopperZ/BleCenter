package com.pressure.blecentral.utils;


import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.pressure.blecentral.ui.activity.MainActivity;

/**
 * @author zhangfeng
 * @data： 23/12/17
 * @description：初始化蓝牙
 */

public class BleInitUtils {
    private static  final String TAG="BleInitUtils";
    //判断蓝牙并初始化蓝牙
    public static boolean initBle(Context mContext){
        boolean isEnable=false;
        if (judgeSupportBle(mContext)) {
            Log.e(TAG,"judgeSupportBle  is true");
            //获取蓝牙管理类对象
            BluetoothManager manager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (manager == null) {
                Toast.makeText(mContext, "该设备无法搜索到蓝牙设备", Toast.LENGTH_SHORT).show();
                isEnable=false;
                Log.e(TAG,"manager  is null");
            } else {
                Log.e(TAG,"manager not is null");
                //获取蓝牙适配器
                ((MainActivity)mContext).mBleAdapter= manager.getAdapter();
                if (((MainActivity)mContext).mBleAdapter == null) {
                    Toast.makeText(mContext, "该设备无法搜索到蓝牙设备", Toast.LENGTH_SHORT).show();
                    isEnable=false;
                } else {
                    // TODO: 2017/12/18 客户是否需求为自动打开蓝牙or弹窗由用户手动打开
                    if (!((MainActivity)mContext).mBleAdapter.isEnabled()) {
                        ((MainActivity)mContext).mBleAdapter.enable();
                        isEnable=false;
                        Log.e(TAG,"该设备上BLE未打开，已强制打开");
                    } else {
                        Log.e(TAG,"该设备上BLE已打开");
                        isEnable=true;
                        // 蓝牙打开状态的操作
//                        mBleInitView.onStartScan(adapter,adapter.getAddress());
                    }
                    //当然此处也可采用，通过用户点击来打开蓝牙
                    // Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);            //enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //context.startActivity(enableBtIntent);
                }
            }
        }else {
            isEnable=false;
        }
        return isEnable;
    }

    //判断是否支持ble蓝牙
    private static boolean judgeSupportBle(Context mContext){
        Log.e(TAG,"judgeSupportBle");
        if(!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.e(TAG,"该设备上不支持BLE2");
            Toast.makeText(mContext, "该设备上不支持BLE2", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Toast.makeText(mContext, "该设备上支持BLE2", Toast.LENGTH_SHORT).show();
            Log.e(TAG,"该设备上支持BLE2");
            return true;
        }
    }
}
