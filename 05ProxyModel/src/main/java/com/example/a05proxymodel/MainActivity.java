package com.example.a05proxymodel;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a05proxymodel.dynamic.DynamicBuy01;
import com.example.a05proxymodel.statics.BuyFoodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
                Log.d(TAG, "btn_proxy_static = " + buyFoodProxy.getClass().getName());
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
                Log.d(TAG, "btn_proxy_dynamic01 = " + proxy.getClass().getName());
            }
        });
        findViewById(R.id.btn_proxy_dynamic02).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //不创建新的类，直接使用接口创建具体的代理类对象
                IBuyFood iBuyFood = (IBuyFood) Proxy.newProxyInstance(getClassLoader(), new Class<?>[]{IBuyFood.class},
                        new InvocationHandler() {
                            public String TAG = "Proxy=======";

                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args)
                                    throws Throwable {
                                if (method.getName().equals("buyBanana")) {
                                    Log.d(TAG, "准备买香蕉....");
                                    Log.d(TAG, "买香蕉");
                                    Log.d(TAG, "香蕉买完了....");
                                }
                                return null;
                            }
                        });
                iBuyFood.buyBanana();
                Log.d(TAG, "btn_proxy_dynamic02 = " + iBuyFood.getClass().getName());
            }
        });

    }

}

























