package com.zhong.mzglass.bluetooth;

import com.zhong.mzglass.utils.BleDeviceInfo;

public interface IBleViewController {


    void updateMac(String s);

    void updateName(String s);

    void updateUUIDService(String s);

    void updateConnState(String s);

    void updateRecvMsg(String s);

    void updateListView(String s);


}

