package com.dajiabao.eth_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import rx.Subscriber;

import static org.web3j.tx.Contract.GAS_LIMIT;
import static org.web3j.tx.ManagedTransaction.GAS_PRICE;

public class TransactionActivity extends AppCompatActivity {

    TextView mTvMsg ;
    String hexValue = null;
    Web3j web3j;
    Credentials credentials;
    String fromAddress;
    String toAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        mTvMsg = findViewById(R.id.tv_msg);

        Executors.newCachedThreadPool().submit(()->initWeb3j());

        findViewById(R.id.btn_tran).setOnClickListener(v-> {
                Executors.newCachedThreadPool().submit(() -> testNetWork());
        });
        
        findViewById(R.id.btn_tran1).setOnClickListener(v-> {
                try {
                    tran1();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
            }
        });

        findViewById(R.id.btn_other).setOnClickListener(v -> {
            Executors.newCachedThreadPool().submit(()->otherTran());
        });
    }


    /**
     * 转账参数初始化
     */
    private void initWeb3j() {
        //获取web3j的版本号，判断连接是否成功
        web3j = Web3JService.getInstance();
        String version = null;
        try {
            version = web3j.web3ClientVersion().send().getWeb3ClientVersion();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("+++",""+version);

        //转入账户
        toAddress = "0x5b9571487BDa1bEB1b6101d59cf0c3224568E137";
        //转出账户
        fromAddress = "0x38B4D9fe0aC062AC09Cc7a6FB45Ba9319c6B688e";
        //根据私钥生成的转出账户
        credentials = Credentials.create("a98dda1046dad3af3d9034a77202508abca9eb403b5e8325f3be95f97b6db4fd");

    }

    /**
     * 以太坊转账Ganache本地环境
     */
    private void testNetWork() {
        /****************** 转账方式1 *****************************/
        TransactionReceipt transactionReceipt = null;
        try {
            transactionReceipt = Transfer.sendFunds(
                    web3j, credentials, toAddress,
                    BigDecimal.valueOf(1), Convert.Unit.ETHER).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("+++","transactionReceipt1:"+transactionReceipt.getTransactionHash());

        /****************** 转账方式2 *****************************/

        EthGetTransactionCount ethGetTransactionCount = null;
        try {
            ethGetTransactionCount = web3j.ethGetTransactionCount(
                    fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        BigInteger nonce = ethGetTransactionCount.getTransactionCount();

        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                nonce, Convert.toWei("18", Convert.Unit.GWEI).toBigInteger(),
                Convert.toWei("45000", Convert.Unit.WEI).toBigInteger(), toAddress, new BigInteger("3000000000000000000"));
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        EthSendTransaction ethSendTransaction = null;
        try {
            ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (ethSendTransaction.hasError()) {
            Log.e("+++transfer error:", ethSendTransaction.getError().getMessage());
        } else {
            String transactionHash = ethSendTransaction.getTransactionHash();
            Log.e("+++transactionHash:", ""+ transactionHash);
        }


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
            Log.e("+++","交易详情："+transactionResult.getFrom());
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


    /**
     * 代币转账
     */
    private void tran1() throws ExecutionException, InterruptedException {


        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get();

        BigInteger nonce = ethGetTransactionCount.getTransactionCount();

        Function function = new Function(
                "transfer",
                Arrays.asList(new Address(toAddress), new Uint256(new BigInteger("3"))),
                Arrays.asList(new TypeReference<Type>() {
                }));

        String encodedFunction = FunctionEncoder.encode(function);

        //contractAddress为代币的合约地址
        String contractAddress = "";
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, Convert.toWei("18", Convert.Unit.GWEI).toBigInteger(),
                Convert.toWei("100000", Convert.Unit.WEI).toBigInteger(), contractAddress, encodedFunction);

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        Log.e("+++","transfer hexValue:" + hexValue);

        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        if (ethSendTransaction.hasError()) {
            Log.e("+++","transfer error:"+ ethSendTransaction.getError().getMessage());

        } else {
            String transactionHash = ethSendTransaction.getTransactionHash();
            Log.e("+++","Transfer transactionHash:" + transactionHash);

        }
    }


    /**
     * 第三方私链转账
     */
    private void otherTran() {
        Web3j web3j = Web3jFactory.build(new HttpService("https://ropsten.infura.io/v3/b1a395a114ba485586c43d0fa227d443"));
        String formAddress = "0x36A76b81cBfe5A9EC362dD8dCF84090659c41e0A";
        String toAddress = "0x2B3e66B96924a170c4367e564C6638F28a620110";
        Credentials credentials1  = Credentials.create("D3F293CC53D86F1B93A16E873FEAD44BA14F0E50987719307318A21A0A7C21D1");

        TransactionReceipt transactionReceipt = null;
        try {
            transactionReceipt = Transfer.sendFunds(
                    web3j, credentials1, toAddress,
                    BigDecimal.valueOf(0.01), Convert.Unit.ETHER).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("+++","第三方私链转账:"+transactionReceipt.getTransactionHash());
    }
}
