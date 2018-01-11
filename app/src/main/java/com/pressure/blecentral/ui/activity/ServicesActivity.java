package com.pressure.blecentral.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pressure.blecentral.R;
import com.pressure.blecentral.utils.BleConnectUtils;
import com.pressure.blecentral.utils.ConstansUtils;
import com.pressure.blecentral.utils.DataUtils;

import java.util.Timer;
import java.util.TimerTask;


public class ServicesActivity extends BaseActivity {
    private ImageView mReturn;
    private TextView mDevicesName,mReadData,mPressure,mTemperature,mBattery,mTips;
    private EditText mSenddata;
    private Button mSend;
    private String data="";
    private Timer timer;
    private TimerTask task;
    public static ServicesActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        initView();
        change();
        registerReceiver(localReceiver,makeGattUpdateIntentFilter());
        //防止断开30s ，发送一条数据
        senddata();
        instance=this;
    }

    private void senddata() {
         timer=new Timer();
         task=new TimerTask() {
            @Override
            public void run() {
                DataUtils.sendData("5346");
            }
        };
        timer.schedule(task,8*1000,8*1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer!=null){
            timer.cancel();
            timer=null;
        }
        if (task!=null){
            task.cancel();
            task=null;
        }
    }

    private void initView() {
        mTips=findViewById(R.id.tips);
        mReturn=findViewById(R.id.back);
        mReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BleConnectUtils.cutConnectBluetooth();
            }
        });
        mDevicesName=findViewById(R.id.conStatus);
        mDevicesName.setText(MainActivity.conDeviceName);
        mReadData=findViewById(R.id.readData);
        mPressure=findViewById(R.id.pressure);
        mTemperature=findViewById(R.id.temperature);
        mBattery=findViewById(R.id.battery);
        mSenddata=findViewById(R.id.senddata);
        mSend=findViewById(R.id.send);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/12/26 发送数据
                if (mSenddata.getText().toString().isEmpty()){
                    Toast.makeText(ServicesActivity.this,"请输入发送数据",Toast.LENGTH_SHORT).show();
                }else {
                    DataUtils.sendData(mSenddata.getText().toString().trim());
                }
            }
        });
    }

    private void listenerDataChange(final String pressure, final String temperatur, final String battery, final String data){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPressure.setText("压力数据："+pressure+"公斤");
                mTemperature.setText("温度数据："+temperatur+"摄氏度");
                mBattery.setText("电量数据："+battery+"伏特");
                mReadData.setText(data);
            }
        });
    }

    //注册本地广播
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstansUtils.BLE_NOTIFY);
        intentFilter.addAction(ConstansUtils.BLE_DISCONNECT);
        return intentFilter;
    }

    //本地广播处理
    private final BroadcastReceiver localReceiver = new BroadcastReceiver() {
        String TAG = "localReceiver";
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            switch (action){
                case ConstansUtils.BLE_NOTIFY:
                    mTips.setVisibility(View.VISIBLE);
                    // TODO: 30/11/17 数据变化
                    Log.e(TAG,"ConstansUtils.BLE_NOTIFY");
                    final String pressure=intent.getStringExtra("pressure");
                    final String temperature=intent.getStringExtra("temperature");
                    final String battery=intent.getStringExtra("battery");
                    data=intent.getStringExtra("data")+"\n"+data;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listenerDataChange(pressure,temperature,battery,data);
                        }
                    });
                    break;
                case ConstansUtils.BLE_DISCONNECT:
                    unregisterReceiver(localReceiver);
                    finish();
                    break;
                default:
                    break;
            }
        }

    };

    private void change(){
        mSenddata.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                }else{
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BleConnectUtils.cutConnectBluetooth();
        }
        return super.onKeyDown(keyCode, event);
    }
}
