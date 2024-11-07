package components.sheetRoomComponents.sheet;

import impl.engine.CellDTO;
import impl.engine.SheetDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;
import util.ColorUtil;

import java.util.HashMap;
import java.util.Map;

public class SheetGridBuilder {

    public static GridPane createDynamicSheet(SheetDTO dto, Map<Label, String> cells) {
        int height = dto.getSheetHeight();
        int width = dto.getSheetWidth();
        int cellHeight = dto.getCellHeight();
        int cellWidth = dto.getCellWidth();
        Map<String, CellDTO> cellsData = dto.getActiveCells();

        GridPane gridPane = new GridPane();

        addColumnHeaders(gridPane, width, cellWidth, cellHeight);
        addRowHeaders(gridPane, height, cellWidth, cellHeight);
        addCellsToGrid(gridPane, dto, cellsData, cells, height, width, cellWidth, cellHeight);

        gridPane.setHgap(10.0);
        gridPane.setVgap(10.0);


        return gridPane;
    }

    private static void addColumnHeaders(GridPane gridPane, int width, int cellWidth, int cellHeight) {
        ColumnConstraints rowHeaderColConstraints = new ColumnConstraints(cellWidth);
        gridPane.getColumnConstraints().add(rowHeaderColConstraints);

        for (int i = 0; i < width; i++) {
            ColumnConstraints colConstraints = new ColumnConstraints(cellWidth);
            gridPane.getColumnConstraints().add(colConstraints);

            Label columnHeader = new Label(Character.toString((char) ('A' + i)));
            columnHeader.setMinSize(cellWidth, cellHeight);
            columnHeader.setPrefSize(cellWidth, cellHeight);
            columnHeader.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            columnHeader.setAlignment(Pos.CENTER);
            columnHeader.setStyle("-fx-border-color: black; -fx-font-weight: bold;");
            gridPane.add(columnHeader, i + 1, 0);
        }

        RowConstraints columnHeaderRowConstraints = new RowConstraints(cellHeight);
        gridPane.getRowConstraints().add(columnHeaderRowConstraints);

    }

    private static void addRowHeaders(GridPane gridPane, int height, int cellWidth, int cellHeight) {
        for (int i = 0; i < height; i++) {
            RowConstraints rowConstraints = new RowConstraints(cellHeight);
            gridPane.getRowConstraints().add(rowConstraints);

            Label rowHeader = new Label(String.format("%02d", i + 1));
            rowHeader.setMinSize(cellWidth, cellHeight);
            rowHeader.setPrefSize(cellWidth, cellHeight);
            rowHeader.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            rowHeader.setStyle("-fx-border-color: black; -fx-font-weight: bold;");
            gridPane.add(rowHeader, 0, i + 1);
        }
    }

    private static void addCellsToGrid(GridPane gridPane, SheetDTO dto, Map<String, CellDTO> cellsData, Map<Label, String> cells,
                                       int height, int width, int cellWidth, int cellHeight) {
        Map<String, Color> backgroundColors = getBackgroundColorsAsColors(dto);
        Map<String, Color> textColors = getTextColorsAsColors(dto);

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                String cellId = Character.toString((char) ('A' + col)) + (row + 1);
                Label label = createCellLabel(cellWidth, cellHeight);
                gridPane.add(label, col + 1, row + 1);

                if (cellsData.containsKey(cellId)) {
                    CellDTO cellDTO = cellsData.get(cellId);
                    if (cellDTO != null) {
                        Object value = cellDTO.getCellEffectiveValue();
                        String cellValue = FormattedValuePrinter.formatCellValue(value);
                        label.setText(cellValue);
                    }
                }

                if (backgroundColors.containsKey(cellId)) {
                    BackgroundFill backgroundFill = new BackgroundFill(backgroundColors.get(cellId), CornerRadii.EMPTY, Insets.EMPTY);
                    label.setBackground(new Background(backgroundFill));
                } else {
                    label.getStyleClass().add("background-cell");
                }

                if (textColors.containsKey(cellId)) {
                    label.setTextFill(textColors.get(cellId));
                }

                cells.put(label, cellId);
            }
        }
    }

    public static Map<String, Color> getTextColorsAsColors(SheetDTO sheetDTO) {
        Map<String, Color> colors = new HashMap<>();
        sheetDTO.getTextColors().forEach((coordinate, colorString) -> colors.put(coordinate, ColorUtil.hexToColor(colorString)));
        return colors;
    }

    public static Map<String, Color> getBackgroundColorsAsColors(SheetDTO sheetDTO) {
        Map<String, Color> colors = new HashMap<>();
        sheetDTO.getBackgroundColors().forEach((coordinate, colorString) -> colors.put(coordinate, ColorUtil.hexToColor(colorString)));
        return colors;
    }

    private static Label createCellLabel(int cellWidth, int cellHeight) {
        Label label = new Label();
        label.setMinSize(cellWidth, cellHeight);
        label.setPrefSize(cellWidth, cellHeight);
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        label.getStyleClass().add("single-cell");
        label.setAlignment(Pos.CENTER);
        return label;
    }
}
