package impl.ui;

public class UniqueValuesForColumnRequestDTO {
    private String from;
    private String to;
    private String column;

    public UniqueValuesForColumnRequestDTO(String column, String from, String to) {
        this.from = from;
        this.to = to;
        this.column = column;
    }

    public String getFrom() { return from; }

    public String getTo() { return to; }

    public String getColumn() { return column; }

}
