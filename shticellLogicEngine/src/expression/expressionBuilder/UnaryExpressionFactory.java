package expression.expressionBuilder;

import exception.InvalidCoordinateException;
import expression.api.Expression;
import expression.expressionParser.ExpressionParser;
import expression.impl.unaryExpression.*;
import sheet.api.CellType;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;

import java.util.List;

public class UnaryExpressionFactory {
    private final String operation;

    public UnaryExpressionFactory(String operation) {
        this.operation = operation.toUpperCase();
    }

    public Expression create(List<String> arguments){
        if (arguments.size() != 1) {
            throw new IllegalArgumentException("Invalid number of arguments for " + operation + " function. Expected 1, but got " + arguments.size() + ".");
        }

        Expression expression = ExpressionParser.parse(arguments.getFirst());

         switch (operation) {
            case "ABS" -> {
                return new AbsExpression(expression);
            }
            case "REF" -> {
                try{
                    Coordinate target = CoordinateFactory.parseCoordinate(arguments.getFirst());
                    return new RefExpression(target);
                }
                catch(InvalidCoordinateException e){ throw new IllegalArgumentException("Invalid argument for REF function."); }
            }
            case "NOT" -> {
                return new NotExpression(expression);
            }
            case "SUM" -> {
                return new SumExpression(arguments.getFirst());
            }
            case "AVERAGE" ->{
                return new AverageExpression(arguments.getFirst());
            }

            default -> throw new IllegalArgumentException("Unknown binary operation: " + operation);
        }
    }
}
