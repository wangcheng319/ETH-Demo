package com.dajiabao.eth_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

public class TransactionActivity extends AppCompatActivity {

    TextView mTvMsg ;
    String hexValue = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        mTvMsg = findViewById(R.id.tv_msg);

        findViewById(R.id.btn_tran).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tran();
            }
        });


    }

    /**
     *
     */
    private void Tran() {
            //infurakey :b1a395a114ba485586c43d0fa227d443

        new Thread(new Runnable() {
            @Override
            public void run() {

                Web3j web3j = Web3jFactory.build(new HttpService("https://kovan.infura.io/v3/b1a395a114ba485586c43d0fa227d443"));
                try {
                    String version = web3j.web3ClientVersion().send().getWeb3ClientVersion();
                    Log.e("+++",version);
                    // //获取余额
                    DefaultBlockParameter defaultBlockParameter = new DefaultBlockParameterNumber(58);
                    EthGetBalance ethGetBalance =  web3j.ethGetBalance("0x3a9dc7e1c1a7edeb5a43bde06ae9d0d5e4c8fb70",defaultBlockParameter).send();
                    Log.e("+++","balance:"+ethGetBalance.getBalance());



                    String toAddress = "0x6e27727bbb9f0140024a62822f013385f4194999";
                    Credentials credentials = Credentials.create("0x27bae714a2f0976ecc1e57c2a9ffe5673cee1087fce79de21864cf3734d73f0f");

                    TransactionReceipt transactionReceipt = Transfer.sendFunds(
                            web3j, credentials, toAddress,
                            BigDecimal.valueOf(0.2), Convert.Unit.ETHER).send();

                    Log.e("+++","transactionReceipt:"+transactionReceipt.getTransactionHash());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (TransactionException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();


//        try {
//            hexValue = KeyStoreUtils.signedTransactionData(from, to, nonce, gasPrice, gasLimit, value);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                EthSendTransaction send = null;
//                try {
//                    send = web3j.ethSendRawTransaction(hexValue).send();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                mTvMsg.setText("结果："+send);
//            }
//        }).start();

    }
}
