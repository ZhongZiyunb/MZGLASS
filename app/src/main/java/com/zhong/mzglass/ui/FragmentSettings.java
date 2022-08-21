package com.zhong.mzglass.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.zhong.mzglass.R;
import com.zhong.mzglass.base.BaseFragment;
import com.zhong.mzglass.bluetooth.gatt.BleGattService;
import com.zhong.mzglass.event.EventService;
import com.zhong.mzglass.navigation.NavigateService;
import com.zhong.mzglass.socket.ISocketController;
import com.zhong.mzglass.socket.SocketService;
import com.zhong.mzglass.utils.Constants;
import com.zhong.mzglass.weather.WeatherService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public class FragmentSettings extends BaseFragment {

    private View settingsView;
    private Button wifiConnectBtn;
    private EditText loginIP;
    private final String TAG = "FragmentSettings";
    private Button btn;
    private Button ipSettingInfoBtn;

    private String STATE;
    private String IP;
    private String PORT;
    private EditText loginPort;
    private TextView ipInfo;
    private TextView portInfo;
    private TextView stateInfo;
    private CheckBox weatherServiceCheckBox;
    private CheckBox navigationServiceCheckBox;
    private FragmentManager manager;
    private Button wifiCloseBtn;
    private Intent socketIntent;
    private TextView debugText;
    private CheckBox gattServiceCheckBox;
    private CheckBox eventServiceCheckBox;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        settingsView = inflater.inflate(R.layout.fragment_settings,null);

        initBind();
        initView();
        initService();
        return settingsView;
    }

    private void initService() {
        Intent intent = new Intent(getActivity(), SocketService.class);
        Objects.requireNonNull(getActivity()).bindService(intent, mConn, Context.BIND_AUTO_CREATE);
    }

    private ISocketController isController;
    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            isController = (ISocketController) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private void initBind() {
        manager = getFragmentManager();
//        wifiConnectBtn = (Button) settingsView.findViewById(R.id.wifi_connect_btn);
//        ipSettingInfoBtn = (Button) settingsView.findViewById(R.id.ip_setting_info_btn);
//        wifiCloseBtn = (Button) settingsView.findViewById(R.id.wifi_close_btn);
        weatherServiceCheckBox = (CheckBox) settingsView.findViewById(R.id.weahter_service_cb);
        navigationServiceCheckBox = (CheckBox) settingsView.findViewById(R.id.navigation_service_cb);
        gattServiceCheckBox = (CheckBox) settingsView.findViewById(R.id.gatt_service_cb);
        eventServiceCheckBox = (CheckBox) settingsView.findViewById(R.id.event_service_cb);
        debugText = (TextView) settingsView.findViewById(R.id.debug_text);
    }
    private void initView() {

        /**
        // 用于开启和关闭socket通信
        socketIntent = new Intent(getActivity(), SocketService.class);
//        getActivity()
//                .startService(socketIntent);
        wifiConnectBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CutPasteId")
            @Override
            public void onClick(View view) {
                // 这边准备开启后台的wifi连接服务
                // 考虑把STATE做成一个全局的变量
                //
                Toast.makeText(getActivity(), "connecting...", Toast.LENGTH_SHORT).show();
                View v = View.inflate(getActivity(),R.layout.alertdialog_connection,null);

                AlertDialog alertLogin = new AlertDialog.Builder(getActivity()).setView(v).create();

                loginIP = (EditText) v.findViewById(R.id.wifi_connect_ip_et);
                loginPort = (EditText) v.findViewById(R.id.wifi_connect_port_et);

                btn = (Button) v.findViewById(R.id.wifi_connect_confirm_btn);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO:调用Service启动服务 明天写SERVICE 今天先写接口
                        IP = loginIP.getText().toString();
                        PORT = loginPort.getText().toString();
                        STATE = "TODO";
                        Toast.makeText(getActivity(), "self button", Toast.LENGTH_SHORT).show();
                        // TODO:这边需要做一个输入格式的校验——>
                        // 开启 socket 通信

//                        Objects.requireNonNull(getActivity()).startService(socketIntent);
                        isController.socketRun(IP, PORT);
                        Log.d(TAG, "onClick: start socket service!");
                        alertLogin.cancel();
                    }
                });
                alertLogin.show();
            }
        });

        ipSettingInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = View.inflate(getActivity(),R.layout.alertdialog_ipinfo,null);
                AlertDialog alertLogin = new AlertDialog.Builder(getActivity())
                        .setView(v)
                        .setTitle("IP SETTING INFO")
                        .create();

                ipInfo = (TextView) v.findViewById(R.id.ip_info);
                portInfo = (TextView) v.findViewById(R.id.port_info);
                stateInfo = (TextView) v.findViewById(R.id.state_info);

                ipInfo.setText(IP);
                portInfo.setText(PORT);
                stateInfo.setText(STATE);
                alertLogin.show();
            }
        });

        wifiCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: close socket service");
                Objects.requireNonNull(getActivity()).stopService(socketIntent);
            }
        });
        */

        // 天气服务
        weatherServiceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                ViewPager vpg = (ViewPager) getActivity().findViewById(R.id.vpager);
                FragmentPagerAdapter fpgAdapter = (FragmentPagerAdapter) vpg.getAdapter();
                assert fpgAdapter != null;
                FragmentServices fragmentServices = (FragmentServices) fpgAdapter.instantiateItem(vpg,1);
                Intent intent = new Intent(getActivity(), WeatherService.class);

                if (weatherServiceCheckBox.isChecked()) {
                    Log.d(TAG, "onCheckedChanged: IS CHECKED");
                    fragmentServices.setServiceState(true, Constants.SERVICE_WEATHER);
                    Toast.makeText(getActivity(), "CHECK BOX START WEATHER SERVICE ", Toast.LENGTH_SHORT).show();
                    getActivity().startService(intent);

                } else {

                    getActivity().stopService(intent);
                    fragmentServices.setServiceState(false, Constants.SERVICE_WEATHER);
                    Toast.makeText(getActivity(), "CHECK BOX STOP WEATHER SERVICE ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 导航服务
        navigationServiceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // TODO:导航功能待完成
                ViewPager vpg = (ViewPager) getActivity().findViewById(R.id.vpager);
                FragmentPagerAdapter fpgAdapter = (FragmentPagerAdapter) vpg.getAdapter();
                assert fpgAdapter != null;
                // TODO: 下面这一句有点忘了，注意一下是否有问题
                FragmentServices fragmentServices = (FragmentServices) fpgAdapter.instantiateItem(vpg,1);
                Intent intent = new Intent(getActivity(), NavigateService.class);

                // 开启导航服务
                if (navigationServiceCheckBox.isChecked()) {
                    Log.d(TAG, "onCheckedChanged: IS CHECKED");
                    fragmentServices.setServiceState(true, Constants.SERVICE_NAVIGATE);
                    Toast.makeText(getActivity(), "CHECK BOX START NAVIGATE SERVICE ", Toast.LENGTH_SHORT).show();
                    getActivity().startService(intent);

                } else {

                    getActivity().stopService(intent);
                    fragmentServices.setServiceState(false, Constants.SERVICE_NAVIGATE);
                    Toast.makeText(getActivity(), "CHECK BOX STOP NAVIGATE SERVICE ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 蓝牙服务
        gattServiceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // TODO:导航功能待完成
                ViewPager vpg = (ViewPager) getActivity().findViewById(R.id.vpager);
                FragmentPagerAdapter fpgAdapter = (FragmentPagerAdapter) vpg.getAdapter();
                assert fpgAdapter != null;
                // TODO: 下面这一句有点忘了，注意一下是否有问题
                FragmentServices fragmentServices = (FragmentServices) fpgAdapter.instantiateItem(vpg,1);
                Intent intent = new Intent(getActivity(), BleGattService.class);

                // 开启导航服务
                if (gattServiceCheckBox.isChecked()) {
                    Log.d(TAG, "onCheckedChanged: IS CHECKED");
                    fragmentServices.setServiceState(true, Constants.SERVICE_GATT);
                    Toast.makeText(getActivity(), "CHECK BOX START GATT SERVICE ", Toast.LENGTH_SHORT).show();
                    getActivity().startService(intent);

                } else {

                    getActivity().stopService(intent);
                    fragmentServices.setServiceState(false, Constants.SERVICE_GATT);
                    Toast.makeText(getActivity(), "CHECK BOX STOP GATT SERVICE ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 事件提醒服务
        eventServiceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // TODO:导航功能待完成
                ViewPager vpg = (ViewPager) getActivity().findViewById(R.id.vpager);
                FragmentPagerAdapter fpgAdapter = (FragmentPagerAdapter) vpg.getAdapter();
                assert fpgAdapter != null;
                // TODO: 下面这一句有点忘了，注意一下是否有问题
                FragmentServices fragmentServices = (FragmentServices) fpgAdapter.instantiateItem(vpg,1);
                Intent intent = new Intent(getActivity(), EventService.class);

                // 开启导航服务
                if (eventServiceCheckBox.isChecked()) {
                    Log.d(TAG, "onCheckedChanged: IS CHECKED");
                    fragmentServices.setServiceState(true, Constants.SERVICE_EVENT);
                    Toast.makeText(getActivity(), "CHECK BOX START EVENT SERVICE ", Toast.LENGTH_SHORT).show();
                    getActivity().startService(intent);

                } else {

                    getActivity().stopService(intent);
                    fragmentServices.setServiceState(false, Constants.SERVICE_EVENT);
                    Toast.makeText(getActivity(), "CHECK BOX STOP EVENT SERVICE ", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        if (isController !=null && mConn != null) {
            Objects.requireNonNull(getActivity()).unbindService(mConn);
        }
        super.onDestroy();
    }
}
