package com.dajiabao.eth_demo;

import android.os.Environment;
import android.provider.UserDictionary;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.AdminFactory;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * autour: wangc
 * date: 2018/9/11 11:14
 * 创建钱包
*/
public class CreateWalletActivity extends AppCompatActivity {
    EditText mEdName;
    EditText mEdPasswd;
    TextView mTvBalance;

    String pwd;
    String address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);

        mEdName = findViewById(R.id.ed_name);
        mEdPasswd = findViewById(R.id.ed_passwd);
        mTvBalance = findViewById(R.id.tv_balance);


        String name = mEdName.getText().toString().trim();


        findViewById(R.id.btn_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gen();
            }
        });

        findViewById(R.id.btn_get_balance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBalance();
            }
        });

        findViewById(R.id.btn_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRecord();
            }
        });

    }


    /**
     * 生成钱包
     */
    private void gen() {
        pwd = mEdPasswd.getText().toString().trim();
        try {
            File fileDir = new File(Environment.getExternalStorageDirectory().getPath() + "/LightWallet");
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            ECKeyPair ecKeyPair = Keys.createEcKeyPair();
            //keystore文件名
            String filename = WalletUtils.generateWalletFile(pwd, ecKeyPair, fileDir, false);
            //获取keystore内容
            File KeyStore = new File(Environment.getExternalStorageDirectory().getPath() + "/LightWallet/" + filename);
            Log.e("+++","keystore:"+getDatafromFile(KeyStore.getAbsolutePath()));

            String msg = "fileName:\n" + filename
                    + "\nprivateKey:\n" + Numeric.encodeQuantity(ecKeyPair.getPrivateKey())
                    + "\nPublicKey:\n" + Numeric.encodeQuantity(ecKeyPair.getPublicKey());
            Log.e("+++", "create:" + msg);
            //根据私钥导入,用于获取钱包地址
            importPrivateKey(Numeric.encodeQuantity(ecKeyPair.getPrivateKey()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 私钥导入
     * @param privateKey
     */
    private void importPrivateKey(String privateKey) {
        Credentials credentials = Credentials.create(privateKey);
        ECKeyPair ecKeyPair = credentials.getEcKeyPair();
        KeyStoreUtils.genKeyStore2Files(ecKeyPair);
        String msg = "address:\n" + credentials.getAddress()
                + "\nprivateKey:\n" + Numeric.encodeQuantity(ecKeyPair.getPrivateKey())
                + "\nPublicKey:\n" + Numeric.encodeQuantity(ecKeyPair.getPublicKey());

        Log.e("+++", "privateKeyImport:" + msg);
        address = credentials.getAddress();

        MyApplication.wallets.add(address);

    }

    /**
     * 获取钱包余额
     */
    private void getBalance() {
        //获取钱包余额
        //address:0xa65791830C8993Ae8Ec1Ab463F33f24EDA02A847
        address = "0xa65791830C8993Ae8Ec1Ab463F33f24EDA02A847";
        try {
            EtherscanAPI.getInstance().getBalance(address, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String result = response.body().string();
                    Log.e("+++", "Balance:" + result);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTvBalance.setText("余额：" + result);
                        }
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取交易记录
     */
    private void getRecord() {
        address = "0xa65791830C8993Ae8Ec1Ab463F33f24EDA02A847";
        try {
            EtherscanAPI.getInstance().getNormalTransactions(address, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String result = response.body().string();
                    Log.e("+++", "TranRecord:" + result);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTvBalance.setText("交易记录：" + result);
                        }
                    });
                }
            },false);
        } catch (IOException e) {
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
