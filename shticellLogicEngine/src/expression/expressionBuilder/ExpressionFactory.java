package expression.expressionBuilder;

import expression.api.Expression;
import java.util.*;

public class ExpressionFactory {

    public enum ExpressionType {
        BINARY, UNARY, TERNARY
    }

    private static final Map<String, ExpressionType> expressionTypes = new HashMap<>();

    static {
        expressionTypes.put("PLUS", ExpressionType.BINARY);
        expressionTypes.put("MINUS", ExpressionType.BINARY);
        expressionTypes.put("TIMES", ExpressionType.BINARY);
        expressionTypes.put("POW", ExpressionType.BINARY);
        expressionTypes.put("MOD", ExpressionType.BINARY);
        expressionTypes.put("DIVIDE", ExpressionType.BINARY);
        expressionTypes.put("CONCAT", ExpressionType.BINARY);
        expressionTypes.put("AND", ExpressionType.BINARY);
        expressionTypes.put("BIGGER", ExpressionType.BINARY);
        expressionTypes.put("EQUAL", ExpressionType.BINARY);
        expressionTypes.put("LESS", ExpressionType.BINARY);
        expressionTypes.put("OR", ExpressionType.BINARY);
        expressionTypes.put("PERCENT", ExpressionType.BINARY);
        expressionTypes.put("SUB", ExpressionType.TERNARY);
        expressionTypes.put("IF", ExpressionType.TERNARY);
        expressionTypes.put("REF", ExpressionType.UNARY);
        expressionTypes.put("SUM", ExpressionType.UNARY);
        expressionTypes.put("AVERAGE", ExpressionType.UNARY);
        expressionTypes.put("NOT", ExpressionType.UNARY);
    }

    public static boolean isSupported(String functionName) {
        return expressionTypes.containsKey(functionName.toUpperCase());
    }

    public static Expression create(String functionName, List<String> arguments){

        ExpressionType type = expressionTypes.get(functionName.toUpperCase());

        return switch (type) {
            case BINARY -> new BinaryExpressionFactory(functionName).create(arguments);
            case UNARY -> new UnaryExpressionFactory(functionName).create(arguments);
            case TERNARY -> new TernaryExpressionFactory(functionName).create(arguments);
            default -> throw new IllegalArgumentException("Unsupported expression type: " + type);
        };
    }
}

