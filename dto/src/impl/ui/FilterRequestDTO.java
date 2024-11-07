package impl.ui;

import java.util.List;
import java.util.Map;

public class FilterRequestDTO {
    private String from;
    private String to;
    private Map<String, List<String>> selectedColumnsValues;

    public FilterRequestDTO(Map<String, List<String>> selectedColumnsValues, String from, String to) {
        this.from = from;
        this.to = to;
        this.selectedColumnsValues = selectedColumnsValues;
    }

    public String getFrom() { return from; }

    public String getTo() { return to; }

    public Map<String, List<String>> getValuesForFilter() { return selectedColumnsValues; }
}
