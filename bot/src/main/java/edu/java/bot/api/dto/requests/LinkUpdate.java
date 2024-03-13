package edu.java.bot.api.dto.requests;

import edu.java.bot.api.dto.customvalidations.LinkValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record LinkUpdate(
    long id,
    @NotBlank
    @LinkValidator.LinkValidation
    String url,
    @NotBlank
    String description,
    @NotEmpty
    List<Long> tgChatIds
) {
}
