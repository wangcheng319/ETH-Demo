package com.dajiabao.eth_demo;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.WalletUtils;

import java.security.PrivateKey;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_new).setOnClickListener(this);
        findViewById(R.id.btn_import_keystore).setOnClickListener(this);
        findViewById(R.id.btn_import_private_key).setOnClickListener(this);
        findViewById(R.id.btn_mine).setOnClickListener(this);

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
            case R.id.btn_mine:
                byte[] initialEntropy = new byte[2];
                Log.e("+++",MnemonicUtils.generateMnemonic(initialEntropy));
                break;
        }
    }
}
