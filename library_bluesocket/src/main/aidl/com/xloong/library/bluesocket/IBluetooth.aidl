// IBluetooth.aidl
package com.xloong.library.bluesocket;

import android.bluetooth.BluetoothDevice;

// Declare any non-default types here with import statements

interface IBluetooth {
    // 蓝牙是否连接
    boolean isConnected();

    //启动service 端   startService 和 connectionService只能用一个。
    void startService();

    //连接Service 端  startService 和 connectionService只能用一个。
    void connectionService(in BluetoothDevice device);


}
