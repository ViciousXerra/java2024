package edu.java.bot.memoryuserrepository;

import edu.java.bot.users.User;
import java.util.Optional;

public interface UserRepository {

    Optional<User> getById(long id);

    void saveInDb(long id);

}
