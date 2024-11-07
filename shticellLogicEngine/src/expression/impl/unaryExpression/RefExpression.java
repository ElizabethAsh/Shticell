package expression.impl.unaryExpression;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.cell.api.Cell;
import sheet.coordinate.Coordinate;

public class RefExpression implements Expression {
    private final Coordinate coordinate;

    public RefExpression(Coordinate coordinate) { this.coordinate = coordinate; }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {
        sheet.coordinateWithinBounds(coordinate);

        Cell referencedCell = sheet.getCell(coordinate);
        Cell currentCell = sheet.getCurrentCalculatingCell();

        if (referencedCell == null) {
            return null;
        }

        if(currentCell == referencedCell) {
            throw new IllegalArgumentException("Cell " + coordinate + " cannot reference itself.");
        }

        currentCell.setDependsOn(referencedCell);
        referencedCell.setInfluencingOn(currentCell);

        return sheet.getCell(coordinate).getEffectiveValue();
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public CellType getFunctionResultType() { return CellType.UNKNOWN; }
}
