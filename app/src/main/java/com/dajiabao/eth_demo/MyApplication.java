package com.dajiabao.eth_demo;

import android.app.Application;

import org.web3j.protocol.Web3jService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangc on 2018/9/12
 * E-MAIL:274281610@QQ.COM
 */
public class MyApplication extends Application {

    public static List<String> wallets = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Web3JService.getInstance();
    }
}
