package edu.java.scrapper.dao.repository.jooq;

import domain.jooq.tables.pojos.Chatidlinkid;
import edu.java.scrapper.api.exceptions.UnhandledException;
import edu.java.scrapper.dao.dto.ChatIdLinkId;
import edu.java.scrapper.dao.repository.interfaces.ChatIdLinkIdRepository;
import java.util.List;
import java.util.function.Function;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import static domain.jooq.tables.Chatidlinkid.CHATIDLINKID;

@Repository
public class JooqChatIdLinkIdRepository implements ChatIdLinkIdRepository {

    private final static Function<Chatidlinkid, ChatIdLinkId>
        DTO_CONVERTER_LAMBDA =
        chatidlinkid -> new ChatIdLinkId(
            chatidlinkid.getChatId(),
            chatidlinkid.getLinkId()
        );
    private final DefaultDSLContext dslContext;

    @Autowired
    public JooqChatIdLinkIdRepository(DefaultDSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public void add(long chatId, long linkId) {
        try {
            dslContext
                .insertInto(CHATIDLINKID)
                .set(CHATIDLINKID.CHAT_ID, chatId)
                .set(CHATIDLINKID.LINK_ID, linkId)
                .execute();
        } catch (DataAccessException e) {
            throw new UnhandledException(
                "Reference table constraints violation",
                "Unable to insert data. Constraints violation is presented"
            );
        }
    }

    @Override
    public void remove(long chatId, long linkId) {
        int updates = dslContext
            .delete(CHATIDLINKID)
            .where(CHATIDLINKID.CHAT_ID.eq(chatId)).and(CHATIDLINKID.LINK_ID.eq(linkId))
            .execute();
        if (updates == 0) {
            throw new UnhandledException(
                "Row hasn't been founded in reference table",
                "Unable to delete data. Data is not presented"
            );
        }
    }

    @Override
    public List<ChatIdLinkId> findAll() {
        try {
            return dslContext
                .select().from(CHATIDLINKID)
                .fetchInto(Chatidlinkid.class)
                .stream()
                .map(DTO_CONVERTER_LAMBDA)
                .toList();
        } catch (NullPointerException e) {
            return List.of();
        }
    }

    @Override
    public List<ChatIdLinkId> findAllByChatId(long chatId) {
        try {
            return dslContext
                .select().from(CHATIDLINKID)
                .where(CHATIDLINKID.CHAT_ID.eq(chatId))
                .fetchInto(Chatidlinkid.class)
                .stream()
                .map(DTO_CONVERTER_LAMBDA)
                .toList();
        } catch (NullPointerException e) {
            return List.of();
        }
    }

    @Override
    public List<ChatIdLinkId> findAllByLinkId(long linkId) {
        try {
            return dslContext
                .select().from(CHATIDLINKID)
                .where(CHATIDLINKID.LINK_ID.eq(linkId))
                .fetchInto(Chatidlinkid.class)
                .stream()
                .map(DTO_CONVERTER_LAMBDA)
                .toList();
        } catch (NullPointerException e) {
            return List.of();
        }
    }

}
