package impl.engine;

import javafx.geometry.Insets;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import sheet.api.Sheet;
import sheet.coordinate.Coordinate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import utils.ColorUtil;

public class SheetDTO {
    private final String name;
    private final int versionNumber;
    private final int sheetHeight;
    private final int sheetWidth;
    private final int cellHeight;
    private final int cellWidth;
    private final Map<String, String> backgroundColors;
    private final Map<String, String> textColors;
    private final Map<String, CellDTO> activeCells = new HashMap<>();;
    private final String permissionType;
    private final Set<String> ranges;

    public SheetDTO(Sheet sheet, String permissionType) {
        this.name = sheet.getName();
        this.versionNumber = sheet.getVersion();
        this.sheetHeight = sheet.getSheetHeight();
        this.sheetWidth = sheet.getSheetWidth();
        this.cellHeight = sheet.getCellHeight();
        this.cellWidth = sheet.getCellWidth();

        this.backgroundColors = new HashMap<>();
        sheet.getBackgroundColorMap().forEach((key, value) -> backgroundColors.put(key.toString(), value));


        this.textColors = new HashMap<>();
        sheet.getTextColorMap().forEach((key, value) -> textColors.put(key.toString(), value));

        sheet.getActiveCellMap().forEach((coordinate, cell) ->
                activeCells.put(coordinate.toString(), CellDTO.createCellDTO(cell))
        );

        this.permissionType = permissionType;
        this.ranges = sheet.getRanges();
    }

    public String getSheetName() { return name; }

    public Set<String> getRanges() { return ranges; }

    public int getVersionNumber() { return versionNumber; }

    public int getSheetHeight() { return sheetHeight; }

    public int getSheetWidth() { return sheetWidth; }

    public int getCellHeight() { return cellHeight; }

    public int getCellWidth() { return cellWidth; }

    public Map<String, CellDTO> getActiveCells(){ return activeCells; }

    public String getPermissionType() { return permissionType; }

    public Map<String, String> getBackgroundColors(){
        return backgroundColors;
    }

    public Map<String, String> getTextColors(){ return textColors; }
}


