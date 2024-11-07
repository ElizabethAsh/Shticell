package sheet.cell.api;

import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import sheet.api.EffectiveValue;
import sheet.coordinate.Coordinate;
import java.util.Set;

public interface Cell {
    Coordinate getCoordinate();
    String getOriginalValue();
    void setCellOriginalValue(String value);
    EffectiveValue getEffectiveValue();
    boolean calculateEffectiveValue();
    int getVersion();
    Set<Cell> getDependsOn();
    Set<Cell> getInfluencingOn();
    void setDependsOn(Cell dependsOn);
    void setInfluencingOn(Cell influencingOn);
    void updateVersion(int newVersion);
    void removeDependencies();
    void clearDependencies();
    void setLastUpdatedBy(String lastUpdatedBy);
    String getLastUpdatedBy();
}
