package com.zhong.mzglass;

import static com.zhong.mzglass.utils.Constants.*;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.zhong.mzglass.base.BaseActivity;
import com.zhong.mzglass.ui.FgVpgAdapter;
import com.zhong.mzglass.ui.FragmentHome;
import com.zhong.mzglass.ui.FragmentServices;
import com.zhong.mzglass.ui.FragmentSettings;

import java.util.ArrayList;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBind();
        initView();
    }


    private void initBind() {
        vpg = (ViewPager) findViewById(R.id.vpager);
        rg = (RadioGroup) findViewById(R.id.rg_tab_bar);
        rb_home = (RadioButton) findViewById(R.id.rb_home);
        rb_services = (RadioButton) findViewById(R.id.rb_services);
        rb_settings = (RadioButton) findViewById(R.id.rb_settings);
    }

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


}