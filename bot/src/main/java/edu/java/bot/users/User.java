package edu.java.bot.users;

import java.net.URI;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
public class User {
    private final long id;
    private final List<URI> links;
    @Setter
    private Action action;

    public User(long id, List<URI> links, Action action) {
        this.id = id;
        this.links = links;
        this.action = action;
    }

}
