package com.dajiabao.eth_demo;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.http.HttpService;

/**
 * Created by pc on 2018/1/26.
 */

public class Web3JService {
//    private static final   Web3j web3j = Web3jFactory.build(new HttpService("https://kovan.infura.io/v3/b1a395a114ba485586c43d0fa227d443"));

    private static final   Web3j web3j = Web3jFactory.build(new HttpService("http://192.168.1.112:7545"));
    public static Web3j getInstance() {
        return web3j;
    }

    private Web3JService() {
    }


}
