package com.zhong.mzglass.bluetooth.gatt;

public interface IBleGattViewController {

    void updateMac(String s);

    void updateName(String s);

    void updateUUIDService(String s);

    void updateConnState(String s);

    void updateRecvMsg(String s);

    void updateListView(String s);

}
