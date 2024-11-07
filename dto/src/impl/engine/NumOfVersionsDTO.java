package impl.engine;

import java.util.Map;
import java.util.Set;

public class NumOfVersionsDTO {
    private final Set<Integer> displayVersions;

    public NumOfVersionsDTO(Map<Integer,Integer> displayVersions) {
        this.displayVersions = displayVersions.keySet();
    }

    public Set<Integer> getVersions() { return displayVersions; }

}
