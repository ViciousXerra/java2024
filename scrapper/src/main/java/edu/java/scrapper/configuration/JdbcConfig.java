package edu.java.scrapper.configuration;

import edu.java.scrapper.dao.repository.jdbc.JdbcChatIdLinkIdRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcChatRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.dao.service.interfaces.ChatService;
import edu.java.scrapper.dao.service.interfaces.LinkService;
import edu.java.scrapper.dao.service.interfaces.LinkUpdater;
import edu.java.scrapper.dao.service.jdbc.JdbcChatService;
import edu.java.scrapper.dao.service.jdbc.JdbcLinkService;
import edu.java.scrapper.dao.service.jdbc.JdbcLinkUpdater;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
@EnableTransactionManagement
public class JdbcConfig {

    private ApplicationConfig applicationConfig;
    private DataSource dataSource;
    private JdbcClient jdbcClient;

    @Autowired
    public JdbcConfig(ApplicationConfig applicationConfig, DataSource dataSource, JdbcClient jdbcClient) {
        this.applicationConfig = applicationConfig;
        this.dataSource = dataSource;
        this.jdbcClient = jdbcClient;
    }

    @Bean
    public PlatformTransactionManager txManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public JdbcChatRepository jdbcChatRepository() {
        return new JdbcChatRepository(jdbcClient);
    }

    @Bean
    public JdbcChatIdLinkIdRepository jdbcChatIdLinkIdRepository() {
        return new JdbcChatIdLinkIdRepository(jdbcClient);
    }

    @Bean
    public JdbcLinkRepository jdbcLinkRepository() {
        return new JdbcLinkRepository(jdbcClient);
    }

    @Bean
    public ChatService jdbcChatService(
        JdbcChatRepository chatRepository,
        JdbcChatIdLinkIdRepository chatIdLinkIdRepository,
        JdbcLinkRepository linkRepository
    ) {
        return new JdbcChatService(
            chatRepository,
            chatIdLinkIdRepository,
            linkRepository
        );
    }

    @Bean
    public LinkService jdbcLinkService(
        JdbcChatRepository chatRepository,
        JdbcChatIdLinkIdRepository chatIdLinkIdRepository,
        JdbcLinkRepository linkRepository
    ) {
        return new JdbcLinkService(
            chatRepository,
            chatIdLinkIdRepository,
            linkRepository
        );
    }

    @Bean
    public LinkUpdater jdbcLinkUpdater(
        JdbcLinkRepository linkRepository,
        JdbcChatIdLinkIdRepository chatIdLinkIdRepository
    ) {
        return new JdbcLinkUpdater(
            linkRepository,
            chatIdLinkIdRepository,
            applicationConfig
        );
    }

}
