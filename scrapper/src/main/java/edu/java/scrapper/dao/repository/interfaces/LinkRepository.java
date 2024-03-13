package edu.java.scrapper.dao.repository.interfaces;

import edu.java.scrapper.dao.repository.dto.Link;
import java.time.ZonedDateTime;
import java.util.List;

public interface LinkRepository {

    Link add(String url);

    Link remove(String url);

    List<Link> findAll();

    List<Link> findUpToCheck(int limit);

    void modifyUpdatedAtTimestamp(String url, ZonedDateTime newCheckedAt, ZonedDateTime newUpdatedAt);

    void modifyCheckedAtTimestamp(String url, ZonedDateTime newCheckedAt);

}
