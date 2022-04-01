package com.afs.rethinkingservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.afs.rethinkingservice.maidl.ChangeStringImpl;


public class MainService extends Service {

    public static final String TAG = "MainService=========";

    IBinder mBinder = new ChangeStringImpl();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() executed");
        return mBinder;
    }


}