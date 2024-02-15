package edu.java.bot.services;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

public interface ResponseService {

    SendMessage prepareResponse(Update update);

}
