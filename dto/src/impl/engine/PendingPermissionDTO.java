package impl.engine;

import java.util.UUID;


public class PendingPermissionDTO {
    private String username;
    private String permissionType;
    private String sheetName;
    private UUID requestId;

    public PendingPermissionDTO(String username, String permissionType, String sheetName, UUID requestId) {
        this.username = username;
        this.permissionType = permissionType;
        this.sheetName = sheetName;
        this.requestId = requestId;
    }

    public String getUsername() { return username; }

    public String getSheetName() { return sheetName; }

    public String getPermissionType() { return permissionType; }

    public UUID getRequestId() { return requestId; }
}
