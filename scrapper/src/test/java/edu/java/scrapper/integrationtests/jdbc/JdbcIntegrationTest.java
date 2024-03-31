package edu.java.scrapper.integrationtests.jdbc;

import edu.java.scrapper.integrationtests.IntegrationTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@DirtiesContext(hierarchyMode = DirtiesContext.HierarchyMode.CURRENT_LEVEL)
public abstract class JdbcIntegrationTest extends IntegrationTest {

    @DynamicPropertySource
    static void stubDatabaseAccessType(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> "jdbc");
    }

}
