package com.zhong.mzglass.weather;

import android.content.Context;
import android.os.Binder;
import android.util.Log;

import androidx.viewpager.widget.PagerAdapter;

import com.google.gson.Gson;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Unit;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;
import com.zhong.mzglass.bluetooth.gatt.IBleGattController;
import com.zhong.mzglass.socket.ISocketController;
import com.zhong.mzglass.utils.Constants;
import com.zhong.mzglass.utils.WeatherInfo;


public class WeatherPresenter extends Binder implements IWeatherController {

    int State = Constants.STATE_INIT_FALSE;
    Context mContext;
    public WeatherInfo info;
    IWeatherViewController miwvController = null;
    IBleGattController mGatt = null;
    ISocketController misController = null;
    private String TAG = "WeatherService";

    WeatherPresenter(Context context) {
        mContext = context;
    }

    @Override
    public void registerIwvController(IWeatherViewController iwvController) {
        if (miwvController == null) {
            miwvController = iwvController;
        }
    }

    @Override
    public void unregisterIwvController() {
        if (miwvController != null) {
            miwvController = null;
        }
    }

    @Override
    public void registerGattService(IBleGattController Gatt) {
        if (mGatt == null) {
            mGatt = Gatt;
        }
    }

    @Override
    public void unregisterGattService() {
        if (mGatt != null) {
            mGatt = null;
        }
    }

    @Override
    public void registerSocketService(ISocketController isController) {
        if (misController == null) {
            misController = isController;
        }
    }

    @Override
    public void unregisterSocketService() {
        if (misController != null) {
            misController = null;
        }
    }

    @Override
    public WeatherInfo getWeatherInfo() {

        return info;
    }

    @Override
    public void UpdateNewDistrict() {
        //TODO:功能待定。
    }

    /**
     * 在当前的配置下请求天气数据
     * 1.0版先做简单的功能，不搞太复杂，只实现Update功能
     * */

    @Override
    public void Update() {
        if (State == Constants.STATE_INIT_FALSE) {

            throw new RuntimeException("PLEASE INIT HEWEATHER FIRST");

        } else if (State == Constants.STATE_INIT_OK) {


            queryWeather();

        } else {

            throw new RuntimeException("WEATHER STATE VALUE WRONG!");

        }
    }

    // 应该拼接好协议后再统一由socketService进行发送
    // 建立socket通信，发送指令
    private void sendCmd(String cmd) {

        misController.socketSend(cmd);

    }

    private void queryWeather() {

        Log.d(TAG, "queryWeather: IN");

        QWeather.getWeatherNow(mContext, "CN101280603", Lang.ZH_HANS, Unit.METRIC, new QWeather.OnResultWeatherNowListener() {
            @Override
            public void onError(Throwable throwable) {

                System.out.println("Weather Now Error:" + throwable);

            }

            @Override
            public void onSuccess(WeatherNowBean weatherNowBean) {
                System.out.println("获取天气成功： " + new Gson().toJson(weatherNowBean));
                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                Log.d(TAG, "queryWeather: OK");

                if (Code.OK == weatherNowBean.getCode()) {

                    WeatherNowBean.NowBaseBean now = weatherNowBean.getNow();
                    if (info == null) {

                        info = new WeatherInfo(now.getText()
                                , now.getTemp()+"℃"
                                ,       now.getWindScale()
                                ,       now.getWindDir());

                    } else {

                        info.write(new WeatherInfo(now.getText()
                                , now.getTemp()+"℃"
                                ,       now.getWindScale()
                                ,       now.getWindDir()));

                    }

                    if (mGatt != null) {
                        String msg = info.weather + " " + info.temperature;
                        mGatt.sendMessage(msg,Constants.WEATHER_INFO);
                    }

                } else {

                    //在此查看返回数据失败的原因
                    Code code = weatherNowBean.getCode();
                    System.out.println("失败代码: " + code);
                    //Log.i(TAG, "failed code: " + code);

                }
            }
        });
    }

    public void init() {

        HeConfig.init("HE2203041114261755",
                "a046dbcab34f4410b1f017db2e170c78");
        HeConfig.switchToDevService();

        State = Constants.STATE_INIT_OK;
    }

}
