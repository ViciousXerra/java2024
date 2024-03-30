package edu.java.bot.configuration.ratelimit;

import io.github.bucket4j.Bucket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitTrackerImpl implements RateLimitTracker {

    private final Map<String, Bucket> bucketMapping = new ConcurrentHashMap<>();

    @Override
    public Bucket getBucket(String remoteAddress) {

    }

}
