package edu.java.scrapper.botlinkupdateservices;

import edu.java.scrapper.telegrambotclient.ClientException;
import edu.java.scrapper.telegrambotclient.clients.BotUpdateClient;
import edu.java.scrapper.telegrambotclient.dto.requests.LinkUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RequiredArgsConstructor
@Log4j2
public class BotLinkUpdateClientService implements BotLinkUpdateService {

    private final BotUpdateClient botUpdateClient;

    @Override
    public void postLinkUpdate(LinkUpdate linkUpdate) {
        try {
            botUpdateClient.postLinkUpdate(linkUpdate);
        } catch (ClientException e) {
            log.error("Client error: {}", e.getBotApiErrorResponse().description());
        } catch (WebClientResponseException e) {
            log.error("Internal service error: {}", e.getMessage());
        }

    }

}
