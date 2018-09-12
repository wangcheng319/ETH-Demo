package com.dajiabao.eth_demo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class KeyStoreImportActivity extends AppCompatActivity {
    EditText mEdKeyStore;
    EditText mEdPasswd;
    private TextView mTvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_store_import);

        mEdKeyStore = findViewById(R.id.ed_key_store);
        mEdPasswd = findViewById(R.id.ed_passwd);
        mTvMsg = findViewById(R.id.tv_msg);

        findViewById(R.id.btn_import).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyStoreImport();
            }
        });

        findViewById(R.id.btn_get_balance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBalance();
            }
        });
    }

    /**
     * keystore导入钱包
     */

    public void keyStoreImport(){
        String password = mEdPasswd.getText().toString().trim();
        String keystore = mEdKeyStore.getText().toString().trim();
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
            mTvMsg.setText("Address:"+generateWalletFile.getAddress());
            MyApplication.wallets.add(generateWalletFile.getAddress());
        } catch (CipherException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取钱包余额
     */
    private void getBalance() {
        //获取钱包余额
        //address:0xa65791830C8993Ae8Ec1Ab463F33f24EDA02A847
        String address = "0x"+mTvMsg.getText().toString().trim();
        try {
            EtherscanAPI.getInstance().getBalance(address, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String result = response.body().string();
                    Log.e("+++", "yue:" + result);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTvMsg.setText("余额：" + result);
                        }
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onImportWallet() {
        try {
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/keystore.json";
            File file = new File(filePath);
            WalletUtils.loadCredentials("123456", file);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }
    }



}
