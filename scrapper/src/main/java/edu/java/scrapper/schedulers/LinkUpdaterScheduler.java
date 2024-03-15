package edu.java.scrapper.schedulers;

import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.dao.repository.interfaces.LinkRepository;
import edu.java.scrapper.dao.service.interfaces.LinkUpdater;
import edu.java.scrapper.webclients.clients.GitHubClient;
import edu.java.scrapper.webclients.clients.StackOverFlowClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class LinkUpdaterScheduler {

    private final LinkUpdater linkUpdater;
    private final LinkRepository linkRepository;
    private final GitHubClient gitHubClient;
    private final StackOverFlowClient stackOverFlowClient;

    @Autowired
    public LinkUpdaterScheduler(
        LinkUpdater linkUpdater,
        LinkRepository linkRepository,
        GitHubClient gitHubClient,
        StackOverFlowClient stackOverFlowClient
    ) {
        this.linkUpdater = linkUpdater;
        this.linkRepository = linkRepository;
        this.gitHubClient = gitHubClient;
        this.stackOverFlowClient = stackOverFlowClient;
    }

    @Scheduled(fixedDelayString = "#{scheduler.interval()}")
    public void update() {
        List<Link> linksToCheck = linkUpdater.update();

    }

    private enum LinkProcessor {
        
    }

}
