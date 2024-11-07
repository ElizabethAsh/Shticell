package components.sheetRoomComponents.sheet;

import impl.engine.CellDTO;
import impl.engine.SheetDTO;
import components.sheetRoomComponents.sheetRoom.SheetRoomMainController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import sheet.coordinate.Coordinate;

import java.util.Set;

public interface SheetController {
    SimpleBooleanProperty getHasSelectedCell();

    void alignCells(javafx.geometry.Pos alignment);

    void changeColumnWidth(double width);

    void changeRowWidth(double width);

    void changeTextColor(Color color);

    void changeBackgroundColor(Color color);

    GridPane createDynamicSheet(SheetDTO dto);

    void resetCellStyle();

    void setAnimationsEnabled(boolean animationsEnabled);

    void setMainController(SheetRoomMainController appController);

    void displayCellDetails(CellDTO cellDTO);

    void clearPreviousCellMarkings(Set<Label> previouslyMarkedCells, String styleClass);

    Set<Label> markCells(Set<String> cellCoordinates, String styleClass);

    void displayRange(Set<String> cellsInRange);

    void resetSelectedCell();

    void updateSheetAfterAddingRange(SheetDTO sheetDTO);

    void updateSheetAfterUpdateCell(SheetDTO sheetDTO);

    void updateSheetAfterUpdateInDynamicAnalysis(SheetDTO sheetDTO);

    void clearPreviousMarkings();

    String getSelectedCellCoordinate();

    void resetSelectedCellValue();
}
