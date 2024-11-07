package sheet.api;

import exception.InvalidCoordinateException;
import exception.RangeExistsException;
import exception.RangeInUseException;
import sheet.cell.api.Cell;
import sheet.coordinate.Coordinate;

import java.util.Map;

public interface SheetUpdateActions {
    Sheet updateCellValueAndCalculate(int row, int column, String value, String currentUsername);
    void setCurrentCalculatingCell(Cell currentCalculatingCell);
    void addRange(String rangeName, String from, String to) throws InvalidCoordinateException, RangeExistsException;
    void deleteRange(String name) throws RangeInUseException;
    void updateCellValueWithoutCalculation(int row, int column, String originalValue);
    void updateCellBackground(Coordinate coordinate, String background);
    void updateCellTextColor(Coordinate coordinate, String textColor);
    void updateCellValueInDynamicAnalysis(Coordinate coordinate, String newCellValue);
    void setBackgroundColorMap(Map<Coordinate, String> backgroundColorMap);
    void setTextColorMap(Map<Coordinate, String> textColorMap);
}
