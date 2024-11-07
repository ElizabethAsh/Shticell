package impl.engine;

public class UserPermissionDTO {
    private String userName;
    private String permissionType;
    private String approvalStatus;

    public UserPermissionDTO(String userName, String permissionType, String approvalStatus) {
        this.userName = userName;
        this.permissionType = permissionType;
        this.approvalStatus = approvalStatus;
    }

    public String getUserName() {
        return userName;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }
}
