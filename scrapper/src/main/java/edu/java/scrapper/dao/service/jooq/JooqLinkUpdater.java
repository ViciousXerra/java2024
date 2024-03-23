package edu.java.scrapper.dao.service.jooq;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.dao.repository.jooq.JooqLinkRepository;
import edu.java.scrapper.dao.service.interfaces.LinkUpdater;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class JooqLinkUpdater implements LinkUpdater {

    private final JooqLinkRepository linkRepository;
    private final int fetchLimit;

    public JooqLinkUpdater(JooqLinkRepository linkRepository, ApplicationConfig applicationConfig) {
        this.linkRepository = linkRepository;
        this.fetchLimit = applicationConfig.scheduler().fetchLimit();
    }

    @Override
    public List<Link> update() {
        return linkRepository.findUpToCheck(fetchLimit);
    }

}
