package com.zhong.mzglass.navigation;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapException;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.CustomMapStyleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapCarInfo;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.model.NaviPoi;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.RouteSearch;
import com.zhong.mzglass.R;
import com.zhong.mzglass.ui.FragmentHome;
import com.zhong.mzglass.utils.Constants;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;

public class NavigateActivity extends BaseActivity implements PoiSearch.OnPoiSearchListener {

    private INavigateViewController mNavigateViewController;
    private INavigateController mNavigateController;
    private AutoCompleteTextView edtxt_start;
    private AutoCompleteTextView edtxt_destination;
    private TextView edtxt_motion;
    private Button start_navigate;
    final private String TAG = "NavigateActivity";
    private ProgressDialog progDialog = null;

    private LatLonPoint start_poi = null;
    private LatLonPoint end_poi = null;

    private String start_location = "";
    private String end_location = "";

    private boolean search_ok;
    private String keyWord= "";
    private int search_state = Constants.SEARCH_START;
    private int currentPage;
//    private PoiSearch.Query query;
    private PoiSearch poiSearch;
    private PoiResult poiResult; // poi返回的结果
    private PoiSearch.Query query_start;
    private PoiSearch.Query query_end;

    private BroadcastReceiver naviReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.START_NAVI.equals(action)) {
                if (start_location.equals("")){
                    if (end_poi != null) {
                        try {
                            mNavigateController.navigate(end_poi);
                        } catch (AMapException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (!end_location.equals("")) {
                    if (start_poi != null && end_poi != null) {
                        try {
                            mNavigateController.navigate(start_poi,end_poi);
                        } catch (AMapException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigate);

        mAMapNaviView = (AMapNaviView) findViewById(R.id.mapview);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);

        MapsInitializer.updatePrivacyAgree(this, true);
        MapsInitializer.updatePrivacyShow(this, true, true);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.START_NAVI);
        registerReceiver(naviReceiver,intentFilter);

        initBind();
        initView();
    }

    // 初始化界面
    void initView() {
        // 相关
        edtxt_start = (AutoCompleteTextView) findViewById(R.id.navigate_start);
        edtxt_destination = (AutoCompleteTextView) findViewById(R.id.navigate_destination);
        edtxt_motion = (TextView) findViewById(R.id.navigate_motion);
        start_navigate = (Button) findViewById(R.id.start_navigate);

        // 获取出发点信息
        edtxt_start.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                String newText = s.toString().trim();
                if (!newText.equals("")) {
                    InputtipsQuery inputquery = new InputtipsQuery(newText, edtxt_start.getText().toString());
                    Inputtips inputTips = new Inputtips(NavigateActivity.this, inputquery);
                    inputTips.setInputtipsListener(new Inputtips.InputtipsListener() {
                        @Override
                        public void onGetInputtips(List<Tip> tipList, int rCode) {
                            if (rCode == com.amap.api.services.core.AMapException.CODE_AMAP_SUCCESS) {// 正确返回
                                List<String> listString = new ArrayList<String>();
                                for (int i = 0; i < tipList.size(); i++) {
                                    listString.add(tipList.get(i).getName());
                                }
                                ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
                                        getApplicationContext(),
                                        R.layout.route_inputs, listString);
                                edtxt_start.setAdapter(aAdapter);
                                aAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(NavigateActivity.this,"错误码："+ rCode,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    inputTips.requestInputtipsAsyn();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(TAG, "afterTextChanged: start"+ editable.toString());
                start_location = editable.toString();
            }
        });

        edtxt_start.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                Log.d(TAG, "onEditorAction: 出发地:" + textView.toString());
                start_location = textView.toString();

                return false;
            }
        });

        // 获取目的地信息
        edtxt_destination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                String newText = s.toString().trim();
                if (!newText.equals("")) {
                    InputtipsQuery inputquery = new InputtipsQuery(newText, edtxt_destination.getText().toString());
                    Inputtips inputTips = new Inputtips(NavigateActivity.this, inputquery);
                    inputTips.setInputtipsListener(new Inputtips.InputtipsListener() {
                        @Override
                        public void onGetInputtips(List<Tip> tipList, int rCode) {
                            if (rCode == com.amap.api.services.core.AMapException.CODE_AMAP_SUCCESS) {// 正确返回
                                List<String> listString = new ArrayList<String>();
                                for (int i = 0; i < tipList.size(); i++) {
                                    listString.add(tipList.get(i).getName());
                                }
                                ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
                                        getApplicationContext(),
                                        R.layout.route_inputs, listString);
                                edtxt_destination.setAdapter(aAdapter);
                                aAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(NavigateActivity.this,"错误码："+ rCode,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    inputTips.requestInputtipsAsyn();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(TAG, "afterTextChanged: end"+ editable.toString());
                end_location = editable.toString();
            }
        });

        edtxt_destination.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                Log.d(TAG, "onEditorAction: 目的地:" + textView.toString());

                end_location = textView.toString();

                return false;
            }
        });

        start_navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mNavigateController != null) {

                    if (start_location.equals("")) {
                        end_poi = null;
                        doSearchQuery(end_location,Constants.SEARCH_END);
//                        try {
//                            mNavigateController.navigate(end_poi);
//                        } catch (AMapException e) {
//                            e.printStackTrace();
//                        }

                    } else if (!end_location.equals("")) {
                        Log.d(TAG, "onClick: in");
                        start_poi = null;
                        end_poi = null;
                        doSearchQuery(start_location,Constants.SEARCH_START);
                        Log.d(TAG, "onClick: out");
                        doSearchQuery(end_location,Constants.SEARCH_END);

//                        try {
//                            mNavigateController.navigate(start_poi,end_poi);
//                        } catch (AMapException e) {
//                            e.printStackTrace();
//                        }

                    } else {
                        Toast.makeText(NavigateActivity.this,"请输入目的地",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void showProgressDialog() {
        if (progDialog == null) {
            progDialog = new ProgressDialog(this);
        }
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    protected void doSearchQuery(String location,int mode) {
        if (location == null) {
            return;
        }

        if (mode == Constants.SEARCH_START) {

            showProgressDialog();// 显示进度框
            currentPage = 0;
            query_start = new PoiSearch.Query(location, "", keyWord);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
            query_start.setPageSize(10);// 设置每页最多返回多少条poiitem
            query_start.setPageNum(currentPage);// 设置查第一页

            try {
                poiSearch = new PoiSearch(this, query_start);
                poiSearch.setOnPoiSearchListener(this);
                poiSearch.searchPOIAsyn();
            } catch (com.amap.api.services.core.AMapException e) {
                e.printStackTrace();
            }

        } else if (mode == Constants.SEARCH_END) {

            showProgressDialog();// 显示进度框
            query_end = new PoiSearch.Query(location, "", keyWord);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
            query_end.setPageSize(10);// 设置每页最多返回多少条poiitem
            query_end.setPageNum(currentPage);// 设置查第一页

            try {
                poiSearch = new PoiSearch(this, query_end);
                poiSearch.setOnPoiSearchListener(this);
                poiSearch.searchPOIAsyn();
            } catch (com.amap.api.services.core.AMapException e) {
                e.printStackTrace();
            }

        }

    }

    void initBind() {
        Intent intent = new Intent(this, NavigateService.class);

        bindService(intent, mConn, BIND_AUTO_CREATE);
    }

    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mNavigateController = (INavigateController) iBinder;
            mNavigateController.registerViewController(mNavigateViewController);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mNavigateController.unregisterViewController(mNavigateViewController);
        }
    };


    @Override
    protected void onPause() {
        super.onPause();

        mAMapNaviView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAMapNaviView.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mConn != null) {
            unbindService(mConn);
        }
        unregisterReceiver(naviReceiver);
        mAMapNaviView.onDestroy();
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        dissmissProgressDialog();// 隐藏对话框
        if (rCode == com.amap.api.services.core.AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query_start)) {// 是否是同一条
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                    if (poiItems != null && poiItems.size() > 0) {
                        // TODO:当搜索到了目标地点 选取其中一个进行导航 为了简化实现 取第一个作为目的地
                        // TODO:集成定位功能
                        start_poi = poiItems.get(0).getLatLonPoint();
                        Intent intent = new Intent(Constants.START_NAVI);
                        sendBroadcast(intent);
                    } else {
                        Toast.makeText(this,"query start failed",Toast.LENGTH_SHORT).show();
                    }
                } else if (result.getQuery().equals(query_end)) {
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                    if (poiItems != null && poiItems.size() > 0) {
                        // TODO:当搜索到了目标地点 选取其中一个进行导航 为了简化实现 取第一个作为目的地
                        // TODO:集成定位功能
                        end_poi = poiItems.get(0).getLatLonPoint();
                        Intent intent = new Intent(Constants.START_NAVI);
                        sendBroadcast(intent);
                    } else {
                        Toast.makeText(this,"query end failed",Toast.LENGTH_SHORT).show();
                    }
                }

            } else {

            }
        } else {

        }

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }


}