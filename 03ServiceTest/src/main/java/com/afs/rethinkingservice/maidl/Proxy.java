package com.afs.rethinkingservice.maidl;

import static com.afs.rethinkingservice.maidl.Stub.DESCRIPTOR;
import static com.afs.rethinkingservice.maidl.Stub.getDefaultImpl;

public class Proxy implements com.afs.rethinkingservice.maidl.MainAidlService {
    private android.os.IBinder mRemote;

    Proxy(android.os.IBinder remote) {
        mRemote = remote;
    }

    @Override
    public android.os.IBinder asBinder() {
        return mRemote;
    }

    public java.lang.String getInterfaceDescriptor() {
        return DESCRIPTOR;
    }

    @Override
    public int plus(int a, int b) throws android.os.RemoteException {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
            _data.writeInterfaceToken(DESCRIPTOR);
            _data.writeInt(a);
            _data.writeInt(b);
            boolean _status = mRemote.transact(Stub.TRANSACTION_plus, _data, _reply, 0);
            if (!_status && getDefaultImpl() != null) {
                return getDefaultImpl().plus(a, b);
            }
            _reply.readException();
            _result = _reply.readInt();
        } finally {
            _reply.recycle();
            _data.recycle();
        }
        return _result;
    }

    @Override
    public java.lang.String toUpperCase(java.lang.String str) throws android.os.RemoteException {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
            _data.writeInterfaceToken(DESCRIPTOR);
            _data.writeString(str);
            boolean _status = mRemote.transact(Stub.TRANSACTION_toUpperCase, _data, _reply, 0);
            if (!_status && getDefaultImpl() != null) {
                return getDefaultImpl().toUpperCase(str);
            }
            _reply.readException();
            _result = _reply.readString();
        } finally {
            _reply.recycle();
            _data.recycle();
        }
        return _result;
    }

    public static MainAidlService sDefaultImpl;
}