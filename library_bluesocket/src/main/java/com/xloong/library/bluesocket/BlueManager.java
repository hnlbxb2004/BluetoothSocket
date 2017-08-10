package com.xloong.library.bluesocket;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.xloong.library.bluesocket.message.IMessage;

/**
 * 蓝牙的管理类。
 * 用于连接蓝牙，发送消息等。
 * Created by xubingbing on 2017/8/10.
 */
public class BlueManager {

    private static BlueManager mManager;
    private Context mContext;
    private IBluetooth mProxyService;
    private BlueManager(Context context){
        mContext = context;
        initService();
    }

    public static void init(Context context){
        if (mManager == null){
            synchronized (BlueManager.class){
                if (mManager == null){
                    mManager = new BlueManager(context);
                }
            }
        }
    }

    public static BlueManager getInstance(){
        return mManager;
    }

    private void initService(){
        Intent intent = new Intent(mContext,BlueService.class);

        mContext.bindService(intent,mConnection,Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mProxyService = IBluetooth.Stub.asInterface(service);
            try {
                mProxyService.asBinder().linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mProxyService = null;
        }
    };


    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            mProxyService.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mProxyService = null;
            initService();
        }
    };


    public boolean isConnection(){
        if (mProxyService != null){
            try {
                return mProxyService.isConnected();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * 启动蓝牙Service 端，此方法用于Service 端
     */
    public void startService(){
        if (mProxyService != null){
            try {
                mProxyService.startService();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 连接蓝牙 此方法用于Client  端
     * @param device
     */
    public void connection(BluetoothDevice device){
        if (mProxyService != null){
            try {
                mProxyService.connectionService(device);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 发送消息
     * @param message
     */
    public void sendMessage(IMessage message){
        Intent intent = new Intent(BlueService.ACTION_MESSAGE_SEND);
        intent.putExtra("message",message);
        mContext.sendBroadcast(intent);
    }


}
