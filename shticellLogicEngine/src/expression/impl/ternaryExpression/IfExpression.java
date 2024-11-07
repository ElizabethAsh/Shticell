package expression.impl.ternaryExpression;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.impl.EffectiveValueImpl;

public class IfExpression implements Expression {
    protected final Expression condition;
    protected final Expression thenExpression;
    protected final Expression elseExpression;

    public IfExpression(Expression condition, Expression thenExpression, Expression elseExpression) {
        this.condition = condition;
        this.thenExpression = thenExpression;
        this.elseExpression = elseExpression;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {
        EffectiveValue conditionValue = condition.eval(sheet);

        try {
            EffectiveValue thenValue = thenExpression.eval(sheet);
            EffectiveValue elseValue = elseExpression.eval(sheet);

            Boolean conditionResult = conditionValue.extractValueWithExpectation(Boolean.class);
            EffectiveValue resultValue = conditionResult ? thenValue : elseValue;

            return resultValue;
        } catch (Exception e) {
            return new EffectiveValueImpl(CellType.STRING, "UNKNOWN");
        }
    }


    @Override
    public CellType getFunctionResultType() {
        return CellType.UNKNOWN;
    }
}
