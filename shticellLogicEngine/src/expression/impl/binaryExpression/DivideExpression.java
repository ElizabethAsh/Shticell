package expression.impl.binaryExpression;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.impl.EffectiveValueImpl;

public class DivideExpression implements Expression {
    protected final Expression expression1;
    protected final Expression expression2;

    public DivideExpression(Expression expression1, Expression expression2) {
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {
        EffectiveValue leftValue = expression1.eval(sheet);
        EffectiveValue rightValue = expression2.eval(sheet);

        try {
            Double leftNumericValue = leftValue.extractValueWithExpectation(Double.class);
            Double rightNumericValue = rightValue.extractValueWithExpectation(Double.class);
            double result = rightNumericValue == 0 ? Double.NaN : leftNumericValue / rightNumericValue;

            return new EffectiveValueImpl(CellType.NUMERIC, result);

        } catch(Exception e) {
            return new EffectiveValueImpl(CellType.NUMERIC, Double.NaN);
        }
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }
}
