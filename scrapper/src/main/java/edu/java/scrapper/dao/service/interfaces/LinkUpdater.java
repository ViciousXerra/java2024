package edu.java.scrapper.dao.service.interfaces;

import edu.java.scrapper.dao.dto.ChatIdLinkId;
import edu.java.scrapper.dao.dto.Link;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public interface LinkUpdater {

    List<Link> update();

    List<ChatIdLinkId> modifyUpdatedAtAndReturnRelations(Map<Link, ZonedDateTime> linkZonedDateTimeMap);

}
