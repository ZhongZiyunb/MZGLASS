package com.zhong.mzglass.bluetooth.gatt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.zhong.mzglass.bluetooth.BleDevice;
import com.zhong.mzglass.utils.BleDeviceInfo;
import com.zhong.mzglass.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BleGattPresenter extends Binder implements IBleGattController {

    private BleDeviceInfo mTargetBleDevice = new BleDeviceInfo(); // 我的目标蓝牙设备信息
    private ArrayList<BleDeviceInfo> mBleDeviceList = new ArrayList<BleDeviceInfo>();
    private Context mContext;
    private ArrayList<BluetoothDevice> mBleDevices = new ArrayList<BluetoothDevice>();
    private BluetoothAdapter mBtAdapter; // 我的蓝牙设配器对象
    private String TAG = "BleGattPresenter";
    private BluetoothManager mBluetoothManager; // 我的蓝牙管理者对象
    private BluetoothGatt mBluetoothGatt; // GATT服务对象
    private BluetoothGattCharacteristic mTXCharacter; // GATT 服务的 写character
    private BluetoothGattCharacteristic mRXCharacter; // GATT 服务的 读character

    private IBleGattViewController mGattViewController;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 收到的广播类型
            String action = intent.getAction();
            // 发现设备的广播
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 从intent中获取设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 判断是否配对过
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    // 添加到列表
                    if (!mBleDevices.contains(device)) {
                        if (device.getName() != null) {
                            mBleDevices.add(device);
                            addToDeviceList(device);
                        }
                    }

                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG, "onReceive: finish discovery");
                for (BluetoothDevice d:mBleDevices) {
                    if (mGattViewController != null) {
                        mGattViewController.updateListView(d.getName());
                    }
                }
                dumpDeviceList();
            } else if (Constants.UPDATE_UUID.equals(action)) {
                if (mGattViewController != null) {
                    mGattViewController.updateName(intent.getStringExtra("DATA"));
                }
            } else if (Constants.UPDATE_CONN_STATE.equals(action)) {
                if (mGattViewController != null) {
                    mGattViewController.updateConnState(intent.getStringExtra("DATA"));
                }
            } else if (Constants.UPDATE_UUID_SERVICE.equals(action)) {
                if (mGattViewController != null) {
                    mGattViewController.updateUUIDService(intent.getStringExtra("DATA"));
                }
            }
        }
    };


    BleGattPresenter(Context c) {
        mContext = c;
        mTXCharacter = null;
        mRXCharacter = null;

    }

    void addToDeviceList(BluetoothDevice device) {
        BleDeviceInfo tmp_device_info = new BleDeviceInfo();

        if (device.getUuids() != null) {
            for (ParcelUuid dd : device.getUuids()) {
                Log.d(TAG, "scanDevice: service:" + dd.toString());
                tmp_device_info.uuids.add(dd.toString());
            }
            tmp_device_info.name = device.getName();
            tmp_device_info.macAddress = device.getAddress();
            mBleDeviceList.add(tmp_device_info);
        }
    }

    void dumpDeviceList() {
        Log.d(TAG, "dumpDeviceList: ============================");
        for(BluetoothDevice d:mBleDevices) {
            Log.d(TAG, "=====================================");
            Log.d(TAG, "scanDevice: NAME:" + d.getName() + ":");
            Log.d(TAG, "scanDevice: MAC:" + d.getAddress() + ":");

            if (d.getUuids() == null) continue;
            for (ParcelUuid dd:d.getUuids()) {
                Log.d(TAG, "scanDevice: service:" + dd.toString());
            }
        }
    }

    void registerBroadcast() {

        Log.d(TAG, "registerBroadcast:" );
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction("UPDATE_UUID");
        intentFilter.addAction("UPDATE_CONN_STATE");
        intentFilter.addAction("UPDATE_UUID_SERVICE");
        mContext.registerReceiver(receiver,intentFilter);
    }

    private boolean first_in = true;
    // 进行初始化
    void init() {

        if (first_in) {
            registerBroadcast();
            first_in = false;
        }

        mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        // 开启蓝牙
        if (!mBtAdapter.isEnabled()) {
            // 强行打开
            mBtAdapter.enable();
            Log.d(TAG, "init: bluetooth enable ");
        }

    }

    @Override
    public void scanDevice() {

        init();

        List<BluetoothDevice> GattDevices = mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
        if (GattDevices!=null && GattDevices.size()>0){
            for (int i=0;i<GattDevices.size();i++) {
                if (!mBleDevices.contains(GattDevices.get(i))) {
                    mBleDevices.add(GattDevices.get(i));
                    addToDeviceList(GattDevices.get(i));
                }
            }
        }
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        // 判断是否有配对过的设备
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                // 遍历到列表中
                if (!mBleDevices.contains(device)) {
                    mBleDevices.add(device);
                    addToDeviceList(device);
                    Log.d(TAG, "scanDevice: found" + device.getName());
                }
            }
        }

        // 开始搜索
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        mBtAdapter.startDiscovery();

        // 显示每个结果的名称
        for(BluetoothDevice d:mBleDevices) {
            Log.d(TAG, "=====================================");
            Log.d(TAG, "scanDevice: NAME:" + d.getName() + ":");
            Log.d(TAG, "scanDevice: MAC:" + d.getAddress() + ":");
            //mGattViewController.updateListView(d.getName());
            if (d.getUuids() == null) continue;
            for (ParcelUuid dd:d.getUuids()) {
                Log.d(TAG, "scanDevice: service:" + dd.toString());
            }
        }
    }



    @Override
    public void connect(String s) {

        // 连接指定s
        boolean flag = false;
        init();
        dumpDeviceList();
        // 再加载一次缓存中的
        List<BluetoothDevice> GattDevices = mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
        if (GattDevices!=null && GattDevices.size()>0){
            for (int i=0;i<GattDevices.size();i++) {
                if (!mBleDevices.contains(GattDevices.get(i))) {
                    mBleDevices.add(GattDevices.get(i));
                    addToDeviceList(GattDevices.get(i));
                }
            }
        }
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        // 判断是否有配对过的设备
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                // 遍历到列表中
                if (!mBleDevices.contains(device)) {
                    mBleDevices.add(device);
                    addToDeviceList(device);
                    Log.d(TAG, "scanDevice: found" + device.getName());
                }
            }
        }

        // 在list中查找对应的设备
        for (BluetoothDevice d:mBleDevices) {
            if (d.getName() == null) continue;
            if (d.getName().equals(s)) {
                if (d.getAddress() != null) {
                    mTargetBleDevice.macAddress = d.getAddress();
                    mTargetBleDevice.name = d.getName();
                    String[] a;
                    if (d.getUuids() != null) {
                        for (ParcelUuid uuid : d.getUuids()) {
                            mTargetBleDevice.uuids.add(uuid.toString());
                        }
                    }
                    Log.d(TAG, "connect: found target device:" + d.getName());
                    flag = true;
                }

                break;
            }
        }
        // 如果查找不到这样的设备，提示一下
        if (!flag) {
            Toast.makeText(mContext,"no such device or still finding",Toast.LENGTH_SHORT).show();
            Log.d(TAG, "connect: no such device name or unavailable");
            return;
        }
        if (mGattViewController != null){
            mGattViewController.updateListView(mTargetBleDevice.name);
        }
        Log.d(TAG, "connect: " + mTargetBleDevice.name);
        Log.d(TAG, "connecting: " + mTargetBleDevice.name);
        BluetoothDevice btDevice = mBtAdapter.getRemoteDevice(mTargetBleDevice.macAddress);
        mBluetoothGatt = btDevice.connectGatt(mContext, false, mGattCallback);

    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.d(TAG, "onConnectionStateChange: " + newState);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.d(TAG, "onConnectionStateChange: " + mTargetBleDevice.name + "connected");
                    gatt.discoverServices(); // 连接后开始搜索服务
                    break;
                case BluetoothProfile.STATE_CONNECTING:
                    Log.d(TAG, "onConnectionStateChange: " + mTargetBleDevice.name + "connecting" );
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.d(TAG, "onConnectionStateChange: " + mTargetBleDevice.name + "disconnected" );
                    gatt.close(); // 断开后关闭服务
                    break;
                case BluetoothProfile.STATE_DISCONNECTING:
                    Log.d(TAG, "onConnectionStateChange: " + mTargetBleDevice.name + "disconnecting" );
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            switch (status) {
                case BluetoothGatt.GATT_SUCCESS:
                    List<BluetoothGattService> serviceList = gatt.getServices();
                    for (BluetoothGattService btGattService : serviceList) {
                        // 遍历服务中包含的所有Characteristic
                        // 直接筛选目标服务
                        Log.d(TAG, "onServicesDiscovered: service:" + btGattService.getUuid().toString());
                        mTargetBleDevice.uuids.add("Service: " +btGattService.getUuid().toString());
                        for (BluetoothGattCharacteristic c: btGattService.getCharacteristics()) {
                            Log.d(TAG, "onServicesDiscovered: " + c.getUuid().toString());
                            mTargetBleDevice.uuids.add("character: "+ c.getUuid().toString());
                            // 将新的服务UUID加入进来
                            for (int i=0;i<mBleDeviceList.size();i++) {
                                if (!mBleDeviceList.get(i).name.equals(mTargetBleDevice.name)) continue;
                                if (!mBleDeviceList.get(i).uuids.contains(c.getUuid().toString())) {
                                    mBleDeviceList.get(i).uuids.add(c.getUuid().toString());
                                }
                            }
                        }
                        Log.d(TAG, "onServicesDiscovered: chracter:" + btGattService.getCharacteristics().toString());
                        if(btGattService.getUuid().toString().equals(Constants.UUID_UART_SERVICE)) {
                            List<BluetoothGattCharacteristic> charics = btGattService.getCharacteristics();
                            // 找到对应的Character
                            findTargetCharacter(charics);
                        } else {
                            // 显示未找到目标设备
                            Log.d(TAG, "onServicesDiscovered: not found expected service");
                        }
                    }
                    break;
            }
            Intent intent = new Intent(Constants.UPDATE_CONN_STATE);
            intent.putExtra("DATA","UUID INFO COLECT FINISH");
            mContext.sendBroadcast(intent);

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.d(TAG, "onCharacteristicRead: ");
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d(TAG, "onCharacteristicWrite: ");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.d(TAG, "onCharacteristicChanged: ");
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.d(TAG, "onDescriptorRead: ");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.d(TAG, "onDescriptorWrite: ");
        }
    }; // GATT服务的回调函数

    // 辅助函数
    private void findTargetCharacter(@NonNull List<BluetoothGattCharacteristic> charics) {
        for (BluetoothGattCharacteristic c: charics) {
            if (c.getUuid().toString().equals(Constants.UUID_RX_CHARACTERISTIC)) {
                mBluetoothGatt.setCharacteristicNotification(c, true);
                // UI显示监听是否设置成功
                Log.d(TAG, "onServicesDiscovered: found RX LISTEN CHARACTER " + c.getUuid().toString());
                break;
            } else if (c.getUuid().toString().equals(Constants.UUID_TX_CHARACTERISTIC)) {
                mTXCharacter = c;

                Log.d(TAG, "onServicesDiscovered: found TX LISTEN CHARACTER " + c.getUuid().toString());
                Intent intent = new Intent(Constants.UPDATE_UUID);
                intent.putExtra("DATA","found TX LISTEN CHARACTER");
                Intent intent1 = new Intent(Constants.UPDATE_UUID_SERVICE);
                intent1.putExtra("DATA",Constants.UUID_TX_CHARACTERISTIC);
                mContext.sendBroadcast(intent);
                mContext.sendBroadcast(intent1);
                Log.d(TAG, "onServicesDiscovered: found TX LISTEN CHARACTER " + c.getUuid().toString());
            } else {
                Log.d(TAG, "onServicesDiscovered: not found RX LISTEN CHARACTER");
            }
        }
    }


    @Override
    synchronized public void sendMessage(String s, String func) {
        //TODO:添加数据封装功能
        Log.d(TAG, "sendMessage: " + s);
        String info = dataWrap(s, func);
        if (mTXCharacter != null) {
            mTXCharacter.setValue(info);
            mBluetoothGatt.writeCharacteristic(mTXCharacter);
        } else {
            Toast.makeText(mContext,"蓝牙服务未连接",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public BleDeviceInfo getDeviceInfo(int i) {
        if (i < mBleDeviceList.size()) {
            return mBleDeviceList.get(i);
        } else {
            return null;
        }
    }

    @Override
    public BleDeviceInfo getTargetDeviceInfo() {
        return mTargetBleDevice;
    }

    @Override
    public void registerViewController(IBleGattViewController viewController) {
        mGattViewController = viewController;
    }

    @Override
    public void unregisterViewController() {
        mGattViewController = null;
    }

    private String dataWrap(String s, String func) {
        // TODO: 增加不同类型数据的封装功能
        String res = "at" + " " + func + " " + s;
        return res;
    }



    @Override
    public void close() {
        //TODO:
        Log.d(TAG, "close: ");
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
        }
        if (mContext != null) {
            try {
                mContext.unregisterReceiver(receiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        try {
            mContext.unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
