package com.zhong.mzglass.bluetooth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BleService extends Service {

    BlePresenter mBlePresenter;


    @Override
    public void onCreate() {
        super.onCreate();
        mBlePresenter = new BlePresenter(getApplicationContext());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBlePresenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBlePresenter.close();
    }
}
