package com.zhong.mzglass.weather;

import com.zhong.mzglass.utils.WeatherInfo;

public interface IWeatherViewController {

    /**
     * Name:
     * Function: 更新UI信息
     */

    void UpdateView(WeatherInfo wInfo);
}
