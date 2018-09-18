package com.dajiabao.eth_demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import rx.Subscriber;

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
                startActivity(new Intent(this, TransactionActivity.class));
                break;
        }
    }

}
