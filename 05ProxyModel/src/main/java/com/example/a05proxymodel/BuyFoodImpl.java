package com.example.a05proxymodel;

import android.util.Log;

public class BuyFoodImpl implements IBuyFood {
    public static String TAG = "XiaoMing=======";

    @Override
    public void buyBanana() {
        Log.d(TAG, "买香蕉");
    }
}
