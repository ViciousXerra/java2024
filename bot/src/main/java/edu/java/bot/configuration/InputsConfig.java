package edu.java.bot.configuration;

import edu.java.bot.dispatch.InputsDispatcher;
import edu.java.bot.inputs.HelpInput;
import edu.java.bot.inputs.Input;
import edu.java.bot.inputs.ListInput;
import edu.java.bot.inputs.StartInput;
import edu.java.bot.inputs.TrackInput;
import edu.java.bot.inputs.UntrackInput;
import edu.java.bot.temp_repository.UserTemporaryRepository;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InputsConfig {

    @Bean
    InputsDispatcher inputsDispatcher(UserTemporaryRepository repository) {
        Input start = new StartInput(repository);
        Input list = new ListInput(repository);
        Input track = new TrackInput(repository);
        Input untrack = new UntrackInput(repository);
        Input help = new HelpInput(List.of(start, list, track, untrack));
        return new InputsDispatcher(
            Map.of(
                "/start", start,
                "/list", list,
                "/track", track,
                "/untrack", untrack,
                "/help", help
            )
        );
    }

}
