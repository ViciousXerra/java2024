package edu.java.bot.api.ratelimit;

import edu.java.bot.api.exceptions.RateLimitException;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private RateLimitTracker rateLimitTracker;

    @Autowired
    public RateLimitInterceptor(RateLimitTracker rateLimitTracker) {
        this.rateLimitTracker = rateLimitTracker;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String remoteAddress = request.getRemoteAddr();
        Bucket bucket = rateLimitTracker.getBucket(remoteAddress);
        if (bucket.tryConsume(1)) {
            return true;
        } else {
            throw new RateLimitException(
                "Rate limit has been exhausted",
                "API request quota has been exhausted"
            );
        }
    }

}
