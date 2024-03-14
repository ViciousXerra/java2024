package edu.java.scrapper.dao.service.interfaces;

import edu.java.scrapper.dao.dto.Link;
import java.net.URI;
import java.util.Collection;

public interface LinkService {

    Link add(long tgChatId, String url);
    Link remove(long tgChatId, String url);
    Collection<Link> listAll(long tgChatId);

}
