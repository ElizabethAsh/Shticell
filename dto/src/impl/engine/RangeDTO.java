package impl.engine;

import sheet.coordinate.Coordinate;
import java.util.Set;
import java.util.stream.Collectors;

public class RangeDTO {
    private final Set<String> coordinateInRange;

    public RangeDTO(Set<Coordinate> cells){
        coordinateInRange = cells.stream()
                .map(Coordinate::toString)
                .collect(Collectors.toSet());
    }

    public Set<String> getCellsInRange () { return coordinateInRange; }
}
