package edu.java.scrapper.dao.dto;

import java.time.ZonedDateTime;

public record Link(long linkId, String url, ZonedDateTime updatedAt) {
}
