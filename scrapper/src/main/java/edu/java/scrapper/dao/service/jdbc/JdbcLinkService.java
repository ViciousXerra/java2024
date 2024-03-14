package edu.java.scrapper.dao.service.jdbc;

import edu.java.scrapper.api.exceptions.NotFoundException;
import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.dao.repository.jdbc.JdbcChatIdLinkIdRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcChatRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.dao.service.interfaces.LinkService;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JdbcLinkService implements LinkService {

    private final static Function<List<Link>, List<String>> URL_STRING_CONVERTER =
        links -> links.stream().map(Link::url).toList();
    private final JdbcChatRepository chatRepository;
    private final JdbcLinkRepository linkRepository;
    private final JdbcChatIdLinkIdRepository chatIdLinkIdRepository;

    @Autowired
    public JdbcLinkService(
        JdbcChatRepository chatRepository,
        JdbcLinkRepository linkRepository,
        JdbcChatIdLinkIdRepository chatIdLinkIdRepository
    ) {
        this.chatRepository = chatRepository;
        this.linkRepository = linkRepository;
        this.chatIdLinkIdRepository = chatIdLinkIdRepository;
    }

    @Override
    @Transactional
    public Link add(long tgChatId, String url) {
        List<Long> registeredIds = chatRepository.findAll();
        if (!registeredIds.contains(tgChatId)) {
            throw new NotFoundException("Links not found", "Registration required for managing links for tracking");
        }
        return null;
    }

    @Override
    @Transactional
    public Link remove(long tgChatId, String url) {
        return null;
    }

    @Override
    @Transactional
    public Collection<Link> listAll(long tgChatId) {
        return null;
    }
}
