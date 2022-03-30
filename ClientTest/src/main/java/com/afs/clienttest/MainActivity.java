package com.afs.clienttest;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.afs.rethinkingservice.maidl.MainAIDLService;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Client_Main=========";

    private MainAIDLService mBinder;

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected() executed");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected() executed");
            mBinder = MainAIDLService.Stub.asInterface(service);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_start_service).setOnClickListener(v -> {
            Log.d(TAG, "btn_start_service click");
            Intent intent = new Intent("com.afs.rethinkingservice.maidl.MainAIDLService");
            intent.setPackage("com.afs.rethinkingservice02");
            startService(intent);
        });
        findViewById(R.id.btn_bind_service).setOnClickListener(v -> {
            Log.d(TAG, "btn_bind_service click");
            Intent intent = new Intent("com.afs.rethinkingservice.maidl.MainAIDLService");
            intent.setPackage("com.afs.rethinkingservice02");
            bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
        });
        findViewById(R.id.btn_test_aidl_method).setOnClickListener(v -> {
            try {
                Log.d(TAG, "btn_test_aidl_method click");
                int total = mBinder.plus(1, 1);
                Log.d(TAG, "total === " + total);
                String newString = mBinder.toUpperCase("aaabbbcccddd");
                Log.d(TAG, "newString === " + newString);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }
}