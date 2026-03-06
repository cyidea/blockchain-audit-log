package com.auditlog;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.utils.Numeric;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import java.math.BigInteger;

public class BlockchainAnchor {

    private Web3j web3j;
    private Credentials credentials;

    // Gas settings for Sepolia testnet
    private static final BigInteger GAS_LIMIT = BigInteger.valueOf(50000);
    private static final BigInteger GAS_PRICE = BigInteger.valueOf(20_000_000_000L);

    public BlockchainAnchor(String infuraUrl, String privateKey) {
        this.web3j = Web3j.build(new HttpService(infuraUrl));
        this.credentials = Credentials.create(privateKey);
        System.out.println("🔐 Wallet Address: " + credentials.getAddress());
    }

    public String anchorHash(String hash) throws Exception {

        // Step 1: Get the nonce (transaction count) for our wallet
        EthGetTransactionCount ethGetTransactionCount = web3j
                .ethGetTransactionCount(
                        credentials.getAddress(),
                        DefaultBlockParameterName.LATEST)
                .send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        System.out.println("📋 Nonce: " + nonce);

        // Step 2: Convert hash string to bytes and embed in transaction data
        String data = "0x" + hash;

        // Step 3: Build the raw transaction
        // We send 0 ETH to ourselves - we just want to store the hash in the data field
        RawTransaction rawTransaction = RawTransaction.createTransaction(
                nonce,
                GAS_PRICE,
                GAS_LIMIT,
                credentials.getAddress(), // sending to ourselves
                BigInteger.ZERO,          // 0 ETH value
                data                      // our audit hash
        );

        // Step 4: Sign the transaction with our private key
        byte[] signedMessage = TransactionEncoder.signMessage(
                rawTransaction,
                credentials
        );
        String hexValue = Numeric.toHexString(signedMessage);

        // Step 5: Send it to the blockchain!
        EthSendTransaction ethSendTransaction = web3j
                .ethSendRawTransaction(hexValue)
                .send();

        if (ethSendTransaction.hasError()) {
            throw new Exception("Transaction error: " +
                    ethSendTransaction.getError().getMessage());
        }

        String transactionHash = ethSendTransaction.getTransactionHash();
        System.out.println("⛓️  Transaction Hash: " + transactionHash);
        System.out.println("🔍 View on Etherscan: https://sepolia.etherscan.io/tx/" + transactionHash);

        return transactionHash;
    }
}