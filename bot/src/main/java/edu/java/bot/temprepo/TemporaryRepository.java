package edu.java.bot.temprepo;

import edu.java.bot.users.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class TemporaryRepository implements Registry {

    private final Map<Long, User> registeredUsersMap;

    public TemporaryRepository() {
        registeredUsersMap = new HashMap<>();
    }

    public Optional<User> getById(long id) {
        return registeredUsersMap.containsKey(id) ? Optional.of(registeredUsersMap.get(id)) : Optional.empty();
    }

    public void saveInDb(long id) {
        registeredUsersMap.put(id, new User());
    }

}
