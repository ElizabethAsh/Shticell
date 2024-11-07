package sheet.command.sort;

import exception.InvalidCoordinateException;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.cell.impl.CellImpl;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;
import sheet.impl.SheetImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SheetSorter implements Serializable {

    public static List<String> getAvailableColumnsForSort(Sheet sheet, String from, String to) throws InvalidCoordinateException {
        Coordinate fromCoordinate = CoordinateFactory.parseCoordinate(from);
        Coordinate toCoordinate = CoordinateFactory.parseCoordinate(to);
        sheet.getSheetLayout().coordinateWithinBounds(fromCoordinate);
        sheet.getSheetLayout().coordinateWithinBounds(toCoordinate);

        List<String> numericColumns = new ArrayList<>();

        for (int column = fromCoordinate.getColumn(); column <= toCoordinate.getColumn(); column++) {
            boolean numericColumn = true;

            for (int row = fromCoordinate.getRow(); row <= toCoordinate.getRow(); row++) {
                Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);
                Cell cell = sheet.getCell(coordinate);

                if (cell != null) {
                    Object value = cell.getEffectiveValue().getValue();
                    if (!(value instanceof Number)) {
                        numericColumn = false;
                        break;
                    }
                }
                else{
                    numericColumn = false;
                    break;
                }
            }

            if (numericColumn) { numericColumns.add(CoordinateFactory.getColumnName(column)); }
        }
        return numericColumns;
    }

    public static Sheet getSortedSheet(Sheet sheet, String from, String to, List<String> selectedColumns) throws InvalidCoordinateException {
        Coordinate fromCoordinate = CoordinateFactory.parseCoordinate(from);
        Coordinate toCoordinate = CoordinateFactory.parseCoordinate(to);
        sheet.getSheetLayout().coordinateWithinBounds(fromCoordinate);
        sheet.getSheetLayout().coordinateWithinBounds(toCoordinate);

        SheetImpl sortedSheet = new SheetImpl(sheet.getName(), sheet.getSheetLayout());

        for (int row = 0; row < fromCoordinate.getRow(); row++) {
            copyRowOutsideRange(sheet, sortedSheet, row);
        }

        for (int row = toCoordinate.getRow() + 1; row < sheet.getSheetLayout().getRows(); row++) {
            copyRowOutsideRange(sheet, sortedSheet, row);
        }

        List<List<Cell>> rows = new ArrayList<>();

        for (int row = fromCoordinate.getRow(); row <= toCoordinate.getRow(); row++) {
            copyCellsOutsideColumnRange(sheet, sortedSheet, row, fromCoordinate, toCoordinate);
            List<Cell> rowCells = new ArrayList<>();
            for (int column = fromCoordinate.getColumn(); column <= toCoordinate.getColumn(); column++) {
                Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);
                Cell cell = sheet.getCell(coordinate);

                if (cell == null) {
                    cell = new CellImpl(row, column, "", 0, sortedSheet);
                }
                rowCells.add(cell);
            }
            rows.add(rowCells);
        }

        rows.sort((row1, row2) -> {
            for (String columnLetter : selectedColumns) {
                int columnIndex = CoordinateFactory.getColumnIndex(columnLetter);

                Object value1 = row1.get(columnIndex - fromCoordinate.getColumn()).getEffectiveValue().getValue();
                Object value2 = row2.get(columnIndex - fromCoordinate.getColumn()).getEffectiveValue().getValue();

                if (value1 == null && value2 == null) {
                    continue;
                } else if (value1 == null) {
                    return -1;
                } else if (value2 == null) {
                    return 1;
                }

                if (value1 instanceof Comparable && value2 instanceof Comparable) {
                    int comparison = ((Comparable) value1).compareTo(value2);
                    if (comparison != 0) {
                        return comparison;
                    }
                }
            }

            return 0;
        });

        for (int row = 0; row < rows.size(); row++) {
            List<Cell> sortedRow = rows.get(row);
            for (int col = 0; col < sortedRow.size(); col++) {
                Cell sortedCell = sortedRow.get(col);
                if (sortedCell != null) {
                    Coordinate coordinate = CoordinateFactory.createCoordinate(fromCoordinate.getRow() + row, fromCoordinate.getColumn() + col);
                    sortedSheet.updateCellValueWithoutCalculation(coordinate.getRow(), coordinate.getColumn(), sortedCell.getEffectiveValue().getValue().toString());

                    Coordinate originalCoordinate = sortedCell.getCoordinate();

                    String  background = sheet.getBackgroundColorMap().get(originalCoordinate);
                    if (background != null) {
                        sortedSheet.updateCellBackground(coordinate, background);
                    }

                    String  textColor = sheet.getTextColorMap().get(originalCoordinate);
                    if (textColor != null) {
                        sortedSheet.updateCellTextColor(coordinate, textColor);
                    }
                }
            }
        }


        return sortedSheet;
    }

    private static void copyCellsOutsideColumnRange(Sheet sheet, Sheet filteredSheet, int row, Coordinate fromCoordinate, Coordinate toCoordinate) {
        for (int col = 0; col < sheet.getSheetLayout().getColumns(); col++) {
            if (col < fromCoordinate.getColumn() || col > toCoordinate.getColumn()) {
                copyCell(sheet, filteredSheet, row, row, col);
            }
        }
    }

    private static void copyRowOutsideRange(Sheet sheet, Sheet filteredSheet, int row) {
        for (int col = 0; col < sheet.getSheetLayout().getColumns(); col++) {
            copyCell(sheet, filteredSheet, row, row, col);
        }
    }

    private static void copyCell(Sheet sheet, Sheet filteredSheet, int originalRow, int filteredRow, int col) {
        Coordinate cellCoordinate = CoordinateFactory.createCoordinate(originalRow, col);
        Cell currentCell = sheet.getCell(cellCoordinate);
        Coordinate newCoordinate = CoordinateFactory.createCoordinate(filteredRow, col);

        if (currentCell != null) {
            filteredSheet.updateCellValueWithoutCalculation(newCoordinate.getRow(), newCoordinate.getColumn(), currentCell.getEffectiveValue().getValue().toString());
        }

        String  background = sheet.getBackgroundColorMap().get(cellCoordinate);
        if (background != null) {
            filteredSheet.updateCellBackground(newCoordinate, background);
        }

        String  textColor = sheet.getTextColorMap().get(cellCoordinate);
        if (textColor != null) {
            filteredSheet.updateCellTextColor(newCoordinate, textColor);
        }
    }
}
