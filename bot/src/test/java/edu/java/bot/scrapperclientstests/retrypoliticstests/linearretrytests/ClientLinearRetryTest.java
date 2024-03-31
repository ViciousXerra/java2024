package edu.java.bot.scrapperclientstests.retrypoliticstests.linearretrytests;

import edu.java.bot.scrapperclientstests.retrypoliticstests.RetryPoliticsTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@DirtiesContext(hierarchyMode = DirtiesContext.HierarchyMode.CURRENT_LEVEL)
class ClientLinearRetryTest  extends RetryPoliticsTest {

    @DynamicPropertySource
    static void stubScrapperBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("app.client-retry-settings.backoff-type", () -> "linear");
    }

}
