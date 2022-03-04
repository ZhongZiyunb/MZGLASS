package com.zhong.mzglass.weather;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zhong.mzglass.MainActivity;
import com.zhong.mzglass.R;
import com.zhong.mzglass.ui.FragmentServices;
import com.zhong.mzglass.utils.WeatherInfo;

public class WeatherActivity extends AppCompatActivity {

    private IWeatherController iWeatherController;
    private TextView mWindForceTxt;
    private TextView mWindDirectionTxt;
    private TextView mTemperatureTxt;
    private TextView mWeatherTxt;
    private Button backBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        iniBind();
        initService();
        initView();
    }

    private void iniBind() {
        mWeatherTxt = (TextView) findViewById(R.id.info_weather);
        mTemperatureTxt = (TextView) findViewById(R.id.info_temperature);
        mWindDirectionTxt = (TextView) findViewById(R.id.info_windDirection);
        mWindForceTxt = (TextView) findViewById(R.id.info_windForce);
        backBtn = (Button) findViewById(R.id.weather_to_service_btn);
    }

    private void initView() {


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(WeatherActivity.this, MainActivity.class);
//                setResult(1);
//                startActivity(intent);
                finish();
            }
        });

    }

    private void initService() {

        Intent intent = new Intent(this, WeatherService.class);

        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iWeatherController = (IWeatherController) iBinder;
            iWeatherController.registerIwvController(iwViewController);
            iwViewController.UpdateView(iWeatherController.getWeatherInfo());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            iwViewController = null;
        }
    };

    private IWeatherViewController iwViewController = new IWeatherViewController() {

        //District: 地区变量。

        @Override
        public void UpdateView(WeatherInfo wInfo) {
            mWeatherTxt.setText(wInfo.weather);
            mTemperatureTxt.setText((wInfo.temperature));
            mWindDirectionTxt.setText((wInfo.windDirection));
            mWindForceTxt.setText((wInfo.windForce));
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mConnection != null && iWeatherController != null) {
            iWeatherController.unregisterIwvController();
        }
    }
}
