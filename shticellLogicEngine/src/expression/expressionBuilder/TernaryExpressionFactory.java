package expression.expressionBuilder;

import expression.api.Expression;
import expression.expressionParser.ExpressionParser;
import expression.impl.ternaryExpression.IfExpression;
import expression.impl.ternaryExpression.SubExpression;
import sheet.api.CellType;

import java.util.List;

public class TernaryExpressionFactory {
    private final String operation;

    public TernaryExpressionFactory(String operation) {
        this.operation = operation.toUpperCase();
    }

    public Expression create(List<String> arguments) {
        if (arguments.size() != 3) {
            throw new IllegalArgumentException("Invalid number of arguments for " + operation + " function. Expected 3, but got " + arguments.size() + ".");
        }

        Expression exp1 = ExpressionParser.parse(arguments.get(0));
        Expression exp2 = ExpressionParser.parse(arguments.get(1));
        Expression exp3 = ExpressionParser.parse(arguments.get(2));

        switch (operation) {
            case "SUB" -> {
                return new SubExpression(exp1, exp2, exp3);
            }
            case "IF" -> {
                CellType thenType = exp2.getFunctionResultType();
                CellType elseType = exp3.getFunctionResultType();

                if(thenType != elseType) {
                    throw new IllegalArgumentException("Invalid return type for " + operation + " function.\n" +
                            "Then and Else expressions must return the same type.");
                }

                return new IfExpression(exp1, exp2, exp3);
            }
            default -> throw new IllegalArgumentException("Unknown binary operation: " + operation);
        }
    }
}
