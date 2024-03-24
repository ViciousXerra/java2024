package edu.java.bot.commandtests;

class TrackCommandTest {
/*
    private static Optional<User> emptyUserOptional;
    private static Optional<User> presentUserOptional;
    private static Update mockUpdate;

    @BeforeAll
    public static void setup() {
        emptyUserOptional = Optional.empty();
        presentUserOptional = TestUtils.createUserOptionalWithEmptyList();
        mockUpdate = TestUtils.createMockUpdate("/track", "username", 0L);
    }

    @Test
    @DisplayName("Test /track command.")
    void testTrackCommand() {
        //Given
        Command trackCommand = new TrackCommand();
        //When
        String actualCommand = trackCommand.command();
        String actualDescription = trackCommand.description();
        String actualNewUserMessage = trackCommand.createMessage(emptyUserOptional, "username1", 1L);
        String actualDefaultConditionMessage = trackCommand.createMessage(presentUserOptional, "username2", 2L);
        String actualAwaitingToTrackMessage = trackCommand.createMessage(presentUserOptional, "username2", 2L);
        //Setting to UNTRACK condition
        presentUserOptional.get().setCondition(UserChatCondition.AWAITING_LINK_TO_UNTRACK);
        String actualAwaitingToUntrackMessage = trackCommand.createMessage(presentUserOptional, "username2", 2L);
        boolean actualSupports = trackCommand.isSupport(mockUpdate);
        //Then
        Assertions.assertAll(
            () -> assertThat(actualCommand)
                .isEqualTo("/track"),
            () -> assertThat(actualDescription)
                .isEqualTo("Allows you to indicate interesting links by next message."),
            () -> assertThat(actualNewUserMessage)
                .isEqualTo("First you need to register by entering the command /start."),
            () -> assertThat(actualDefaultConditionMessage)
                .isEqualTo("Waiting for a link to be entered."),
            () -> assertThat(actualAwaitingToTrackMessage)
                .isEqualTo("The link to save is already expected."),
            () -> assertThat(actualAwaitingToUntrackMessage)
                .isEqualTo("OK, now the link sent in the next message will be saved."),
            () -> assertThat(actualSupports).isTrue()
        );
    }
*/
}
