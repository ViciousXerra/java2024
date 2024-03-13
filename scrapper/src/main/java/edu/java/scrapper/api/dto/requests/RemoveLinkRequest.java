package edu.java.scrapper.api.dto.requests;

import edu.java.scrapper.api.dto.customvalidations.LinkValidator;

public record RemoveLinkRequest(@LinkValidator.LinkValidation String link) {
}
