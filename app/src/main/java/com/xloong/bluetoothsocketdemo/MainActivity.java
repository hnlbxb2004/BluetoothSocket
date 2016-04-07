package com.xloong.bluetoothsocketdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void client(View view){
        startActivity(new Intent(this, ClientActivity.class));
    }

    public void service(View view){
        startActivity(new Intent(this,ServiceActivity.class));
    }
}
