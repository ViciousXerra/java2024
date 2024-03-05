package edu.java.bot.usertests;

import edu.java.bot.TestUtils;
import edu.java.bot.users.User;
import java.net.URI;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserTest {

    private static User user;
    private static URI url1;
    private static URI url2;

    @BeforeAll
    public static void setup() {
        user = TestUtils.createUserOptionalWithEmptyList().get();
        url1 = URI.create("https://github.com/ViciousXerra");
        url2 = URI.create(
            "https://stackoverflow.com/questions/35531661/using-env-variable-in-spring-boots-application-properties");
    }

    @Test
    @Order(1)
    @DisplayName("Test empty url set.")
    void testUserWithEmptyUriSet() {
        //Then
        assertThat(user.getTrackingLinks()).isEmpty();
    }

    @Test
    @Order(2)
    @DisplayName("Test url insertion.")
    void testUrlInsertion() {
        //When
        user.saveLink(url1);
        user.saveLink(url1);
        user.saveLink(url2);
        //Then
        assertThat(user.getTrackingLinks()).containsOnlyOnce(url1, url2);
    }

    @Test
    @Order(3)
    @DisplayName("Test url removal.")
    void testUrlRemoval() {
        //When
        user.removeLink(url1);
        //Then
        assertThat(user.getTrackingLinks()).containsOnlyOnce(url2);
    }

}
