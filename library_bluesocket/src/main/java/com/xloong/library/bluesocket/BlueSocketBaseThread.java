package com.xloong.library.bluesocket;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.util.UUID;

/**
 * @author bingbing
 * @date 16/4/6
 */
public abstract class BlueSocketBaseThread extends Thread {

    protected final String TAG = this.getClass().getSimpleName();
    public static final String NAME_SECURE = "XLOONG Bluetooth";

    public static final UUID UUID_ANDROID_DEVICE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    public enum BlueSocketStatus {

        NONE,           //初始状态
        LISTENING,      //服务端正在监听被连接状态
        ACCEPTED,       //服务端接受到连接申请   或者客户端响应到服务端的连接
        CONNECTIONING,  //正在建立连接通道
        CONNEDTIONED,   //已经连接
        DISCONNECTION,  //断开连接
        MESSAGERECEIVE  //消息达到
    }

    /**
     * 此handler 用来和 manager 通讯
     */
    protected Handler mHandler;
    /**
     * 是否还在运行
     */
    protected boolean isRunning;

    protected BluetoothAdapter mBluetoothAdapter;

    protected BlueSocketBaseThread(Handler handler) {
        super();
        mHandler = handler;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    /**
     * 获取当前的链接的socket 对象
     *
     * @return
     */
    public abstract BluetoothSocket getSocket();

    /**
     * 取消当前线程,释放内存
     */
    public void cancle() {
        isRunning = false;
        mBluetoothAdapter = null;
    }

    public void sendMessage(BlueSocketStatus status) {
        if (mHandler != null && isRunning)
            mHandler.obtainMessage(status.ordinal()).sendToTarget();
    }

    public void sendMessage(BlueSocketStatus status, Object object) {
        if (mHandler != null && isRunning)
            mHandler.obtainMessage(status.ordinal(), object).sendToTarget();
    }


    @Override
    public synchronized void start() {
        isRunning = true;
        super.start();
    }
}
