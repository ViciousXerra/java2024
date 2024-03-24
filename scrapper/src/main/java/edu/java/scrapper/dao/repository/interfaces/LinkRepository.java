package edu.java.scrapper.dao.repository.interfaces;

import edu.java.scrapper.dao.dto.Link;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface LinkRepository {

    Link add(String url);

    Link remove(String url);

    void removeByIds(long... linkIds);

    Optional<Link> findByUrl(String url);

    List<Link> findAll();

    List<Link> findUpToCheck(int limit);

    void modifyUpdatedAtTimestamp(String url, ZonedDateTime newUpdatedAt);

    void modifyCheckedAtTimestamp(String url, ZonedDateTime newCheckedAt);

}
