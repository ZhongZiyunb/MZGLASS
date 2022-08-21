package com.zhong.mzglass.utils;

import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;

import java.io.Serializable;
import java.util.ArrayList;

public class BleDeviceInfo implements Serializable {

    public String macAddress = "";
    public String name = "";
    public ArrayList<String> uuids = new ArrayList<String>();

}
