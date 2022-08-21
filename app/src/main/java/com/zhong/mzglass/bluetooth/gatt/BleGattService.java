package com.zhong.mzglass.bluetooth.gatt;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BleGattService extends Service {


    private BleGattPresenter mBleGattPresenter = new BleGattPresenter(this);

    // 蓝牙设置界面

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBleGattPresenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBleGattPresenter.close();
    }
}
