package com.zhong.mzglass.weather;

import com.zhong.mzglass.bluetooth.gatt.IBleGattController;
import com.zhong.mzglass.socket.ISocketController;
import com.zhong.mzglass.utils.WeatherInfo;

public interface IWeatherController {


    WeatherInfo wInfo = null;
    /**
     * Name:
     * Function: 用于更新新的城市。
     */
    void UpdateNewDistrict();

    /**
     * Name:
     * Function: 用于拉去信息进行更新。
     */
    void Update();

    void registerIwvController(IWeatherViewController iwvController);

    void unregisterIwvController();

    void registerGattService(IBleGattController mGatt);

    void unregisterGattService();

    /**
     * 注册socket服务
     * */

    void registerSocketService(ISocketController isController);

    void unregisterSocketService();

    WeatherInfo getWeatherInfo();

}
