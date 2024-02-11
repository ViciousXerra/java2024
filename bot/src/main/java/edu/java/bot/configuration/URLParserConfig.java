package edu.java.bot.configuration;

import edu.java.bot.parsers.GitHubURLParser;
import edu.java.bot.parsers.StackOverFlowURLParser;
import edu.java.bot.parsers.URLParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class URLParserConfig {

    @Bean
    URLParser urlParser() {
        return new GitHubURLParser(new StackOverFlowURLParser(null));
    }

}
