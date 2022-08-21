package com.zhong.mzglass.bluetooth.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhong.mzglass.R;


import java.util.ArrayList;
import java.util.List;

public class BleDemoActivity extends AppCompatActivity implements View.OnClickListener {

    private final int REQUEST_ENABLE_BT = 1;
    private RecyclerView rvDeviceList;
    private Button btnScan;
    private BluetoothAdapter mBtAdapter;
    private BleDemoService mBleService;
    private BroadcastReceiver mBleReceiver;
    private DeviceListAdapter mDeviceListAdapter;
    private List<BluetoothDevice> mBluetoothDeviceList;
    private List<String> mRssiList;
    private String TAG = "BleDemoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_demo);

        rvDeviceList = findViewById(R.id.rv_device_list);
        btnScan = findViewById(R.id.btn_scan);
        btnScan.setOnClickListener(this);

        initBle();
        initData();
        registerBleReceiver();
    }

    /**
     * 初始化蓝牙
     */
    private void initBle() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            Toast.makeText(this, "蓝牙不可用", Toast.LENGTH_LONG).show();
            return;
        }

        if (!mBtAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
            return;
        }

        // 搜索蓝牙设备
        scanBleDevice();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 蓝牙设备列表
        mBluetoothDeviceList = new ArrayList<>();
        // 蓝牙设备RSSI列表
        mRssiList = new ArrayList<>();
        mDeviceListAdapter = new DeviceListAdapter(mBluetoothDeviceList, mRssiList);
        rvDeviceList.setLayoutManager(new LinearLayoutManager(this));
        rvDeviceList.setAdapter(mDeviceListAdapter);

        // 连接蓝牙设备
        mDeviceListAdapter.setOnItemClickListener(new DeviceListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(BleDemoActivity.this, "开始连接", Toast.LENGTH_SHORT).show();
                mBtAdapter.stopLeScan(mLeScanCallback);
                mBleService.connect(mBtAdapter, mBluetoothDeviceList.get(position).getAddress());
            }
        });
    }

    /**
     * 注册蓝牙信息接收器
     */
    private void registerBleReceiver() {
        // 绑定服务
        Intent intent = new Intent(this, BleDemoService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        startService(intent);

        // 注册蓝牙信息广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BleDemoService.ACTION_GATT_CONNECTED);
        filter.addAction(BleDemoService.ACTION_GATT_DISCONNECTED);
        filter.addAction(BleDemoService.ACTION_GATT_SERVICES_DISCOVERED);
        filter.addAction(BleDemoService.ACTION_DATA_AVAILABLE);
        filter.addAction(BleDemoService.ACTION_CONNECTING_FAIL);
        mBleReceiver = new BleReceiver();
        registerReceiver(mBleReceiver, filter);
    }

    /**
     * 搜索蓝牙设备
     */
    private void scanBleDevice() {
        mBtAdapter.stopLeScan(mLeScanCallback);
        mBtAdapter.startLeScan(mLeScanCallback);
        // 搜索10s
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBtAdapter.stopLeScan(mLeScanCallback);
            }
        }, 10000);
    }

    /**
     * 搜索蓝牙设备回调
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            Log.d(TAG, "onLeScan: in");
            if (!mBluetoothDeviceList.contains(bluetoothDevice)) {
                mBluetoothDeviceList.add(bluetoothDevice);
                mRssiList.add(String.valueOf(i));
                mDeviceListAdapter.setmRssiList(mRssiList);
                mDeviceListAdapter.setmBluetoothDeviceList(mBluetoothDeviceList);
                mDeviceListAdapter.notifyDataSetChanged();
                rvDeviceList.setAdapter(mDeviceListAdapter);
            }
        }
    };

    /**
     * 服务
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mBleService = ((BleDemoService.LocalBinder) rawBinder).getService();
        }

        public void onServiceDisconnected(ComponentName classname) {
            mBleService = null;
        }
    };

    /**
     * 蓝牙信息接收器
     */
    private class BleReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            switch (action) {
                case BleDemoService.ACTION_GATT_CONNECTED:
                    Toast.makeText(BleDemoActivity.this, "蓝牙已连接", Toast.LENGTH_SHORT).show();
                    break;

                case BleDemoService.ACTION_GATT_DISCONNECTED:
                    Toast.makeText(BleDemoActivity.this, "蓝牙已断开", Toast.LENGTH_SHORT).show();
                    mBleService.release();
                    break;

                case BleDemoService.ACTION_CONNECTING_FAIL:
                    Toast.makeText(BleDemoActivity.this, "蓝牙已断开", Toast.LENGTH_SHORT).show();
                    mBleService.disconnect();
                    break;

                case BleDemoService.ACTION_DATA_AVAILABLE:
                    byte[] data = intent.getByteArrayExtra(BleDemoService.EXTRA_DATA);
                    Log.i("蓝牙", "收到的数据：" + ByteUtils.byteArrayToHexString(data));
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_scan: // 搜索蓝牙
                // 搜索蓝牙设备
                scanBleDevice();
                // 初始化数据
                initData();
                // 注册蓝牙信息接收器
                registerBleReceiver();
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                // 搜索蓝牙设备
                scanBleDevice();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBleReceiver != null) {
            unregisterReceiver(mBleReceiver);
            mBleReceiver = null;
        }
        unbindService(mServiceConnection);
        mBleService = null;
    }
}
