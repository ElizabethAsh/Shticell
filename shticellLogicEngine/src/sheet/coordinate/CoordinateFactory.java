package sheet.coordinate;

import exception.InvalidCoordinateException;
import java.util.HashMap;
import java.util.Map;

public class CoordinateFactory {

    private static Map<String, Coordinate> cachedCoordinates = new HashMap<>();

    public static Coordinate createCoordinate(int row, int column) {

        String key = row + ":" + column;
        if (cachedCoordinates.containsKey(key)) {
            return cachedCoordinates.get(key);
        }

        CoordinateImpl coordinate = new CoordinateImpl(row, column);
        cachedCoordinates.put(key, coordinate);

        return coordinate;
    }

    public static Coordinate parseCoordinate(String coordinate) throws InvalidCoordinateException {

        coordinate = coordinate.trim().toUpperCase();

        if(coordinate.length() < 2){
            throw new InvalidCoordinateException("Invalid coordinate: " + coordinate + ". A valid coordinate should start with a letter followed by a number (e.g., A4).");
        }

        char columnPart = coordinate.charAt(0);
        String rowPart = coordinate.substring(1);
        int row;

        if(!(columnPart >= 'A' && columnPart <= 'Z')){
            throw new InvalidCoordinateException("Invalid coordinate: " + coordinate + " A valid coordinate should start with a letter (e.g., A4).");
        }

        try {
            row = Integer.parseInt(rowPart);
        }
        catch(NumberFormatException e){
            throw new InvalidCoordinateException("Invalid coordinate: " + coordinate + ". The row number should be a valid integer (e.g., A4).");
        }

        int column = columnPart - 'A';
        row -= 1;

        return createCoordinate(row, column);
    }

    public static String getColumnName(int column) {
        return String.valueOf((char)('A' + column));
    }

    public static int getColumnIndex(String column) {
        return column.charAt(0) - 'A';
    }
}
