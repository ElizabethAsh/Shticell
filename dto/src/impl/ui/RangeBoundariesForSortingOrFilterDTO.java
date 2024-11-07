package impl.ui;

public class RangeBoundariesForSortingOrFilterDTO {
    private final String from;
    private final String to;

    public RangeBoundariesForSortingOrFilterDTO(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String getFrom() { return from; }

    public String getTo() { return to; }
}
