package expression.impl.binaryExpression;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.impl.EffectiveValueImpl;

public class PercentExpression implements Expression {
    protected final Expression partExpression;
    protected final Expression wholeExpression;

    public PercentExpression(Expression partExpression, Expression wholeExpression) {
        this.partExpression = partExpression;
        this.wholeExpression = wholeExpression;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {

        EffectiveValue partValue = partExpression.eval(sheet);
        EffectiveValue wholeValue = wholeExpression.eval(sheet);

        try {
            Double partNumericValue = partValue.extractValueWithExpectation(Double.class);
            Double wholeNumericValue = wholeValue.extractValueWithExpectation(Double.class);

            double result = (partNumericValue * wholeNumericValue) / 100;

            return new EffectiveValueImpl(CellType.NUMERIC, result);
        } catch (Exception e) {
            return new EffectiveValueImpl(CellType.NUMERIC, Double.NaN);
        }
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC ;
    }
}
