package com.zhong.mzglass.navigation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.MaskFilter;
import android.os.Binder;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapException;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.enums.TravelStrategy;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviToViaInfo;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.model.NaviPoi;
import com.amap.api.services.core.LatLonPoint;
import com.zhong.mzglass.bluetooth.gatt.IBleGattController;
import com.zhong.mzglass.socket.ISocketController;
import com.zhong.mzglass.utils.Constants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class NavigatePresenter extends BaseNaviPresenter implements INavigateController {

    private static final String TAG = "NavigatePresenter";
    IBleGattController mGatt = null; // 蓝牙服务接口
    INavigateViewController mNaviView = null; // UI界面控制接口

    private Context mContext;
    private AMapNavi mAMapNavi;

    private double pixMapScaleFactor = -1;
    private double startEndAngle = 0;
    private double maxEastPixNum = 250;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.START_WALK_NAVI.equals(action)) {
                try {
                    navi();
                } catch (AMapException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    NavigatePresenter(Context thisContext) {
        mContext = thisContext;

    }

    public void init() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.START_WALK_NAVI);
        mContext.registerReceiver(receiver,intentFilter);
    }

    // TODO: 子线程和主线程之间通信
    // TODO: 导航事件更新
    // TODO: navigate传入出发地和目的地
    // TODO: 简易版的导航就行了 不搞太复杂
    // TODO: 尽量这周搞定吧

    private boolean first_in = false;
    private LatLonPoint end_;
    private void navi() throws AMapException {
        if (first_in) {
            mAMapNavi = AMapNavi.getInstance(mContext);
            NaviLatLng startNaviPoi = new NaviLatLng(mylatlng.latitude, mylatlng.longitude);
            NaviLatLng endNaviPoi = new NaviLatLng(end_.getLatitude(), end_.getLongitude());

            LatLng startPoi = new LatLng(mylatlng.latitude, mylatlng.longitude);
            LatLng endPoi = new LatLng(end_.getLatitude(), end_.getLongitude());
            mAMapNavi.setUseInnerVoice(true, true);
            mAMapNavi.calculateWalkRoute(startNaviPoi, endNaviPoi);
            mAMapNavi.addAMapNaviListener(this);
        } else {
            Toast.makeText(mContext,"locating now",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void navigate(LatLonPoint end) throws AMapException {

        end_ = end;
        flag = true;
        // 指定UI发生改变
        // 利用SDK拿到导航信息
        if (end_ == null) {
            Toast.makeText(mContext, "请输入终点再进行导航", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }

        try {
            locationInit();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        if (first_in) {
//            mAMapNavi = AMapNavi.getInstance(mContext);
//            NaviLatLng startNaviPoi = new NaviLatLng(mylatlng.latitude, mylatlng.longitude);
//            NaviLatLng endNaviPoi = new NaviLatLng(end.getLatitude(), end.getLongitude());
//
//            LatLng startPoi = new LatLng(mylatlng.latitude, mylatlng.longitude);
//            LatLng endPoi = new LatLng(end.getLatitude(), end.getLongitude());
//            mAMapNavi.calculateWalkRoute(startNaviPoi, endNaviPoi);
//            mAMapNavi.addAMapNaviListener(this);
//        } else {
//            Toast.makeText(mContext,"locating now",Toast.LENGTH_SHORT).show();
//        }

    }

    @Override
    public void navigate(LatLonPoint start, LatLonPoint end) throws AMapException {
        flag = true;
        if (start == null || end == null) {
            Toast.makeText(mContext, "请正确输入目的地", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }

        // FOR DEBUG
        mylatlng = new LatLng(start.getLatitude(),start.getLongitude());


        mAMapNavi = AMapNavi.getInstance(mContext);
        NaviLatLng startNaviPoi = new NaviLatLng(start.getLatitude(), start.getLongitude());
        NaviLatLng endNaviPoi = new NaviLatLng(end.getLatitude(), end.getLongitude());


        LatLng startPoi = new LatLng(start.getLatitude(), start.getLongitude());
        LatLng endPoi = new LatLng(end.getLatitude(), end.getLongitude());

        mAMapNavi.calculateWalkRoute(startNaviPoi, endNaviPoi);
        mAMapNavi.addAMapNaviListener(this);
    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
        super.onCalculateRouteSuccess(aMapCalcRouteResult);

        Log.d(TAG, "onCalculateRouteSuccess: 开始导航");
        mAMapNavi.startNavi(NaviType.GPS);

        if (mGatt != null) {
            Log.d(TAG, "onCalculateRouteSuccess: "+"导航开始辣!");
            mGatt.sendMessage("导航开始辣！", Constants.NOTICE);
        }
    }


    private AMapLocationClient mLocationClient;
    //声明mLocationOption对象
    private AMapLocationClientOption mLocationOption;
    private LatLng mylatlng = new LatLng(39.942295, 116.335891);//故宫博物院

    //设置定位监听
    private void locationInit() throws Exception {

        mLocationClient = new AMapLocationClient(mContext);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                mylatlng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                Log.d(TAG, "onLocationChanged: "+mylatlng);
                first_in = true;
                Intent intent = new Intent(Constants.START_WALK_NAVI);
                mContext.sendBroadcast(intent);
            }
        });

        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
//        mLocationOption.setInterval(2000);
        mLocationOption.setOnceLocation(true);
        //设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        // 启动定位
        mLocationClient.startLocation();


    }

    // 用于绑定相关的控制器
    @Override
    public void registerViewController(INavigateViewController mNavigateView) {
        mNaviView = mNavigateView;
    }

    @Override
    public void unregisterViewController(INavigateViewController mNavigateView) {
        if (mNaviView != null) {
            mNaviView = null;
        }
    }

    @Override
    public void registerBleService(IBleGattController mBle) {
        mGatt = mBle;
    }

    @Override
    public void unregisterBleService(IBleGattController mBle) {
        if (mGatt != null) {
            mGatt = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mAMapNavi.stopNavi();
        }
        if (mContext != null) {
            mContext.unregisterReceiver(receiver);
        }
    }

    // 发送导航信息
    @Override
    public void onGetNavigationText(String s) {
        if (mGatt != null) {
//            Log.d(TAG, "onGetNavigationText: ");
//            Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
            // 发送导航转向信息
            Log.d(TAG, "onGetNavigationText: "+ s);
            mGatt.sendMessage(s, Constants.NAVI_TEXT);
        } else {
            Toast.makeText(mContext, "蓝牙服务尚未连接", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onGetNavigationText: 蓝牙服务尚未连接");
        }
    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

        String caliMsg = String.valueOf(aMapNaviLocation.getBearing());
        Log.d(TAG, "onLocationChange: " + Constants.NAVI_CALI + caliMsg);
        mGatt.sendMessage(caliMsg,Constants.NAVI_CALI);

    }

    boolean flag = true;
    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {
        // 发送导航时间信息
        // 发送导航距离信息
        String msg = String.valueOf(naviInfo.getPathRetainTime()) + " " +
                String.valueOf(naviInfo.getPathRetainDistance()) + " " +
                String.valueOf(naviInfo.getCurStepRetainTime()) + " " +
                String.valueOf(naviInfo.getCurStepRetainDistance());

        AMapNaviToViaInfo[] n = naviInfo.getToViaInfo();
        Log.d(TAG, "onNaviInfoUpdate: " + naviInfo.getCurStepRetainDistance());
        Log.d(TAG, "onNaviInfoUpdate: " + naviInfo.getCurStepRetainTime());
        Log.d(TAG, "onNaviInfoUpdate: " + naviInfo.getPathRetainDistance());
        Log.d(TAG, "onNaviInfoUpdate: " + naviInfo.getPathRetainTime());
        Log.d(TAG, "onNaviInfoUpdate: " + msg);

        mGatt.sendMessage(msg, Constants.NAVI_TIME_DIST);
    }

    @Override
    public void onStartNavi(int i) {
        Toast.makeText(mContext, "开始导航", Toast.LENGTH_SHORT).show();
    }

}


