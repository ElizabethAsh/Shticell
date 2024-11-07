package permission;

import impl.engine.PendingPermissionDTO;
import impl.engine.UserPermissionDTO;

import java.util.*;

public class SheetPermissionManager {
    private Map<String, List<PermissionRequestDetails>> permissionRequests;
    private Map<String, PermissionsType> userActivePermissions;

    public SheetPermissionManager() {
        permissionRequests = new LinkedHashMap<>();
        userActivePermissions = new HashMap<>();
    }

    public void setUserPermissions(String userName, PermissionsType permission) {
        userActivePermissions.put(userName, permission);
        PermissionRequestDetails approvedRequest = new PermissionRequestDetails(permission, RequestStatus.APPROVED);

        permissionRequests
                .computeIfAbsent(userName, k -> new ArrayList<>())
                .add(approvedRequest);
    }

    public PermissionsType getUserPermissionType(String userName) {
        return userActivePermissions.getOrDefault(userName, PermissionsType.NONE);
    }

    public void addPendingRequest(String userName, PermissionsType permission) {
        permissionRequests
                .computeIfAbsent(userName, k -> new ArrayList<>())
                .add(new PermissionRequestDetails(permission, RequestStatus.PENDING));
    }

    public void approveUserPermission(String userName, UUID requestId) {
        List<PermissionRequestDetails> requests = permissionRequests.get(userName);
        if (requests != null) {
            requests.stream()
                    .filter(request -> request.getRequestId().equals(requestId) && request.getStatus() == RequestStatus.PENDING)
                    .findFirst()
                    .ifPresent(request -> {
                        request.setStatus(RequestStatus.APPROVED);
                        userActivePermissions.put(userName, request.getPermission());
                    });
        }
    }

    public void rejectUserPermission(UUID requestId) {
        permissionRequests.values().forEach(requests ->
                requests.stream()
                        .filter(request -> request.getRequestId().equals(requestId) && request.getStatus() == RequestStatus.PENDING)
                        .findFirst()
                        .ifPresent(request -> request.setStatus(RequestStatus.REJECTED))
        );
    }

    // מחזירים את כל הבקשות הממתינות של כל המשתמשים
    public List<PendingPermissionDTO> getPendingPermissionDTOs(String sheetName) {
        List<PendingPermissionDTO> pendingPermissionDTOs = new ArrayList<>();

        permissionRequests.forEach((userName, requests) -> {
            requests.stream()
                    .filter(request -> request.getStatus() == RequestStatus.PENDING)
                    .forEach(request -> pendingPermissionDTOs.add(
                            new PendingPermissionDTO(userName, request.getPermission().name(), sheetName, request.getRequestId())
                    ));
        });

        return pendingPermissionDTOs;
    }

    // מחזירים את כל בקשות ההרשאה כ-DTOs עבור המשתמשים
    public List<UserPermissionDTO> getUsersPermissionsAsDTOs() {
        List<UserPermissionDTO> dtoList = new ArrayList<>();

        permissionRequests.forEach((userName, requests) -> {
            for (PermissionRequestDetails request : requests) {
                dtoList.add(new UserPermissionDTO(userName, request.getPermission().name(), request.getStatus().name()));
            }
        });

        return dtoList;
    }
}

