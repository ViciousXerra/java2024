package edu.java.bot.inputs;

import com.pengrad.telegrambot.model.Update;
import java.util.List;

public class HelpInput implements Input {

    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String INPUTS_HELP_HEADER = "List of all supported inputs:";
    private static final String TEMPLATE_TO_FORMAT = "%s: %s.";

    private final List<Input> inputsList;

    public HelpInput(List<Input> inputsList) {
        this.inputsList = inputsList;
    }

    @Override
    public String text() {
        return "/help";
    }

    @Override
    public String description() {
        return "Allows you to see all supported slash-inputs.";
    }

    @Override
    public String prepareResponse(Update update) {
        StringBuilder sb = new StringBuilder();
        sb.append(INPUTS_HELP_HEADER);
        sb.append(LINE_SEPARATOR);
        inputsList.forEach(input -> {
            sb.append(String.format(TEMPLATE_TO_FORMAT, input.text(), input.description()));
            sb.append(LINE_SEPARATOR);
        });
        return sb.toString();
    }

}
