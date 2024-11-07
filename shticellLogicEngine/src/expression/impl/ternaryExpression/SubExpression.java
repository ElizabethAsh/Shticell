package expression.impl.ternaryExpression;

import expression.api.Expression;
import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.SheetReadActions;
import sheet.impl.EffectiveValueImpl;

public class SubExpression implements Expression {
    protected final Expression expression1;
    protected final Expression expression2;
    protected final Expression expression3;

    public SubExpression(Expression expression1, Expression expression2, Expression expression3) {
        this.expression1 = expression1;
        this.expression2 = expression2;
        this.expression3 = expression3;
    }

    @Override
    public EffectiveValue eval(SheetReadActions sheet) {

        EffectiveValue strVal = expression1.eval(sheet);
        EffectiveValue start = expression2.eval(sheet);
        EffectiveValue end = expression3.eval(sheet);

        try {
            double startIndex = extractIndex(start);
            double endIndex = extractIndex(end);
            String source = extractSourceString(strVal);
            String result = calculateSubString(source, startIndex, endIndex);

            if(Double.isNaN(startIndex) || Double.isNaN(endIndex) || "!UNDEFINED!".equals(source)){
                return new EffectiveValueImpl(CellType.STRING, "!UNDEFINED!");
            }else{
                return new EffectiveValueImpl(CellType.STRING, result);
            }
        }catch (Exception e) {
            return new EffectiveValueImpl(CellType.STRING, "!UNDEFINED!");
        }
    }

    private String extractSourceString(EffectiveValue strVal) {
        return strVal.extractValueWithExpectation(String.class);
    }

    private double extractIndex(EffectiveValue value) {
        double index = value.extractValueWithExpectation(Double.class);

        if (index % 1 != 0) {
            throw new IllegalArgumentException("Start and end indices must be whole numbers.");
        }

        return index;
    }

    private String calculateSubString(String source, double startIndex, double endIndex) {
        if (startIndex < 0 || endIndex >= source.length() || startIndex > endIndex) {
            return "!UNDEFINED!";
        }
        return source.substring((int) startIndex, (int) (endIndex + 1));
    }

    @Override
    public CellType getFunctionResultType() { return CellType.STRING; }
}
