package com.dajiabao.eth_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.utils.Numeric;

public class PrivateKeyImportActivity extends AppCompatActivity {
    private TextView mTvMsg;
    private EditText mEdPrivateKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_key_import);

        mEdPrivateKey = findViewById(R.id.ed_private_key);
        mTvMsg = findViewById(R.id.tv_msg);

        findViewById(R.id.btn_import).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String privateKey = mEdPrivateKey.getText().toString().trim();
                importPrivateKey(privateKey);
            }
        });
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

        Log.e("+++", "daoru:" + msg);
        mTvMsg.setText(msg);
    }

}
