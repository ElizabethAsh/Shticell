package sheet.structure.impl;

import sheet.coordinate.Coordinate;
import sheet.structure.api.CellSize;
import sheet.structure.api.Layout;

import java.io.Serializable;

public class SheetLayout implements Layout, Serializable {
    private final CellSize cellSize;
    private final int rows;
    private final int columns;
    public static final int maxRows = 50;
    public static final int maxColumns = 20;
    public static final int minimumRows = 1;
    public static final int minimumColumns = 1;

    public SheetLayout(CellSize cellSize, int rows, int columns){
        this.cellSize = cellSize;
        this.rows = rows;
        this.columns = columns;
    }
    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public int getColumns() {
        return columns;
    }

    @Override
    public CellSize getCellSize() {
        return cellSize;
    }

    public void coordinateWithinBounds(Coordinate coordinate) {
        int row = coordinate.getRow();
        int column = coordinate.getColumn();

        if(!withinTheBoundaries(row , 0, rows - 1)){
            throw new IndexOutOfBoundsException("The coordinate is out of boundaries.\n" + "Row must be between "+ minimumRows + " and " + rows + ".");
        }

        if(!withinTheBoundaries(column, 0, columns - 1)){
            throw new IndexOutOfBoundsException("The coordinate is out of boundaries.\n" + "Column must be between A and " + (char)((columns - 1) + 'A') + ".");
        }

    }

    public boolean structureWithinBounds(int row, int column) {

        return withinTheBoundaries(row, minimumRows, maxRows) && withinTheBoundaries(column, minimumColumns, maxColumns);
    }

    public static boolean withinTheBoundaries(int value, int minValue, int maxValue){ return ((value >= minValue) && (value <= maxValue)); }
}
