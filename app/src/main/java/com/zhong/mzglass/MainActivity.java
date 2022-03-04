package com.zhong.mzglass;

import static com.zhong.mzglass.utils.Constants.*;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.zhong.mzglass.base.BaseActivity;
import com.zhong.mzglass.ui.FgVpgAdapter;
import com.zhong.mzglass.ui.FragmentHome;
import com.zhong.mzglass.ui.FragmentServices;
import com.zhong.mzglass.ui.FragmentSettings;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.BlockingDeque;

import com.zhong.mzglass.utils.Constants;

public class MainActivity extends BaseActivity {

    private FgVpgAdapter fgVpgAdapter;
    private FragmentHome fragmentHome;
    private FragmentServices fragmentServices;
    private FragmentSettings fragmentSettings;
    private ViewPager vpg;
    private ArrayList<Fragment> fgList;
    private RadioGroup rg;
    private RadioButton rb_settings;
    private RadioButton rb_home;
    private RadioButton rb_services;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private HttpURLConnection httpURLConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBind();
        initView();
        Log.d("Weather", "run: out");
//
//        new Thread(new Runnable() {
//            private InputStream inputStream;
//
//            @Override
//            public void run() {
//                try {
////                    URL url = new URL("https://restapi.amap.com/v3/weather/weatherInfo?city=110101&key=" +
////                            "5b54ba64d699a8f8f2b9e728073582e4");
//                    URL url = new URL("https://ww2.sinaimg.cn/large/7a8aed7bgw1evshgr5z3oj20hs0qo0vq.jpg");
//                    if(url != null){
//                        try {
//                            httpURLConnection = (HttpURLConnection) url.openConnection();
//                            //设置超时时间
//                            httpURLConnection.setConnectTimeout(3000);
//                            //设置请求方式
//                            httpURLConnection.setRequestMethod("GET");
//                            Log.d("Weather", "run: out");
//                            int responsecode = httpURLConnection.getResponseCode();
//                            Log.d("Weather", "run: out");
//                            if(responsecode == HttpURLConnection.HTTP_OK){
//                                inputStream = httpURLConnection.getInputStream();
//                            }
//                            Log.d("Weather", "run: ok");
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }
//                } catch (MalformedURLException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }


    private void initBind() {
        vpg = (ViewPager) findViewById(R.id.vpager);
        rg = (RadioGroup) findViewById(R.id.rg_tab_bar);
        rb_home = (RadioButton) findViewById(R.id.rb_home);
        rb_services = (RadioButton) findViewById(R.id.rb_services);
        rb_settings = (RadioButton) findViewById(R.id.rb_settings);
    }

    @SuppressLint("ResourceType")
    private void initView() {
        fragmentHome = new FragmentHome();
        fragmentServices = new FragmentServices();
        fragmentSettings = new FragmentSettings();

        fgList = new ArrayList<Fragment>();

        fgList.add(fragmentHome);
        fgList.add(fragmentServices);
        fgList.add(fragmentSettings);

        fgVpgAdapter = new FgVpgAdapter(getSupportFragmentManager(),0,fgList);
        vpg.setAdapter(fgVpgAdapter);
        vpg.setOffscreenPageLimit(2);
        vpg.setCurrentItem(0);
        rb_home.setChecked(true);


        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                // here the “i” denotes the index in layout.
                switch (i) {
                    case R.id.rb_home:
                        vpg.setCurrentItem(0);
                        break;
                    case R.id.rb_services:
                        vpg.setCurrentItem(1);
                        break; 
                    case R.id.rb_settings:
                        vpg.setCurrentItem(2);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + i);
                
                }
            }
        });

        vpg.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 2) {
                    switch (vpg.getCurrentItem()) {
                        case PAGE_HOME:
                            rb_home.setChecked(true);
                            break;
                        case PAGE_SERVICE:
                            rb_services.setChecked(true);
                            break;
                        case PAGE_SETTINGS:
                            rb_settings.setChecked(true);
                            break;
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1 && resultCode == 1) {
            vpg.setCurrentItem(1);
            Log.d("BACK", "onActivityResult: ok");
//        }
    }

}