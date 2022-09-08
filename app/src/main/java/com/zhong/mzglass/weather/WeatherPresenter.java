package com.zhong.mzglass.weather;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.google.gson.Gson;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Unit;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;
import com.zhong.mzglass.bluetooth.gatt.IBleGattController;
import com.zhong.mzglass.socket.ISocketController;
import com.zhong.mzglass.utils.Constants;
import com.zhong.mzglass.utils.WeatherInfo;

import java.util.List;

// TODO: 定时发送
public class WeatherPresenter extends Binder implements IWeatherController {

    int State = Constants.STATE_INIT_FALSE;
    Context mContext;
    public WeatherInfo info;
    IWeatherViewController miwvController = null;
    IBleGattController mGatt = null;
    ISocketController misController = null;
    private String TAG = "WeatherService";

    // 定位服务
    public static final int LOCATION_CODE = 301;
    private LocationManager locationManager;
    private String locationProvider = null;
    private Location myLocation =null;
    WeatherPresenter(Context context) {
        mContext = context;
    }

    private void getLocation(){
        //1.获取位置管理器
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        //2.获取位置提供器，GPS或是NetWork
        List<String> providers = locationManager.getProviders(true);

        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
            Log.v("TAG", "定位方式GPS");
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
            Log.v("TAG", "定位方式Network");
        }else {
            Toast.makeText(mContext, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //获取权限（如果没有开启权限，会弹出对话框，询问是否开启权限）
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);
            } else {
                //3.获取上次的位置，一般第一次运行，此值为null
                Location location = locationManager.getLastKnownLocation(locationProvider);
                if (location!=null){
                    Toast.makeText(mContext, location.getLongitude() + " " +
                            location.getLatitude() + "",Toast.LENGTH_SHORT).show();
                    myLocation = location;
                    Log.v("TAG", "获取上次的位置-经纬度："+location.getLongitude()+"   "+location.getLatitude());
                    //getAddress(location);

                }else{
                    //监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
                    locationManager.requestLocationUpdates(locationProvider, 3000, 1,locationListener);
                }
            }
        } else {
            Location location = locationManager.getLastKnownLocation(locationProvider);
            if (location!=null){
                Toast.makeText(mContext, location.getLongitude() + " " +
                        location.getLatitude() + "", Toast.LENGTH_SHORT).show();
                myLocation = location;
                Log.v("TAG", "获取上次的位置-经纬度："+location.getLongitude()+"   "+location.getLatitude());
                //getAddress(location);

            }else{
                //监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
                locationManager.requestLocationUpdates(locationProvider, 3000, 1,locationListener);
            }
        }
    }

    public LocationListener locationListener = new LocationListener() {
        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {
        }
        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {
        }
        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                //如果位置发生变化，重新显示地理位置经纬度
                Log.v("TAG", "监视地理位置变化-经纬度："+location.getLongitude()+"   "+location.getLatitude());
            }
        }
    };


    @Override
    public void registerIwvController(IWeatherViewController iwvController) {
        if (miwvController == null) {
            miwvController = iwvController;
        }
    }

    @Override
    public void unregisterIwvController() {
        if (miwvController != null) {
            miwvController = null;
        }
    }

    @Override
    public void registerGattService(IBleGattController Gatt) {
        if (mGatt == null) {
            mGatt = Gatt;
        }
    }

    @Override
    public void unregisterGattService() {
        if (mGatt != null) {
            mGatt = null;
        }
    }

    @Override
    public void registerSocketService(ISocketController isController) {
        if (misController == null) {
            misController = isController;
        }
    }

    @Override
    public void unregisterSocketService() {
        if (misController != null) {
            misController = null;
        }
    }

    @Override
    public WeatherInfo getWeatherInfo() {

        return info;
    }

    @Override
    public void UpdateNewDistrict() {
        //TODO:功能待定。
    }

    /**
     * 在当前的配置下请求天气数据
     * 1.0版先做简单的功能，不搞太复杂，只实现Update功能
     * */

    @Override
    public void Update() {
        if (State == Constants.STATE_INIT_FALSE) {

            throw new RuntimeException("PLEASE INIT HEWEATHER FIRST");

        } else if (State == Constants.STATE_INIT_OK) {


            queryWeather();

        } else {

            throw new RuntimeException("WEATHER STATE VALUE WRONG!");

        }
    }

    // 应该拼接好协议后再统一由socketService进行发送
    // 建立socket通信，发送指令
    private void sendCmd(String cmd) {

        misController.socketSend(cmd);

    }

    private void queryWeather() {

        getLocation();

        Log.d(TAG, "queryWeather: IN");
        if (myLocation == null) {
            Log.d(TAG, "queryWeather: location permission deny");
            return;
        }
        String loc = String.valueOf(myLocation.getLongitude()) + "," + String.valueOf(myLocation.getLatitude());
        Log.d(TAG, "queryWeather: myLocation" + loc);
        QWeather.getWeatherNow(mContext,loc, Lang.ZH_HANS, Unit.METRIC, new QWeather.OnResultWeatherNowListener() {
            @Override
            public void onError(Throwable throwable) {

                System.out.println("Weather Now Error:" + throwable);

            }

            @Override
            public void onSuccess(WeatherNowBean weatherNowBean) {
                System.out.println("获取天气成功： " + new Gson().toJson(weatherNowBean));
                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                Log.d(TAG, "queryWeather: OK");

                if (Code.OK == weatherNowBean.getCode()) {

                    WeatherNowBean.NowBaseBean now = weatherNowBean.getNow();
                    if (info == null) {

                        info = new WeatherInfo(now.getText()
                                , now.getTemp()+"℃"
                                ,       now.getWindDir()
                                ,       now.getWindScale());

                    } else {

                        info.write(new WeatherInfo(now.getText()
                                , now.getTemp()+"℃"
                                ,       now.getWindDir()
                                ,       now.getWindScale()));

                    }

                    if (mGatt != null) {
                        String msg = info.weather + " " + info.temperature + " " + info.windDirection + " "
                                    + info.windForce;
                        mGatt.sendMessage(msg,Constants.WEATHER_INFO);
                    }

                } else {

                    //在此查看返回数据失败的原因
                    Code code = weatherNowBean.getCode();
                    System.out.println("失败代码: " + code);
                    //Log.i(TAG, "failed code: " + code);

                }
            }
        });
    }

    public void init() {

        HeConfig.init("HE2203041114261755",
                "a046dbcab34f4410b1f017db2e170c78");
        HeConfig.switchToDevService();

        State = Constants.STATE_INIT_OK;
    }

}
