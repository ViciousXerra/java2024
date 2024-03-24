package edu.java.scrapper.dao.repository.jpa.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Chat")
@Getter
@Setter
@NoArgsConstructor
public class ChatEntity {

    @Id
    private Long id;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
        name = "ChatIdLinkId",
        joinColumns = @JoinColumn(name = "chat_id", referencedColumnName = "id", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "link_id", referencedColumnName = "id", nullable = false)
    )
    private Set<LinkEntity> registeredLinks = new HashSet<>();

    public void addLink(LinkEntity link) {
        link.getRelatedChats().add(this);
        this.registeredLinks.add(link);
    }

    public void removeLink(LinkEntity link) {
        link.getRelatedChats().remove(this);
        this.registeredLinks.remove(link);
    }

    public boolean isAlreadyTracking(LinkEntity link) {
        return registeredLinks.stream().anyMatch(linkEntity -> linkEntity.getUrl().equals(link.getUrl()));
    }

}
