package edu.java.scrapper.dao.repository.interfaces;

import edu.java.scrapper.dao.dto.Link;
import java.util.List;

public interface LinkRepository {

    Link add(String url);

    Link remove(String url);

    List<Link> findAll();

}
