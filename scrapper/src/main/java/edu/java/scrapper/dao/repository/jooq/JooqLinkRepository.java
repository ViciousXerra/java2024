package edu.java.scrapper.dao.repository.jooq;

import edu.java.scrapper.api.exceptions.UnhandledException;
import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.dao.repository.interfaces.LinkRepository;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import static domain.jooq.tables.Link.LINK;

@Repository
public class JooqLinkRepository implements LinkRepository {

    private final static Function<domain.jooq.tables.pojos.Link, Link> DTO_CONVERTER_LAMBDA =
        link -> new Link(
            link.getId(),
            link.getUrl(),
            link.getUpdatedAt().atZoneSameInstant(ZoneOffset.UTC),
            link.getCheckedAt().atZoneSameInstant(ZoneOffset.UTC)
        );
    private final DefaultDSLContext dslContext;

    @Autowired
    public JooqLinkRepository(DefaultDSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public Link add(String url) {
        try {
            return DTO_CONVERTER_LAMBDA.apply(
                dslContext
                    .insertInto(LINK)
                    .set(LINK.URL, url)
                    .returning()
                    .fetchOneInto(domain.jooq.tables.pojos.Link.class)
            );
        } catch (DataAccessException e) {
            throw new UnhandledException("URL must be unique", "Unable to insert url data");
        }
    }

    @Override
    public Link remove(String url) {
        try {
            return DTO_CONVERTER_LAMBDA.apply(
                dslContext
                    .delete(LINK)
                    .where(LINK.URL.eq(url))
                    .returning()
                    .fetchOneInto(domain.jooq.tables.pojos.Link.class)
            );
        } catch (NullPointerException e) {
            throw new UnhandledException("URL hasn't been founded", "Unable to delete url data");
        }
    }

    @Override
    public void removeByIds(long... linkIds) {
        if (linkIds.length == 0) {
            return;
        }
        dslContext
            .delete(LINK)
            .where(LINK.ID.in(Arrays.stream(linkIds).boxed().toList()))
            .execute();
    }

    @Override
    public Optional<Link> findByUrl(String url) {
        try {
            return Optional.of(DTO_CONVERTER_LAMBDA.apply(
                dslContext
                    .select().from(LINK)
                    .where(LINK.URL.eq(url))
                    .fetchOneInto(domain.jooq.tables.pojos.Link.class))
            );
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Link> findAll() {
        try {
            return dslContext
                .select().from(LINK)
                .fetchInto(domain.jooq.tables.pojos.Link.class)
                .stream()
                .map(DTO_CONVERTER_LAMBDA)
                .toList();
        } catch (NullPointerException e) {
            return List.of();
        }
    }

    @Override
    public List<Link> findUpToCheck(int limit) {
        try {
            List<Link> checkedLinks = dslContext
                .select().from(LINK)
                .orderBy(LINK.CHECKED_AT)
                .limit(limit)
                .fetchInto(domain.jooq.tables.pojos.Link.class)
                .stream()
                .map(DTO_CONVERTER_LAMBDA)
                .toList();
            ZonedDateTime currentCheckDateTime = ZonedDateTime.now();
            checkedLinks.forEach(link -> modifyCheckedAtTimestamp(link.url(), currentCheckDateTime));
            return checkedLinks;
        } catch (NullPointerException e) {
            return List.of();
        }
    }

    @Override
    public void modifyUpdatedAtTimestamp(String url, ZonedDateTime newUpdatedAt) {
        dslContext
            .update(LINK)
            .set(LINK.UPDATED_AT, newUpdatedAt.withZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime())
            .where(LINK.URL.eq(url))
            .execute();
    }

    @Override
    public void modifyCheckedAtTimestamp(String url, ZonedDateTime newCheckedAt) {
        dslContext
            .update(LINK)
            .set(LINK.CHECKED_AT, newCheckedAt.withZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime())
            .where(LINK.URL.eq(url))
            .execute();
    }

}
