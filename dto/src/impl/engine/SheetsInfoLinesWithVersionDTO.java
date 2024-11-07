package impl.engine;

import java.util.List;

public class SheetsInfoLinesWithVersionDTO {
    private int version;
    private List<SheetDetailsDTO> sheets;

    public SheetsInfoLinesWithVersionDTO(int version, List<SheetDetailsDTO> sheets) {
        this.version = version;
        this.sheets = sheets;
    }

    public int getVersion() { return version; }

    public List<SheetDetailsDTO> getSheets() { return sheets; }
}
