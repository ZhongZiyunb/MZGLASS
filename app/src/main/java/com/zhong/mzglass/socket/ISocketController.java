package com.zhong.mzglass.socket;


// 一个是初始化 ——>
// 一个是主动发送指令 ——>
// 一个是关闭连接 ——>
// 需要加一个线程池对象用于管理线程

public interface ISocketController {


    boolean socketRun(String ip, String port);

    boolean socketSend(String cmd);

    void socketClose();

    void setIPInfo(String ip, String port);

    String getResponse();

}
