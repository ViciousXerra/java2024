package edu.java.scrapper.dao.service.jooq;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.dao.repository.jooq.JooqLinkRepository;
import edu.java.scrapper.dao.service.interfaces.LinkUpdater;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(JooqLinkUpdater.JOOQ_LINK_UPDATER_SERVICE)
@Transactional
public class JooqLinkUpdater implements LinkUpdater {

    public static final String JOOQ_LINK_UPDATER_SERVICE = "jooq-link-updater-service";
    private final JooqLinkRepository linkRepository;
    private final int fetchLimit;

    @Autowired
    public JooqLinkUpdater(JooqLinkRepository linkRepository, ApplicationConfig applicationConfig) {
        this.linkRepository = linkRepository;
        this.fetchLimit = applicationConfig.scheduler().fetchLimit();
    }

    @Override
    public List<Link> update() {
        return linkRepository.findUpToCheck(fetchLimit);
    }

}
