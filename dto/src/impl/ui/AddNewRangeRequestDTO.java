package impl.ui;

public class AddNewRangeRequestDTO {
    private final String rangeName;
    private final String from;
    private final String to;

    public AddNewRangeRequestDTO(String rangeName, String from, String to) {
        this.rangeName = rangeName;
        this.from = from;
        this.to = to;
    }

    public String getRangeName() { return rangeName; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
}
