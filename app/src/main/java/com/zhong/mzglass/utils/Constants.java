package com.zhong.mzglass.utils;

public class Constants {

    // view pager
    static final public int PAGE_HOME = 0;
    static final public int PAGE_SERVICE = 1;
    static final public int PAGE_SETTINGS = 2;

    static final public int STATE_INIT_FALSE = 0;
    static final public int STATE_INIT_OK = 1;

    // socket state
    static final public int SOCKET_CLOSED = 0;
    static final public int SOCKET_CONNECTED = 1;

    // service state
    static final public int SERVICE_WEATHER = 0; // 0001
    static final public int SERVICE_NAVIGATE = 1; // 0010
    static final public int SERVICE_GATT = 2; // 0010
    static final public int SERVICE_EVENT = 3;

    // UUID for ble
    static final public String UUID_SPP = "00001101-0000-1000-8000-00805F9B34FB";

    static final public String TARGET_DEVICE_NAME = "HUAWEI FreeBuds 4";
    static final public String UUID_UART_SERVICE = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    static final public String UUID_TX_CHARACTERISTIC = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
    static final public String UUID_RX_CHARACTERISTIC = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";

    // UUID TEST for ble
    static final public String TARGET_TEST_DEVICE_NAME = "HUAWEI FreeBuds 4";
    static final public String UUID_TEST_UART_SERVICE = "00001101-0000-1000-8000-00805f9b34fb";
    static final public String UUID_TEST_TX_CHARACTERISTIC = "6E400002-B5A3-F393-E0A9-E50E24DCCA9E";
    static final public String UUID_TEST_RX_CHARACTERISTIC = "6E400003-B5A3-F393-E0A9-E50E24DCCA9E";

    // Broadcast
    static final public String UPDATE_UUID = "UPDATE_UUID";
    static final public String UPDATE_CONN_STATE = "UPDATE_CONN_STATE";
    static final public String UPDATE_UUID_SERVICE = "UPDATE_UUID_SERVICE";

    // navi broadcast
    static final public String START_NAVI = "START_NAVI";
    static final public String START_WALK_NAVI = "START_WALK_NAVI";

    // protocol
    static final public String NOTICE = "notice";
    static final public String TEST = "test";


    static final public String WEATHER_INFO = "weather_info";

    static final public String NAVI_TIME_DIST = "navi_time_dist";
    static final public String NAVI_ANGLE = "navi_angle";
    static final public String NAVI_CALI = "navi_cali";
    static final public String NAVI_TEXT = "navi_text";
    static final public String NAVI_START = "navi_start";
    static final public String NAVI_STOP = "navi_stop";

    static final public String EVENT_CALL = "event_call";
    static final public String EVENT_SMS = "event_sms";



    // navi location search state
    static final public int SEARCH_START = 0;
    static final public int SEARCH_END = 1;



}
