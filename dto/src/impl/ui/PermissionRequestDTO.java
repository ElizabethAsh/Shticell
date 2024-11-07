package impl.ui;

public class PermissionRequestDTO {
    private String sheetName;
    private String username;
    private String permissionType;

    public PermissionRequestDTO(String sheetName, String username, String permissionType) {
        this.sheetName = sheetName;
        this.username = username;
        this.permissionType = permissionType;
    }

    public String getSheetName() { return sheetName; }
    public String getUsername() { return username; }
    public String getPermissionType() { return permissionType; }
}
