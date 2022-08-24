package com.zhong.mzglass.event;



// TODO:集成事件提醒的功能
// 1 来电提醒
// 2 短信提醒
// 3 日程提醒

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.zhong.mzglass.bluetooth.gatt.BleGattPresenter;
import com.zhong.mzglass.bluetooth.gatt.BleGattService;
import com.zhong.mzglass.bluetooth.gatt.IBleGattController;
import com.zhong.mzglass.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingDeque;


// 这个功能只需要监听 然后绑定Gatt服务进行发送即可

// TODO: 短信的监听
// TODO: 备忘录实现
// TODO: 明天尝试真机测试
public class EventService extends Service {

    private IntentFilter mIntentFilter = null;
    private EventReciever mEventReciever = null;
    private IBleGattController mBleGattController = null;
    final private static String TAG = "EventService";

    private class EventReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: "+intent.getAction().toString());
            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {

                Log.d(TAG, "onReceive: message received");
                Bundle bundle = intent.getExtras();
                //如果不为空
                if (bundle != null) {
                    //将pdus里面的内容转化成Object[]数组
                    Object pdusData[] = (Object[]) bundle.get("pdus");
                    //解析短信
                    SmsMessage[] msg = new SmsMessage[pdusData.length];
                    for (int i = 0; i < msg.length; i++) {
                        byte pdus[] = (byte[]) pdusData[i];
                        msg[i] = SmsMessage.createFromPdu(pdus);
                    }
                    StringBuffer content = new StringBuffer();//获取短信内容
                    StringBuffer phoneNumber = new StringBuffer();//获取地址
                    StringBuffer receiveData = new StringBuffer();//获取时间
                    //分析短信具体参数
                    for (SmsMessage temp : msg) {
                        content.append(temp.getMessageBody());
                        phoneNumber.append(temp.getOriginatingAddress());
                        receiveData.append(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS")
                                .format(new Date(temp.getTimestampMillis())));
                    }
                    /**
                     * 这里还可以进行好多操作，比如我们根据手机号进行拦截（取消广播继续传播）等等
                     */
                    Log.d(TAG, "onReceive: "+ phoneNumber.toString() + content + receiveData);
                    mBleGattController.sendMessage("短信:" + phoneNumber.toString() + content + receiveData,Constants.EVENT_SMS);
                    Toast.makeText(context, phoneNumber.toString() + content + receiveData, Toast.LENGTH_LONG).show();//短信内容
                }
            } else if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                    Log.d(TAG, "onReceive: phone call out");
            } else {
                Log.d(TAG, "onReceive: phone call in");
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
                tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
            }

        }

    }

    private PhoneStateListener listener =new PhoneStateListener(){

        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            // TODO Auto-generated method stub
            //state 当前状态 incomingNumber,貌似没有去电的API
            super.onCallStateChanged(state, incomingNumber);
            switch(state){
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.e("TAG","挂断");
                    mBleGattController.sendMessage("挂断: " + incomingNumber,Constants.EVENT_CALL);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.e("TAG","接听");

                    break;
                case TelephonyManager.CALL_STATE_RINGING:

                    //输出来电号码
                    Log.d("PhoneStateListener","响铃:来电号码 "+ incomingNumber);
                    Log.d("PhoneStateListener","响铃:====== "+Thread.currentThread().getName());
                    mBleGattController.sendMessage("来电:" + incomingNumber, Constants.EVENT_CALL);
                    break;
            }
        }
    };




    @Override
    public void onCreate() {
        super.onCreate();

        initBind();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.intent.action.PHONE_STATE");
        mIntentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");

        mEventReciever = new EventReciever();

        registerReceiver(mEventReciever,mIntentFilter);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        
        return null;
    }

    private void initBind() {
        // TODO:绑定Gatt服务
        Intent intent = new Intent(this, BleGattService.class);

        bindService(intent, mConn, BIND_AUTO_CREATE);

    }
    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBleGattController = (IBleGattController) iBinder;

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mConn != null) {
            unbindService(mConn);
        }
        unregisterReceiver(mEventReciever);
    }
}
