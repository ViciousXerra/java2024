package edu.java.bot.users;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
public class User {

    private final Set<URI> trackingLinks;
    @Setter
    private UserChatCondition condition;

    public User() {
        this.trackingLinks = new HashSet<>();
        condition = UserChatCondition.DEFAULT;
    }

    public boolean saveLink(URI url) {
        return trackingLinks.add(url);
    }

    public boolean removeLink(URI url) {
        return trackingLinks.remove(url);
    }

}
