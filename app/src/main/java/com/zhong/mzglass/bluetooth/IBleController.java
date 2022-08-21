package com.zhong.mzglass.bluetooth;

import com.zhong.mzglass.utils.BleDeviceInfo;

public interface IBleController {


    void init();

    void scanDevice();

    void findDevice();

    void connect(String s); // 连接特定的GATT设备

    void sendMessage(String s); // 发送信息

    void registerViewController(IBleViewController viewController);

    void unregisterViewController();

    void close(); // 关闭连接

    BleDeviceInfo getDeviceInfo();

}
