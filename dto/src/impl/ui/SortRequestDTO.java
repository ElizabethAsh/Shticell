package impl.ui;

import java.util.ArrayList;

public class SortRequestDTO {
    private String from;
    private String to;
    private ArrayList<String> selectedColumns;

    public SortRequestDTO(String from, String to, ArrayList<String> selectedColumns) {
        this.from = from;
        this.to = to;
        this.selectedColumns = selectedColumns;
    }

    public String getFrom() { return from; }

    public String getTo() { return to; }

    public ArrayList<String> getSelectedColumns() { return selectedColumns; }
}
