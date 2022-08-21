package com.zhong.mzglass;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zhong.mzglass.socket.ISocketController;
import com.zhong.mzglass.socket.SocketService;

import java.io.IOException;
import java.util.concurrent.BlockingDeque;

import javax.net.ssl.HandshakeCompletedEvent;

public class TestActivity extends AppCompatActivity {


    private Button test_go_back;
    private EditText test_ip;
    private EditText test_port;
    private Button test_connect_wifi;
    private Button test_send_message_btn;
    private EditText test_send_message;
    private TextView test_ip_state;
    private TextView test_port_state;
    private TextView test_receive_message;

    // 接收信息的线程
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {

            if (!message.obj.equals("")) {
                String str = (String) message.obj;
                test_receive_message.setText(str);
            }

            return true;
        }
    });
    private String TAG = "TestActivity";
    private boolean exit_flag = false;
    private boolean stop = false;
    private Runnable mRecvMsg = new Runnable() {
        @Override
        public void run() {
            String recv_msg = "";
            while (!stop) {

                SystemClock.sleep(2000);
//              recv_msg = mSocket.getResponse();
                recv_msg = "in";
                if (!recv_msg.equals("")) {
                    Message msg = new Message();
                    msg.obj = recv_msg;
                    mHandler.sendMessage(msg);
                }
                Log.d(TAG, "run: recv msg running!");
            }
            Log.d(TAG, "run: recv msg finish ");
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

        initBindService();
        initView();
        initBind();
    }


    private void initBindService() {
        Intent intent = new Intent(this, SocketService.class);

        bindService(intent, mConn, BIND_AUTO_CREATE);
    }

    private ISocketController mSocket;
    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mSocket = (ISocketController) iBinder;

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    private void initView() {
        test_go_back = (Button) findViewById(R.id.test_go_back);

        test_ip = (EditText) findViewById(R.id.test_ip);
        test_port = (EditText) findViewById(R.id.test_port);

        test_connect_wifi = (Button) findViewById(R.id.test_connect_wifi);
        test_send_message_btn = (Button) findViewById(R.id.test_send_message_btn);
        test_send_message = (EditText) findViewById(R.id.test_send_message);

        test_ip_state = (TextView) findViewById(R.id.test_ip_state);
        test_port_state = (TextView) findViewById(R.id.test_port_state);

        test_receive_message = (TextView) findViewById(R.id.test_recv_message);

    }

    private void initBind() {

        // 回到上一个页面 (TODO:需要细看一下startActivity功能)
        test_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 出去直接销毁本Activity
            }
        });

        // 设定wifi
        test_connect_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String IP = test_ip.getText().toString();
                String PORT = test_port.getText().toString();
                if (!IP.equals("") && !PORT.equals("")) {
                    test_ip_state.setText(IP);
                    test_port_state.setText(PORT);
                }
                mSocket.socketRun(IP,PORT);
                Log.d(TAG, "onClick: here");
            }
        });

        // 发送信息
        test_send_message_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = test_send_message.getText().toString();
                mSocket.socketSend(msg);
                Log.d(TAG, "onClick: send message" + msg);
            }
        });

        // 接收信息
        new Thread(mRecvMsg).start();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mConn != null) {
            unbindService(mConn);
            mSocket.socketClose();
        }
        stop = true;

    }
}
