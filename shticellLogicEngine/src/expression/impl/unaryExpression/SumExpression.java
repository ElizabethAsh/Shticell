package expression.impl.unaryExpression;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.cell.api.Cell;
import sheet.impl.EffectiveValueImpl;

import java.util.List;
import java.util.Set;

public class SumExpression implements Expression {
    private final String rangeName;

    public SumExpression(String rangeName) {
        this.rangeName = rangeName;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {

        if (!sheet.rangeExists(rangeName)) {
            return new EffectiveValueImpl(CellType.NUMERIC, Double.NaN);
        }

        sheet.registerRangeUsage(rangeName);

        Set<Cell> rangeCells = sheet.getRangeCells(rangeName);
        double sum = 0;
        Cell currentCell = sheet.getCurrentCalculatingCell();

        for (Cell cell : rangeCells) {
            try {
                cell.setInfluencingOn(currentCell);
                currentCell.setDependsOn(cell);
                Double numericValue = cell.getEffectiveValue().extractValueWithExpectation(Double.class);
                sum += numericValue;

            } catch (Exception e) {}
        }

        return new EffectiveValueImpl(CellType.NUMERIC, sum);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }
}
