package com.afs.rethinkingservice.maidl;

import android.os.Binder;
import android.os.Parcel;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ChangeStringImpl extends Binder implements ChangeStringInterface {
    public static final int Request_ToUpperCase = 100;

    @Override
    public String toUpperCase(String text) {
        return text.toUpperCase();
    }

    @Override
    protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
        if (Request_ToUpperCase == code) {
            String text = data.readString();
            String newText = toUpperCase(text);
            if (reply != null)
                reply.writeString(newText);
            return true;
        }
        return super.onTransact(code, data, reply, flags);
    }
}
