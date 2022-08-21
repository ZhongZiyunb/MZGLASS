package com.zhong.mzglass.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.enums.TravelStrategy;
import com.amap.api.navi.model.AMapCalcRouteResult;
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
import com.amap.api.navi.model.NaviPoi;
import com.zhong.mzglass.R;
import com.zhong.mzglass.TestActivity;
import com.zhong.mzglass.base.BaseFragment;
import com.zhong.mzglass.bluetooth.BleActivity;
import com.zhong.mzglass.bluetooth.demo.BleDemoActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FragmentHome extends BaseFragment implements INaviInfoCallback {

    private View homeView;
    private Button open_test;
    private Button close_test;
    private String TAG = "FragmentHome";

    /**
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            Log.d(TAG, "onLocationChanged: in");
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
//可在其中解析amapLocation获取相应内容。
                    assert aMapLocation != null;
                    Log.d(TAG, "onLocationChanged: latitude:" + aMapLocation.getLatitude() +
                            " longitude:" + aMapLocation.getLongitude() +
                            " city:" + aMapLocation.getCity() +
                            " district:" + aMapLocation.getDistrict() +
                            " street:" + aMapLocation.getStreet() +
                            " num:" + aMapLocation.getStreetNum());



                    city.setText(aMapLocation.getCity());
                    longitude.setText(String.valueOf(aMapLocation.getLongitude()));
                    latitude.setText(String.valueOf(aMapLocation.getLatitude()));
                    district.setText(aMapLocation.getDistrict());
                    street.setText(aMapLocation.getStreet());
                    streetNum.setText(aMapLocation.getStreetNum());

                    Log.d(TAG, "onLocationChanged: change happened !");

                }else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError","location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }


        }
    };

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    private AMapNavi mAMapNavi;

    private TextView city;
    private TextView longitude;
    private TextView latitude;
    private TextView district;
    private TextView street;
    private TextView streetNum;

    */
    private Button go_to_test_page;
    private Button ble_test;
//初始化AMapLocationClientOption对象

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: in");
    }

    //TODO: 定位权限弹窗 下个版本再做


    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        homeView = inflater.inflate(R.layout.fragment_home,null);
        Log.d(TAG, "onCreateView: in");
        initview();
        Log.d(TAG, "onCreateView: out");
        return homeView;
    }

    private void initview() {
        Log.d(TAG, "initview: first");
//        open_test = (Button) homeView.findViewById(R.id.home_btn1);
//        close_test = (Button) homeView.findViewById(R.id.home_btn2);
//        city = (TextView) homeView.findViewById(R.id.city);
//        longitude = (TextView) homeView.findViewById(R.id.longitude);
//        latitude = (TextView) homeView.findViewById(R.id.latitude);
//        district = (TextView) homeView.findViewById(R.id.district);
//        street = (TextView) homeView.findViewById(R.id.street);
//        streetNum = (TextView) homeView.findViewById(R.id.street_num);

        go_to_test_page = (Button) homeView.findViewById(R.id.go_to_test_page);

        ble_test = (Button) homeView.findViewById(R.id.ble_test);

//        Log.d(TAG, "initview: second");
//        open_test.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mLocationOption.setOnceLocation(true);
//                mLocationClient.setLocationOption(mLocationOption);
//                mLocationClient.startLocation();
//                Log.d(TAG, "onClick: " + "click");
//            }
//        });

//        close_test.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
//            }
//        });

        // 进入蓝牙测试
        ble_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BleActivity.class);

                startActivity(intent);
            }
        });

        // 进入到测试面板
        go_to_test_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), TestActivity.class);
//
//                startActivity(intent);
                LatLng p2 = new LatLng(39.917337, 116.397056);//故宫博物院
                LatLng p3 = new LatLng(39.904556, 116.427231);//北京站
                AmapNaviParams params = new AmapNaviParams(new Poi("北京站", p3, ""), null, new Poi("故宫博物院", p2, ""), AmapNaviType.WALK);
                params.setUseInnerVoice(true);
                AmapNaviPage.getInstance().showRouteActivity(getContext(), params, FragmentHome.this);
            }
        });


