package edu.java.scrapper.dto.github;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RepositoryVisibility {
    PRIVATE,
    PUBLIC;

    @JsonCreator
    public static RepositoryVisibility fromString(String text) {
        for (RepositoryVisibility type : RepositoryVisibility.values()) {
            if (type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unresolved repository accessibility type: %s".formatted(text));
    }

}
