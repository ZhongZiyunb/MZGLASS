package com.zhong.mzglass.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.zhong.mzglass.weather.WeatherService;

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

        return settingsView;
    }


    private void initBind() {
        manager = getFragmentManager();
        wifiConnectBtn = (Button) settingsView.findViewById(R.id.wifi_connect_btn);
        ipSettingInfoBtn = (Button) settingsView.findViewById(R.id.ip_setting_info_btn);
        weatherServiceCheckBox = (CheckBox) settingsView.findViewById(R.id.weahter_service_cb);
        navigationServiceCheckBox = (CheckBox) settingsView.findViewById(R.id.navigation_service_cb);
    }
    private void initView() {
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
                loginPort = (EditText) v.findViewById(R.id.wifi_connect_ip_et);

                btn = (Button) v.findViewById(R.id.wifi_connect_confirm_btn);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO:调用Service启动服务 明天写SERVICE 今天先写接口
                        IP = loginIP.getText().toString();
                        PORT = loginPort.getText().toString();
                        STATE = "TODO";
                        Toast.makeText(getActivity(), "self button", Toast.LENGTH_SHORT).show();
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
                    fragmentServices.setServiceState(true);
                    Toast.makeText(getActivity(), "CHECK BOX START WEATHER SERVICE ", Toast.LENGTH_SHORT).show();

                    getActivity().startService(intent);

                } else {

                    getActivity().stopService(intent);
                    fragmentServices.setServiceState(false);
                    Toast.makeText(getActivity(), "CHECK BOX STOP WEATHER SERVICE ", Toast.LENGTH_SHORT).show();

                }

            }
        });

        navigationServiceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //TODO:导航功能待完成
            }
        });

    }



}
