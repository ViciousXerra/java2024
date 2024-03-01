package edu.java.bot.scrapperclient;

import edu.java.bot.scrapperclient.dto.errorresponses.ScrapperApiErrorResponse;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class ClientException extends RuntimeException {

    private final ScrapperApiErrorResponse clientErrorResponseBody;

}
