package expression.impl.identityExpression;


import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.impl.EffectiveValueImpl;

public class IdentityExpression implements Expression {
    private final Object value;
    private final CellType cellType;

    public IdentityExpression(Object value, CellType cellType) {
        this.value = value;
        this.cellType = cellType;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) { return new EffectiveValueImpl(cellType, value); }

    @Override
    public CellType getFunctionResultType() { return cellType; }
}
