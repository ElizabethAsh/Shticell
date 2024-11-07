package sheet.api;

import exception.InvalidCoordinateException;
import impl.engine.CellDTO;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import sheet.cell.api.Cell;
import sheet.coordinate.Coordinate;
import sheet.impl.SheetImpl;
import sheet.structure.impl.SheetLayout;
import sheet.version.VersionManager;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SheetReadActions {
    String getName();
    int getVersion();
    int getSheetHeight();
    int getSheetWidth();
    int getCellHeight();
    int getCellWidth();
    Map<Coordinate, Cell> getActiveCellMap();
    Cell getCell(Coordinate coordinate);
    Cell getCurrentCalculatingCell();
    void coordinateWithinBounds(Coordinate coordinate);
    VersionManager getVersionManager();
    SheetLayout getSheetLayout();
    Set<Cell> getRangeCells(String rangeName);
    boolean rangeExists(String rangeName);
    Set<String> getRanges();
    Set<Coordinate> getCoordinatesInRange(String name);
    void registerRangeUsage(String rangeName);
    List<String> getAvailableColumnsForSort(String from, String to) throws InvalidCoordinateException;
    List<String> getAvailableColumnsForFilter(String from, String to) throws InvalidCoordinateException;
    Sheet getSortedSheet(String from, String to, List<String> selectedColumns) throws InvalidCoordinateException;
    Map<Coordinate, String> getTextColorMap();
    Map<Coordinate, String> getBackgroundColorMap();
    Set<String> getDistinctValuesForColumnInRange(String column, String from, String to) throws InvalidCoordinateException;
    Sheet getFilteredSheet(Map<String, List<String>> valuesForFilter, String from, String to) throws InvalidCoordinateException;
    CellDTO checkIfCellAllowsDynamicAnalysis(Coordinate selectedCoordinate);
    SheetImpl copySheet();
}
