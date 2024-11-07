package sheet.sheetFactory;

import exception.InvalidCoordinateException;
import exception.RangeExistsException;
import exception.SheetConversionException;
import file.schema.generated.*;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;
import sheet.impl.SheetImpl;
import sheet.structure.api.CellSize;
import sheet.structure.impl.SheetCellSize;
import sheet.structure.impl.SheetLayout;
import java.util.List;

public class SheetFactory {

    public static SheetImpl createSheet(STLSheet stlSheet) throws SheetConversionException, InvalidCoordinateException, RangeExistsException {
        checkIfSheetLayoutValid(stlSheet);
        return createSheetImplFromSTLSheet(stlSheet);
    }

    private static SheetImpl createSheetImplFromSTLSheet(STLSheet stlSheet) throws InvalidCoordinateException, SheetConversionException, RangeExistsException {
        String sheetName = stlSheet.getName();
        STLLayout stlLayout = stlSheet.getSTLLayout();

        CellSize cellSize = new SheetCellSize(stlLayout.getSTLSize().getRowsHeightUnits(), stlLayout.getSTLSize().getColumnWidthUnits());
        SheetLayout sheetLayout = new SheetLayout(cellSize, stlLayout.getRows(), stlLayout.getColumns());
        List<STLCell> StlCells = stlSheet.getSTLCells().getSTLCell();

        SheetImpl sheet = new SheetImpl(sheetName, sheetLayout);
        populateRangesFromSTLSheet(stlSheet, sheet);
        populateCellsFromSTLSheet(stlSheet, sheet);

        sheet.getVersionManager().setNumberOfChangedCellsInVersion(1, StlCells.size());
        sheet.getVersionManager().setVersion(1, sheet);
        return sheet;
    }

    private static void populateRangesFromSTLSheet(STLSheet stlSheet, SheetImpl sheet) throws SheetConversionException, InvalidCoordinateException, RangeExistsException {
        STLRanges stlRanges = stlSheet.getSTLRanges();

        if (stlRanges == null) {
            return;
        }

        for (STLRange stlRange : stlRanges.getSTLRange()) {
            String rangeName = stlRange.getName();
            String fromCoordinate = stlRange.getSTLBoundaries().getFrom();
            String toCoordinate = stlRange.getSTLBoundaries().getTo();

            if (sheet.rangeExists(rangeName)) {
                throw new RuntimeException("Duplicate range name: " + rangeName + " in new sheet. Please choose a different name.");
            }

            sheet.addRange(rangeName, fromCoordinate, toCoordinate);
        }
    }


    private static void populateCellsFromSTLSheet(STLSheet stlSheet, SheetImpl sheet) throws InvalidCoordinateException {
        List<STLCell> stlCells = stlSheet.getSTLCells().getSTLCell();

        for (STLCell cell : stlCells) {
            String coordinateString = (cell.getColumn() + cell.getRow()).toUpperCase();
            Coordinate coordinate = CoordinateFactory.parseCoordinate(coordinateString);
            sheet.setCellFromFile(coordinate, cell.getSTLOriginalValue());
        }
    }


    private static void checkIfSheetLayoutValid(STLSheet stlSheet) throws SheetConversionException {
        int rows = stlSheet.getSTLLayout().getRows();
        int columns = stlSheet.getSTLLayout().getColumns();
        if(withinTheBoundaries(rows, SheetLayout.minimumRows, SheetLayout.maxRows)) { throw new SheetConversionException("rows exceeds the maximum limit of " + SheetLayout.maxRows);}
        if(withinTheBoundaries(columns, SheetLayout.minimumColumns, SheetLayout.maxColumns)) { throw new SheetConversionException("columns exceeds the maximum limit of " + SheetLayout.maxColumns);}

    }

    public static boolean withinTheBoundaries(int value, int minValue, int maxValue){
        return ((value < minValue) || (value > maxValue));
    }
}

