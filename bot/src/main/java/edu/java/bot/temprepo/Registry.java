package edu.java.bot.temprepo;

import edu.java.bot.users.User;
import java.util.Optional;

public interface Registry {

    Optional<User> getById(long id);

    void saveInDb(long id);

}
