package impl.ui;

import sheet.coordinate.Coordinate;

public class CellUpdateRequestDTO {
    private final String coordinate;
    private final String newCellValue;

    public CellUpdateRequestDTO(String coordinate, String newCellValue) {
        this.coordinate = coordinate;
        this.newCellValue = newCellValue;
    }

    public String getCoordinate() { return coordinate; }

    public String getNewCellValue() { return newCellValue; }
}
