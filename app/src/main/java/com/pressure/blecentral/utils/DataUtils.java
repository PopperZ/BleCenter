package com.pressure.blecentral.utils;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.pressure.blecentral.ui.activity.MainActivity;

import java.util.UUID;

/**
 * @author zhangfeng
 * @data： 23/12/17
 * @description：
 */

public class DataUtils {
    private static String TAG="DataUtils";

    //发送蓝牙数据
    public static void sendData(String data){
        BluetoothGattService service= MainActivity.mBluetoothGatt.getService(UUID.fromString(ConstansUtils.SERVICES_UUID));
        if (service!=null){
            BluetoothGattCharacteristic characteristic=service.getCharacteristic(UUID.fromString(ConstansUtils.WRITE_UUID));
            if (characteristic!=null){
                if (characteristic.getProperties()== BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE){
                    byte[]bytes=BleUtils.hexStringToByte(data);
                    characteristic.setValue(bytes);
                    MainActivity.mBluetoothGatt.writeCharacteristic(characteristic);
                    Log.e(TAG,"write is done");
                }
            }else {
                Log.e(TAG,"characteristic is null");
            }
        }else {
            Log.e(TAG,"mServices is null");
        }
    }

    public static void readChar(){
        Log.e(TAG, "BluetoothAdapter readChar");
        if (MainActivity.mBleAdapter == null || MainActivity.mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
//        MainActivity.mBluetoothGatt.readCharacteristic(MainActivity.mChar);
        MainActivity.mBluetoothGatt.setCharacteristicNotification(MainActivity.mChar,true);
    }

    private static byte[] start(){
//        0x53 0x46 0xB4 0x02 0x00 0x00 0x00 0x00 0x00 0x01
        byte[]bytes=new byte[10];
        bytes[0]=0x53;
        bytes[1]=0x46;
        bytes[2]= (byte) 0xB4;
        bytes[3]=0x02;
        bytes[4]=0x00;
        bytes[5]=0x00;
        bytes[6]=0x00;
        bytes[7]=0x00;
        bytes[8]=0x00;
        bytes[9]=0x01;
        return bytes;
    }

    private static byte[] stop(){
//        0x53 0x46 0xB4 0x03
        byte[]bytes=new byte[4];
        bytes[0]=0x53;
        bytes[1]=0x46;
        bytes[2]= (byte) 0xB4;
        bytes[3]=0x03;
        return bytes;
    }

}
