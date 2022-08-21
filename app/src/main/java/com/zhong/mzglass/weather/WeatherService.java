package com.zhong.mzglass.weather;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;


import com.zhong.mzglass.bluetooth.gatt.BleGattService;
import com.zhong.mzglass.bluetooth.gatt.IBleGattController;
import com.zhong.mzglass.socket.ISocketController;
import com.zhong.mzglass.socket.SocketService;

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

public class WeatherService extends Service {

    // 理一下逻辑：
    // 首先 前端绑定weather作操作。
    // 然后WeatherService绑定后端作传输操作。


    private static final String TAG = "WeatherService";
    WeatherPresenter wPresenter;
    private ISocketController iSocketController;
    private IBleGattController mBleGattController;

    @Override
    public void onCreate() {
        super.onCreate();
        // 开启API
        Log.d(TAG, "onCreate: weather service start");
        wPresenter = new WeatherPresenter(getApplicationContext());

        initService();
        initBindService();
    }

    private void initBindService() {
        Intent intent = new Intent(this, BleGattService.class);
        bindService(intent,mConn,BIND_AUTO_CREATE);
    }


    ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBleGattController = (IBleGattController) iBinder;
            wPresenter.registerGattService(mBleGattController);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            wPresenter.unregisterGattService();
        }
    };

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
        unbindService(mConn);
    }

}
