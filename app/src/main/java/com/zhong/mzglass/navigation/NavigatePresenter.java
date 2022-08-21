package com.zhong.mzglass.navigation;

import android.content.Context;
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

    NavigatePresenter(Context thisContext) {
        mContext = thisContext;

    }


    // TODO: 子线程和主线程之间通信
    // TODO: 导航事件更新
    // TODO: navigate传入出发地和目的地
    // TODO: 简易版的导航就行了 不搞太复杂
    // TODO: 尽量这周搞定吧

    private boolean first_in = false;

    @Override
    public void navigate(LatLonPoint end) throws AMapException {

        flag = true;

        // 指定UI发生改变
        // 利用SDK拿到导航信息
        if (end == null) {
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
//        AmapNaviParams params = new AmapNaviParams(new Poi("您当前位置", p3, ""), null, new Poi("您当前位置", p2, ""), AmapNaviType.WALK);
//        p2 = new LatLng(39.942295, 116.335891);
//        p3 = new LatLng(39.995576, 116.481288);

//        AmapNaviParams params = new AmapNaviParams(new Poi("您当前位置", mylatlng, ""), null, new Poi("您当前位置", end, ""), AmapNaviType.WALK);
//
//        params.setUseInnerVoice(true);
//        AmapNaviPage.getInstance().showRouteActivity(mContext.getApplicationContext(), params, NavigatePresenter.this);
//        AMapNavi mAmapNavi = AMapNavi.getInstance(mContext);

        if (first_in) {
            mAMapNavi = AMapNavi.getInstance(mContext);
            NaviLatLng startNaviPoi = new NaviLatLng(mylatlng.latitude, mylatlng.longitude);
            NaviLatLng endNaviPoi = new NaviLatLng(end.getLatitude(), end.getLongitude());

            LatLng startPoi = new LatLng(mylatlng.latitude, mylatlng.longitude);
            LatLng endPoi = new LatLng(end.getLatitude(), end.getLongitude());
            startEndAngle = calculatePositionAngle(startPoi, endPoi);
            mAMapNavi.calculateWalkRoute(startNaviPoi, endNaviPoi);
            mAMapNavi.addAMapNaviListener(this);
        } else {
            Toast.makeText(mContext,"locating now",Toast.LENGTH_SHORT).show();
        }

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
        startEndAngle = calculatePositionAngle(startPoi,endPoi);

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
            //TODO:开始导航后在回调函数中进行信息发送
            // 需要的信息为：
            // ---- 初始的方位角 ->
            // ---- 实时距离 ->
            // ---- 下一步转向信息
            // ---- 用户坐标在显示屏上的地点 ->location change的时候发送
            // ---- 初始目的地在显示屏的地方
        }
    }


    private AMapLocationClient mLocationClient;
    //声明mLocationOption对象
    private AMapLocationClientOption mLocationOption;
    private LatLng mylatlng = new LatLng(39.942295, 116.335891);//故宫博物院
//    private LatLng p3;//北京站

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

    // 计算方位角
    private double calculatePositionAngle(LatLng p1, LatLng p2) {
        double angle = 0;
        Log.d(TAG, "calculatePositionAngle: in");
        double lat_res = p2.latitude - p1.latitude;
        double lon_res = p2.longitude - p1.longitude;

        // 与正北方向的夹角
        angle = Math.atan2(lat_res,lon_res);
        Log.d(TAG, "calculatePositionAngle: out");

        return angle;
    }

    // 计算距离
    private double calculatePositionDist(LatLng p1, LatLng p2) {
        double dist = 0;
        double r = 6371000;
        double lat_res = p2.latitude - p1.latitude;
        double lon_res = p2.longitude - p1.longitude;

        double a = r * lat_res;
        double b = r * lon_res;

        dist = Math.sqrt(a * a + b * b);

        return dist;

    }


    // 计算二维像素坐标
    private ArrayList<Double> calPixPos(double lon,double lat) {

        ArrayList<Double> pos = new ArrayList<Double>();
        LatLng nowlatlng = new LatLng(lat,lon);
        double angle = calculatePositionAngle(mylatlng,nowlatlng);
//        double length = calculatePositionDist(mylatlng,nowlatlng);
        double length = AMapUtils.calculateLineDistance(mylatlng,nowlatlng);

        Log.d(TAG, "calPixPos: " + angle + " " + length);

        double north = length * Math.cos(angle);
        double east = length * Math.sin(angle);
        pos.add(0,north);
        pos.add(1,east);

        return pos;
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
    }


    // 导航相关的回调函数
    @Override
    public void onInitNaviFailure() {

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
//        String A = String.valueOf(aMapNaviLocation.getBearing())
//                + " " + String.valueOf(aMapNaviLocation.getRoadBearing())
//                + " " + String.valueOf(aMapNaviLocation.getCoord().toString());
//        Log.d(TAG, "onLocationChange:" + A);

        String caliMsg = String.valueOf(aMapNaviLocation.getBearing());

        Log.d(TAG, "onLocationChange: " + Constants.NAVI_CALI + caliMsg);
        mGatt.sendMessage(caliMsg,Constants.NAVI_CALI);

        // TODO:发送角度方向标
        // TODO: 发送地图二维像素坐标
        // 先计算尺度标，然后更新目前位置相对于出发点的距离和角度
        // 所以本质上需要实现一个计算方位角的函数

        // 发送标定信息

        double now_lon = aMapNaviLocation.getCoord().getLongitude();
        double now_lat = aMapNaviLocation.getCoord().getLatitude();

        ArrayList<Double> pixpos = calPixPos(now_lon,now_lat);

        String msg = String.valueOf((pixpos.get(0)) * pixMapScaleFactor +
                " " + (pixpos.get(1)) * pixMapScaleFactor) ;
        Log.d(TAG, "onLocationChange: " + pixpos.get(0) + " " + pixpos.get(1)
                                    + " " + pixMapScaleFactor);
        Log.d(TAG, "onLocationChange: " + msg);
        // 发送方位信息
        if (pixMapScaleFactor != -1) {
            mGatt.sendMessage(msg,Constants.NAVI_DRAW);
        }


        // 发送相对原点正北方向的坐标
        // 剩下的由树莓派作图指定具体坐标
//        Toast.makeText(mContext, A, Toast.LENGTH_SHORT).show();
    }

    boolean flag = true;
    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

        if (flag) {
            pixMapScaleFactor = maxEastPixNum / (naviInfo.getPathRetainDistance() * Math.sin(startEndAngle));
            String calimsg = String.valueOf(maxEastPixNum) + " "+ String.valueOf( maxEastPixNum / Math.tan(startEndAngle));
            mGatt.sendMessage(calimsg,Constants.NAVI_CALI);
            flag = false;
        }
        // 发送导航时间信息
        // 发送导航距离信息
        String msg = String.valueOf(naviInfo.getPathRetainTime()) + " " +
                String.valueOf(naviInfo.getPathRetainDistance());

        Log.d(TAG, "onNaviInfoUpdate: " + pixMapScaleFactor + " " + startEndAngle + " " +
                naviInfo.getPathRetainDistance());
        mGatt.sendMessage(msg, Constants.NAVI_TIME_DIST);
    }


    // 定位信息
    //

    @Override
    public void onStartNavi(int i) {
        Toast.makeText(mContext, "开始导航", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }


    @Override
    public void onArrivedWayPoint(int i) {

    }

}


