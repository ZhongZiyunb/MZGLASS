package com.zhong.mzglass.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothHidDeviceAppQosSettings;
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

import com.zhong.mzglass.utils.BleDeviceInfo;
import com.zhong.mzglass.utils.Constants;

import java.io.CharArrayWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingDeque;

public class BlePresenter extends Binder implements IBleController {

    private String TAG = "BlePresenter";
    private IBleViewController mViewController;
    private BleDeviceInfo deviceInfo = new BleDeviceInfo();
    private BluetoothAdapter mBtAdapter;
    private ArrayList<BluetoothDevice> detected_devices;
    private Context mContext;
    private String targetServiceUUID = "";
    private BluetoothGattCharacteristic mTXCharacter = null;
    private BluetoothGattCharacteristic mRXCharacter = null;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothManager bluetoothManager;
    private ArrayList<BluetoothDevice> tvDevices = new ArrayList<BluetoothDevice>();
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
                    tvDevices.add(device);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG, "onReceive: finish discovery");
                for (BluetoothDevice d:tvDevices) {
                    Log.d(TAG, "onReceive: MAC:" + d.getAddress()+" NAME:" + d.getName());
                }
            } else if (Constants.UPDATE_UUID.equals(action)) {
                if (mViewController != null) {
                    mViewController.updateName(intent.getStringExtra("DATA"));
                }
            } else if (Constants.UPDATE_CONN_STATE.equals(action)) {
                if (mViewController != null) {
                    mViewController.updateConnState(intent.getStringExtra("DATA"));
                }
            }
        }
    };


    BlePresenter(Context context) {
        mContext = context;
    }


    public boolean isConnected(String macAddress){
        if (!BluetoothAdapter.checkBluetoothAddress(macAddress)){
            return false;
        }
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);

        Method isConnectedMethod = null;
        boolean isConnected;
        try {
            isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
            isConnectedMethod.setAccessible(true);
            isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
        } catch (NoSuchMethodException e) {
            isConnected = false;
        } catch (IllegalAccessException e) {
            isConnected = false;
        } catch (InvocationTargetException e) {
            isConnected = false;
        }
        return isConnected;
    }

    // 获取已经配对过的设备列表
    public ArrayList<BluetoothDevice> getConnectedDevicesV2(Context context){

        ArrayList<BluetoothDevice> result = new ArrayList<>();
        Set<BluetoothDevice> deviceSet = new HashSet<>();

        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        // 获取BLE的设备, profile只能是GATT或者GATT_SERVER
        List GattDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
        if (GattDevices!=null && GattDevices.size()>0){
            deviceSet.addAll(GattDevices);
        }
        //获取已配对的设备
        Set ClassicDevices = bluetoothManager.getAdapter().getBondedDevices();
        if (ClassicDevices!=null && ClassicDevices.size()>0){
            deviceSet.addAll(ClassicDevices);
        }

        for (BluetoothDevice dev:deviceSet
        ) {
            String Type = "";
            switch (dev.getType()){
                case BluetoothDevice.DEVICE_TYPE_CLASSIC:
                    Type = "经典";
                    break;
                case BluetoothDevice.DEVICE_TYPE_LE:
                    Type = "BLE";
                    break;
                case BluetoothDevice.DEVICE_TYPE_DUAL:
                    Type = "双模";
                    break;
                default:
                    Type = "未知";
                    break;
            }
            String connect = "设备未连接";
            if (isConnected(dev.getAddress())){
//                result.add(dev);
                connect = "设备已连接";
            }
            result.add(dev);
            Log.d(TAG, connect+", address = "+dev.getAddress() + "("+ Type + "), name --> "+dev.getName());
        }
        return result;
    }


    // TODO:用户决定是否开启蓝牙
    @Override
    public void init() {

        // 注册广播
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(Constants.UPDATE_UUID);
        intentFilter.addAction(Constants.UPDATE_CONN_STATE);
        mContext.registerReceiver(receiver,intentFilter);
        bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        // 开启蓝牙
        if (!mBtAdapter.isEnabled()) {
            // 强行打开
            mBtAdapter.enable();
            Log.d(TAG, "init: bluetooth enable ");
        }
    }

    // 搜索可用的蓝牙设备 + 配对过的蓝牙设备 统一存在一个列表中
    @Override
    public void scanDevice() {

        init();
        List<BluetoothDevice> GattDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
        if (GattDevices!=null && GattDevices.size()>0){
            for (BluetoothDevice d: GattDevices) {
                tvDevices.add(d);
            }
        }
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        // 判断是否有配对过的设备
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                // 遍历到列表中
                tvDevices.add(device);
                Log.d(TAG, "scanDevice: found" + device.getName());

            }
        }

        // 开始搜索
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        mBtAdapter.startDiscovery();
        Log.d(TAG, "scanDevice: scan finished");

        for(BluetoothDevice d:tvDevices) {
            Log.d(TAG, "scanDevice: " + d.getName() + ":");
            if (d.getUuids() == null) continue;
            for (ParcelUuid dd:d.getUuids()) {
                Log.d(TAG, "scanDevice: service:" + dd.toString());
            }
        }
    }

    @Override
    public void findDevice() {
        detected_devices = getConnectedDevicesV2(mContext);
        // 选取目标 device 建立 deviceInfo

        Log.d(TAG, "findDevice: size:" + detected_devices.size());
        for (BluetoothDevice btd:detected_devices) {
            // 查找目标设备设备 开始建立 deviceInfo
            String n = btd.getName();
            Log.d(TAG, "findDevice: device: " + n);
            if (n == null) continue;
            if (n.equals("HUAWEI FreeBuds 4")){
                // 查找目标设备中的目标服务
                deviceInfo.macAddress = btd.getAddress();
                deviceInfo.name = btd.getName();

                mViewController.updateMac(deviceInfo.macAddress);
                mViewController.updateName(deviceInfo.name);
                Log.d(TAG, "findDevice: found target device" + btd.getName());

                for(ParcelUuid uuid: btd.getUuids()) {
                    Log.d(TAG, "findDevice: " + uuid.toString());
                    if(uuid.toString().equals(Constants.UUID_TEST_UART_SERVICE)) {
                        // 说明发现了Service 进行UI状态更新
                        mViewController.updateUUIDService(uuid.toString());
                        Log.d(TAG, "findDevice: uuid.toString()");
                    } else {
                        // 未找到目标Service 暂不进行UI更新
//                        mViewController.updateUUIDService("no target service found");
//                        Log.d(TAG, "findDevice: no target service found");
                    }
                }
                break;
            } else {
                // TODO: 没查找到则UI进行相应的更新
                mViewController.updateName("no target device found");
//                Log.d(TAG, "findDevice: no target device found");
            }

        }
    }
    // GATT服务的回调函数
    private BluetoothGattCallback mGattCallback =  new BluetoothGattCallback() {
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "onServicesDiscovered: in " + status);
            switch (status) {
                case BluetoothGatt.GATT_SUCCESS:
                    List<BluetoothGattService> serviceList = gatt.getServices();
                    for (BluetoothGattService btGattService : serviceList) {
                        // 遍历服务中包含的所有Characteristic
                        // 直接筛选目标服务
                        UUID uuid = btGattService.getUuid();
                        Log.d(TAG, "onServicesDiscovered: service:" + btGattService.getUuid().toString());
                        deviceInfo.uuids.add("Service: " +btGattService.getUuid().toString());
                        for (BluetoothGattCharacteristic c: btGattService.getCharacteristics()) {
                            Log.d(TAG, "onServicesDiscovered: " + c.getUuid().toString());
                            deviceInfo.uuids.add("character: "+ c.getUuid().toString());
                        }
                        Log.d(TAG, "onServicesDiscovered: chracter:" + btGattService.getCharacteristics().toString());
                        if(btGattService.getUuid().toString().equals(Constants.UUID_UART_SERVICE)) {
                            //
                            targetServiceUUID = btGattService.getUuid().toString();
                            List<BluetoothGattCharacteristic> charics = btGattService.getCharacteristics();
                            // 对RX设置监听
                            for (BluetoothGattCharacteristic c:charics) {
                                if (c.getUuid().toString().equals(Constants.UUID_RX_CHARACTERISTIC)) {
                                    mBluetoothGatt.setCharacteristicNotification(c, true);
                                    // UI显示监听是否设置成功
//                                    mViewController.updateConnState("found RX LISTEN CHARACTER");
                                    Log.d(TAG, "onServicesDiscovered: found RX LISTEN CHARACTER " + c.getUuid().toString());
                                    Intent intent = new Intent(Constants.UPDATE_UUID);
                                    intent.putExtra("DATA","found RX LISTEN CHARACTER");
                                    mContext.sendBroadcast(intent);
                                    break;
                                } else if (c.getUuid().toString().equals(Constants.UUID_TX_CHARACTERISTIC)) {
                                    mTXCharacter = c;
                                    Log.d(TAG, "onServicesDiscovered: found TX LISTEN CHARACTER " + c.getUuid().toString());
                                } else {
                                    Log.d(TAG, "onServicesDiscovered: not found RX LISTEN CHARACTER");

                                }

                            }
                        } else {
                            // 显示未找到目标设备
//                            mViewController.updateConnState("not found expected service");
//                            Intent intent = new Intent(Constants.UPDATE_CONN_STATE);
//                            intent.putExtra("DATA","not found expected service");
//                            mContext.sendBroadcast(intent);
                            Log.d(TAG, "onServicesDiscovered: not found expected service");
                        }
                    }
                    Intent intent = new Intent(Constants.UPDATE_CONN_STATE);
                    intent.putExtra("DATA","UUID INFO COLECT FINISH");
                    mContext.sendBroadcast(intent);
//                    Toast.makeText(mContext,"finish",Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            Log.d(TAG, "onConnectionStateChange: " + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();
                Log.d(TAG, deviceInfo.name + "连接成功");

            } else {
                Intent intent = new Intent(Constants.UPDATE_CONN_STATE);
                intent.putExtra("DATA","CLOSE CONNECTION");
                mContext.sendBroadcast(intent);
//                mViewController.updateConnState("CLOSE CONNECTION");
                Log.d(TAG, deviceInfo.name + "连接断开");
                gatt.close();

            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d(TAG,"receive data: "+ characteristic.getStringValue(0));
            // 更新UI
            mViewController.updateRecvMsg(characteristic.getStringValue(0));
        }

    };


    @Override
    public void connect(String s) {
        // 连接设备
        // TODO:根据设备是否连接进行区分
        Log.d(TAG, "connect: in connecting" + s);
        // 筛选设备
        String addr = "";
        boolean flag = false;
        for (BluetoothDevice d:tvDevices) {
            if(d.getName() == null) continue;
            if (d.getName().equals(s)) {
                deviceInfo.macAddress = d.getAddress();
                deviceInfo.name = d.getName();
//                String[] a;
//                for (ParcelUuid uuid : d.getUuids()) {
//                    deviceInfo.uuids.add(uuid.toString());
//                }
                Log.d(TAG, "connect: found target device:" + d.getName());
                flag = true;
                break;
            }
        }

        if (!flag) {
            Log.d(TAG, "connect: no such device name");
            mViewController.updateConnState("no such device name");
            return;
        }
        mViewController.updateListView(deviceInfo.name);
        Log.d(TAG, "connect: " + deviceInfo.name);
        BluetoothDevice btDevice = mBtAdapter.getRemoteDevice(deviceInfo.macAddress);

        mBluetoothGatt = btDevice.connectGatt(mContext, false, mGattCallback);
        mViewController.updateMac(deviceInfo.macAddress);
        mViewController.updateName(deviceInfo.name);
        Log.d(TAG, "connect: finish connect");
    }

    @Override
    public void sendMessage(String s) {
        Log.d(TAG, "sendMessage: " + s);
        mTXCharacter.setValue(s);
        mBluetoothGatt.writeCharacteristic(mTXCharacter);
    }

    @Override
    public void registerViewController(IBleViewController viewController) {
        mViewController = viewController;
    }

    @Override
    public void unregisterViewController() {
        mViewController = null;
    }


    @Override
    public void close() {

    }


    @Override
    public BleDeviceInfo getDeviceInfo() {
        return deviceInfo;
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mContext.unregisterReceiver(receiver);
    }
}
