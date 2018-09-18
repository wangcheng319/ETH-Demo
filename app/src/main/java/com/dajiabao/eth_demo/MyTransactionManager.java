package com.dajiabao.eth_demo;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.tx.TransactionManager;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by wangc on 2018/9/17
 * E-MAIL:274281610@QQ.COM
 */
public class MyTransactionManager extends TransactionManager {


    public MyTransactionManager(Web3j web3j, String fromAddress) {
        super(web3j, fromAddress);
    }

    @Override
    public EthSendTransaction sendTransaction(BigInteger gasPrice, BigInteger gasLimit, String to, String data, BigInteger value) throws IOException {
        return null;
    }
}
