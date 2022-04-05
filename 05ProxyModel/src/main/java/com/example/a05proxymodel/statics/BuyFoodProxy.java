package com.example.a05proxymodel.statics;

import android.util.Log;

import com.example.a05proxymodel.IBuyFood;

public class BuyFoodProxy implements IBuyFood {
    public static String TAG = "Proxy=======";
    IBuyFood iBuyFood;

    public BuyFoodProxy(IBuyFood iBuyFood) {
        this.iBuyFood = iBuyFood;
    }

    @Override
    public void buyBanana() {
        Log.d(TAG, "准备买香蕉....");
        iBuyFood.buyBanana();
        Log.d(TAG, "香蕉买完了....");
    }
}
