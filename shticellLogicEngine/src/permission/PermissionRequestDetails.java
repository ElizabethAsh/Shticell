package permission;

import java.util.UUID;

public class PermissionRequestDetails {
    private final UUID requestId;
    private PermissionsType permission;
    private RequestStatus status;

    public PermissionRequestDetails(PermissionsType permission, RequestStatus status) {
        this.requestId = UUID.randomUUID();
        this.permission = permission;
        this.status = status;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public PermissionsType getPermission() {
        return permission;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}
