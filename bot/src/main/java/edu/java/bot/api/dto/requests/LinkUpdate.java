package edu.java.bot.api.dto.requests;

import edu.java.bot.api.dto.customvalidations.LinkValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record LinkUpdate(
    long id,
    @LinkValidation
    String url,
    @NotBlank
    String description,
    @NotEmpty
    List<Long> tgChatIds
) {
}
