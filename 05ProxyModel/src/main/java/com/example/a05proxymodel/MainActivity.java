package com.example.a05proxymodel;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a05proxymodel.dynamic.DynamicBuy;
import com.example.a05proxymodel.statics.BuyFoodProxy;

import java.lang.reflect.Proxy;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_proxy_static).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IBuyFood iBuyFood = new XiaoMing();
                BuyFoodProxy buyFoodProxy = new BuyFoodProxy(iBuyFood);
                buyFoodProxy.buyBanana();
            }
        });

        findViewById(R.id.btn_proxy_dynamic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IBuyFood iBuyFood = new XiaoMing();
                DynamicBuy dynamicBuy = new DynamicBuy(iBuyFood);
                ClassLoader classLoader = getClassLoader();
                IBuyFood proxy = (IBuyFood) Proxy.newProxyInstance(classLoader, new Class[]{IBuyFood.class}, dynamicBuy);
                proxy.buyBanana();
            }
        });
    }
}