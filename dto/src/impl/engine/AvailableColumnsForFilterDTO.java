package impl.engine;

import java.util.List;

public class AvailableColumnsForFilterDTO {
    List<String> availableColumns;

    public AvailableColumnsForFilterDTO(List<String> availableColumns) {
        this.availableColumns = availableColumns;
    }

    public List<String> getAvailableColumns() { return availableColumns; }
}
