package sheet.range.boundaries;

import java.io.Serializable;

public class Boundaries implements Serializable{
    private final String to;
    private final String from;

    public Boundaries(String from, String to) {
        this.to = to;
        this.from = from;
    }

    public String getTo() { return to; }

    public String getFrom() { return from; }

}
