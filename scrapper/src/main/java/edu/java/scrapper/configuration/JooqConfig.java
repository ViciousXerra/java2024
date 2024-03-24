package edu.java.scrapper.configuration;

import javax.sql.DataSource;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

@Configuration
public class JooqConfig {

    private DataSource dataSource;

    @Autowired
    public JooqConfig(DataSource dataSource) {
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

}
