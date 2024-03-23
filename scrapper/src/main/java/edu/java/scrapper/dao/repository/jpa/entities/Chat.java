package edu.java.scrapper.dao.repository.jpa.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;

@Entity
@Getter
public class Chat {

    @Id
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
        name = "ChatIdLinkId",
        joinColumns = @JoinColumn(name = "chat_id", referencedColumnName = "id", nullable = false, updatable = false),
        inverseJoinColumns = @JoinColumn(name = "link_id",
                                         referencedColumnName = "id",
                                         nullable = false,
                                         updatable = false)
    )
    private Set<Link> registeredLinks = new HashSet<>();

    public Chat(Long id) {
        this.id = id;
    }

}
