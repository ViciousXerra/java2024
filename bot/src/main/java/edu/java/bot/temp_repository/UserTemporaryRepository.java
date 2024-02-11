package edu.java.bot.temp_repository;

import edu.java.bot.users.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserTemporaryRepository {

    private final Map<Long, User> tempStorage;

    public UserTemporaryRepository() {
        tempStorage = new HashMap<>();
    }

    public void save(User user) {
        tempStorage.put(user.getId(), user);
    }

    public Optional<User> getPossibleUser(long id) {
        return tempStorage.containsKey(id) ? Optional.of(tempStorage.get(id)) : Optional.empty();
    }

}
