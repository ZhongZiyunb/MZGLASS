package com.zhong.mzglass.weather;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.BreakIterator;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// TODO: 下午把天气页面做完 这周任务基本完成

public class WeatherService extends Service implements WeatherSearch.OnWeatherSearchListener {

    private static final String TAG = "WeatherService";
    WeatherPresenter wPresenter;
    private WeatherSearchQuery mquery;
    private WeatherSearch mweathersearch;
    private BreakIterator reporttime1;
    private BreakIterator weather;
    private LocalWeatherLive weatherlive;
    private BreakIterator Temperature;
    private BreakIterator wind;
    private BreakIterator humidity;
    private ActionBar ToastUtil;
    private HttpURLConnection httpURLConnection;
    private InputStream inputStream;

    @Override
    public void onCreate() {
        super.onCreate();
        // 开启API
        Log.d(TAG, "onCreate: weather service start");
        wPresenter = new WeatherPresenter(getApplicationContext());

        initService();



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: weather service start");
        return super.onStartCommand(intent, flags, startId);
    }

    private void initService() {
        wPresenter.init();
        wPresenter.Update();
        Log.d(TAG, "initService: weather pull finish");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: weather service bind");
        return wPresenter;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: weather service destroy");
        super.onDestroy();
    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {
        if (i == 1000) {
            if (localWeatherLiveResult != null && localWeatherLiveResult.getLiveResult() != null) {
                weatherlive = localWeatherLiveResult.getLiveResult();
                reporttime1.setText(weatherlive.getReportTime() + "发布");
                weather.setText(weatherlive.getWeather());
                Temperature.setText(weatherlive.getTemperature() + "°");
                wind.setText(weatherlive.getWindDirection() + "风     " + weatherlive.getWindPower() + "级");
                humidity.setText("湿度         " + weatherlive.getHumidity() + "%");
                Log.d(TAG, "onWeatherLiveSearched: "+ weatherlive.getWeather());
            }
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

    }
}
