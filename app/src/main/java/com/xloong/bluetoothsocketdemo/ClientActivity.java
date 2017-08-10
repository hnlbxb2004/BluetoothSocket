package com.xloong.bluetoothsocketdemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xloong.library.bluesocket.BlueSocketBaseThread;
import com.xloong.library.bluesocket.BluetoothSppHelper;
import com.xloong.library.bluesocket.message.IMessage;
import com.xloong.library.bluesocket.message.ImageMessage;
import com.xloong.library.bluesocket.message.StringMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bingbing
 * @date 16/4/7
 */
public class ClientActivity extends BaseActivity implements BluetoothSppHelper.BlueSocketListener, AdapterView.OnItemClickListener {

    private EditText mEdit;
    private TextView mConnectionStatus;
    private ListView mList;
    private BluetoothSppHelper mHelper;
    private List<BluetoothDevice> devices = new ArrayList<>();
    private BaseAdapter mBlueAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        mEdit = (EditText) findViewById(R.id.edit);
        mList = (ListView) findViewById(R.id.list);
        mConnectionStatus = (TextView) findViewById(R.id.text);
        mHelper = new BluetoothSppHelper();
        mHelper.setBlueSocketListener(this);
        devices.addAll(BluetoothAdapter.getDefaultAdapter().getBondedDevices());
        mBlueAdapter = new MyAdapter();
        mList.setAdapter(mBlueAdapter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, intentFilter);
        mList.setOnItemClickListener(this);
    }

    public void find(View view) {
        BluetoothAdapter.getDefaultAdapter().startDiscovery();
    }

    public void send(View view) {
//        mHelper.write(mEdit.getText().toString());
    }


    public void sendText(View view) {
        StringMessage message = new StringMessage();
        message.setContent("我是Client内容","扩展信息");
        mHelper.write(message);
    }

    public void sendImage(View view) {
        chooseImage();
    }


    @Override
    public void onBlueSocketStatusChange(BlueSocketBaseThread.BlueSocketStatus status, BluetoothDevice device) {
        mConnectionStatus.setText(status.toString());
    }

    @Override
    public void onBlueSocketMessageReceiver(IMessage message) {
        if (message instanceof StringMessage){
            Toast.makeText(this, ((StringMessage)message).getContent(), Toast.LENGTH_SHORT).show();
        }else if (message instanceof ImageMessage){
            Toast.makeText(this, ((ImageMessage)message).getContent().getAbsolutePath(), Toast.LENGTH_SHORT).show();
        }

    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            devices.add((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
            mBlueAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHelper.stop();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        mHelper.connect(devices.get(position));
    }

    @Override
    public void onChooseImage(String path) {
        File file = new File(path);
        ImageMessage message = new ImageMessage();
        message.setContent(new File(path),file.getName());
        mHelper.write(message);
    }


    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView text = new TextView(parent.getContext());
            text.setText(devices.get(position).getName());
            return text;
        }
    }
}
