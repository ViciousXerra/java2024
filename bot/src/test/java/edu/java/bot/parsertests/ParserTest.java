package edu.java.bot.parsertests;

import edu.java.bot.urlparsers.AbstractUrlParser;
import edu.java.bot.urlparsers.GitHubUrlParser;
import edu.java.bot.urlparsers.StackOverFlowUrlParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.assertj.core.api.Assertions.assertThat;

class ParserTest {

    private static Object[][] provideUrlParserTest() {
        return new Object[][] {
            {
                "https://github.com/ViciousXerra",
                true
            },
            {
                "https://stackoverflow.com/questions/35531661/using-env-variable-in-spring-boots-application-properties",
                true
            },
            {
                //Because javarush is weirdo
                "https://javarush.com/login",
                false
            }
        };
    }

    @ParameterizedTest
    @DisplayName("Test url parser.")
    @MethodSource("provideUrlParserTest")
    void testUrlParser(String url, boolean isValidExpected) {
        //Given
        AbstractUrlParser parser = new GitHubUrlParser(new StackOverFlowUrlParser(null));
        //When
        boolean actualIsValid = parser.isValid(url);
        //Then
        assertThat(actualIsValid).isEqualTo(isValidExpected);
    }
}
