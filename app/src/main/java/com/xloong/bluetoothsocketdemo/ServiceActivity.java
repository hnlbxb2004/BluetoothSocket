package com.xloong.bluetoothsocketdemo;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xloong.library.bluesocket.BlueSocketBaseThread;
import com.xloong.library.bluesocket.BluetoothSocketHelper;

/**
 * @author bingbing
 * @date 16/4/7
 */
public class ServiceActivity extends Activity implements BluetoothSocketHelper.BlueSocketListener {

    private EditText mEdit;
    private BluetoothSocketHelper mHelper;
    private TextView mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        mEdit = (EditText) findViewById(R.id.edit);
        mStatus = (TextView) findViewById(R.id.text);
        mHelper = new BluetoothSocketHelper();
        mHelper.setBlueSocketListener(this);
        mHelper.strat();
    }


    public void send(View view){
        mHelper.write(mEdit.getText().toString());
    }

    @Override
    public void onBlueSocketStatusChange(BlueSocketBaseThread.BlueSocketStatus status, BluetoothDevice remoutDevice) {
        mStatus.setText(status.toString());
    }

    @Override
    public void onBlueSocketMessageReceiver(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHelper.stop();
    }
}
