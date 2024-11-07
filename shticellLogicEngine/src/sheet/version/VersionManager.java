package sheet.version;

import sheet.api.Sheet;
import sheet.impl.SheetImpl;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class VersionManager implements Serializable {
    private Map<Integer, Sheet> versions;
    private Map<Integer, Integer> numberOfChangedCellsInVersions;

    public VersionManager() {
        this.versions = new HashMap<>();
        this.numberOfChangedCellsInVersions = new HashMap<>();
    }

    public void setVersion(int version, Sheet sheet) {
        Sheet copiedSheet = ((SheetImpl) sheet).copySheet();
        versions.put(version, copiedSheet);
    }

    public Sheet getVersion(int version) {
        if(!versions.containsKey(version)) {
            throw new IllegalArgumentException("Invalid version: " + version);
        }
        return versions.get(version);
    }

    public void setNumberOfChangedCellsInVersion(int version, int numberOfChangedCells) {
        numberOfChangedCellsInVersions.put(version, numberOfChangedCells);
    }

    public Map<Integer, Integer> getNumberOfCellsChangeInVersion() {
       return numberOfChangedCellsInVersions;
    }

}
