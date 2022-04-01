package com.afs.rethinkingservice.maidl;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

public class ChangeStringProxy implements ChangeStringInterface {
    public static final String TAG = "Proxy=========";

    private IBinder mBinder;

    private ChangeStringProxy(IBinder binder) {
        this.mBinder = binder;
    }

    // 通过Binde把小写字符串转化为大写字符串
    @Override
    public String toUpperCase(String str) {
        try {
            Parcel parcel = Parcel.obtain();
            Parcel replay = Parcel.obtain();
            parcel.writeString(str);
            mBinder.transact(ChangeStringImpl.Request_ToUpperCase, parcel, replay, 0);
            return replay.readString();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 实例化Binder代理类的对象
    public static ChangeStringInterface asInterface(IBinder binder) {
        if (binder == null) {
            return null;
        }
        if (binder instanceof ChangeStringInterface) {
            Log.d(TAG, "当前进程");
            // 如果是同一个进程的请求，则直接返回Binder
            return (ChangeStringInterface) binder;
        } else {
            Log.d(TAG, "远程进程");
            // 如果是跨进程查询则返回Binder的代理对象
            return new ChangeStringProxy(binder);
        }
    }

}
