package edu.java.scrapper.dao.repository.jpa.repositories;

import edu.java.scrapper.dao.repository.jpa.entities.LinkEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLinkRepository extends JpaRepository<LinkEntity, Long> {

    Optional<LinkEntity> findByUrl(String url);

    List<LinkEntity> findByOrderByCheckedAtAsc(Limit limit);

}
