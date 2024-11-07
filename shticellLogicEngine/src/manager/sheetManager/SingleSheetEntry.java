package manager.sheetManager;

import impl.engine.PendingPermissionDTO;
import impl.engine.UserPermissionDTO;
import manager.userVersionManager.UserVersionManager;
import permission.PermissionsType;
import permission.SheetPermissionManager;
import sheet.api.Sheet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SingleSheetEntry {
    private Sheet sheet;
    private final String uploaderUsername;
    private final SheetPermissionManager sheetPermissionManager;
    private final UserVersionManager userVersionManager;

    public SingleSheetEntry(Sheet sheet, String uploaderUsername) {
        this.sheet = sheet;
        this.uploaderUsername = uploaderUsername;
        this.sheetPermissionManager = new SheetPermissionManager();
        userVersionManager = new UserVersionManager();
    }

    public Sheet getSheet() { return sheet; }

    public String getUsername() {
        return uploaderUsername;
    }

    public SheetPermissionManager getSheetPermissionManager() {
        return sheetPermissionManager;
    }

    public void askForSheetPermission(String userName, PermissionsType permissionType) {
        sheetPermissionManager.addPendingRequest(userName, permissionType);
    }

    public void approvePermissionRequest(String userName, UUID requestId) {
        sheetPermissionManager.approveUserPermission(userName, requestId);
    }

    public void rejectPermissionRequest(UUID requestId) {
        sheetPermissionManager.rejectUserPermission(requestId);
    }

    public List<UserPermissionDTO> getUsersPermissionsAsDTOs() {
        return sheetPermissionManager.getUsersPermissionsAsDTOs();
    }

    public List<PendingPermissionDTO> getPendingPermissions() {
        return sheetPermissionManager.getPendingPermissionDTOs(sheet.getName());
    }

    public void setUserPermissions(String name, PermissionsType permissionsType) {
        sheetPermissionManager.setUserPermissions(name, permissionsType);
    }

    public PermissionsType getUserPermissionType(String currentUsername) {
        return sheetPermissionManager.getUserPermissionType(currentUsername);
    }

    public List<String> getAvailablePermissionsForUser(String currentUsername) {
        PermissionsType userPermission = sheetPermissionManager.getUserPermissionType(currentUsername);
        List<String> availablePermissions = new ArrayList<>();

        if (userPermission == PermissionsType.OWNER) {
            throw new IllegalArgumentException("The user is an OWNER and cannot receive new permissions.");
        } else if (userPermission == PermissionsType.NONE) {
            availablePermissions.add(PermissionsType.WRITER.name());
            availablePermissions.add(PermissionsType.READER.name());
        } else if (userPermission == PermissionsType.WRITER) {
            availablePermissions.add(PermissionsType.READER.name());
        } else if (userPermission == PermissionsType.READER) {
            availablePermissions.add(PermissionsType.WRITER.name());
        }

        return availablePermissions;
    }

    public UserVersionManager getUserVersionManager(){
        return userVersionManager;
    }

    public void setUserCurrentSheetVersion(String userName, int version){
        userVersionManager.setUserCurrentSheetVersion(userName, version);
    }

    public void setSheet(Sheet newSheet) {
        this.sheet = newSheet;
    }

}
