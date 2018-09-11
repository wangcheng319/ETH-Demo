package com.dajiabao.eth_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class KeyStoreImportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_store_import);

        findViewById(R.id.btn_import).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyStoreImport();
            }
        });
    }

    /**
     * keystore导入钱包
     */

    public void keyStoreImport(){

        String password ="123456789";
        String keystore = "{\"address\":\"8f530ace9700ec4522410e994564816592756d53\",\"id\":\"c23cf728-1249-449c-956c-45f0d17e0830\",\"version\":3,\"crypto\":{\"cipher\":\"aes-128-ctr\",\"cipherparams\":{\"iv\":\"587fe1dd86714bb14a4d7b2651f7f1dc\"},\"ciphertext\":\"48aa70a4ae4174649834ad576ff168b0e3ff82660d7ba1f51286fa2a3076b9ad\",\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":4096,\"p\":6,\"r\":8,\"salt\":\"b5832fe1e5f5ecf74e3ac5a07e87bd97dbd008ef9b30292c794195ac98923fd7\"},\"mac\":\"f22d9de793ca895d3ba008124cd538f97c28349316a223c12d530e28466ff59b\"}}";
        ObjectMapper objectMapper = new ObjectMapper();
        WalletFile walletFile = null;
        try {
            walletFile = objectMapper.readValue(keystore, WalletFile.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ECKeyPair keyPair = Wallet.decrypt(password, walletFile);
            WalletFile generateWalletFile = Wallet.createLight(password, keyPair);
            Log.e("+++","keyStoreImportAddress:"+generateWalletFile.getAddress());
        } catch (CipherException e) {
            e.printStackTrace();
        }
    }
}
