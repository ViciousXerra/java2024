package edu.java.scrapper.integrationtests.jdbc.repositorytests;

import edu.java.scrapper.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.integrationtests.IntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;

public class JdbcLinkRepositoryTest extends IntegrationTest {

    @Autowired
    private JdbcLinkRepository chatRepository;
    @Autowired
    private JdbcClient jdbcClient;

}
