package impl.engine;

import java.util.HashSet;
import java.util.Set;

public class UniqueValuesForColumnDTO {
    private Set<String> uniqueValues= new HashSet<String>();

    public UniqueValuesForColumnDTO(Set<String> uniqueValues) {
        this.uniqueValues = uniqueValues;
    }
    public Set<String> getUniqueValues() {
        return uniqueValues;
    }
}
