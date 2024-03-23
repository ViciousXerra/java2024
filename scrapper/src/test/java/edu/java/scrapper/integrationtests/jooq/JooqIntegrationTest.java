package edu.java.scrapper.integrationtests.jooq;

import edu.java.scrapper.integrationtests.IntegrationTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class JooqIntegrationTest extends IntegrationTest {

    @DynamicPropertySource
    static void stubDatabaseAccessType(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> "jooq");
    }

}
