package edu.java.bot.commandtests;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.WithoutKafkaTestConfig;
import edu.java.bot.commandexecutors.LinkUpdateCommandExecutor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(WithoutKafkaTestConfig.class)
abstract class CommandTest {

    @MockBean
    private TelegramBot bot;
    @MockBean
    private LinkUpdateCommandExecutor linkUpdateCommandExecutor;

}
