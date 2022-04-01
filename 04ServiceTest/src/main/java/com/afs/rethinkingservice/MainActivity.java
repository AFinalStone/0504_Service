package com.afs.rethinkingservice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.afs.rethinkingservice.maidl.ChangeStringInterface;
import com.afs.rethinkingservice.maidl.ChangeStringProxy;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity=========";

    private ChangeStringInterface mBinder;// 远程服务的Binder

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected() executed");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected() executed");
            mBinder = ChangeStringProxy.asInterface(service);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_bind_service).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainService.class);
            bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
        });
        findViewById(R.id.change_text_to_uppercase).setOnClickListener(v -> {
            String text = "aaabbbcccddd";
            String newText = mBinder.toUpperCase(text);
            Log.d(TAG, "newString === " + newText);
        });
    }

}