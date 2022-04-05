package com.example.a05proxymodel.dynamic;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DynamicBuy implements InvocationHandler {
    public static String TAG = "DynamicBuy=======";
    Object object;

    public DynamicBuy(Object object) {
        this.object = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("buyBanana")) {
            Log.d(TAG, "准备买香蕉....");
        }
        Object result = method.invoke(object, args);
        if (method.getName().equals("buyBanana")) {
            Log.d(TAG, "香蕉买完了....");
        }
        return result;
    }
}
