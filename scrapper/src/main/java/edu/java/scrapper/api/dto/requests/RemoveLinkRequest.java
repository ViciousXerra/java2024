package edu.java.scrapper.api.dto.requests;

import edu.java.scrapper.api.dto.customvalidations.LinkValidation;

public record RemoveLinkRequest(@LinkValidation String link) {
}
