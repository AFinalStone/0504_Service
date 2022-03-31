package com.afs.rethinkingservice.maidl;

import android.util.Log;

/**
 * Local-side IPC implementation stub class.
 */
public abstract class Stub extends android.os.Binder implements com.afs.rethinkingservice.maidl.MainAidlService {
    public static final java.lang.String DESCRIPTOR = "com.afs.rethinkingservice.maidl.MainAidlService";
    public static final String TAG = "Stub=========";

    /**
     * Construct the stub at attach it to the interface.
     */
    public Stub() {
        this.attachInterface(this, DESCRIPTOR);
    }

    /**
     * Cast an IBinder object into an com.afs.rethinkingservice.maidl.MainAidlService interface,
     * generating a proxy if needed.
     */
    public static com.afs.rethinkingservice.maidl.MainAidlService asInterface(android.os.IBinder obj) {
        if ((obj == null)) {
            return null;
        }
        android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
        if (iin == null) {
            Log.d(TAG, "iin的类型==================null");
        } else {
            Log.d(TAG, "iin的类型==================" + iin.getClass());
        }
        if (((iin != null) && (iin instanceof com.afs.rethinkingservice.maidl.MainAidlService))) {
            return ((com.afs.rethinkingservice.maidl.MainAidlService) iin);
        }
        return new Proxy(obj);
    }

    @Override
    public android.os.IBinder asBinder() {
        return this;
    }

    @Override
    public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
        java.lang.String descriptor = DESCRIPTOR;
        switch (code) {
            case INTERFACE_TRANSACTION: {
                reply.writeString(descriptor);
                return true;
            }
            case TRANSACTION_plus: {
                data.enforceInterface(descriptor);
                int _arg0;
                _arg0 = data.readInt();
                int _arg1;
                _arg1 = data.readInt();
                int _result = this.plus(_arg0, _arg1);
                reply.writeNoException();
                reply.writeInt(_result);
                return true;
            }
            case TRANSACTION_toUpperCase: {
                data.enforceInterface(descriptor);
                java.lang.String _arg0;
                _arg0 = data.readString();
                java.lang.String _result = this.toUpperCase(_arg0);
                reply.writeNoException();
                reply.writeString(_result);
                return true;
            }
            default: {
                return super.onTransact(code, data, reply, flags);
            }
        }
    }


    static final int TRANSACTION_plus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_toUpperCase = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);

    public static boolean setDefaultImpl(com.afs.rethinkingservice.maidl.MainAidlService impl) {
        // Only one user of this interface can use this function
        // at a time. This is a heuristic to detect if two different
        // users in the same process use this function.
        if (Proxy.sDefaultImpl != null) {
            throw new IllegalStateException("setDefaultImpl() called twice");
        }
        if (impl != null) {
            Proxy.sDefaultImpl = impl;
            return true;
        }
        return false;
    }

    public static MainAidlService getDefaultImpl() {
        return Proxy.sDefaultImpl;
    }
}