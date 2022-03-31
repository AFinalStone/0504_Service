package com.afs.rethinkingservice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity=========";
    private IBinder mBinder;

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected() executed");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected() executed");
            mBinder = service;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate() executed");
        Log.d(TAG, "onCreate() executed Thread id is " + Thread.currentThread().getId());
        Log.d(TAG, "onCreate() executed process id is " + Process.myPid());
        findViewById(R.id.btn_start_service).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainService.class);
            startService(intent);
        });
        findViewById(R.id.btn_bind_service).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainService.class);
            bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
        });
        findViewById(R.id.btn_stop_service).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainService.class);
            stopService(intent);
        });
        findViewById(R.id.btn_test_aidl_method).setOnClickListener(v -> {
            try {
                String text = "aaabbbcccddd";
                Parcel parcel = Parcel.obtain();
                Parcel replay = Parcel.obtain();
                parcel.writeString(text);
                mBinder.transact(MainService.Request_ToUpperCase, parcel, replay, 0);
                String newText = replay.readString();
                Log.d(TAG, "newString === " + newText);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() executed");
    }
}