package com.dajiabao.eth_demo;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.utils.Numeric;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;

import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.Words;
import io.github.novacrypto.bip39.wordlists.English;

import static org.web3j.crypto.Hash.sha256;
import static org.web3j.crypto.WalletUtils.generateWalletFile;

public class MnemonicActivity extends AppCompatActivity {

    EditText mEdPasswd;
    TextView mTvMsg;
    String password;
    String keystore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mnemonic);
        mEdPasswd = findViewById(R.id.ed_passwd);
        mTvMsg = findViewById(R.id.tv_msg);

        findViewById(R.id.btn_gen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = mEdPasswd.getText().toString().trim();
                genMemoryWordWallet();
            }
        });
    }

    /**
     *生成带助记词和keystore的钱包
     */
    private void genMemoryWordWallet() {
        File fileDir = new File(Environment.getExternalStorageDirectory().getPath() + "/LightWallet");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        StringBuilder sb = new StringBuilder();
        byte[] entropy = new byte[Words.TWELVE.byteLength()];
        new SecureRandom().nextBytes(entropy);
        new MnemonicGenerator(English.INSTANCE).createMnemonic(entropy, sb::append);
        String mnemonics = sb.toString();
        android.util.Log.e("+++","生成的助记词："+mnemonics);

        byte[] seed = MnemonicUtils.generateSeed(mnemonics, password);
        ECKeyPair privateKey = ECKeyPair.create(sha256(seed));

        String walletFile = null;
        try {
            walletFile = generateWalletFile(password, privateKey, fileDir, false);
        } catch (CipherException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bip39Wallet bip39Wallet = new Bip39Wallet(walletFile, mnemonics);

        Log.e("+++",bip39Wallet.getFilename());

        keystore = getDatafromFile(new File(Environment.getExternalStorageDirectory().getPath() + "/LightWallet/"+bip39Wallet.getFilename()).getAbsolutePath());
        Log.e("+++","keystore:"+keystore);

        //导入keystore获取到钱包地址
        //keyStoreImport();

        //导入助记词获取钱包地址
        Credentials credentials = WalletUtils.loadBip39Credentials(password,bip39Wallet.getMnemonic());
        Log.e("+++","导入助记词获取钱包地址:"+credentials.getAddress());

        String msg = "\n助记词:\n" + bip39Wallet.getMnemonic()
                +"\naddress:\n" + credentials.getAddress()
                + "\nprivateKey:\n" + Numeric.encodeQuantity(credentials.getEcKeyPair().getPrivateKey())
                + "\nPublicKey:\n" + Numeric.encodeQuantity(credentials.getEcKeyPair().getPublicKey());

        mTvMsg.setText(msg);
        Log.e("+++",msg);

        MyApplication.wallets.add(credentials.getAddress());

    }

    /**
     * keystore导入钱包
     */
    public void keyStoreImport(){
        ObjectMapper objectMapper = new ObjectMapper();
        //未知属性报错
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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

    /**
     * 读取keystore
     * @param fileName
     * @return
     */
    private String getDatafromFile(String fileName) {
        BufferedReader reader = null;
        String laststr = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return laststr;
    }
}
