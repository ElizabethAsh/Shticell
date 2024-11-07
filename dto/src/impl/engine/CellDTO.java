package impl.engine;

import sheet.cell.api.Cell;
import sheet.cell.impl.CellImpl;
import sheet.coordinate.Coordinate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CellDTO {
    public final String coordinate;
    private String originalValue;
    private Object effectiveValue;
    private int lastModifiedVersion;
    private String lastUpdatedBy;
    private Set<String> dependsOn;
    private Set<String> influencingOn;

    public CellDTO(Cell cell) {
        this.coordinate = cell.getCoordinate().toString();
        this.originalValue = cell.getOriginalValue();
        this.effectiveValue = (cell.getEffectiveValue() != null) ? cell.getEffectiveValue().getValue() : " ";
        this.lastModifiedVersion = cell.getVersion();
        this.lastUpdatedBy = cell.getLastUpdatedBy();
        this.dependsOn = extractCoordinates(cell.getDependsOn());
        this.influencingOn = extractCoordinates(cell.getInfluencingOn());
    }

    public static CellDTO createCellDTO(Cell cell) {
        if (cell == null) {
            return null;
        }
        return new CellDTO(cell);
    }

    public int getCellVersion() { return lastModifiedVersion; }

    public String getCellOriginalValue() { return originalValue;}

    public Object getCellEffectiveValue() { return effectiveValue; }

    public Set<String> getDependsOn() { return dependsOn; }

    public Set<String> getInfluencingOn() { return influencingOn; }

    private Set<String> extractCoordinates(Set<Cell> cells) {
        if (cells == null) {
            return Collections.emptySet();
        }

        return cells.stream()
                .map(cell -> cell.getCoordinate().toString())
                .collect(Collectors.toSet());
    }

    public String getLastUpdatedBy() { return lastUpdatedBy; }
}
