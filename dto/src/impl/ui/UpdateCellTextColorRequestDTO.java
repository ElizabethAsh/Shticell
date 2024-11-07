package impl.ui;

import javafx.scene.paint.Color;

public class UpdateCellTextColorRequestDTO {
    String cellId;
    String newColor;

    public UpdateCellTextColorRequestDTO(String cellId, String newColor) {
        this.cellId = cellId;
        this.newColor = newColor;
    }
    public String getCellId() {
        return cellId;
    }
    public String getNewColor() {
        return newColor;
    }
}