//初始化定位
/*
        Log.d(TAG, "initview: third");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 设置好要导航的路线
                    // 然后通过监听事件进行回调
                    String s = sHA1(getActivity());
                    Log.d(TAG, "run:  " + s);
                    mAMapNavi = AMapNavi.getInstance(getActivity());
                    NaviPoi start = new NaviPoi("北京首都机场", new LatLng(40.080525,116.603039), "B000A28DAE");
//途经点
                    List<Poi> poiList = new ArrayList();
                    poiList.add(new Poi("故宫", new LatLng(39.918058,116.397026), "B000A8UIN8"));
//终点
                    NaviPoi end = new NaviPoi("北京大学", new LatLng(39.941823,116.426319), "B000A816R6");
                    boolean b = mAMapNavi.calculateWalkRoute(start, end, TravelStrategy.SINGLE);



                    mAMapNavi.addAMapNaviListener(new AMapNaviListener() {
                        @Override
                        public void onInitNaviFailure() {


                        }

                        @Override
                        public void onInitNaviSuccess() {
                            Log.d(TAG, "onInitNaviSuccess: ");
                        }

                        @Override
                        public void onStartNavi(int i) {
                            Log.d(TAG, "onStartNavi: ");
                        }

                        @Override
                        public void onTrafficStatusUpdate() {

                        }

                        @Override
                        public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

                        }

                        @Override
                        public void onGetNavigationText(int i, String s) {

                        }

                        @Override
                        public void onGetNavigationText(String s) {
                            Log.d("NAVI:",s);
                        }

                        @Override
                        public void onEndEmulatorNavi() {

                        }

                        @Override
                        public void onArriveDestination() {

                        }

                        @Override
                        public void onCalculateRouteFailure(int i) {

                        }

                        @Override
                        public void onReCalculateRouteForYaw() {

                        }

                        @Override
                        public void onReCalculateRouteForTrafficJam() {

                        }

                        @Override
                        public void onArrivedWayPoint(int i) {

                        }

                        @Override
                        public void onGpsOpenStatus(boolean b) {

                        }

                        @Override
                        public void onNaviInfoUpdate(NaviInfo naviInfo) {
                            Log.d(TAG, "onNaviInfoUpdate: " + naviInfo.getCurPoint());
                            Log.d(TAG, "onNaviInfoUpdate: " + naviInfo.getCurrentRoadName());
                            Log.d(TAG, "onNaviInfoUpdate: " + naviInfo.getNextRoadName());
                            Log.d(TAG, "onNaviInfoUpdate: " + naviInfo.getPathRetainTime());
                            Log.d(TAG, "onNaviInfoUpdate: " + naviInfo.getPathRetainDistance());



                        }

                        @Override
                        public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

                        }

                        @Override
                        public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

                        }

                        @Override
                        public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

                        }

                        @Override
                        public void showCross(AMapNaviCross aMapNaviCross) {

                        }

                        @Override
                        public void hideCross() {

                        }

                        @Override
                        public void showModeCross(AMapModelCross aMapModelCross) {

                        }

                        @Override
                        public void hideModeCross() {

                        }

                        @Override
                        public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

                        }

                        @Override
                        public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

                        }

                        @Override
                        public void hideLaneInfo() {

                        }

                        @Override
                        public void onCalculateRouteSuccess(int[] ints) {
                            Log.d(TAG, "onCalculateRouteSuccess: s");
                        }

                        @Override
                        public void notifyParallelRoad(int i) {

                        }

                        @Override
                        public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

                        }

                        @Override
                        public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

                        }

                        @Override
                        public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

                        }

                        @Override
                        public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

                        }

                        @Override
                        public void onPlayRing(int i) {

                        }

                        @Override
                        public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
                            Log.d(TAG, "onCalculateRouteSuccess: real s");
                            Log.d(TAG, "onCalculateRouteSuccess: " + aMapCalcRouteResult.getCalcRouteType());
                            Log.d(TAG, "onCalculateRouteSuccess: " + aMapCalcRouteResult.getRouteid());
                            mAMapNavi.startNavi(1);

                        }

                        @Override
                        public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {

                        }

                        @Override
                        public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {

                        }

                        @Override
                        public void onGpsSignalWeak(boolean b) {

                        }
                    });


//                    AMapLocationClient.updatePrivacyShow(getActivity(),true,true);
//                    AMapLocationClient.updatePrivacyAgree(getActivity(),true);
//                    mLocationClient = new AMapLocationClient(getActivity());
//
//
//                    mLocationOption = new AMapLocationClientOption();
//                    mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
//                    mLocationOption.setLocationCacheEnable(false);
//                    mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//                    mLocationOption.setInterval(1000);
//                    mLocationOption.setNeedAddress(true);
//                    mLocationOption.setMockEnable(true);
//                    mLocationOption.setHttpTimeOut(20000);
//
//                    if(null != mLocationClient){
//                        mLocationClient.setLocationOption(mLocationOption);
//                        mLocationClient.setLocationListener(mLocationListener);
//                        //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
//                        mLocationClient.stopLocation();
//                        mLocationClient.startLocation();
//                        Log.d(TAG, "run: mLocationClient ok");
//                    } else {
//                        Log.d(TAG, "run: mLocationClient no ok");
//                    }

//                    给定位客户端对象设置定位参数
//                    mLocationClient.setLocationOption(mLocationOption);
//                    启动定位
//                    mLocationClient.startLocation();



//                    mLocationClient.stopLocation();
//                    mLocationClient.setLocationListener(mLocationListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }).start();
*/

//设置定位回调监听

    }

    public static String sHA1(Context context){
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length()-1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
//        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onGetNavigationText(String s) {
        Log.d("NAVI_text:",s);
    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onArriveDestination(boolean b) {

    }

    @Override
    public void onStartNavi(int i) {
        Log.d(TAG, "onStartNavi: navipage");
    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onStopSpeaking() {

    }

    @Override
    public void onReCalculateRoute(int i) {

    }

    @Override
    public void onExitPage(int i) {

    }

    @Override
    public void onStrategyChanged(int i) {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onMapTypeChanged(int i) {

    }

    @Override
    public void onNaviDirectionChanged(int i) {
        Log.d("NAVI:","");
    }

    @Override
    public void onDayAndNightModeChanged(int i) {

    }

    @Override
    public void onBroadcastModeChanged(int i) {

    }

    @Override
    public void onScaleAutoChanged(boolean b) {

    }

    @Override
    public View getCustomMiddleView() {
        return null;
    }

    @Override
    public View getCustomNaviView() {
        return null;
    }

    @Override
    public View getCustomNaviBottomView() {
        return null;
    }
}
