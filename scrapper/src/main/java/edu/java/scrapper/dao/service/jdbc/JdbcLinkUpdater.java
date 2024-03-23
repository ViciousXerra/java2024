package edu.java.scrapper.dao.service.jdbc;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.dao.service.interfaces.LinkUpdater;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class JdbcLinkUpdater implements LinkUpdater {

    private final JdbcLinkRepository linkRepository;
    private final int fetchLimit;

    public JdbcLinkUpdater(JdbcLinkRepository linkRepository, ApplicationConfig applicationConfig) {
        this.linkRepository = linkRepository;
        this.fetchLimit = applicationConfig.scheduler().fetchLimit();
    }

    @Override
    public List<Link> update() {
        return linkRepository.findUpToCheck(fetchLimit);
    }

}
