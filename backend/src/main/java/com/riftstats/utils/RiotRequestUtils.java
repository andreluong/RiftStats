package com.riftstats.utils;

import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.function.Function;

import static com.riftstats.utils.Utils.sleepSeconds;

@Slf4j
public final class RiotRequestUtils {

    private RiotRequestUtils() {}

    // Rate limit reached. Sleeps for "Retry-After" seconds
    private static long getRetryAfterSeconds(Response res) {
        return res.headers().get("Retry-After").stream()
                .findFirst()
                .map(Long::parseLong)
                .orElse(120L);
    }

    // Get request to Riot API with 3 retries
    // Rate limit of 100 req/2mins & 20 req/s with development key
    // Returns response body on success. Otherwise, empty
    public static Optional<Response.Body> fetchWithRetries(String key, String value, Function<String, Response> request) {
        final int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                Response res = request.apply(value);
                switch (HttpStatus.resolve(res.status())) {
                    case OK -> {
                        return Optional.ofNullable(res.body());
                    }
                    case TOO_MANY_REQUESTS -> {
                        long retryAfterSecs = getRetryAfterSeconds(res);
                        log.warn("Rate limit reached on attempt {} for {}={}. Retrying after {} seconds.", attempt, key, value, retryAfterSecs);
                        sleepSeconds(retryAfterSecs);
                    }
                    default -> {
                        int retryAfterSecs = 3;
                        log.warn("Unexpected status {} on attempt {}: {}", res.status(), attempt, res.body().toString());
                        sleepSeconds(retryAfterSecs);
                    }
                }
            } catch (Exception e) {
                log.error("Attempt {} failed: {}", attempt, e.getMessage());
            }
        }
        return Optional.empty();
    }
}
