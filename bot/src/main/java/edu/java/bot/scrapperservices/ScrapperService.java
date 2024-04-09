package edu.java.bot.scrapperservices;

import edu.java.bot.scrapperclient.clients.ChatClient;
import edu.java.bot.scrapperclient.clients.LinksClient;
import edu.java.bot.scrapperclient.dto.requests.AddLinkRequest;
import edu.java.bot.scrapperclient.dto.requests.RemoveLinkRequest;
import edu.java.bot.scrapperclient.dto.responses.LinkResponse;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScrapperService {

    private final ChatClient chatClient;
    private final LinksClient linksClient;

    @Autowired
    public ScrapperService(ChatClient chatClient, LinksClient linksClient) {
        this.chatClient = chatClient;
        this.linksClient = linksClient;
    }

    public void addChat(long chatId) {
        chatClient.signUpChat(chatId);
    }

    public void removeChat(long chatId) {
        chatClient.removeChat(chatId);
    }

    public void addLink(long chatId, String text) {
        linksClient.addLink(chatId, new AddLinkRequest(text));
    }

    public void removeLink(long chatId, String text) {
        linksClient.removeLink(chatId, new RemoveLinkRequest(text));
    }

    public List<URI> getAllLinks(long chatId) {
        return linksClient.getAllLinks(chatId).links().stream().map(LinkResponse::url).toList();
    }

}
