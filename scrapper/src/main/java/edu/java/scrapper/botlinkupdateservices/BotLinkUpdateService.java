package edu.java.scrapper.botlinkupdateservices;

import edu.java.scrapper.telegrambotclient.dto.requests.LinkUpdate;

public interface BotLinkUpdateService {

    void postLinkUpdate(LinkUpdate linkUpdate);

}
