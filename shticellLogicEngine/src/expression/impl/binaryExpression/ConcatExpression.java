package expression.impl.binaryExpression;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.impl.EffectiveValueImpl;

public class ConcatExpression implements Expression {
    protected final Expression expression1;
    protected final Expression expression2;

    public ConcatExpression(Expression expression1, Expression expression2) {
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {
        EffectiveValue leftValue = expression1.eval(sheet);
        EffectiveValue rightValue = expression2.eval(sheet);

        try {
            String leftString = leftValue.extractValueWithExpectation(String.class);
            String rightString = rightValue.extractValueWithExpectation(String.class);

            if("!UNDEFINED!".equals(leftString) || "!UNDEFINED!".equals(rightString)) {
                return new EffectiveValueImpl(CellType.STRING, "!UNDEFINED!");
            }else{
                String result = leftString + rightString;
                return new EffectiveValueImpl(CellType.STRING, result);
            }

        }
        catch(Exception e) {
            return new EffectiveValueImpl(CellType.STRING, "!UNDEFINED!");
        }
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.STRING;
    }
}
