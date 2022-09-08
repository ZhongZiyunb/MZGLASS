package com.zhong.mzglass.navigation;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.zhong.mzglass.bluetooth.BlePresenter;
import com.zhong.mzglass.bluetooth.gatt.BleGattPresenter;
import com.zhong.mzglass.bluetooth.gatt.BleGattService;
import com.zhong.mzglass.bluetooth.gatt.IBleGattController;
import com.zhong.mzglass.socket.SocketPresenter;
import com.zhong.mzglass.socket.SocketService;

public class NavigateService extends Service {


    NavigatePresenter mNavigatePresenter = null;
//    SocketPresenter mSocketPresenter;
    IBleGattController mBlePresenter = null;

    @Override
    public void onCreate() {
        super.onCreate();

        mNavigatePresenter = new NavigatePresenter(this);
        mNavigatePresenter.init();
        initBindService();

    }

    private void initBindService() {
        Intent intent = new Intent(this, BleGattService.class);

        bindService(intent, mConn, BIND_AUTO_CREATE);
    }

    // TODO: 修正一下用intent
    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBlePresenter = (IBleGattController) iBinder;
            mNavigatePresenter.registerBleService(mBlePresenter);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mNavigatePresenter.unregisterBleService(mBlePresenter);
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mNavigatePresenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mConn != null) {
            unbindService(mConn);
        }
        try {
            mNavigatePresenter.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
