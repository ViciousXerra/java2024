package edu.java.bot.configuration;

import edu.java.bot.urlparsers.AbstractUrlParser;
import edu.java.bot.urlparsers.GitHubUrlParser;
import edu.java.bot.urlparsers.StackOverFlowUrlParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParserConfig {

    @Bean
    public AbstractUrlParser urlParser() {
        return new GitHubUrlParser(new StackOverFlowUrlParser(null));
    }

}
