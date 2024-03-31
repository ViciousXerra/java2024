package edu.java.bot.api.ratelimit;

import io.github.bucket4j.Bucket;

public interface RateLimitTracker {

    Bucket getBucket(String remoteAddress);

}
