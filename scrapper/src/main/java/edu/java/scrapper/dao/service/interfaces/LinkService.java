package edu.java.scrapper.dao.service.interfaces;

import edu.java.scrapper.dao.dto.Link;
import java.net.URI;
import java.util.Collection;

public interface LinkService {

    Link add(long tgChatId, URI url);
    Link remove(long tgChatId, URI url);
    Collection<Link> listAll(long tgChatId);

}
