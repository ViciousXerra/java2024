package edu.java.bot.commandtests;

class UntrackCommandTest {
/*
    private static Optional<User> emptyUserOptional;
    private static Optional<User> presentUserOptional;
    private static Update mockUpdate;

    @BeforeAll
    public static void setup() {
        emptyUserOptional = Optional.empty();
        presentUserOptional = TestUtils.createUserOptionalWithEmptyList();
        mockUpdate = TestUtils.createMockUpdate("/untrack", "username", 0L);
    }

    @Test
    @DisplayName("Test /untrack command.")
    void testUntrackCommand() {
        //Given
        Command untrackCommand = new UntrackCommand();
        //When
        String actualCommand = untrackCommand.command();
        String actualDescription = untrackCommand.description();
        String actualNewUserMessage = untrackCommand.createMessage(emptyUserOptional, "username1", 1L);
        String actualDefaultConditionMessage = untrackCommand.createMessage(presentUserOptional, "username2", 2L);
        String actualAwaitingToUntrackMessage = untrackCommand.createMessage(presentUserOptional, "username2", 2L);
        //Setting to TRACK condition
        presentUserOptional.get().setCondition(UserChatCondition.AWAITING_LINK_TO_TRACK);
        String actualAwaitingToTrackMessage = untrackCommand.createMessage(presentUserOptional, "username2", 2L);
        boolean actualSupports = untrackCommand.isSupport(mockUpdate);
        //Then
        Assertions.assertAll(
            () -> assertThat(actualCommand)
                .isEqualTo("/untrack"),
            () -> assertThat(actualDescription)
                .isEqualTo("Allows you to remove interesting links by next message."),
            () -> assertThat(actualNewUserMessage)
                .isEqualTo("First you need to register by entering the command /start."),
            () -> assertThat(actualDefaultConditionMessage)
                .isEqualTo("Waiting for a link to be entered."),
            () -> assertThat(actualAwaitingToUntrackMessage)
                .isEqualTo("The link to delete is already expected."),
            () -> assertThat(actualAwaitingToTrackMessage)
                .isEqualTo("OK, now the link sent in the next message will be deleted."),
            () -> assertThat(actualSupports).isTrue()
        );
    }
*/
}
