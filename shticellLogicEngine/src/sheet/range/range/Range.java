package sheet.range.range;

import sheet.range.boundaries.Boundaries;

import java.io.Serializable;

public class Range implements Serializable {
    private Boundaries boundaries;
    private final String name;

    public Range(Boundaries boundaries, String name) {
        this.boundaries = boundaries;
        this.name = name;
    }

    public Boundaries getBoundaries() { return boundaries; }

    public void setBoundaries(Boundaries value) {
        this.boundaries = value;
    }

    public String getName() { return name; }

}
