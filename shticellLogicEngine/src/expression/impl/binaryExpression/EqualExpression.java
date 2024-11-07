package expression.impl.binaryExpression;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.impl.EffectiveValueImpl;

public class EqualExpression implements Expression {
    protected final Expression expression1;
    protected final Expression expression2;

    public EqualExpression(Expression expression1, Expression expression2) {
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {
        EffectiveValue leftValue = expression1.eval(sheet);
        EffectiveValue rightValue = expression2.eval(sheet);

        Object left = leftValue.getValue();
        Object right = rightValue.getValue();

        if (left.getClass().equals(right.getClass())) {
            boolean result = left.equals(right);
            return new EffectiveValueImpl(CellType.BOOLEAN, result);
        } else {
            return new EffectiveValueImpl(CellType.BOOLEAN, false);
        }
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.BOOLEAN;
    }
}
