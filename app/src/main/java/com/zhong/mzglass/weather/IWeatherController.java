package com.zhong.mzglass.weather;

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

    WeatherInfo getWeatherInfo();

}
