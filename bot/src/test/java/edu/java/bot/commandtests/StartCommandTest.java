package edu.java.bot.commandtests;

class StartCommandTest {
/*
    private static UserRepository repositoryStub;
    private static Optional<User> emptyUserOptional;
    private static Optional<User> presentUserOptional;
    private static Update mockUpdate;

    @BeforeAll
    public static void setup() {
        repositoryStub = TestUtils.createRepositoryStub();
        emptyUserOptional = Optional.empty();
        presentUserOptional = TestUtils.createUserOptionalWithEmptyList();
        mockUpdate = TestUtils.createMockUpdate("/start", "username", 0L);
    }

    @Test
    @DisplayName("Test /start command.")
    void testStartCommand() {
        //Given
        Command startCommand = new StartCommand(repositoryStub);
        //When
        String actualCommand = startCommand.command();
        String actualDescription = startCommand.description();
        String actualNewUserMessage = startCommand.createMessage(emptyUserOptional, "username1", 1L);
        String actualRegisteredUSerMesssage = startCommand.createMessage(presentUserOptional, "username2", 2L);
        boolean actualSupports = startCommand.isSupport(mockUpdate);
        //Then
        Assertions.assertAll(
            () -> assertThat(actualCommand).isEqualTo("/start"),
            () -> assertThat(actualDescription).isEqualTo("Allows you to start using the service."),
            () -> assertThat(actualNewUserMessage).isEqualTo("Nice to meet you, username1."),
            () -> assertThat(actualRegisteredUSerMesssage).isEqualTo("Hello again, username2. Shall we continue?"),
            () -> assertThat(actualSupports).isTrue()
        );
    }
*/
}
