package sheet.range.ranges;

import exception.InvalidCoordinateException;
import exception.RangeExistsException;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;
import sheet.range.boundaries.Boundaries;
import sheet.range.range.Range;
import sheet.structure.impl.SheetLayout;

import java.io.Serializable;
import java.util.*;

public class Ranges implements Serializable {
    private Map<String, Range> rangesMap = new HashMap<>();
    private Set<Range> ranges = new HashSet<>();
    private SheetLayout sheetLayout;

    public Ranges(SheetLayout sheetLayout) {
        this.sheetLayout = sheetLayout;
    }


    public void addRange(String rangeName, String from, String to) throws InvalidCoordinateException, RangeExistsException {
        sheetLayout.coordinateWithinBounds(CoordinateFactory.parseCoordinate(from));
        sheetLayout.coordinateWithinBounds(CoordinateFactory.parseCoordinate(to));

        validateCoordinatesOrder(from, to);

        if(rangeExists(rangeName)){
            throw new RangeExistsException("The range name: " + rangeName + " exists. Please choose a different name");
        }

        Range range = new Range(new Boundaries(from, to), rangeName);
        ranges.add(range);
        rangesMap.put(range.getName(), range);
    }

    public boolean rangeExists(String rangeName) {
        return (rangesMap != null) && (rangesMap.containsKey(rangeName));
    }

    public Set<Coordinate> getCoordinatesInRange(String rangeName){

        Range targetRange = rangesMap.get(rangeName);

        if (targetRange == null) {
            return Set.of();
        }

        Boundaries boundaries = targetRange.getBoundaries();
        Coordinate from = null;
        Coordinate to = null;
        try {
            from = CoordinateFactory.parseCoordinate(boundaries.getFrom());
            to = CoordinateFactory.parseCoordinate(boundaries.getTo());
        } catch (InvalidCoordinateException e) {}

        Set<Coordinate> coordinatesInRange = new HashSet<>();
        for (int row = from.getRow(); row <= to.getRow(); row++) {
            for (int column = from.getColumn(); column <= to.getColumn(); column++) {
                coordinatesInRange.add(CoordinateFactory.createCoordinate(row, column));
            }
        }

        return coordinatesInRange;
    }

    public Set<String> getRangesNames() {
        return rangesMap.keySet();
    }

    public void deleteRange(String name) {
        if(rangeExists(name)){
            Range range = rangesMap.get(name);
            ranges.remove(range);
            rangesMap.remove(name);
        }
        else{
            throw new RuntimeException("Range not found: " + name);
        }
    }

    private void validateCoordinatesOrder(String from, String to) throws InvalidCoordinateException {
        Coordinate fromCoordinate = CoordinateFactory.parseCoordinate(from);
        Coordinate toCoordinate = CoordinateFactory.parseCoordinate(to);

        if (fromCoordinate.getColumn() > toCoordinate.getColumn() ||
                (fromCoordinate.getColumn() == toCoordinate.getColumn() && fromCoordinate.getRow() > toCoordinate.getRow())) {
            throw new InvalidCoordinateException("Invalid range: 'from' coordinate must be above and to the left of 'to' coordinate.");
        }
    }
}
