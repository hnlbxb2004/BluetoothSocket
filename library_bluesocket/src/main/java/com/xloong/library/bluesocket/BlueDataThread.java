package com.xloong.library.bluesocket;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 服务端和客户端共用的数据传输线程
 *
 *
 * @author bingbing
 * @date 16/4/6
 */
public class BlueDataThread extends BlueSocketBaseThread {


    private BluetoothSocket mBlueSocket;
    private DataOutputStream mBlueSocketOutputStream;
    private DataInputStream mBlueSocketInputStream;

    protected BlueDataThread(BluetoothSocket bluetoothSocket, Handler handler) {
        super(handler);
        mBlueSocket = bluetoothSocket;
    }


    @Override
    public void run() {
        super.run();
        if (!isRunning) return;
        try {
            mBlueSocketOutputStream = new DataOutputStream(mBlueSocket.getOutputStream());
            mBlueSocketInputStream = new DataInputStream(mBlueSocket.getInputStream());
            sendMessage(BlueSocketStatus.CONNEDTIONED,mBlueSocket.getRemoteDevice());
        } catch (IOException e) {
            e.printStackTrace();
            sendMessage(BlueSocketStatus.DISCONNECTION);
        }
        //监听通道后,死循环监听通道,去读取message ,
        while (isRunning) {
            try {
                String result = mBlueSocketInputStream.readUTF();
                sendMessage(BlueSocketStatus.MESSAGERECEIVE, result);
            } catch (IOException e) {
                e.printStackTrace();
                sendMessage(BlueSocketStatus.DISCONNECTION);
            }

        }
    }

    public boolean sendData(String data) {
        try {
            mBlueSocketOutputStream.writeUTF(data);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            sendMessage(BlueSocketStatus.DISCONNECTION);
        }
        return false;
    }

    @Override
    public BluetoothSocket getSocket() {
        return null;
    }

    @Override
    public void cancle() {
        super.cancle();
        try {
            if (mBlueSocketInputStream != null)
                mBlueSocketInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (mBlueSocket != null)
                mBlueSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (mBlueSocketOutputStream != null)
                mBlueSocketOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mBlueSocketInputStream = null;
        mBlueSocketOutputStream = null;
        mBlueSocket = null;

    }
}
