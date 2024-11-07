package engine.impl;

import engine.api.Engine;
import file.STLSheetFactory.STLSheetFactory;
import file.schema.generated.STLSheet;
import impl.engine.*;
import impl.ui.*;
import sheet.api.Sheet;
import sheet.sheetFactory.SheetFactory;
import manager.sheetManager.SheetManager;
import manager.userManager.UserManager;
import java.util.List;
import java.util.Set;


public class EngineImpl implements Engine {
    private final SheetManager sheetsManager;
    private final UserManager userManager;

    public EngineImpl() {
        this.sheetsManager = new SheetManager(this);
        this.userManager = new UserManager(this);
    }

    @Override
    public void loadSheet(SheetLoadDTO data){
        STLSheet stlSheet = STLSheetFactory.createStlSheet(data.getFileStream());
        Sheet sheet = SheetFactory.createSheet(stlSheet);
        sheetsManager.addSheet(sheet, data.getUploaderUserName());
    }

    @Override
    public SheetDTO getSheetForView(SheetUserIdentifierDTO dtoFromUi) {
        return sheetsManager.getSheetForView(dtoFromUi.getSheetName(), dtoFromUi.getCurrentUsername());
    }

    @Override
    public CellDTO displayCell(CellIdDTO cellId, SheetUserIdentifierDTO sheetUserIdentifierDTO){
        return sheetsManager.displayCell(sheetUserIdentifierDTO.getSheetName(), sheetUserIdentifierDTO.getCurrentUsername(), cellId.getCellId());
    }

    @Override
    public SheetDTO updateCellValue(CellUpdateRequestDTO data, SheetUserIdentifierDTO sheetUserIdentifierDTO){
        return sheetsManager.updateCellValue(sheetUserIdentifierDTO.getSheetName(), sheetUserIdentifierDTO.getCurrentUsername(), data.getCoordinate(), data.getNewCellValue());
    }

    @Override
    public NumOfVersionsDTO displayVersions(SheetUserIdentifierDTO sheetUserIdentifierDTO) {
        return sheetsManager.displayVersions(sheetUserIdentifierDTO.getSheetName(), sheetUserIdentifierDTO.getCurrentUsername());
    }

    @Override
    public SheetDTO displayVersion(DisplayVersionSheetRequestDTO dto, SheetUserIdentifierDTO sheetUserIdentifierDTO) {
        return sheetsManager.displayVersion(sheetUserIdentifierDTO.getSheetName(), sheetUserIdentifierDTO.getCurrentUsername(), dto.getVersion());
    }

    @Override
    public Set<RangeNameDTO> getRangesForDeleteRequest(SheetUserIdentifierDTO sheetUserIdentifierDTO) {
        return sheetsManager.getRangesForDeleteRequest(sheetUserIdentifierDTO.getSheetName(), sheetUserIdentifierDTO.getCurrentUsername());
    }

    @Override
    public Set<RangeNameDTO> getRangesForDisplayRequest(SheetUserIdentifierDTO sheetUserIdentifierDTO) {
        return sheetsManager.getRangesForDisplayRequest(sheetUserIdentifierDTO.getSheetName(), sheetUserIdentifierDTO.getCurrentUsername());
    }

    @Override
    public SheetDTO addNewRange(AddNewRangeRequestDTO data, SheetUserIdentifierDTO sheetUserIdentifierDTO){
        return sheetsManager.addNewRange(sheetUserIdentifierDTO.getSheetName(), sheetUserIdentifierDTO.getCurrentUsername(), data.getRangeName(), data.getFrom(), data.getTo());
    }

    @Override
    public void deleteRange(RangeNameDTO rangeNameDTO, SheetUserIdentifierDTO sheetUserIdentifierDTO) {
        sheetsManager.deleteRange(sheetUserIdentifierDTO.getSheetName(), sheetUserIdentifierDTO.getCurrentUsername(), rangeNameDTO.getName());
    }

    @Override
    public RangeDTO displayRange(RangeNameDTO rangeNameDTO, SheetUserIdentifierDTO sheetUserIdentifierDTO) {
        return sheetsManager.displayRange(sheetUserIdentifierDTO.getSheetName(), sheetUserIdentifierDTO.getCurrentUsername(), rangeNameDTO.getName());
    }

    @Override
    public AvailableColumnsForSortDTO getAvailableColumnsForSort(RangeBoundariesForSortingOrFilterDTO dtoFromUi, SheetUserIdentifierDTO sheetUserIdentifierDTO) {
        return sheetsManager.getAvailableColumnsForSort(sheetUserIdentifierDTO.getSheetName(), sheetUserIdentifierDTO.getCurrentUsername(), dtoFromUi);
    }

    @Override
    public SheetDTO sortSheet(SortRequestDTO dtoFromUi, SheetUserIdentifierDTO sheetUserIdentifierDTO) {
        return sheetsManager.sortSheet(sheetUserIdentifierDTO.getSheetName(), sheetUserIdentifierDTO.getCurrentUsername(), dtoFromUi);
    }

