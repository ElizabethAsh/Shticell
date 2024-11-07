package sheet.cell.impl;

import expression.api.Expression;
import expression.expressionParser.ExpressionParser;
import javafx.geometry.Insets;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.api.EffectiveValue;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateImpl;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class CellImpl implements Cell, Serializable {

    private final Coordinate coordinate;
    private String originalValue;
    private EffectiveValue effectiveValue;
    private int version;
    private Set<Cell> dependsOn;
    private Set<Cell> influencingOn;
    private final Sheet sheet;
    private String lastUpdatedBy;

    public CellImpl(int row, int column, String originalValue, int version, Sheet sheet) {
        this.sheet = sheet;
        this.coordinate = new CoordinateImpl(row, column);
        this.originalValue = originalValue;
        this.version = version;
        this.dependsOn = new HashSet<>();
        this.influencingOn = new HashSet<>();
    }
    @Override
    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public String getOriginalValue() {
        return originalValue;
    }

    @Override
    public void setCellOriginalValue(String value) {
        this.originalValue = value;
    }

    @Override
    public EffectiveValue getEffectiveValue() {
        if(effectiveValue == null) {
            calculateEffectiveValue();
        }
        return effectiveValue;
    }

    @Override
    public boolean calculateEffectiveValue() {
        sheet.setCurrentCalculatingCell(this);
        Expression expression = ExpressionParser.parse(originalValue);
        EffectiveValue newEffectiveValue = expression.eval(sheet);
        sheet.setCurrentCalculatingCell(null);

        if (newEffectiveValue == null || newEffectiveValue.equals(effectiveValue)) { //adding for ref
            return false;
        } else {
            effectiveValue = newEffectiveValue;
            return true;
        }
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public Set<Cell> getDependsOn() { return dependsOn; }

    public void setDependsOn(Cell dependsOn) { this.dependsOn.add(dependsOn); }

    @Override
    public Set<Cell> getInfluencingOn() { return influencingOn; }

    public void setInfluencingOn(Cell influencingOn) { this.influencingOn.add(influencingOn); }

    @Override
    public void updateVersion(int newVersion) {
        version = newVersion;
    }

    @Override
    public void removeDependencies(){

        for(Cell influent : getDependsOn()){
            influent.getInfluencingOn().remove(this);
        }

        clearDependencies();
    }

    @Override
    public void clearDependencies() {
        dependsOn.clear();
    }

    @Override
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    @Override
    public String getLastUpdatedBy() {
        return (lastUpdatedBy != null) ? lastUpdatedBy : "No updates yet";
    }

}
