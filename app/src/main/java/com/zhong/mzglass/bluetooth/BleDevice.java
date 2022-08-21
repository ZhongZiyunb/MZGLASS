package com.zhong.mzglass.bluetooth;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zhong.mzglass.R;
import com.zhong.mzglass.utils.BleDeviceInfo;

public class BleDevice extends AppCompatActivity {


    private BleDeviceInfo mDeviceInfo;
    private ListView info_list;
    private BleAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        mDeviceInfo = (BleDeviceInfo) getIntent().getExtras().get("deviceInfo");
        initView();

    }

    private void initView() {
        info_list = (ListView) findViewById(R.id.device_list);
        mAdapter = new BleAdapter(mDeviceInfo,this);
        info_list.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
