package com.xloong.library.bluesocket;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.xloong.library.bluesocket.message.IMessage;
import com.xloong.library.bluesocket.utils.TypeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 服务端和客户端共用的数据传输线程
 *
 * @author bingbing
 * @date 16/4/6
 */
public class BlueDataThread extends BlueSocketBaseThread {


    private BluetoothSocket mBlueSocket;
    private OutputStream mBlueSocketOutputStream;
    private InputStream mBlueSocketInputStream;
    private ConcurrentLinkedQueue<IMessage> mQueue;
    private boolean isSendFinish = true;  //数据是否发送完成
    private SendMessageThread mSendThread;

    public BlueDataThread(ConcurrentLinkedQueue<IMessage> queue, BluetoothSocket bluetoothSocket, Handler handler) {
        super(handler);
        mBlueSocket = bluetoothSocket;
        mQueue = queue;
    }


    @Override
    public void run() {
        super.run();
        if (!isRunning) return;
        try {
            mBlueSocketOutputStream = mBlueSocket.getOutputStream();
            mBlueSocketInputStream = mBlueSocket.getInputStream();
            sendMessage(BlueSocketStatus.CONNEDTIONED, mBlueSocket.getRemoteDevice());
        } catch (IOException e) {
            e.printStackTrace();
            sendMessage(BlueSocketStatus.DISCONNECTION);
            return;
        }

        //监听通道后,死循环监听通道,去读取message ,
        while (isRunning) {
            try {
                IMessage message = TypeUtils.readHeader(mBlueSocketInputStream);
                if (message != null){
                    message.parseContent(mBlueSocketInputStream);
                    sendMessage(BlueSocketStatus.MESSAGERECEIVE, message);
                    Log.d("BLUE","完成");
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendMessage(BlueSocketStatus.DISCONNECTION);
                return;
            }

        }
    }


    /**
     * 开始消息队列
     */
    public synchronized void startQueue() {
        if (mQueue != null && !mQueue.isEmpty() && isSendFinish) {
            if (mSendThread != null) {
                mSendThread.cancle();
            }
            mSendThread = new SendMessageThread();
            mSendThread.start();
        }

    }


    @Override
    public BluetoothSocket getSocket() {
        return null;
    }

    @Override
    public void cancle() {
        super.cancle();

        if (mSendThread != null) {
            mSendThread.cancle();
            mSendThread = null;
            mQueue = null;
        }
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

    private class SendMessageThread extends Thread {
        private boolean isCancle = false;

        @Override
        public void run() {
            super.run();
            isSendFinish = false;
            while (mQueue != null && !mQueue.isEmpty() && !isCancle) {
                try {
                    Log.d("BlueDataThread", "开始发消息给客户端");
                    IMessage messageObject = mQueue.poll();
                    messageObject.writeContent(mBlueSocketOutputStream);
                    Log.d("BlueDataThread", "发消息给客户端成功");
                } catch (IOException e) {
                    e.printStackTrace();
                    sendMessage(BlueSocketStatus.DISCONNECTION);
                    isCancle = true;
                }
                SystemClock.sleep(50);
            }
            isSendFinish = true;
        }

        public void cancle() {
            isCancle = true;
        }


    }
}
