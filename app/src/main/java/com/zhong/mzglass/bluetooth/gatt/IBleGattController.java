package com.zhong.mzglass.bluetooth.gatt;

import com.zhong.mzglass.bluetooth.BleDevice;
import com.zhong.mzglass.bluetooth.IBleViewController;
import com.zhong.mzglass.utils.BleDeviceInfo;

public interface IBleGattController {



    void scanDevice();

    void connect(String s);

    void sendMessage(String s, String func);

    BleDeviceInfo getDeviceInfo(int i);

    BleDeviceInfo getTargetDeviceInfo();

    void registerViewController(IBleGattViewController viewController);

    void unregisterViewController();

    void close();

}
