package expression.impl.unaryExpression;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.impl.EffectiveValueImpl;

public class AbsExpression implements Expression {
    protected final Expression expression;

    public AbsExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {
        EffectiveValue value = expression.eval(sheet);

        try{
            double result = Math.abs(value.extractValueWithExpectation(Double.class));
            return new EffectiveValueImpl(CellType.NUMERIC, result);
        } catch(Exception e) {
            return new EffectiveValueImpl(CellType.NUMERIC, Double.NaN);
        }
    }

    @Override
    public CellType getFunctionResultType() { return CellType.NUMERIC; }
}
