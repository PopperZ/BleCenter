package com.pressure.blecentral.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
import android.util.Log;

import com.pressure.blecentral.ui.activity.MainActivity;

/**
 * @author zhangfeng
 * @data： 23/12/17
 * @description：蓝牙连接工具类
 */

public class BleConnectUtils {
    private static String TAG="BleConnectUtils";


    public static void ConnectPeripherals( String address, Context context){
        Log.e(TAG,"conectBluetooth");
        BluetoothDevice devices=(MainActivity.mBleAdapter).getRemoteDevice(address);
        if (devices==null){
            Log.e(TAG,"devices==null");
        }else{
            //得到gatt通信的对象
            MainActivity.mBluetoothGatt=devices.connectGatt(context,false,((MainActivity)context).mGattCallback);
            //连接
            MainActivity.mBluetoothGatt.connect();
        }
    }

    //断开连接
    public static void cutConnectBluetooth(){
        if (MainActivity.mBleAdapter.getRemoteDevice(MainActivity.mBleAdapter.getAddress())==null){
            Log.e(TAG," devices  is null");
        }else{
            if (MainActivity.mBluetoothGatt==null){
                Log.e(TAG,"mBluetooth is isconnect");
                return;
            }else{
                Log.e(TAG,"do disconnect mBluetooth");
                MainActivity.mBluetoothGatt.disconnect();
                MainActivity.mBluetoothGatt=null;
            }
        }
    }

}
