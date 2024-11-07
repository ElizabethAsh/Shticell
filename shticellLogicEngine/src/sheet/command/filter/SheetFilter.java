package sheet.command.filter;

import exception.InvalidCoordinateException;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;
import sheet.impl.SheetImpl;

import java.io.Serializable;
import java.util.*;

public class SheetFilter implements Serializable {

    public static List<String> getAvailableColumnsForFilter(Sheet sheet, String from, String to) throws InvalidCoordinateException {
        Coordinate fromCoordinate = CoordinateFactory.parseCoordinate(from);
        Coordinate toCoordinate = CoordinateFactory.parseCoordinate(to);
        sheet.getSheetLayout().coordinateWithinBounds(fromCoordinate);
        sheet.getSheetLayout().coordinateWithinBounds(toCoordinate);

        List<String> columnsList = new ArrayList<>();

        for (int column = fromCoordinate.getColumn(); column <= toCoordinate.getColumn(); column++) {
            boolean columnWithValues = false;

            for (int row = fromCoordinate.getRow(); row <= toCoordinate.getRow(); row++) {
                Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);
                Cell cell = sheet.getCell(coordinate);

                if (cell != null && cell.getEffectiveValue() != null && !cell.getEffectiveValue().getValue().toString().isEmpty()) {
                    columnWithValues = true;
                    break;
                }
            }

            if (columnWithValues) {
                columnsList.add(CoordinateFactory.getColumnName(column));
            }
        }

        return columnsList;
    }

    public static Set<String> getDistinctValuesForColumnInRange(Sheet sheet, String column, String from, String to) throws InvalidCoordinateException {
        Coordinate fromCoordinate = CoordinateFactory.parseCoordinate(from);
        Coordinate toCoordinate = CoordinateFactory.parseCoordinate(to);

        int columnIndex = CoordinateFactory.getColumnIndex(column);
        Set<String> distinctValues = new HashSet<>();

        for (int row = fromCoordinate.getRow(); row <= toCoordinate.getRow(); row++) {
            Coordinate coordinate = CoordinateFactory.createCoordinate(row, columnIndex);
            Cell cell = sheet.getCell(coordinate);

            if (cell != null && cell.getEffectiveValue() != null) {
                String value = cell.getEffectiveValue().getValue().toString().trim();
                if (!value.isEmpty()) {
                    distinctValues.add(value);
                }
            }
        }

        return distinctValues;
    }


    public static Sheet getFilteredSheet(SheetImpl sheet, Map<String, List<String>> valuesForFilter, String from, String to) throws InvalidCoordinateException {
        Coordinate fromCoordinate = CoordinateFactory.parseCoordinate(from);
        Coordinate toCoordinate = CoordinateFactory.parseCoordinate(to);

        SheetImpl filteredSheet = new SheetImpl(sheet.getName(), sheet.getSheetLayout());

        for (int row = 0; row < fromCoordinate.getRow(); row++) {
            copyRowOutsideRange(sheet, filteredSheet, row);
        }

        for (int row = toCoordinate.getRow() + 1; row < sheet.getSheetLayout().getRows(); row++) {
            copyRowOutsideRange(sheet, filteredSheet, row);
        }

        int currentRowInFilteredSheet = fromCoordinate.getRow();

        for (int row = fromCoordinate.getRow(); row <= toCoordinate.getRow(); row++) {
            boolean rowMatchesAllFilters = true;
            copyCellsOutsideColumnRange(sheet, filteredSheet, row, fromCoordinate, toCoordinate);

            for (Map.Entry<String, List<String>> filterEntry : valuesForFilter.entrySet()) {
                String column = filterEntry.getKey();
                List<String> valuesForColumn = filterEntry.getValue();
                int columnIndex = CoordinateFactory.getColumnIndex(column);

                Coordinate coordinate = CoordinateFactory.createCoordinate(row, columnIndex);
                Cell cell = sheet.getCell(coordinate);

                if (cell == null || cell.getEffectiveValue() == null || !valuesForColumn.contains(cell.getEffectiveValue().getValue().toString().trim())) {
                    rowMatchesAllFilters = false;
                    break;
                }
            }

            if (rowMatchesAllFilters) {
                copyFilteredRow(sheet, filteredSheet, row, currentRowInFilteredSheet, fromCoordinate, toCoordinate);
                currentRowInFilteredSheet++;
            }

        }

        return filteredSheet;
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

    private static void copyFilteredRow(Sheet sheet, Sheet filteredSheet, int originalRow, int filteredRow, Coordinate fromCoordinate, Coordinate toCoordinate) {
        for (int col = fromCoordinate.getColumn(); col <= toCoordinate.getColumn(); col++) {
            copyCell(sheet, filteredSheet, originalRow, filteredRow, col);
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
        String  textColor = sheet.getTextColorMap().get(cellCoordinate);

        if (background != null) {
            filteredSheet.updateCellBackground(newCoordinate, background);
        }

        if (textColor != null) {
            filteredSheet.updateCellTextColor(newCoordinate, textColor);
        }

    }

}
