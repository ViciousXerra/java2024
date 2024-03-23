package edu.java.scrapper.dao.repository.jpa.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;

@Entity
@Getter
public class Link {

    private static final String DEFAULT_TIMESTAMP_DEFINITION = "timestamp with time zone not null default now()";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String url;

    @Column(name = "updated_at", nullable = false, columnDefinition = DEFAULT_TIMESTAMP_DEFINITION)
    private Timestamp updatedAt;

    @Column(name = "checked_at", nullable = false, columnDefinition = DEFAULT_TIMESTAMP_DEFINITION)
    private Timestamp checkedAt;

    @ManyToMany(mappedBy = "registeredLinks", cascade = CascadeType.MERGE)
    private Set<Chat> relatedChats = new HashSet<>();

    public Link(String url) {
        this.url = url;
    }

    public void setUpdatedAt(ZonedDateTime newUpdatedAt) {
        updatedAt = Timestamp.from(newUpdatedAt.toInstant());
    }

    public void setCheckedAt(ZonedDateTime newCheckedAt) {
        checkedAt = Timestamp.from(newCheckedAt.toInstant());
    }

}
