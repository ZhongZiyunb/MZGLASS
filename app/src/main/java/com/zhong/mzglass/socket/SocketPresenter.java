package com.zhong.mzglass.socket;

import android.os.Binder;
import android.util.Log;

import com.zhong.mzglass.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SocketPresenter extends Binder implements ISocketController {


    private final String TAG = "SocketPresenter";
    private ExecutorService mExecutor;
    private Socket mSocket;
    private String mIP;
    private String mPORT;
    private int STATE;
    private Object lock = new Object();
    private InputStream is;
    private InputStreamReader isr;
    private BufferedReader br;
    private String response;
    private OutputStream os;

    // 考虑增加一个共享访问的变量

    SocketPresenter () {

        // 初始化线程池
        mExecutor = Executors.newCachedThreadPool();
        STATE = Constants.SOCKET_CLOSED;
//        Log.d(TAG, "SocketPresenter: IP:" + InetAddress.getLocalHost().getHostAddress());
    }
    /**
     * socketRun
     * 进行 TCP连接和接收消息操作
     * 注意连接线程和接收线程有先后顺序
     *
     * @return*/

    @Override
    public boolean socketRun(String ip, String port) {
        Log.d(TAG, "socketRun: in");
         //
//        ip = "192.168.1.109";

         if (ip == null || port == null) {
             Log.d(TAG, "socketRun: ip or port wrong");
             return false;
         }

         Log.d(TAG, "socketRun: run");
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    try {

                        Log.d(TAG, "run: local info:" + InetAddress.getLocalHost().getHostAddress());
                        Log.d(TAG, "run: ip info: ip——>" + ip + " port——>"+ port);
                        mSocket = new Socket(ip, Integer.parseInt(port));
                        Log.d(TAG, "run: ok");
                        if (mSocket.isConnected()) {
                            Log.d(TAG, "run: connect successs");
                            STATE = Constants.SOCKET_CONNECTED;
//                            notifyAll();
                        } else {
                            Log.d(TAG, "run: connecting...");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
               synchronized (lock) {
                   // 获取锁
                   while (STATE != Constants.SOCKET_CONNECTED) {
                       try {
                           Log.d(TAG, "run: recv msg: waiting...");
                           lock.wait(); // 等待被唤醒
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }
                   }

                   Log.d(TAG, "run: recv msg: start recieving msg");
                   // 开始接收消息
                   while (STATE == Constants.SOCKET_CONNECTED) {
                       try {
                           is = mSocket.getInputStream();
                           isr = new InputStreamReader(is);
                           br = new BufferedReader(isr);

                           response = br.readLine();
                           Log.d(TAG, "run: recv msg:" + response);

                       } catch (IOException e) {
                           e.printStackTrace();
                       }

                   }
               }
            }
        });

        return true;
    }

    // 同步一下，保证每次只会有一个对象调用这个函数
    @Override
    public synchronized boolean socketSend(String cmd) {
        Log.d(TAG, "socketSend: message:" + cmd);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (STATE != Constants.SOCKET_CONNECTED) {
                        Log.d(TAG, "run: no connection! can't send msg");
                        return;
                    }
                    os = mSocket.getOutputStream();
                    os.write(cmd.getBytes(StandardCharsets.UTF_8));
                    os.flush();

                    Log.d(TAG, "run: send msg:" + cmd);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return true;
    }

    @Override
    public void socketClose() {
        Log.d(TAG, "socketClose: close");
        try {

            if (os !=null) {
                os.close();
            }
            if (br !=null) {
                is.close();
                isr.close();
                br.close();
            }
            if (mSocket !=null) {
                mSocket.close();
            }
            if (mExecutor != null) {
                mExecutor.shutdown();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        STATE = Constants.SOCKET_CLOSED;
    }

    @Override
    public void setIPInfo(String ip, String port) {
        mIP = ip;
        mPORT = port;
    }

    @Override
    public String getResponse() {
        return response;
    }

    public void finalize() {
        Log.d(TAG, "finalize: object die");
        // 关闭线程池
        if (mExecutor != null)
            mExecutor.shutdown();
    }

    public String getIP() {
         return mIP;
    }

    public String getPort() {
        return mPORT;
    }



}
