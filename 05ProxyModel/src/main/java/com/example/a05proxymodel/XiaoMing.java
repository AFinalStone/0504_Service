package com.example.a05proxymodel;

import android.util.Log;

public class XiaoMing implements IBuyFood {
    public static String TAG = "XiaoMing=======";

    @Override
    public void buyBanana() {
        Log.d(TAG, "买香蕉");
    }
}
