package manager.sheetManager;

import engine.api.Engine;
import impl.engine.*;
import impl.ui.*;
import permission.PermissionsType;
import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SheetManager {
    private final Map<String, SingleSheetEntry> sheetDataMap;
    private final Map<String, ReentrantReadWriteLock> sheetLocks;
    private final Engine engine;


    public SheetManager(Engine engine) {
        this.sheetDataMap = new HashMap<>();
        this.engine = engine;
        this.sheetLocks = new HashMap<>();
    }

    public void addSheet(Sheet sheet, String uploaderUsername) {
        String sheetName = sheet.getName();
        if (!sheetDataMap.containsKey(sheetName)) {
            SingleSheetEntry newSheet = new SingleSheetEntry(sheet, uploaderUsername);
            sheetDataMap.put(sheetName, newSheet);
            initializeSheetPermissions(newSheet, uploaderUsername);
            sheetLocks.putIfAbsent(sheetName, new ReentrantReadWriteLock());
        } else {
            throw new RuntimeException("Sheet with the name '" + sheetName + "' already exists.");
        }
    }

    private void initializeSheetPermissions(SingleSheetEntry sheet, String uploaderUsername) {
        sheet.setUserPermissions(uploaderUsername, PermissionsType.OWNER);

        Set<String> userNames = engine.getUsersNames();
        userNames.stream()
                .filter(name -> !name.equals(uploaderUsername))
                .forEach(name -> sheet.setUserPermissions(name, PermissionsType.NONE));
    }

    public List<SheetDetailsDTO> getSheetsInfo(String currentUsername) {
        List<SheetDetailsDTO> sheetDetails = new ArrayList<>();
        for (SingleSheetEntry sheet : sheetDataMap.values()) {
            String sheetName = sheet.getSheet().getName();
            String usernameOfUploader = sheet.getUsername();
            String size = sheet.getSheet().getSheetLayout().getRows() + "x" + sheet.getSheet().getSheetLayout().getColumns();
            PermissionsType permission = sheet.getUserPermissionType(currentUsername);
            sheetDetails.add(new SheetDetailsDTO(sheetName, permission.name(), usernameOfUploader, size));
        }
        return sheetDetails;
    }

    public void requestPermission(PermissionRequestDTO data) {
        Optional.ofNullable(sheetDataMap.get(data.getSheetName()))
                .ifPresentOrElse(
                        sheet -> {
                            PermissionsType permissionType = convertToPermissionType(data.getPermissionType());
                            sheet.askForSheetPermission(data.getUsername(), permissionType);
                        },
                        () -> { throw new RuntimeException("Sheet with the name '" + data.getSheetName() + "' does not exist."); }
                );
    }

    public void approveUserPermissionRequest(String userName, String sheetName, UUID requestId) {
        Optional.ofNullable(sheetDataMap.get(sheetName))
                .ifPresentOrElse(
                        sheet -> sheet.approvePermissionRequest(userName, requestId),
                        () -> { throw new RuntimeException("Sheet with the name '" + sheetName + "' does not exist."); }
                );
    }

    public void rejectUserPermissionRequest(String sheetName, UUID requestId) {
        Optional.ofNullable(sheetDataMap.get(sheetName))
                .ifPresentOrElse(
                        sheet -> sheet.rejectPermissionRequest(requestId),
                        () -> { throw new RuntimeException("Sheet with the name '" + sheetName + "' does not exist."); }
                );
    }

    public List<UserPermissionDTO> getPermissionsTableForSheet(String sheetName) {
        return Optional.ofNullable(sheetDataMap.get(sheetName))
                .map(SingleSheetEntry::getUsersPermissionsAsDTOs)
                .orElseThrow(() -> new RuntimeException("Sheet with the name '" + sheetName + "' does not exist."));
    }

    public List<PendingPermissionDTO> getPendingPermissions(String userName) {
        List<PendingPermissionDTO> pendingPermissions = new ArrayList<>();
        for (SingleSheetEntry sheetEntry : sheetDataMap.values()) {
            if (sheetEntry.getUsername().equals(userName)) {
                pendingPermissions.addAll(sheetEntry.getPendingPermissions());
            }
        }

        return pendingPermissions;
    }

    public void handlePermissionDecision(PermissionRequestDecisionDTO decisionDTO) {
        UUID requestId = decisionDTO.getRequestId();
        String sheetName = decisionDTO.getSheetName();
        String decision = decisionDTO.getDecision();
        String username = decisionDTO.getUsername();

        if ("APPROVED".equalsIgnoreCase(decision)) {
            approveUserPermissionRequest(username, sheetName, requestId);
        } else if ("DENIED".equalsIgnoreCase(decision)) {
            rejectUserPermissionRequest(sheetName, requestId);
        } else {
            throw new RuntimeException("Invalid decision: " + decision);
        }
    }

    public List<String> getAvailablePermissionsForUser(String sheetName, String currentUsername) {
        return Optional.ofNullable(sheetDataMap.get(sheetName))
                .map(sheetEntry -> sheetEntry.getAvailablePermissionsForUser(currentUsername))
                .orElseThrow(() -> new RuntimeException("Sheet with the name '" + sheetName + "' does not exist."));
    }

    public void addUserPermissionsForAllSheets(String userName) {
        for (SingleSheetEntry sheetEntry : sheetDataMap.values()) {
            sheetEntry.setUserPermissions(userName, PermissionsType.NONE);
        }
    }

    private PermissionsType convertToPermissionType(String permissionType) {
        try {
            return PermissionsType.valueOf(permissionType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid permission type: " + permissionType);
        }
    }

    public void checkNotNonePermission(String username, PermissionsType permission) {
        if (permission == PermissionsType.NONE) {
            throw new RuntimeException("User '" + username + "' does not have the required permission.");
        }
    }

    public void checkWriterOrOwnerPermission(String username, PermissionsType permission) {
        if (permission != PermissionsType.WRITER && permission != PermissionsType.OWNER) {
            throw new RuntimeException("User '" + username + "' does not have sufficient permission to perform this action.");
        }
    }

    public boolean isUserVersionOutdated(String sheetName, String currentUsername, int latestSheetVersion){
        return Optional.ofNullable(sheetDataMap.get(sheetName))
                .map(sheetEntry -> sheetEntry.getUserVersionManager().isUserVersionOutdated(currentUsername, latestSheetVersion))
                .orElseThrow(() -> new RuntimeException("Sheet with the name '" + sheetName + "' does not exist."));

    }

    public SingleSheetEntry getSheetEntry(String sheetName) {
        return Optional.ofNullable(sheetDataMap.get(sheetName))
                .orElseThrow(() -> new RuntimeException("Sheet with the name '" + sheetName + "' does not exist."));
    }

    public SheetDTO getSheetForView(String sheetName, String username) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            SingleSheetEntry sheetEntry = getSheetEntry(sheetName);
            PermissionsType permission = sheetEntry.getUserPermissionType(username);
            checkNotNonePermission(username, permission);

            Sheet sheet = sheetEntry.getSheet();
            int latestVersion = sheet.getVersion();
            sheetEntry.setUserCurrentSheetVersion(username, latestVersion);

            return new SheetDTO(sheet, permission.name());
        } finally {
            readLock.unlock();
        }
    }

    public CellDTO displayCell(String sheetName, String username, String cellId) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            Sheet userSheetVersion = getUserSheetVersion(sheetName, username);
            Coordinate coordinate = CoordinateFactory.parseCoordinate(cellId);

            userSheetVersion.getSheetLayout().coordinateWithinBounds(coordinate);
            Cell cell = userSheetVersion.getCell(coordinate);

            return (cell != null) ? new CellDTO(cell) : null;
        } finally {
            readLock.unlock();
        }
    }

    public SheetDTO updateCellValue(String sheetName, String username, String coordinateString, String newCellValue) {
        ReentrantReadWriteLock.WriteLock writeLock = sheetLocks.get(sheetName).writeLock();
        writeLock.lock();
        try {
            Coordinate coordinate = CoordinateFactory.parseCoordinate(coordinateString);
            SingleSheetEntry sheetEntry = getLatestSheetEntryWithWriteOrOwnerPermission(sheetName, username);
            Sheet sheet = sheetEntry.getSheet();

            sheet = sheet.updateCellValueAndCalculate(coordinate.getRow(), coordinate.getColumn(), newCellValue, username);
            sheetEntry.setSheet(sheet);
            sheetEntry.setUserCurrentSheetVersion(username, sheet.getVersion());

            return new SheetDTO(sheet, null);
        } finally {
            writeLock.unlock();
        }
    }

    public NumOfVersionsDTO displayVersions(String sheetName, String username) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            SingleSheetEntry sheetEntry = getSheetEntry(sheetName);
            PermissionsType permission = sheetEntry.getUserPermissionType(username);
            checkNotNonePermission(username, permission);

            Sheet sheet = sheetEntry.getSheet();
            return new NumOfVersionsDTO(sheet.getVersionManager().getNumberOfCellsChangeInVersion());
        } finally {
            readLock.unlock();
        }
    }

    public SheetDTO displayVersion(String sheetName, String username, int version) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            SingleSheetEntry sheetEntry = getSheetEntry(sheetName);
            PermissionsType permission = sheetEntry.getUserPermissionType(username);
            checkNotNonePermission(username, permission);

            Sheet sheet = sheetEntry.getSheet();
            Sheet requiredSheet = sheet.getVersionManager().getVersion(version);
            return new SheetDTO(requiredSheet, permission.name());
        } finally {
            readLock.unlock();
        }
    }

    public Set<RangeNameDTO> getRangesForDeleteRequest(String sheetName, String username) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            SingleSheetEntry sheetEntry = getLatestSheetEntryWithWriteOrOwnerPermission(sheetName, username);
            Set<String> rangeNames = sheetEntry.getSheet().getRanges();
            Set<RangeNameDTO> rangeNameDTOs = new HashSet<>();
            for (String rangeName : rangeNames) {
                rangeNameDTOs.add(new RangeNameDTO(rangeName));
            }

            return rangeNameDTOs;
        } finally {
            readLock.unlock();
        }
    }

    public Set<RangeNameDTO> getRangesForDisplayRequest(String sheetName, String username) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            Sheet userSheetVersion = getUserSheetVersion(sheetName, username);
            Set<String> rangeNames = userSheetVersion.getRanges();

            Set<RangeNameDTO> rangeNameDTOs = new HashSet<>();
            for (String rangeName : rangeNames) {
                rangeNameDTOs.add(new RangeNameDTO(rangeName));
            }
            return rangeNameDTOs;
        } finally {
            readLock.unlock();
        }
    }

    public SheetDTO addNewRange(String sheetName, String username, String rangeName, String from, String to) {
        ReentrantReadWriteLock.WriteLock writeLock = sheetLocks.get(sheetName).writeLock();
        writeLock.lock();
        try {
            SingleSheetEntry sheetEntry = getLatestSheetEntryWithWriteOrOwnerPermission(sheetName, username);
            Sheet sheet = sheetEntry.getSheet();
            sheet.addRange(rangeName, from, to);
            return new SheetDTO(sheet, null);
        } finally {
            writeLock.unlock();
        }
    }

    public void deleteRange(String sheetName, String username, String rangeName) {
        ReentrantReadWriteLock.WriteLock writeLock = sheetLocks.get(sheetName).writeLock();
        writeLock.lock();
        try {
            SingleSheetEntry sheetEntry = getLatestSheetEntryWithWriteOrOwnerPermission(sheetName, username);
            Sheet sheet = sheetEntry.getSheet();
            sheet.deleteRange(rangeName);
        } finally {
            writeLock.unlock();
        }
    }

    public RangeDTO displayRange(String sheetName, String username, String rangeName) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            Sheet userSheetVersion = getUserSheetVersion(sheetName, username);
            return new RangeDTO(userSheetVersion.getCoordinatesInRange(rangeName));
        } finally {
            readLock.unlock();
        }
    }

    public AvailableColumnsForSortDTO getAvailableColumnsForSort(String sheetName, String username, RangeBoundariesForSortingOrFilterDTO rangeBoundaries) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            Sheet userSheetVersion = getUserSheetVersion(sheetName, username);
            List<String> columns = userSheetVersion.getAvailableColumnsForSort(rangeBoundaries.getFrom(), rangeBoundaries.getTo());
            return new AvailableColumnsForSortDTO(columns);
        } finally {
            readLock.unlock();
        }
    }

    public SheetDTO sortSheet(String sheetName, String username, SortRequestDTO sortRequest) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            Sheet userSheetVersion = getUserSheetVersion(sheetName, username);
            Sheet sortedSheet = userSheetVersion.getSortedSheet(sortRequest.getFrom(), sortRequest.getTo(), sortRequest.getSelectedColumns());
            return new SheetDTO(sortedSheet, null);
        } finally {
            readLock.unlock();
        }
    }

    public AvailableColumnsForFilterDTO getAvailableColumnsForFilter(String sheetName, String username, RangeBoundariesForSortingOrFilterDTO rangeBoundaries) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            Sheet userSheetVersion = getUserSheetVersion(sheetName, username);
            List<String> columns = userSheetVersion.getAvailableColumnsForFilter(rangeBoundaries.getFrom(), rangeBoundaries.getTo());
            return new AvailableColumnsForFilterDTO(columns);
        } finally {
            readLock.unlock();
        }
    }

    public UniqueValuesForColumnDTO getDistinctValuesForColumnInRange(String sheetName, String username, UniqueValuesForColumnRequestDTO columnRequest) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            Sheet userSheetVersion = getUserSheetVersion(sheetName, username);
            Set<String> distinctValues = userSheetVersion.getDistinctValuesForColumnInRange(columnRequest.getColumn(), columnRequest.getFrom(), columnRequest.getTo());
            return new UniqueValuesForColumnDTO(distinctValues);
        } finally {
            readLock.unlock();
        }
    }

    public SheetDTO filterSheet(String sheetName, String username, FilterRequestDTO filterRequest) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            Sheet userSheetVersion = getUserSheetVersion(sheetName, username);
            Sheet filteredSheet = userSheetVersion.getFilteredSheet(filterRequest.getValuesForFilter(), filterRequest.getFrom(), filterRequest.getTo());
            return new SheetDTO(filteredSheet, null);
        } finally {
            readLock.unlock();
        }
    }

    public boolean checkIsNewVersionAvailable(String sheetName, String username) {
        SingleSheetEntry sheetEntry = getSheetEntry(sheetName);
        PermissionsType permission = sheetEntry.getUserPermissionType(username);
        checkNotNonePermission(username, permission);
        int latestVersion = sheetEntry.getSheet().getVersion();

        return isUserVersionOutdated(sheetName, username, latestVersion);
    }

    public void updateCellBackground(String sheetName, String username, String cellID, String newColor) {
        ReentrantReadWriteLock.WriteLock writeLock = sheetLocks.get(sheetName).writeLock();
        writeLock.lock();
        try {
            SingleSheetEntry sheetEntry = getLatestSheetEntryWithWriteOrOwnerPermission(sheetName, username);
            Sheet sheet = sheetEntry.getSheet();
            sheet.updateCellBackground(CoordinateFactory.parseCoordinate(cellID), newColor);
        } finally {
            writeLock.unlock();
        }
    }

    public void updateCellTextColor(String sheetName, String username, String cellID, String newColor) {
        ReentrantReadWriteLock.WriteLock writeLock = sheetLocks.get(sheetName).writeLock();
        writeLock.lock();
        try {
            SingleSheetEntry sheetEntry = getLatestSheetEntryWithWriteOrOwnerPermission(sheetName, username);
            Sheet sheet = sheetEntry.getSheet();
            sheet.updateCellTextColor(CoordinateFactory.parseCoordinate(cellID), newColor);
        } finally {
            writeLock.unlock();
        }
    }

    public CellDTO isCellOriginalValueNumeric(String sheetName, String username, String cellId) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            Sheet userSheetVersion = getUserSheetVersion(sheetName, username);
            Coordinate coordinate = CoordinateFactory.parseCoordinate(cellId);
            userSheetVersion.getSheetLayout().coordinateWithinBounds(coordinate);

            return userSheetVersion.checkIfCellAllowsDynamicAnalysis(coordinate);
        } finally {
            readLock.unlock();
        }
    }

    public SheetDTO updateCellValueInDynamicAnalysis(String sheetName, String username, String coordinateString, String newCellValue) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            Sheet userSheetVersion = getUserSheetVersion(sheetName, username);
            Sheet tempSheet = userSheetVersion.copySheet();

            Coordinate coordinate = CoordinateFactory.parseCoordinate(coordinateString);
            tempSheet.updateCellValueInDynamicAnalysis(coordinate, newCellValue);
            return new SheetDTO(tempSheet, null);
        } finally {
            readLock.unlock();
        }
    }

    private SingleSheetEntry getLatestSheetEntryWithWriteOrOwnerPermission(String sheetName, String username) {
        SingleSheetEntry sheetEntry = getSheetEntry(sheetName);
        PermissionsType permission = sheetEntry.getUserPermissionType(username);
        checkWriterOrOwnerPermission(username, permission);

        int latestVersion = sheetEntry.getSheet().getVersion();
        if (isUserVersionOutdated(sheetName, username, latestVersion)) {
            throw new RuntimeException("User version outdated, please update the sheet to the current version");
        }

        return sheetEntry;
    }

    private Sheet getUserSheetVersion(String sheetName, String username) {
        SingleSheetEntry sheetEntry = getSheetEntry(sheetName);
        PermissionsType permission = sheetEntry.getUserPermissionType(username);
        checkNotNonePermission(username, permission);

        int latestVersion = sheetEntry.getSheet().getVersion();
        int userVersion = sheetEntry.getUserVersionManager().getUserCurrentSheetVersion(username);

        Sheet userSheetVersion;
        if (userVersion == latestVersion) {
            userSheetVersion = sheetEntry.getSheet();
        } else {
            userSheetVersion = sheetEntry.getSheet().getVersionManager().getVersion(userVersion);
        }

        return userSheetVersion;
    }


}
