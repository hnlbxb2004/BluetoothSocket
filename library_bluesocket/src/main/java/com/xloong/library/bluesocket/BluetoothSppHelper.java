package com.xloong.library.bluesocket;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xloong.library.bluesocket.BlueSocketBaseThread.BlueSocketStatus;
import com.xloong.library.bluesocket.message.IMessage;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author bingbing
 * @date 16/4/6
 */
public class BluetoothSppHelper {

    private BlueSocketBaseThread mTargThread;
    private BlueDataThread mDataThread;
    private BlueSocketBaseThread.BlueSocketStatus mNowStatus = BlueSocketBaseThread.BlueSocketStatus.NONE;
    private BlueSocketListener mStatusListener;
    /**
     * 消息队列
     */
    private ConcurrentLinkedQueue<IMessage> mQueue = new ConcurrentLinkedQueue<>();


    public BluetoothSppHelper() {
    }

    /**
     * 开始服务端监听线程,等待客户端连接
     */
    public void strat() {
        if (mTargThread != null) {
            mTargThread.cancle();
            mTargThread = null;
        }

        if (mDataThread != null) {
            mDataThread.cancle();
            mDataThread = null;
        }

        mTargThread = new BlueServiceThread(mSocketHandler);
        mTargThread.start();
    }

    /**
     * 客户端主动发起连接,去连接服务端
     *
     * @param serviceDevice 服务端的蓝牙设备
     */
    public void connect(BluetoothDevice serviceDevice) {
        if (mTargThread != null) {
            mTargThread.cancle();
        }

        if (mDataThread != null) {
            mDataThread.cancle();
        }
        mTargThread = new BlueClientThread(serviceDevice, mSocketHandler);
        mTargThread.start();
    }


    public synchronized boolean write(IMessage message) {
        message.toString();

        if (mNowStatus == BlueSocketBaseThread.BlueSocketStatus.CONNEDTIONED) {
            synchronized (BluetoothSppHelper.class) {
                if (mNowStatus == BlueSocketBaseThread.BlueSocketStatus.CONNEDTIONED) {
                    mQueue.add(message);
                    mDataThread.startQueue();
                    return true;
                }
                return false;
            }
        }
        return false;
    }



    private Handler mSocketHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BlueSocketStatus status = BlueSocketStatus.values()[msg.what];
            synchronized (BluetoothSppHelper.class) {
                if (status != BlueSocketStatus.MESSAGERECEIVE) {
                    mNowStatus = status;
                    if (mStatusListener != null) {
                        mStatusListener.onBlueSocketStatusChange(status, (BluetoothDevice) msg.obj);
                    }
                }

                switch (BlueSocketStatus.values()[msg.what]) {
                    case ACCEPTED:  //当服务端或者客户端监听到对方已经同意连接,则分别开启数据通道线程,接受数据
                        mQueue.clear();
                        mDataThread = new BlueDataThread(mQueue, mTargThread.getSocket(), this);
                        mDataThread.start();
                        break;
                    case DISCONNECTION:
                        //如果连接断开,则停止数据通道线程
                        if (mDataThread != null) {
                            mDataThread.cancle();
                            mDataThread = null;
                        }
                        break;
                    case MESSAGERECEIVE:
                        IMessage message = (IMessage) msg.obj;
                        if (mStatusListener != null) {
                            mStatusListener.onBlueSocketMessageReceiver(message);
                        }
                        break;

                }

            }
        }
    };


    public void setBlueSocketListener(BlueSocketListener statusListener) {
        mStatusListener = statusListener;
    }


    public interface BlueSocketListener {

        /**
         * 蓝牙socket连接状态该改变
         * 连接成功的时候,device 是代表连接的设备,其他状态remoteDevice 为null
         *
         * @param status
         */
        void onBlueSocketStatusChange(BlueSocketStatus status, BluetoothDevice remoteDevice);

        /**
         * 蓝牙socket消息到达
         */
        void onBlueSocketMessageReceiver(IMessage message);
    }

    /**
     * 出去时候停止掉所有线程
     */
    public void stop() {
        synchronized (BluetoothSppHelper.class){
            if (mTargThread != null) {
                mTargThread.cancle();
                mTargThread = null;
            }

            if (mDataThread != null) {
                mDataThread.cancle();
                mDataThread = null;
            }

            if (mStatusListener != null) {
                mStatusListener.onBlueSocketStatusChange(BlueSocketStatus.DISCONNECTION, null);
            }
        }


    }


}
