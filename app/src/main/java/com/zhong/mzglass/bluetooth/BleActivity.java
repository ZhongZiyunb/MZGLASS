package com.zhong.mzglass.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zhong.mzglass.R;
import com.zhong.mzglass.utils.BleDeviceInfo;
import com.zhong.mzglass.utils.BleObject;
import com.zhong.mzglass.weather.WeatherService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BleActivity extends AppCompatActivity {
    private static final String TAG = "BleActivity";
    private Button detect_connected_device_btn;
    private Button ble_go_back;

    private IBleController mIBleController;
    private TextView ble_mac;
    private TextView ble_name;
    private TextView ble_connection_state;
    private TextView ble_service_uuid;
    private Button ble_send_message_btn;
    private EditText ble_send_message;
    private TextView ble_recv_message;
    private EditText ble_target_device_name;
    private ArrayAdapter<String> mAdapter;
    private String[] data = { "unset "};


    // UI控制者实现
    private IBleViewController mIViewController = new IBleViewController() {

        @Override
        public void updateMac(String s) {
            ble_mac.setText(s);
        }

        @Override
        public void updateName(String s) {
            ble_name.setText(s);
        }

        @Override
        public void updateUUIDService(String s) {

            ble_service_uuid.setText(s);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void updateConnState(String s) {
            // 更新连接状态
            ble_connection_state.setText(s);
        }

        @Override
        public void updateRecvMsg(String s) {
            // 显示收到的消息
            ble_recv_message.setText(s);
        }

        @Override
        public void updateListView(String s) {
            data[0] = s;
            mAdapter.notifyDataSetChanged();
        }


    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        initBind();

        initView();
    }


    private void initBind() {
        Intent intent = new Intent(this, BleService.class);

        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected: service on");
            mIBleController = (IBleController) iBinder;

            mIBleController.registerViewController(mIViewController);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mIViewController = null;
        }
    };

    private void initView() {
        detect_connected_device_btn = (Button) findViewById(R.id.ble_detect_device);
        ble_go_back = (Button) findViewById(R.id.ble_go_back_home);
        ble_target_device_name = (EditText) findViewById(R.id.ble_target_device_name);
        ble_mac = (TextView) findViewById(R.id.ble_mac);
        ble_name = (TextView) findViewById(R.id.ble_name);
        ble_service_uuid = (TextView) findViewById(R.id.ble_service_UUID);
        ble_connection_state = (TextView) findViewById(R.id.ble_connect_state);
        ble_send_message_btn = (Button) findViewById(R.id.ble_send_btn);
        ble_send_message = (EditText) findViewById(R.id.ble_send_message);
        ble_recv_message = (TextView) findViewById(R.id.ble_receive_msg);


        mAdapter = new ArrayAdapter<String>(
                BleActivity.this, android.R.layout.simple_list_item_1, data);
        ListView listView = (ListView) findViewById(R.id.ble_list_view);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(),BleDevice.class);
                        intent.putExtra("deviceInfo", mIBleController.getDeviceInfo());
                        startActivity(intent);
                        Log.d(TAG, "onItemClick: click happen");
                        break;
                    default:break;
                }
            }
        });

        detect_connected_device_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mIBleController != null) {
                    Log.d(TAG, "onClick: in click");

                    mIBleController.scanDevice();
                    mIBleController.connect(ble_target_device_name.getText().toString());

                }
            }
        });

        ble_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ble_send_message_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = ble_send_message.getText().toString();
                if (!s.equals("")) {
                    mIBleController.sendMessage(s);
                } else {
                    mIBleController.sendMessage("");
                }
            }
        });

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

    public ArrayList<BluetoothDevice> getConnectedDevicesV2(Context context){

        ArrayList<BluetoothDevice> result = new ArrayList<>();
        Set<BluetoothDevice> deviceSet = new HashSet<>();

        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        //获取BLE的设备, profile只能是GATT或者GATT_SERVER
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
                result.add(dev);
                connect = "设备已连接";
            }
            Log.d(TAG, connect+", address = "+dev.getAddress() + "("+ Type + "), name --> "+dev.getName());
        }
        return result;
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mConnection != null && mIBleController != null) {
            mIBleController.unregisterViewController();
            unbindService(mConnection);
        }

        finish();
    }
}
