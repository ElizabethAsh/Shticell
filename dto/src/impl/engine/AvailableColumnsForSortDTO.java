package impl.engine;

import java.util.List;

public class AvailableColumnsForSortDTO {
    List<String> availableColumns;

    public AvailableColumnsForSortDTO(List<String> availableColumns) {
        this.availableColumns = availableColumns;
    }

    public List<String> getAvailableColumns() { return availableColumns; }
}
