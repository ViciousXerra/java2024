package edu.java.bot.dispatch;

import edu.java.bot.inputs.Input;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;

@Getter
public class InputsDispatcher {
    private final Map<String, Input> inputsMap;

    public InputsDispatcher(Map<String, Input> inputMap) {
        this.inputsMap = inputMap;
    }

    public Optional<Input> getInputValue(String text) {
        return inputsMap.containsKey(text) ? Optional.of(inputsMap.get(text)) : Optional.empty();
    }

}
