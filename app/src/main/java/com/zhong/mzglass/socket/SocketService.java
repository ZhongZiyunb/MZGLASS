package com.zhong.mzglass.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class SocketService extends Service {

    // 网上拉去数据 --> 数据层 在service开一个线程
    // 总共需要三个对象：
    // 给到前端相应显示 --> UI层

    // 先用现成的Service吧

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



}
