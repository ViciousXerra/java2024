package edu.java.scrapper.telegrambotclient;

import edu.java.scrapper.telegrambotclient.dto.errorresponses.BotApiErrorResponse;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class ClientException extends RuntimeException {

    final BotApiErrorResponse botApiErrorResponse;

}
