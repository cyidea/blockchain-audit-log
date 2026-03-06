package com.auditlog;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.utils.Numeric;
import java.util.Optional;

public class AuditVerifier {

    private Web3j web3j;

    public AuditVerifier(String infuraUrl) {
        this.web3j = Web3j.build(new HttpService(infuraUrl));
    }

    public boolean verify(AuditEvent event, String transactionHash) throws Exception {

        System.out.println("🔍 Verifying audit event...");
        System.out.println("📝 Event: " + event);

        // Step 1: Re-hash the event
        AuditLogger logger = new AuditLogger();
        String recomputedHash = logger.hash(event);
        System.out.println("🔑 Recomputed Hash: " + recomputedHash);

        // Step 2: Fetch the transaction from the blockchain
        EthTransaction ethTransaction = web3j
                .ethGetTransactionByHash(transactionHash)
                .send();

        Optional<org.web3j.protocol.core.methods.response.Transaction> transaction
                = ethTransaction.getTransaction();

        if (!transaction.isPresent()) {
            System.out.println("❌ Transaction not found on blockchain!");
            return false;
        }

        // Step 3: Extract the hash we stored in the transaction data field
        String inputData = transaction.get().getInput();
        String storedHash = inputData.substring(2); // remove the "0x" prefix
        System.out.println("⛓️  Stored Hash on Blockchain: " + storedHash);

        // Step 4: Compare the two hashes
        boolean isValid = recomputedHash.equals(storedHash);

        if (isValid) {
            System.out.println("✅ VERIFIED! Audit log has not been tampered with.");
        } else {
            System.out.println("❌ TAMPERED! Audit log does not match blockchain record.");
        }

        return isValid;
    }
}