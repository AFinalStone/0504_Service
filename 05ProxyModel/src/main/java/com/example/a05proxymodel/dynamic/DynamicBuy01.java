package com.example.a05proxymodel.dynamic;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 动态代理
 */
public class DynamicBuy01 implements InvocationHandler {
    public static String TAG = "DynamicBuy01=======";
    Object target;

    public DynamicBuy01(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("buyBanana")) {
            before();
        }
        Object result = method.invoke(target, args);
        if (method.getName().equals("buyBanana")) {
            after();
        }
        return result;
    }


    void before() {
        Log.d(TAG, "准备买香蕉....");
    }

    void after() {
        Log.d(TAG, "香蕉买完了....");
    }

}
