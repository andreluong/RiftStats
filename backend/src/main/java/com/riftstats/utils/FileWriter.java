package com.riftstats.utils;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// For development
// Was used in matchProcessingService to display data for each region and patch

@Slf4j
@Component
public class FileWriter {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 15;

    @PreDestroy
    public void cleanup() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void writeDataToFileAsync(Collection<?> data, String fileName) {
        executor.submit(() -> {
            try {
                overwriteDataToFile(data, fileName);
            } catch (IOException e) {
                log.error("Failed to write data to file={}: {}", fileName, e.getMessage());
            }
        });
    }

    private synchronized void overwriteDataToFile(Collection<?> data, String fileName) throws IOException {
        Path outputDir = Paths.get("riftstats/output");
        Files.createDirectories(outputDir);

        // Write to temporary file
        Path tempFile = Files.createTempFile(outputDir, "temp-", ".tmp");
        try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
            for (Object item : data) {
                writer.write(item.toString());
                writer.newLine();
            }
        }

        // Replace target file with temporary file
        Path targetFile = outputDir.resolve(fileName);
        Files.move(tempFile, targetFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);

        log.info("Overwrote {} items to file={}", data.size(), targetFile);
    }
}
