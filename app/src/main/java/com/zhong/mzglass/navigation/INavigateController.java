package com.zhong.mzglass.navigation;

import com.amap.api.maps.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.zhong.mzglass.bluetooth.gatt.IBleGattController;
import com.zhong.mzglass.socket.ISocketController;

public interface INavigateController {

    void navigate(LatLonPoint end) throws AMapException;

    void navigate(LatLonPoint start, LatLonPoint end) throws AMapException;


    void registerViewController(INavigateViewController mNavigateView);

    void unregisterViewController(INavigateViewController mNavigateView);

    void registerBleService(IBleGattController mSocket);

    void unregisterBleService(IBleGattController mSocket);


}
