package expression.expressionBuilder;

import expression.api.Expression;
import expression.expressionParser.ExpressionParser;
import expression.impl.binaryExpression.*;
import sheet.api.CellType;
import java.util.List;

public class BinaryExpressionFactory {
    private final String operation;

    public BinaryExpressionFactory(String operation) {
        this.operation = operation.toUpperCase();
    }

    public Expression create(List<String> arguments) {
        if (arguments.size() != 2) {
            throw new IllegalArgumentException("Invalid number of arguments for " + operation + " function. Expected 2, but got " + arguments.size() + ".");
        }

        Expression left = ExpressionParser.parse(arguments.get(0));
        Expression right = ExpressionParser.parse(arguments.get(1));
        Expression result = null;

        switch (operation) {
            case "PLUS" -> {
                result = new PlusExpression(left, right);
            }
            case "MINUS" -> {
                result = new MinusExpression(left, right);
            }
            case "TIMES" -> {
                result = new TimesExpression(left, right);
            }
            case "DIVIDE" -> {
                result = new DivideExpression(left, right);
            }
            case "POW" -> {
                result = new PowExpression(left, right);
            }
            case "MOD" -> {
                return new ModExpression(left, right);
            }
            case "CONCAT" -> {
                result = new ConcatExpression(left, right);
            }
            case "AND" -> {
                result = new AndExpression(left, right);
            }
            case "OR" -> {
                result = new OrExpression(left, right);
            }
            case "BIGGER" -> {
                result = new BiggerExpression(left,right);
            }
            case "LESS" ->{
                result = new LessExpression(left, right);
            }
            case "EQUAL" ->{
                result = new EqualExpression(left, right);
            }
            case "PERCENT" ->{
                result = new PercentExpression(left, right);
            }
            default -> throw new IllegalArgumentException("Unknown binary operation: " + operation);
        };

        return result;
    }
}


//package expression.expressionBuilder;
//
//import expression.api.Expression;
//import expression.expressionParser.ExpressionParser;
//import expression.impl.binaryExpression.*;
//        import sheet.api.CellType;
//import java.util.List;
//
//public class BinaryExpressionFactory {
//    private final String operation;
//
//    public BinaryExpressionFactory(String operation) {
//        this.operation = operation.toUpperCase();
//    }
//
//    public Expression create(List<String> arguments) {
//        if (arguments.size() != 2) {
//            throw new IllegalArgumentException("Invalid number of arguments for " + operation + " function. Expected 2, but got " + arguments.size() + ".");
//        }
//
//        Expression left = ExpressionParser.parse(arguments.get(0));
//        Expression right = ExpressionParser.parse(arguments.get(1));
//        Expression result = null;
//
//        CellType leftCellType = left.getFunctionResultType();
//        CellType rightCellType = right.getFunctionResultType();
//
//        final boolean leftNotNumericAndNotUnknown = !leftCellType.equals(CellType.NUMERIC) && !leftCellType.equals(CellType.UNKNOWN);
//        final boolean rightNotNumericAndNotUnknown = !rightCellType.equals(CellType.NUMERIC) && !rightCellType.equals(CellType.UNKNOWN);
//
//        final boolean leftNotStringAndNotUnknown = !leftCellType.equals(CellType.STRING) && !leftCellType.equals(CellType.UNKNOWN);
//        final boolean rightNotStringAndNotUnknown = !rightCellType.equals(CellType.STRING) && !rightCellType.equals(CellType.UNKNOWN);
//
//        switch (operation) {
//            case "PLUS" -> {
//                if (leftNotNumericAndNotUnknown || rightNotNumericAndNotUnknown) {
//                    throw new IllegalArgumentException("Invalid argument types for PLUS function. Expected NUMERIC, but got " + leftCellType + " and " + rightCellType);
//                }
//                result = new PlusExpression(left, right);
//            }
//            case "MINUS" -> {
//                if (leftNotNumericAndNotUnknown || rightNotNumericAndNotUnknown) {
//                    throw new IllegalArgumentException("Invalid argument types for MINUS function. Expected NUMERIC, but got " + leftCellType + " and " + rightCellType);
//                }
//                result = new MinusExpression(left, right);
//            }
//            case "TIMES" -> {
//                if (leftNotNumericAndNotUnknown || rightNotNumericAndNotUnknown) {
//                    throw new IllegalArgumentException("Invalid argument types for TIMES function. Expected NUMERIC, but got " + leftCellType + " and " + rightCellType);
//                }
//                result = new TimesExpression(left, right);
//            }
//            case "DIVIDE" -> {
//                if (leftNotNumericAndNotUnknown || rightNotNumericAndNotUnknown) {
//                    throw new IllegalArgumentException("Invalid argument types for DIVIDE function. Expected NUMERIC, but got " + leftCellType + " and " + rightCellType);
//                }
//                result = new DivideExpression(left, right);
//            }
//            case "POW" -> {
//                if (leftNotNumericAndNotUnknown || rightNotNumericAndNotUnknown) {
//                    throw new IllegalArgumentException("Invalid argument types for POW function. Expected NUMERIC, but got " + leftCellType + " and " + rightCellType);
//                }
//                result = new PowExpression(left, right);
//            }
//            case "MOD" -> {
//                if (leftNotNumericAndNotUnknown || rightNotNumericAndNotUnknown) {
//                    throw new IllegalArgumentException("Invalid argument types for MOD function. Expected NUMERIC, but got " + leftCellType + " and " + rightCellType);
//                }
//                return new ModExpression(left, right);
//            }
//            case "CONCAT" -> {
//                if (leftNotStringAndNotUnknown || rightNotStringAndNotUnknown) {
//                    throw new IllegalArgumentException("Invalid argument types for CONCAT function. Expected STRING, but got " + leftCellType + " and " + rightCellType);
//                }
//                result = new ConcatExpression(left, right);
//            }
//            default -> throw new IllegalArgumentException("Unknown binary operation: " + operation);
//        };
//
//        return result;
//    }
//}

