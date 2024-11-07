package expression.impl.unaryExpression;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.impl.EffectiveValueImpl;

public class NotExpression implements Expression {
    protected final Expression expression;

    public NotExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {

        EffectiveValue value = expression.eval(sheet);

        try{
            Boolean argument = value.extractValueWithExpectation(Boolean.class);
            return new EffectiveValueImpl(CellType.BOOLEAN, !argument);

        }catch(Exception e){
            return new EffectiveValueImpl(CellType.BOOLEAN, "UNKNOWN");
        }
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.BOOLEAN;
    }
}
