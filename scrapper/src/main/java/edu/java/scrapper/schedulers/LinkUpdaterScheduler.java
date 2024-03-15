package edu.java.scrapper.schedulers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LinkUpdaterScheduler {

    private final static Logger LOGGER = LogManager.getLogger();

    @Scheduled(fixedDelayString = "#{scheduler.interval()}")
    public void update() {
        LOGGER.info("//Listening//");
    }

}
