package manager.userManager;

import engine.api.Engine;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UserManager {
    Engine engine;
    private final Set<String> usersSet;

    public UserManager(Engine engine) {
        usersSet = new HashSet<>();
        this.engine = engine;
    }

    public synchronized void addUser(String username) {
        if (isUserExists(username)) {
            throw new RuntimeException("Username already exists.");
        }

        usersSet.add(username);
    }

    public synchronized void removeUser(String username) {
        usersSet.remove(username);
    }

    public synchronized Set<String> getUsers() {
        return Collections.unmodifiableSet(usersSet);
    }

    public boolean isUserExists(String username) {
        return usersSet.contains(username);
    }
}
