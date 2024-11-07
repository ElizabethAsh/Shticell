package manager.userVersionManager;

import permission.PermissionsType;
import sheet.api.Sheet;

import java.util.HashMap;
import java.util.Map;

public class UserVersionManager {
    private Map<String, Integer> usersCurrentSheetVersion;

    public UserVersionManager() {
        usersCurrentSheetVersion = new HashMap<>();
    }

    public int getUserCurrentSheetVersion(String userName) {
        return usersCurrentSheetVersion.getOrDefault(userName, 1);
    }

    public void setUserCurrentSheetVersion(String userName, int version) {
        usersCurrentSheetVersion.put(userName, version);
    }

    public boolean isUserVersionOutdated(String userName, int latestSheetVersion) {
        return getUserCurrentSheetVersion(userName) < latestSheetVersion;
    }
}
