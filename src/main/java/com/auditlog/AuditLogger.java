package com.auditlog;

import java.security.MessageDigest;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class AuditLogger {

    private static final String LOG_FILE = "audit.log";

    // Write the event to a local log file
    public void log(AuditEvent event) throws Exception {
        String logLine = event.toLogString() + "\n";
        Files.writeString(
            Paths.get(LOG_FILE),
            logLine,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND
        );
        System.out.println("📝 Logged: " + event);
    }

    // Generate a SHA-256 hash of the event
    public String hash(AuditEvent event) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(
            event.toLogString().getBytes("UTF-8")
        );

        // Convert bytes to hex string
        StringBuilder hex = new StringBuilder();
        for (byte b : hashBytes) {
            hex.append(String.format("%02x", b));
        }

        String hashValue = hex.toString();
        System.out.println("🔑 Hash: " + hashValue);
        return hashValue;
    }
}