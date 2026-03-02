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

        // Confirm blockchain connection
        BigInteger blockNumber = web3.ethBlockNumber().send().getBlockNumber();
        System.out.println("✅ Connected to Ethereum Sepolia Testnet!");
        System.out.println("📦 Latest Block Number: " + blockNumber);
        System.out.println("-----------------------------------");

        // Create an audit event
        AuditEvent event = new AuditEvent(
            "jane",
            "DELETE",
            "customer-record-1234"
        );

        // Log it and hash it
        AuditLogger logger = new AuditLogger();
        logger.log(event);
        String hash = logger.hash(event);

        System.out.println("-----------------------------------");
        System.out.println("✅ Audit event logged and hashed!");
        System.out.println("Next step: anchor this hash to the blockchain.");
        System.out.println("⏳ Hash ready to anchor: " + hash);

        web3.shutdown();
    }
}