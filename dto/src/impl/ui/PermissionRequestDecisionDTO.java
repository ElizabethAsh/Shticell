package impl.ui;

import java.util.UUID;


public class PermissionRequestDecisionDTO {
    private String username;
    private String sheetName;
    private String permissionType;
    private String decision;
    private UUID requestId;

    public PermissionRequestDecisionDTO(String username, String sheetName, String permissionType, String decision, UUID requestId) {
        this.username = username;
        this.sheetName = sheetName;
        this.permissionType = permissionType;
        this.decision = decision;
        this.requestId = requestId;
    }

    public String getUsername() { return username; }

    public String getSheetName() { return sheetName; }

    public String getPermissionType() { return permissionType; }

    public String getDecision() { return decision; }

    public UUID getRequestId() { return requestId; }
}

