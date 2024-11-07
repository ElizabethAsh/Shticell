package impl.engine;

import java.util.Set;

public class RangesNamesDTO {
    private final Set<String> rangesName;

    public RangesNamesDTO(Set<String> rangesName){
        this.rangesName = rangesName;
    }

    public Set<String> getRangesName(){ return rangesName;}
}