    @Override
    public AvailableColumnsForFilterDTO getAvailableColumnsForFilter(RangeBoundariesForSortingOrFilterDTO dtoFromUi, SheetUserIdentifierDTO sheetUserIdentifierDTO) {
        return sheetsManager.getAvailableColumnsForFilter(sheetUserIdentifierDTO.getSheetName(), sheetUserIdentifierDTO.getCurrentUsername(), dtoFromUi);
    }

    @Override
    public UniqueValuesForColumnDTO getDistinctValuesForColumnInRange(UniqueValuesForColumnRequestDTO dtoFromUi, SheetUserIdentifierDTO sheetUserIdentifierDTO) {
        return sheetsManager.getDistinctValuesForColumnInRange(sheetUserIdentifierDTO.getSheetName(), sheetUserIdentifierDTO.getCurrentUsername(), dtoFromUi);
    }

    @Override
    public SheetDTO filterSheet(FilterRequestDTO dtoFromUi, SheetUserIdentifierDTO sheetUserIdentifierDTO) {
        return sheetsManager.filterSheet(sheetUserIdentifierDTO.getSheetName(), sheetUserIdentifierDTO.getCurrentUsername(), dtoFromUi);
    }

    @Override
    public void updateCellBackground(UpdateCellStyleRequestDTO updateRequestDTO, SheetUserIdentifierDTO sheetUserIdentifierDTO) {
        sheetsManager.updateCellBackground(sheetUserIdentifierDTO.getSheetName(), sheetUserIdentifierDTO.getCurrentUsername(), updateRequestDTO.getCellId(), updateRequestDTO.getNewColor());
    }

    @Override
    public void updateCellTextColor(UpdateCellStyleRequestDTO updateRequestDTO, SheetUserIdentifierDTO sheetUserIdentifierDTO) {
        sheetsManager.updateCellTextColor(sheetUserIdentifierDTO.getSheetName(), sheetUserIdentifierDTO.getCurrentUsername(), updateRequestDTO.getCellId(), updateRequestDTO.getNewColor());
    }

    @Override
    public boolean checkIsNewVersionAvailable(SheetUserIdentifierDTO sheetUserIdentifierDTO) {
        return sheetsManager.checkIsNewVersionAvailable(sheetUserIdentifierDTO.getSheetName(), sheetUserIdentifierDTO.getCurrentUsername());
    }

    @Override
    public CellDTO isCellOriginalValueNumeric(CellIdDTO cellIdDTO, SheetUserIdentifierDTO sheetUserIdentifierDTO) {
        return sheetsManager.isCellOriginalValueNumeric(sheetUserIdentifierDTO.getSheetName(), sheetUserIdentifierDTO.getCurrentUsername(), cellIdDTO.getCellId());
    }

    @Override
    public SheetDTO updateCellValueInDynamicAnalysis(CellUpdateRequestDTO dataFromUiToUpdateCellValue, SheetUserIdentifierDTO sheetUserIdentifierDTO) {
        return sheetsManager.updateCellValueInDynamicAnalysis(sheetUserIdentifierDTO.getSheetName(), sheetUserIdentifierDTO.getCurrentUsername(), dataFromUiToUpdateCellValue.getCoordinate(), dataFromUiToUpdateCellValue.getNewCellValue());
    }

    public void addUser(UserNameDTO userNameDTO) {
        String userName = userNameDTO.getName();
        userManager.addUser(userName);
        sheetsManager.addUserPermissionsForAllSheets(userName);
    }

    @Override
    public boolean isUserExists(UserNameDTO userNameDTO) {
        return userManager.isUserExists(userNameDTO.getName());
    }

    @Override
    public Set<String> getUsersNames() {
        return userManager.getUsers();
    }

    @Override
    public List<SheetDetailsDTO> getSheetsInfo(UserNameDTO userNameDTO) {
        return sheetsManager.getSheetsInfo(userNameDTO.getName());
    }

    public List<UserPermissionDTO> getPermissionsTableForSheet(CurrentSheetNameDTO currentSheetNameDTO){
        return sheetsManager.getPermissionsTableForSheet(currentSheetNameDTO.getSheetName());
    }

    @Override
    public void requestPermission(PermissionRequestDTO data) {
        sheetsManager.requestPermission(data);
    }

    @Override
    public List<PendingPermissionDTO> getPendingPermissions(UserNameDTO userNameDTO) {
        return sheetsManager.getPendingPermissions(userNameDTO.getName());
    }

    @Override
    public void handlePermissionDecision(PermissionRequestDecisionDTO decisionDTO) {
        sheetsManager.handlePermissionDecision(decisionDTO);
    }

    @Override
    public List<String> getAvailablePermissionsForUser(SheetUserIdentifierDTO dtoFromUi) {
        return sheetsManager.getAvailablePermissionsForUser(dtoFromUi.getSheetName(), dtoFromUi.getCurrentUsername());
    }

}
