package engine.api;

import impl.engine.*;
import impl.ui.*;

import java.util.List;
import java.util.Set;

public interface Engine {
    void loadSheet(SheetLoadDTO data);
    CellDTO displayCell(CellIdDTO cellId, SheetUserIdentifierDTO sheetUserIdentifierDTO);
    SheetDTO updateCellValue(CellUpdateRequestDTO data, SheetUserIdentifierDTO sheetUserIdentifierDTO);
    NumOfVersionsDTO displayVersions(SheetUserIdentifierDTO sheetUserIdentifierDTO);
    SheetDTO displayVersion(DisplayVersionSheetRequestDTO dto, SheetUserIdentifierDTO sheetUserIdentifierDTO);
    boolean checkIsNewVersionAvailable(SheetUserIdentifierDTO sheetUserIdentifierDTO);
    Set<RangeNameDTO> getRangesForDeleteRequest(SheetUserIdentifierDTO sheetUserIdentifierDTO);
    Set<RangeNameDTO> getRangesForDisplayRequest(SheetUserIdentifierDTO sheetUserIdentifierDTO);
    SheetDTO addNewRange(AddNewRangeRequestDTO data, SheetUserIdentifierDTO sheetUserIdentifierDTO);
    void deleteRange(RangeNameDTO data, SheetUserIdentifierDTO sheetUserIdentifierDTO);
    RangeDTO displayRange(RangeNameDTO rangeNameDTO, SheetUserIdentifierDTO sheetUserIdentifierDTO);
    AvailableColumnsForSortDTO getAvailableColumnsForSort(RangeBoundariesForSortingOrFilterDTO dtoFromUi, SheetUserIdentifierDTO sheetUserIdentifierDTO);
    SheetDTO sortSheet(SortRequestDTO dtoFromUi, SheetUserIdentifierDTO sheetUserIdentifierDTO);
    void updateCellBackground(UpdateCellStyleRequestDTO updateRequestDTO, SheetUserIdentifierDTO sheetUserIdentifierDTO);
    void updateCellTextColor(UpdateCellStyleRequestDTO updateRequestDTO, SheetUserIdentifierDTO sheetUserIdentifierDTO);
    AvailableColumnsForFilterDTO getAvailableColumnsForFilter(RangeBoundariesForSortingOrFilterDTO dtoFromUi, SheetUserIdentifierDTO sheetUserIdentifierDTO);
    UniqueValuesForColumnDTO getDistinctValuesForColumnInRange(UniqueValuesForColumnRequestDTO dtoFromUi, SheetUserIdentifierDTO sheetUserIdentifierDTO);
    SheetDTO filterSheet(FilterRequestDTO dtoFromUi, SheetUserIdentifierDTO sheetUserIdentifierDTO);
    CellDTO isCellOriginalValueNumeric(CellIdDTO cellIdDTO, SheetUserIdentifierDTO sheetUserIdentifierDTO);
    SheetDTO updateCellValueInDynamicAnalysis(CellUpdateRequestDTO dataFromUiToUpdateCellValue, SheetUserIdentifierDTO sheetUserIdentifierDTO);
    void addUser(UserNameDTO userNameDTO);
    boolean isUserExists(UserNameDTO userNameDTO);
    Set<String> getUsersNames();
    List<SheetDetailsDTO> getSheetsInfo(UserNameDTO userNameDTO);
    SheetDTO getSheetForView(SheetUserIdentifierDTO dtoFromUi);
    List<UserPermissionDTO> getPermissionsTableForSheet(CurrentSheetNameDTO currentSheetNameDTO);
    void requestPermission(PermissionRequestDTO data);
    List<PendingPermissionDTO> getPendingPermissions(UserNameDTO userNameDTO);
    void handlePermissionDecision(PermissionRequestDecisionDTO decisionDTO);
    List<String> getAvailablePermissionsForUser(SheetUserIdentifierDTO dtoFromUi);
}

