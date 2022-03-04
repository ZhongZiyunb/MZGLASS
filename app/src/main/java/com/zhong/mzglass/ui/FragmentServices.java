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
    private boolean serviceState = false;

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
        iconList.add(new IconObject("no","unknown"));
        iconList.add(new IconObject("no","unknown"));

        fgGVAdapter = new FgGVAdapter(iconList,getActivity());
        gv.setAdapter(fgGVAdapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        if (serviceState) {
                            Log.d(TAG, "onItemClick: OK");
                            Intent intent = new Intent(getActivity(), WeatherActivity.class);
                            startActivityForResult(intent,1);
                        } else {
                            Log.d(TAG, "onItemClick: NO");
                        }
                        break;
                    case 1:
                    case 2:
                    case 3:
                        Toast.makeText(getActivity(), "NO SERVICE RIGHTNOW", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    // 用于外部通信
    public void setServiceState(boolean b) {
        serviceState = b;
    }



}
