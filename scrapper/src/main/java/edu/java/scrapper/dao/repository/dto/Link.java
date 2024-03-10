package edu.java.scrapper.dao.repository.dto;

import java.time.ZonedDateTime;

public record Link(long linkId, String url, ZonedDateTime updatedAt, ZonedDateTime checkedAt) {
}
