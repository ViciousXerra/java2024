package edu.java.scrapper.dao.service.interfaces;

import edu.java.scrapper.dao.dto.Link;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface LinkUpdater {

    List<Link> update();

}
