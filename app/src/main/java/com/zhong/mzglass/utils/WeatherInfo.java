package com.zhong.mzglass.utils;

public class WeatherInfo {

    public String weather;
    public String temperature;
    public String windDirection;
    public String windForce;

    public WeatherInfo(String wea, String temp, String windDir, String windFor) {
        weather = wea;
        temperature = temp;
        windDirection = windDir;
        windForce = windFor;
    }

    //TODO: 实现同步方法
    public synchronized void read(WeatherInfo wInfo) {
        wInfo.weather = weather;
        wInfo.temperature = temperature;
        wInfo.windDirection = windDirection;
        wInfo.windForce = windForce;
    }

    public synchronized void write(WeatherInfo wInfo) {
        weather = wInfo.weather;
        temperature = wInfo.temperature;
        windDirection = wInfo.windDirection;
        windForce = wInfo.windForce;
    }

}
