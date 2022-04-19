package com.example.a05proxymodel.dynamic;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 动态代理，直接提供获取代理实例的方法
 *
 * @author syl
 * @time 2022/4/19 16:07
 */
public class DynamicBuy02 implements InvocationHandler {
    public static String TAG = "DynamicBuy02=======";
    Object target;

    public DynamicBuy02(Object target) {
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

    public <T> T getProxy() {
        return (T) Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
    }

}
