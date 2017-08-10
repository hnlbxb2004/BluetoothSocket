package com.xloong.library.bluesocket;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;

/**
 * 蓝牙连接客户端连接线程
 * @author bingbing
 * @date 16/4/7
 */
public class BlueClientThread extends BlueSocketBaseThread {

    private BluetoothDevice mServiceDevice;
    private BluetoothSocket mBlueSocket;

    public BlueClientThread(BluetoothDevice serviceDevice, Handler handler) {
        super(handler);
        mServiceDevice = serviceDevice;
    }


    @Override
    public void run() {
        super.run();
        if (!isRunning)return;
        try {
            sendMessage(BlueSocketStatus.CONNECTIONING);
            mBlueSocket = mServiceDevice.createRfcommSocketToServiceRecord(UUID_ANDROID_DEVICE);
            mBlueSocket.connect();
            sendMessage(BlueSocketStatus.ACCEPTED);
        } catch (IOException e) {
            sendMessage(BlueSocketStatus.DISCONNECTION);
        }
    }


    @Override
    public BluetoothSocket getSocket() {
        return mBlueSocket;
    }

    @Override
    public void cancle() {
        super.cancle();

        try {
            if (mBlueSocket != null)
                mBlueSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mServiceDevice = null;
        mBlueSocket = null;
    }
}
