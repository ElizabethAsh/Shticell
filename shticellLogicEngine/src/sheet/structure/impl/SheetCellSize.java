package sheet.structure.impl;

import sheet.structure.api.CellSize;
import java.io.Serializable;

public class SheetCellSize implements CellSize, Serializable {

    private final int rowsHeightUnits;
    private final int columnWidthUnits;

    public SheetCellSize(int rowsHeightUnits, int columnWidthUnits) {
        this.rowsHeightUnits = rowsHeightUnits;
        this.columnWidthUnits = columnWidthUnits;
    }

    @Override
    public int getRowsHeightUnits() {
        return rowsHeightUnits;
    }

    @Override
    public int getColumnWidthUnits() {
        return columnWidthUnits;
    }
}
