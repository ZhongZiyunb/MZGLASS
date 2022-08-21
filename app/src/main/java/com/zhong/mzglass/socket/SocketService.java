package com.zhong.mzglass.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class SocketService extends Service {

    // 网上拉去数据 --> 数据层 在service开一个线程
    // 总共需要三个对象：
    // 给到前端相应显示 --> UI层

    // 先用现成的Service吧

    SocketPresenter sPresenter = new SocketPresenter();
    private String TAG = "SocketService";

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO: 进行初始化连接
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return sPresenter;
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        sPresenter.socketClose();
        super.onDestroy();
    }
}
