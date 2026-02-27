package com.auditlog;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import java.math.BigInteger;
import io.github.cdimascio.dotenv.Dotenv;

public class App {
    public static void main(String[] args) throws Exception {

        // Load API key safely from .env file
        Dotenv dotenv = Dotenv.load();
        String infuraUrl = dotenv.get("INFURA_SEPOLIA_URL");

        // Connect to Ethereum Sepolia testnet
        Web3j web3 = Web3j.build(new HttpService(infuraUrl));

        // Ask the network for the latest block number
        BigInteger blockNumber = web3.ethBlockNumber().send().getBlockNumber();

        System.out.println("✅ Connected to Ethereum Sepolia Testnet!");
        System.out.println("📦 Latest Block Number: " + blockNumber);

        web3.shutdown();
    }
}