package edu.java.scrapper.dao.service.interfaces;

import edu.java.scrapper.dao.dto.Link;
import java.util.Collection;
import org.springframework.stereotype.Service;

@Service
public interface LinkService {

    Link add(long tgChatId, String url);

    Link remove(long tgChatId, String url);

    Collection<Link> listAll(long tgChatId);

}
