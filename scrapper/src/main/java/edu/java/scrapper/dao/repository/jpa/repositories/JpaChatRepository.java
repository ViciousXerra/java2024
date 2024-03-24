package edu.java.scrapper.dao.repository.jpa.repositories;

import edu.java.scrapper.dao.repository.jpa.entities.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaChatRepository extends JpaRepository<ChatEntity, Long> {
}
