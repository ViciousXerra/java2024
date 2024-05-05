package edu.java.scrapper.api.ratelimit;

import edu.java.scrapper.configuration.ApplicationConfig;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RateLimitTrackerImpl implements RateLimitTracker {

    private ApplicationConfig applicationConfig;
    private final Map<String, Bucket> bucketMapping;

    @Autowired
    public RateLimitTrackerImpl(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
        this.bucketMapping = new ConcurrentHashMap<>();
    }

    @Override
    public Bucket getBucket(String remoteAddress) {
        return bucketMapping.computeIfAbsent(remoteAddress, key -> {
            Bandwidth limit = Bandwidth.classic(
                applicationConfig.apiRateLimitSettings().limit(),
                Refill.intervally(
                    applicationConfig.apiRateLimitSettings().refillLimit(),
                    applicationConfig.apiRateLimitSettings().refillDelay()
                )
            );
            return Bucket.builder().addLimit(limit).build();
        });
    }

}
