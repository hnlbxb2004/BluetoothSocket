package com.xloong.library.bluesocket;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import com.xloong.library.bluesocket.message.IMessage;

/**
 * Created by xubingbing on 2017/8/10.
 */

public class BlueService extends Service implements BluetoothSppHelper.BlueSocketListener {

    public static final String ACTION_MESSAGE_REVEIVER = "com.xloong.library.bluesocket.message.receiver";
    public static final String ACTION_MESSAGE_SEND = "com.xloong.library.bluesocket.message.send";
    public static final int WHAT_CONNECT = 1;
    public static final int WHAT_START = 2;
    private BluetoothSppHelper mHelper;
    private boolean isService;
    private BluetoothDevice mDevice;

    private boolean isConnection = false;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case WHAT_CONNECT:
                    isService = false;
                    mDevice = (BluetoothDevice)msg.obj;
                    mHelper.connect(mDevice);
                    break;

                case WHAT_START:
                    isService = true;
                    mHelper.strat();
                    break;
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        initReceiver();
        mHelper = new BluetoothSppHelper();
        mHelper.setBlueSocketListener(this);

    }

    @Override
    public void onBlueSocketStatusChange(BlueSocketBaseThread.BlueSocketStatus status, BluetoothDevice remoteDevice) {
        if (status == BlueSocketBaseThread.BlueSocketStatus.DISCONNECTION){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isService){
                        mHelper.strat();
                    }else {
                        if (mDevice != null){
                            mHelper.connect(mDevice);
                        }
                    }
                }
            },1000);
        }

        if (status == BlueSocketBaseThread.BlueSocketStatus.CONNEDTIONED){
            isConnection = true;
        }else {
            isConnection = false;
        }
    }

    @Override
    public void onBlueSocketMessageReceiver(IMessage message) {
        Intent intent = new Intent(ACTION_MESSAGE_REVEIVER);
        intent.putExtra("message",message);
        sendBroadcast(intent);
    }


    private void initReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_MESSAGE_SEND);
        registerReceiver(mReceiver,intentFilter);
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case ACTION_MESSAGE_SEND:
                    if (mHelper != null){
                        IMessage message = intent.getParcelableExtra("message");
                        mHelper.write(message);
                    }
                    break;
            }
        }
    };


    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }


    private IBluetooth.Stub mBinder = new IBluetooth.Stub() {
        @Override
        public boolean isConnected() throws RemoteException {
            return isConnection;
        }

        @Override
        public void startService() throws RemoteException {
            mHandler.obtainMessage(WHAT_START).sendToTarget();
        }

        @Override
        public void connectionService(BluetoothDevice device) throws RemoteException {
            mHandler.obtainMessage(WHAT_CONNECT,device).sendToTarget();
        }
    };
}
