package com.pressure.blecentral.ui.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.pressure.blecentral.R;
import com.pressure.blecentral.services.entity.Devices;
import com.pressure.blecentral.ui.adapter.DevicesAdapter;
import com.pressure.blecentral.utils.BleConnectUtils;
import com.pressure.blecentral.utils.BleInitUtils;
import com.pressure.blecentral.utils.BleScanUtils;
import com.pressure.blecentral.utils.BleUtils;
import com.pressure.blecentral.utils.ConstansUtils;
import com.pressure.blecentral.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public  class MainActivity extends BaseActivity {
    private static  final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION  = 100;
    private ListView mDevices;
    private static String TAG ="MainActivity";
    private static List<Devices>mList;
    private static DevicesAdapter mDevicesAdapter;
    private Button mReStartScan;

    //蓝牙变量
    public static  BluetoothAdapter mBleAdapter;
    public static  BluetoothLeScanner mScanner;
    public static  BluetoothGatt mBluetoothGatt;
    public static  String conDeviceName;
    public static  BluetoothGattCharacteristic mChar;
    private long mExitTime;
    private boolean isClick=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mList=new ArrayList<Devices>();
        mDevices=findViewById(R.id.devices);
        mReStartScan=findViewById(R.id.reStartScan);
        mReStartScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mList.isEmpty()){
                    Log.e(TAG,"mlstSize:"+mList.size());
                }else {
                    mList.clear();
                    mDevicesAdapter.notifyDataSetChanged();
                }
                initBle();
            }
        });
        checkBluetoothPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleConnectUtils.cutConnectBluetooth();
        BleScanUtils.stopScnning(MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isClick=true;
    }

    //初始化蓝牙
    private void initBle() {
        if (BleInitUtils.initBle(MainActivity.this)){
            BleScanUtils.startBleScan(MainActivity.this,mBleAdapter.getAddress());
        }
    }

    //显示设备视图
    private  void showDevices() {
        mDevicesAdapter= new DevicesAdapter(mList,MainActivity.this);
        mDevices.setAdapter(mDevicesAdapter);
        mDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isClick){
                    isClick=false;
                    mDevices.setClickable(false);
                    BleScanUtils.stopScnning(MainActivity.this);
                    Log.e(TAG,"position:"+position+"\n"+"data:"+parent.getAdapter().getItem(position).toString());
                    Devices devices= (Devices) parent.getAdapter().getItem(position);
                    Log.e(TAG,devices.getAddress());
                    connectBle(devices.getAddress());
                    conDeviceName=((Devices) parent.getAdapter().getItem(position)).getName();
                }
            }
        });
    }

    //连接蓝牙
    private void connectBle( String address) {
        BleConnectUtils.ConnectPeripherals(address,MainActivity.this);
    }

    // TODO: 2017/12/18 监听蓝牙开启的广播
    /*
     校验蓝牙权限
    */
    private void checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //校验是否已具有模糊定位权限
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            } else {
                //具有权限
                initBle();
            }
        } else {
            //系统不高于6.0直接执行
            initBle();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //同意权限,做跳转逻辑
                initBle();
            } else {
                // 权限拒绝，提示用户开启权限
                denyPermission();
            }
        }
    }

    private void denyPermission() {
        Toast.makeText(MainActivity.this,"蓝牙权限已经拒绝！", Toast.LENGTH_SHORT).show();
    }


    //扫描回调
    public  ScanCallback mBleScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Log.e(TAG, "bleScan success"+result.toString());
                //扫描的设备名字
                Log.e(TAG, "device name:" + result.getDevice().getName());
                //扫描的设备蓝牙地址
                Log.e(TAG, "device address:" + result.getDevice().getAddress());
                //扫描的设备通道的UUID
                ParcelUuid[] uuid=result.getDevice().getUuids();
                Log.e(TAG, "device Services UUID:" + result.getDevice().getUuids());
                //扫描的设备广播标志
                String flags=Integer.toHexString(result.getScanRecord().getAdvertiseFlags());
                Log.e(TAG, "record advertise flags:" + Integer.toHexString(result.getScanRecord().getAdvertiseFlags()));
                Log.e(TAG, "record advertise flags:" + result.getScanRecord().getAdvertiseFlags());
                //蓝牙传输功率
                Log.e(TAG, "record Tx power level:" + result.getScanRecord().getTxPowerLevel());
                //设备名称
                Log.e(TAG, "record device name:" + result.getScanRecord().getDeviceName());
                //服务的UUID
                List<ParcelUuid> ServiceList=new ArrayList<ParcelUuid>();
                ServiceList =result.getScanRecord().getServiceUuids();
                Log.e(TAG, "record services UUID:" + result.getScanRecord().getServiceUuids());
                //广播的数据
                Log.e(TAG, "record services data:" + result.getScanRecord().getServiceData());
                //广播的数据
                Log.e(TAG, "record services data2:" + result.getScanRecord().getServiceData());
                //广播的特征值的UUID
                Log.e(TAG, "record services readuuid:" +result.getDevice().getAddress().toString() );
                String UUID;
                if (ServiceList==null){
                    UUID="no services";
                }else {
                    UUID=ServiceList.size()+"services";
                }
                for (int i=0;i<mList.size();i++){
                    if (mList.get(i).getAddress().equals(result.getDevice().getAddress())){
                        return;
                    }
                }
                Devices devices=new Devices(result.getScanRecord().getDeviceName(),result.getDevice().getAddress()
                        ,UUID,result.getDevice().getAddress().toString());
                mList.add(devices);
                showDevices();
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e(TAG, "Blescan fail " + errorCode);
        }
    };


    //连接回调
    public  BluetoothGattCallback mGattCallback=new BluetoothGattCallback() {
        //连接状态改变
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (gatt==null){
                Log.e(TAG,"mBluetooth is null");
                return;
            }
            if (newState== BluetoothProfile.STATE_CONNECTED){
                //连接上去执行此方法，获取通信服务
                gatt.discoverServices();
                Log.e(TAG,"已连接");
//                Toast.makeText(MainActivity.this,"连接成功",Toast.LENGTH_SHORT).show();
            }else  if (newState== BluetoothProfile.STATE_CONNECTING){
                Log.e(TAG,"正在连接");
            }else  if (newState== BluetoothProfile.STATE_DISCONNECTED){
                Log.e(TAG,"断开连接"+status+newState);
//                ServicesActivity.instance.finish();
                sendBroadcast(new Intent(ConstansUtils.BLE_DISCONNECT));
            }else  if (newState== BluetoothProfile.STATE_DISCONNECTING){
                Log.e(TAG,"正在断开连接");
            }
        }

        //当D发现可用通道服务的时候回调
        @Override//onServicesDiscovered
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.e(TAG,"有可用服务"+gatt.getServices().toString());
            if (status==BluetoothGatt.GATT_SUCCESS){
                List<BluetoothGattService> service=gatt.getServices();
//                getUUID(service);
                Log.e(TAG,"有可用服务");
                for (int i=0;i<service.size();i++){
                    Log.e(TAG,"有可用服务4"+service.get(i).getUuid().toString());
                    if (service.get(i).getUuid().equals(UUID.fromString(ConstansUtils.SERVICES_UUID))){
                        mChar=service.get(i).getCharacteristic(UUID.fromString(ConstansUtils.READ_UUID));
                        Log.e(TAG,"有可用服务2");
                        DataUtils.readChar();
                    }else {
                        Log.e(TAG,"有可用服务3");
                    }
                }
                startActivity(new Intent(MainActivity.this,ServicesActivity.class));
            }else if (status==BluetoothGatt.GATT_FAILURE){
                Log.e(TAG,"没有发现可用服务");
//                Toast.makeText(MainActivity.this,"没有发现可用服务",Toast.LENGTH_SHORT).show();
            }else{
                Log.e(TAG,"没有发现可用服务"+status);
//                Toast.makeText(MainActivity.this,"没有发现可用服务2",Toast.LENGTH_SHORT).show();
            }
        }
        //当通道数据发送改变的时候回调
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.e(TAG,characteristic+"");
            if (characteristic.getValue()!=null){
                Log.e(TAG,"数据发生变化"+ BleUtils.toHexString(characteristic.getValue()));
                byte[] data=characteristic.getValue();
                Log.e(TAG,"压力数据："+ BleUtils.getPressureData(data));
                Log.e(TAG,"温度数据："+ BleUtils.getTemperatureData(data));
                Log.e(TAG,"电量数据："+ BleUtils.getBatteryData(data));
                Log.e(TAG,"数据长度："+ characteristic.getValue().length);
                Intent intent=new Intent(ConstansUtils.BLE_NOTIFY);
                intent.putExtra("pressure",BleUtils.getPressureData(data));
                intent.putExtra("temperature",BleUtils.getTemperatureData(data));
                intent.putExtra("battery",BleUtils.getBatteryData(data));
                intent.putExtra("data",BleUtils.toHexString(characteristic.getValue()));
                sendBroadcast(intent);
            }else{
                Log.e(TAG,"数据未发生变化");
            }
        }

        //当通道写入的时候回调
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.e(TAG,"write state"+status);
            if (status==BluetoothGatt.GATT_SUCCESS){
                Log.e(TAG,"write state is success");
            }else if (status==BluetoothGatt.GATT_FAILURE){
                Log.e(TAG,"write state is fail");
            }
        }

        //当通道被读的时候回调
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status==BluetoothGatt.GATT_SUCCESS){
                Log.e(TAG,characteristic.getValue()+"我是分割线"+status);
            }

        }
        //当Descriptor写入的时候回调
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            String uuid=descriptor.getValue().toString();
            Log.e(TAG,uuid+"");
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

}
