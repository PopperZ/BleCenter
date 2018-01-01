package com.pressure.blecentral.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pressure.blecentral.R;
import com.pressure.blecentral.utils.ConstansUtils;
import com.pressure.blecentral.utils.DataUtils;


public class ServicesActivity extends BaseActivity {
    private ImageView mReturn;
    private TextView mDevicesName,mReadData,mPressure,mTemperature,mBattery;
    private EditText mSenddata;
    private Button mSend;
    private String data="接受到的数据：";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        initView();
        registerReceiver(localReceiver,makeGattUpdateIntentFilter());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(localReceiver);
    }

    private void initView() {
        mReturn=findViewById(R.id.back);
        mReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                mPressure.setText("压力数据："+pressure);
                mTemperature.setText("温度数据："+temperatur);
                mBattery.setText("电量数据："+battery);
                mReadData.setText(data);
            }
        });
    }

    //注册本地广播
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstansUtils.BLE_NOTIFY);
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
                    // TODO: 30/11/17 数据变化
                    Log.e(TAG,"ConstansUtils.BLE_NOTIFY");
                    String pressure=intent.getStringExtra("pressure");
                    String temperature=intent.getStringExtra("temperature");
                    String battery=intent.getStringExtra("battery");
                    data=data+"\n"+intent.getStringExtra("data");
                    listenerDataChange(pressure,temperature,battery,data);
                    break;
                default:
                    break;
            }
        }

    };

}
