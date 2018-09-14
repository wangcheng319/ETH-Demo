package com.dajiabao.eth_demo;

import android.content.Intent;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.util.Log;
import android.view.View;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthFilter;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetBlockTransactionCountByHash;
import org.web3j.protocol.core.methods.response.EthGetStorageAt;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;

import static org.web3j.tx.Contract.GAS_LIMIT;
import static org.web3j.tx.ManagedTransaction.GAS_PRICE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_new).setOnClickListener(this);
        findViewById(R.id.btn_import_keystore).setOnClickListener(this);
        findViewById(R.id.btn_import_private_key).setOnClickListener(this);
        findViewById(R.id.btn_mine).setOnClickListener(this);
        findViewById(R.id.btn_mnemonic).setOnClickListener(this);
        findViewById(R.id.btn_tran).setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_new:
                startActivity(new Intent(this,CreateWalletActivity.class));
                break;
            case R.id.btn_import_keystore:
                startActivity(new Intent(this,KeyStoreImportActivity.class));
                break;
            case R.id.btn_import_private_key:
                startActivity(new Intent(this, PrivateKeyImportActivity.class));
                break;
            case R.id.btn_mnemonic:
                startActivity(new Intent(this, MnemonicActivity.class));
                break;
            case R.id.btn_mine:
                startActivity(new Intent(this, MyWalletsActivity.class));
                break;
            case R.id.btn_tran:
//                startActivity(new Intent(this, TransactionActivity.class));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        testNetWork();
                    }
                }).start();
                break;
        }
    }

    private void testNetWork() {
        //获取web3j的版本号，判断连接是否成功
        Web3j web3j = Web3JService.getInstance();
        String version = null;
        try {
            version = web3j.web3ClientVersion().send().getWeb3ClientVersion();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("+++",""+version);

        //转入账户
        String toAddress = "0x5b9571487BDa1bEB1b6101d59cf0c3224568E137";
        //根据私钥生成的转出账户
        Credentials credentials = Credentials.create("a98dda1046dad3af3d9034a77202508abca9eb403b5e8325f3be95f97b6db4fd");

        TransactionReceipt transactionReceipt = null;
        try {
            transactionReceipt = Transfer.sendFunds(
                    web3j, credentials, toAddress,
                    BigDecimal.valueOf(1), Convert.Unit.ETHER).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("+++","transactionReceipt:"+transactionReceipt.getTransactionHash());


        //查询余额
        DefaultBlockParameter defaultBlockParameter = new DefaultBlockParameterNumber(13);
        EthGetBalance ethGetBalance = null;
        try {
            ethGetBalance = web3j.ethGetBalance("0x38B4D9fe0aC062AC09Cc7a6FB45Ba9319c6B688e", DefaultBlockParameterName.LATEST).send();
            Log.e("+++","balance:"+Convert.fromWei(ethGetBalance.getBalance().toString(), Convert.Unit.ETHER));

        } catch (IOException e) {
            e.printStackTrace();
        }

        //获取交易详情
        try {
            org.web3j.protocol.core.methods.response.Transaction transactionResult =  web3j.ethGetTransactionByHash(transactionReceipt.getTransactionHash()).send().getResult();
            Log.e("","r"+transactionResult);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Object o  = null;
        try {
            o = web3j.ethGetTransactionReceipt(transactionReceipt.getTransactionHash()).send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("+++","o"+o);


        //获取Block总数
        BigInteger o1 = null;
        try {
           o1 =  web3j.ethBlockNumber().send().getBlockNumber();
          Log.e("+++","o1"+o1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //获取交易记录
        List<EthBlock.TransactionResult> transactionResultList = new ArrayList<>();
        for (int i = 0;i < o1.intValue();i++){
            EthBlock.Block ethblock = null;
            try {
                ethblock = web3j.ethGetBlockByNumber(new DefaultBlockParameterNumber(i), true).send().getBlock();
                transactionResultList.addAll(ethblock.getTransactions()) ;
                Log.e("+++",""+transactionResultList.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //获取交易总数
        try {
         EthGetTransactionCount total = web3j.ethGetTransactionCount("0x38B4D9fe0aC062AC09Cc7a6FB45Ba9319c6B688e",DefaultBlockParameterName.LATEST).send();
            Log.e("+++","total:"+total.getTransactionCount().intValue());
        } catch (IOException e) {
            e.printStackTrace();
        }


        //监听交易
        web3j.transactionObservable().subscribe(new Subscriber<Transaction>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Transaction transaction) {
                Log.e("+++",""+transaction);
            }
        });

        //智能合约
        try {
            MetaCoin_sol_MetaCoin contract = MetaCoin_sol_MetaCoin.deploy(web3j, credentials, GAS_PRICE, GAS_LIMIT).send();
            Log.e("+++","智能合约地址:"+contract.getContractAddress());
            Object o2 = contract.getBalance(toAddress).send();
            Log.e("+++","智能合约余额:"+o2);
            contract.sendCoin(toAddress,new BigInteger("10"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
