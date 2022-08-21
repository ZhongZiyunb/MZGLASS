package com.zhong.mzglass.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.zhong.mzglass.R;
import com.zhong.mzglass.base.BaseFragment;
import com.zhong.mzglass.bluetooth.gatt.BleGattActivity;
import com.zhong.mzglass.navigation.NavigateActivity;
import com.zhong.mzglass.navigation.NavigateService;
import com.zhong.mzglass.utils.IconObject;
import com.zhong.mzglass.weather.WeatherActivity;

import java.util.ArrayList;

public class FragmentServices extends BaseFragment {

    private static final String TAG = "FragmentServices";
    private View sevicesView;
    private GridView gv;
    private FgGVAdapter fgGVAdapter;
    private ArrayList<IconObject> iconList;
    private FragmentManager manager;
    private ArrayList<Boolean> serviceState;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        sevicesView = inflater.inflate(R.layout.fragment_services,null);

        // TODO: 注意这边写死了初始化的功能个数，后期如果有改动需要另外设计为变量
        serviceState = new ArrayList<Boolean>();
        serviceState.add(false);
        serviceState.add(false);
        serviceState.add(false);
        serviceState.add(false);

        initBind();
        initView();
        return sevicesView;
    }

    private void initBind() {
        manager = getFragmentManager();
        gv = (GridView) sevicesView.findViewById(R.id.gv_services);

    }

    private void initView() {
        iconList = new ArrayList<IconObject>();
        iconList.add(new IconObject("no","weather"));
        iconList.add(new IconObject("no","navigation"));
        iconList.add(new IconObject("no","gatt"));
        iconList.add(new IconObject("no","unknown"));

        fgGVAdapter = new FgGVAdapter(iconList,getActivity());
        gv.setAdapter(fgGVAdapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick: " + serviceState.get(0) + " " + serviceState.get(1));
                switch (i) {
                    case 0:
                        if (serviceState.get(0)) {
                            Log.d(TAG, "onItemClick: OK");
                            Intent intent = new Intent(getActivity(), WeatherActivity.class);
                            startActivityForResult(intent,1);
                        } else {
                            Log.d(TAG, "onItemClick: NO");
                        }
                        break;
                    case 1:
                        if (serviceState.get(1)) {
                            Log.d(TAG, "onItemClick: OK");
                            Intent intent = new Intent(getActivity(), NavigateActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d(TAG, "onItemClick: NO");
                        }
                        break;
                    case 2:
                        if (serviceState.get(2)) {
                            Log.d(TAG, "onItemClick: OK");
                            Intent intent = new Intent(getActivity(), BleGattActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d(TAG, "onItemClick: NO");
                        }
                        break;
                    case 3:
                        Toast.makeText(getActivity(), "SERVICE EVENT DONT HAVE UI RIGHTNOW", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getActivity(), "NO SERVICE RIGHTNOW", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    // 用于外部通信 设置哪些功能开启了
    public void setServiceState(boolean b, int c) {
        serviceState.set(c, b);
        Log.d(TAG, "setServiceState: 调用" + b + " " + c);
    }



}
