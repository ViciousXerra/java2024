package edu.java.scrapper.configuration;

import edu.java.scrapper.dao.repository.jpa.repositories.JpaChatRepository;
import edu.java.scrapper.dao.repository.jpa.repositories.JpaLinkRepository;
import edu.java.scrapper.dao.service.interfaces.ChatService;
import edu.java.scrapper.dao.service.interfaces.LinkService;
import edu.java.scrapper.dao.service.interfaces.LinkUpdater;
import edu.java.scrapper.dao.service.jpa.JpaChatService;
import edu.java.scrapper.dao.service.jpa.JpaLinkService;
import edu.java.scrapper.dao.service.jpa.JpaLinkUpdater;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
@EnableJpaRepositories(basePackages = "edu.java.scrapper.dao.repository.jpa.repositories")
@EnableTransactionManagement
public class JpaConfig {

    private DataSource dataSource;

    @Autowired
    public JpaConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setJpaVendorAdapter(vendorAdapter);
        entityManagerFactory.setPackagesToScan("edu.java.scrapper.dao.repository.jpa.entities");
        entityManagerFactory.setDataSource(dataSource);
        return entityManagerFactory;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);
        return txManager;
    }

    @Bean
    public ChatService jpaChatService(JpaChatRepository chatRepository) {
        return new JpaChatService(chatRepository);
    }

    @Bean
    public LinkService jpaLinkService(JpaChatRepository chatRepository, JpaLinkRepository linkRepository) {
        return new JpaLinkService(chatRepository, linkRepository);
    }

    @Bean
    public LinkUpdater jpaLinkUpdater(JpaLinkRepository linkRepository, ApplicationConfig applicationConfig) {
        return new JpaLinkUpdater(linkRepository, applicationConfig);
    }

}
