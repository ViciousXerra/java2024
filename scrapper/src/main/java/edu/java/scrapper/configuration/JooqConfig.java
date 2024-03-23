package edu.java.scrapper.configuration;

import edu.java.scrapper.dao.repository.jooq.JooqChatIdLinkIdRepository;
import edu.java.scrapper.dao.repository.jooq.JooqChatRepository;
import edu.java.scrapper.dao.repository.jooq.JooqLinkRepository;
import edu.java.scrapper.dao.service.interfaces.ChatService;
import edu.java.scrapper.dao.service.interfaces.LinkService;
import edu.java.scrapper.dao.service.interfaces.LinkUpdater;
import edu.java.scrapper.dao.service.jooq.JooqChatService;
import edu.java.scrapper.dao.service.jooq.JooqLinkService;
import edu.java.scrapper.dao.service.jooq.JooqLinkUpdater;
import javax.sql.DataSource;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
@EnableTransactionManagement
public class JooqConfig {

    private ApplicationConfig applicationConfig;
    private DataSource dataSource;

    @Autowired
    public JooqConfig(ApplicationConfig applicationConfig, DataSource dataSource) {
        this.applicationConfig = applicationConfig;
        this.dataSource = dataSource;
    }

    @Bean
    public DataSourceConnectionProvider dataSourceConnectionProvider() {
        return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource));
    }

    @Bean
    public DefaultConfiguration defaultConfiguration(DataSourceConnectionProvider dataSourceConnectionProvider) {
        DefaultConfiguration jooqDefaultConfig = new DefaultConfiguration();
        jooqDefaultConfig.set(dataSourceConnectionProvider);
        jooqDefaultConfig.setSQLDialect(SQLDialect.POSTGRES);
        jooqDefaultConfig.settings()
            .withRenderSchema(false)
            .withRenderFormatted(true)
            .withRenderQuotedNames(RenderQuotedNames.NEVER);
        return jooqDefaultConfig;
    }

    @Bean
    public DefaultDSLContext defaultDSLContext(DefaultConfiguration defaultConfiguration) {
        return new DefaultDSLContext(defaultConfiguration);
    }

    @Bean
    public PlatformTransactionManager txManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public JooqChatRepository jooqChatRepository(DefaultDSLContext defaultDSLContext) {
        return new JooqChatRepository(defaultDSLContext);
    }

    @Bean
    public JooqChatIdLinkIdRepository jooqChatIdLinkIdRepository(DefaultDSLContext defaultDSLContext) {
        return new JooqChatIdLinkIdRepository(defaultDSLContext);
    }

    @Bean
    public JooqLinkRepository jooqLinkRepository(DefaultDSLContext defaultDSLContext) {
        return new JooqLinkRepository(defaultDSLContext);
    }

    @Bean
    public ChatService jooqChatService(
        JooqChatRepository chatRepository,
        JooqChatIdLinkIdRepository chatIdLinkIdRepository,
        JooqLinkRepository linkRepository
    ) {
        return new JooqChatService(
            chatRepository,
            chatIdLinkIdRepository,
            linkRepository
        );
    }

    @Bean
    public LinkService jooqLinkService(
        JooqChatRepository chatRepository,
        JooqChatIdLinkIdRepository chatIdLinkIdRepository,
        JooqLinkRepository linkRepository
    ) {
        return new JooqLinkService(
            chatRepository,
            chatIdLinkIdRepository,
            linkRepository
        );
    }

    @Bean
    public LinkUpdater jooqLinkUpdater(JooqLinkRepository linkRepository) {
        return new JooqLinkUpdater(
            linkRepository,
            applicationConfig
        );
    }

}
