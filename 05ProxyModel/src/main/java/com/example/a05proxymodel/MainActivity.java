package com.example.a05proxymodel;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a05proxymodel.dynamic.DynamicBuy01;
import com.example.a05proxymodel.dynamic.DynamicBuy02;
import com.example.a05proxymodel.statics.BuyFoodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    public static String TAG = "MainActivity=======";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_proxy_static).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IBuyFood iBuyFood = new BuyFoodImpl();
                BuyFoodProxy buyFoodProxy = new BuyFoodProxy(iBuyFood);
                buyFoodProxy.buyBanana();
            }
        });

        findViewById(R.id.btn_proxy_dynamic01).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IBuyFood iBuyFood = new BuyFoodImpl();
                DynamicBuy01 dynamicBuy = new DynamicBuy01(iBuyFood);
                ClassLoader classLoader = getClassLoader();
                IBuyFood proxy = (IBuyFood) Proxy.newProxyInstance(classLoader, new Class[]{IBuyFood.class}, dynamicBuy);
                proxy.buyBanana();
            }
        });
        findViewById(R.id.btn_proxy_dynamic02).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IBuyFood iBuyFood = new BuyFoodImpl();
                DynamicBuy02 dynamicBuy = new DynamicBuy02(iBuyFood);
                IBuyFood proxy = dynamicBuy.getProxy();//直接提供了获取实例的方法
                proxy.buyBanana();
                Class clazz = proxy.getClass();
                Log.d(TAG, "动态代理实例对象= " + clazz.getName());
            }
        });

        findViewById(R.id.btn_proxy_dynamic03).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IBuyFood iBuyFood = new BuyFoodImpl();
                DynamicBuy02 dynamicBuy = new DynamicBuy02(iBuyFood);
//                try {
//                    Method method_generateProxy = Proxy.class.getDeclaredMethod("generateProxy", String.class
//                            , Class[].class, ClassLoader.class, Method.class, Class[][].class);
//                    method_generateProxy.setAccessible(true);
//                    Object result =
//                } catch (NoSuchMethodException e) {
//                    e.printStackTrace();
//                }

            }
        });
    }

    private <T> T createInstance(final Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz},
                new InvocationHandler() {
                    public String TAG = "Proxy=======";

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args)
                            throws Throwable {
                        if (method.getName().equals("buyBanana")) {
                            Log.d(TAG, "准备买香蕉....");
                        }
                        Object result = method.invoke(this, args);
                        if (method.getName().equals("buyBanana")) {
                            Log.d(TAG, "香蕉买完了....");
                        }
                        return result;
                    }
                });
    }
}

























