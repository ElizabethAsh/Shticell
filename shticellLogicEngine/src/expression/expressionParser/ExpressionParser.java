package expression.expressionParser;

import expression.api.Expression;
import expression.expressionBuilder.ExpressionFactory;
import expression.impl.identityExpression.IdentityExpression;
import sheet.api.CellType;
import java.util.ArrayList;
import java.util.List;

public class ExpressionParser {

    public static Expression parse(String expressionStr) {
        Expression result = null ;

        if(stringRepresentsFunction(expressionStr)){
            result =  parseFunction(expressionStr);
        }
        else{
            result = parseLiteral(expressionStr);
        }

        return result ;
    }

    private static boolean stringRepresentsFunction(String expressionStr) {
        String str = expressionStr;

        if (expressionStr.length() > 1) {
            str = expressionStr.substring(1, expressionStr.length() - 1);
        }

        String functionName = extractFunctionName(str);

        if(expressionStr.startsWith("{") && expressionStr.endsWith("}")){
            if(!ExpressionFactory.isSupported(functionName)){
                throw new IllegalArgumentException("Invalid function name: " + functionName);
            }

            return true;
        } else {
            return false;
        }
    }

    private static Expression parseFunction(String expressionStr) {

        expressionStr = expressionStr.substring(1, expressionStr.length() - 1);
        String functionName = extractFunctionName(expressionStr);

        List<String> arguments = extractArguments(expressionStr.substring(functionName.length()));

        return ExpressionFactory.create(functionName, arguments);
    }


    private static List<String> extractArguments(String expressionStr) {
        List<String> arguments = new ArrayList<>();
        int bracketLevel = 0;
        StringBuilder currentArg = new StringBuilder();

        for (char ch : expressionStr.toCharArray()) {
            if (ch == '{') bracketLevel++;
            if (ch == '}') bracketLevel--;

            if (ch == ',' && bracketLevel == 0) {
                if(!currentArg.isEmpty()) {
                    arguments.add(currentArg.toString());
                    currentArg.setLength(0);
                }
            } else{
                currentArg.append(ch);
            }
        }

        if (!currentArg.isEmpty()) {
            arguments.add(currentArg.toString());
        }

        return arguments;
    }

    private static String extractFunctionName(String expressionStr) {

        int firstComma = expressionStr.indexOf(',');

        return firstComma == -1 ? expressionStr : expressionStr.substring(0, firstComma);
    }

    private static Expression parseLiteral(String expressionStr) {

        Expression result;

        if (isNumber(expressionStr)) {
            result = new IdentityExpression(Double.parseDouble(expressionStr), CellType.NUMERIC);
        } else if (isBoolean(expressionStr)) {
            result = new IdentityExpression(Boolean.parseBoolean(expressionStr), CellType.BOOLEAN);
        } else {
            result = new IdentityExpression(expressionStr, CellType.STRING);
        }

        return result;
    }

    private static boolean isBoolean(String expressionStr) {
        return expressionStr.equalsIgnoreCase("true") || expressionStr.equalsIgnoreCase("false");
    }

    private static boolean isNumber(String expressionStr) {
        try {
            double res = Double.parseDouble(expressionStr);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
}
