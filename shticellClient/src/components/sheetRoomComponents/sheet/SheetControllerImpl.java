package components.sheetRoomComponents.sheet;

import impl.engine.CellDTO;
import impl.engine.SheetDTO;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.alert.AlertUtils;
import util.animation.AnimationManager;
import components.sheetRoomComponents.sheetRoom.SheetRoomMainController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import util.constants.Constants;
import util.http.HttpClientUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static util.constants.Constants.DYNAMIC_ANALYSIS_URL;

public class SheetControllerImpl implements SheetController {
    private Map<Label, String> cellCoordinates;
    private GridPane masterGridPane;
    private ObjectProperty<Label> selectedCell;
    private SimpleBooleanProperty hasSelectedCell ;
    private UIModel uiModel;
    SheetRoomMainController mainController;
    private Set<Label> previouslyMarkedDependsOnCells = new HashSet<>();
    private Set<Label> previouslyMarkedInfluenceOnCells = new HashSet<>();
    private Set<Label> previouslyMarkedCellsInRange = new HashSet<>();
    private boolean animationsEnabled;

    public SheetControllerImpl() {
        cellCoordinates = new HashMap<>();
        selectedCell = new SimpleObjectProperty<>(null);
        hasSelectedCell = new SimpleBooleanProperty(false);
        this.animationsEnabled = true;
        uiModel = new UIModel();

        hasSelectedCell.bind(selectedCell.isNotNull());
        selectedCell.addListener((observableValue, oldLabelSelection, newSelectedLabel) -> {
            if (oldLabelSelection != null) {
                oldLabelSelection.setId(null);
            }
            if (newSelectedLabel != null) {
                newSelectedLabel.setId("selected-cell");
                mainController.displayCellDetails();
            }
        });
    }

    public SimpleBooleanProperty getHasSelectedCell() { return hasSelectedCell; }

    public void setAnimationsEnabled(boolean animationsEnabled) { this.animationsEnabled = animationsEnabled; }

    public void setMainController(SheetRoomMainController appController) { mainController = appController; }

    public GridPane createDynamicSheet(SheetDTO dto) {
        cellCoordinates.clear();
        selectedCell.set(null);
        clearPreviousMarkings();
        previouslyMarkedDependsOnCells.clear();
        previouslyMarkedInfluenceOnCells.clear();
        previouslyMarkedCellsInRange.clear();

        masterGridPane = SheetGridBuilder.createDynamicSheet(dto, cellCoordinates);
        for (Label label : cellCoordinates.keySet()) { addClickEventForCell(label); }

        uiModel.resetLabelMap(cellCoordinates.keySet().toArray(new Label[0]));

        return masterGridPane;
    }

    @Override
    public void alignCells(Pos alignment) {
        if (selectedCell.get() != null) {
            String selectedCoordinate = getSelectedCellCoordinate();
            int[] parsedCoordinate = parseCoordinate(selectedCoordinate);
            int selectedColumnIndex = parsedCoordinate[1];

            for (Label label : cellCoordinates.keySet()) {
                String cellId = cellCoordinates.get(label);
                int[] parsedCellCoordinate = parseCoordinate(cellId);
                int cellColumnIndex = parsedCellCoordinate[1];

                if (cellColumnIndex == selectedColumnIndex) {
                    label.setAlignment(alignment);
                }
            }
        }
    }

    @Override
    public void changeTextColor(Color newColor) {
        if (selectedCell.get() != null){
            selectedCell.get().setTextFill(newColor);
        }
    }

    @Override
    public void changeBackgroundColor(Color newColor) {
        Label label = selectedCell.get();

        if(label != null) {
            if (!newColor.equals(Color.WHITE)) {
                label.getStyleClass().remove("background-cell");
                BackgroundFill backgroundFill = new BackgroundFill(newColor, CornerRadii.EMPTY, Insets.EMPTY);
                label.setBackground(new Background(backgroundFill));
            } else {
                label.setBackground(null);
                if (!label.getStyleClass().contains("background-cell")) {
                    label.getStyleClass().add("background-cell");
                }
            }
        }
    }

    @Override
    public void resetCellStyle() {
        if (selectedCell.get() != null){
            selectedCell.get().getStyleClass().add("background-cell");
            selectedCell.get().setTextFill(Color.BLACK);
        }
    }

    @Override
    public void changeColumnWidth(double width) {
        if (selectedCell.get() != null) {
            String selectedCoordinate = getSelectedCellCoordinate();
            int[] parsedCoordinate = parseCoordinate(selectedCoordinate);
            int columnIndex = parsedCoordinate[1];
            ColumnConstraints columnConstraints = masterGridPane.getColumnConstraints().get(columnIndex + 1);
            columnConstraints.setMinWidth(width);
            columnConstraints.setPrefWidth(width);
            columnConstraints.setMaxWidth(width);
        }
    }

    @Override
    public void changeRowWidth(double width) {
        if (selectedCell.get() != null) {
            String selectedCoordinate = getSelectedCellCoordinate();
            int[] parsedCoordinate = parseCoordinate(selectedCoordinate);
            int rowIndex = parsedCoordinate[1];
            RowConstraints rowConstraints = masterGridPane.getRowConstraints().get(rowIndex + 1);
            rowConstraints.setMinHeight(width);
            rowConstraints.setPrefHeight(width);
            rowConstraints.setMaxHeight(width);
        }
    }

    @Override
    public void updateSheetAfterUpdateCell(SheetDTO sheetDTO) {
        updateCellsProperties(sheetDTO);
        if(animationsEnabled) {
            AnimationManager.animateLabelShake(selectedCell.get());
        }

        mainController.displayCellDetails();
    }

    @Override
    public void updateSheetAfterUpdateInDynamicAnalysis(SheetDTO sheetDTO) {
        updateCellsProperties(sheetDTO);
        mainController.displayCellDetails();
    }

    @Override
    public void updateSheetAfterAddingRange(SheetDTO sheetDTO) {
        updateCellsProperties(sheetDTO);
    }

    @Override
    public void displayRange(Set<String> cellsInRange) {
        clearPreviousMarkings();
        previouslyMarkedCellsInRange = markCells(cellsInRange, "range-cell");

        for (String cellCoordinate : cellsInRange) {
            Label cellLabel = getLabelByCellId(cellCoordinate);  // השתמש ב-String ישירות
            if (animationsEnabled) {
                AnimationManager.animateLabelHover(cellLabel);
            }
        }
    }

    private void updateCellsProperties(SheetDTO sheetDTO) {
        sheetDTO.getActiveCells().forEach((coordinate, cellDTO) -> {
            Label label = getLabelByCellId(coordinate);
            uiModel.updateCellContent(label, FormattedValuePrinter.formatCellValue(cellDTO.getCellEffectiveValue()));
        });
    }

    public void displayCellDetails(CellDTO cellDTO) {
        clearPreviousMarkings();

        if(cellDTO != null){
            previouslyMarkedDependsOnCells = markCells(cellDTO.getDependsOn(), "depends-on-cell");
            previouslyMarkedInfluenceOnCells = markCells(cellDTO.getInfluencingOn(), "influence-on-cell");
        }
    }

    @Override
    public void resetSelectedCellValue() {
        uiModel.getPropertyForLabel(selectedCell.get()).set(null);
    }

    public void clearPreviousMarkings() {
        clearPreviousCellMarkings(previouslyMarkedDependsOnCells, "depends-on-cell");
        clearPreviousCellMarkings(previouslyMarkedInfluenceOnCells, "influence-on-cell");
        clearPreviousCellMarkings(previouslyMarkedCellsInRange, "range-cell");
    }

    public void clearPreviousCellMarkings(Set<Label> previouslyMarkedCells, String styleClass) {
        for (Label label : previouslyMarkedCells) {
            label.getStyleClass().remove(styleClass);
        }
    }

    public Set<Label> markCells(Set<String> cellCoordinates, String styleClass) {
        Set<Label> markedLabels = new HashSet<>();
        for (String cellId : cellCoordinates) {
            Label label = getLabelByCellId(cellId);
            label.getStyleClass().add(styleClass);
            markedLabels.add(label);
        }
        return markedLabels;
    }

    private Label getLabelByCellId(String cellId) {
        for (Map.Entry<Label, String> entry : cellCoordinates.entrySet()) {
            if (entry.getValue().equals(cellId)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public String getSelectedCellCoordinate() {
        return cellCoordinates.get(selectedCell.get());
    }

    private void addClickEventForCell(Label label) {
        label.setOnMouseClicked(event -> {
            if (animationsEnabled) {
                AnimationManager.animateLabelRotate(label);
            }

            selectedCell.set(label);
            notifyAppControllerAboutSelectedCell(label);

            String selectedCoordinate = getSelectedCellCoordinate();
            checkIfCellAllowsDynamicAnalysis(selectedCoordinate);
        });
    }

    private void notifyAppControllerAboutSelectedCell(Label label) {
        Pos alignment = label.getAlignment();
        Color textColor = (Color) label.getTextFill();

        Background background = label.getBackground();
        Color backgroundColor = null;
        if (background != null && !background.getFills().isEmpty()) {
            backgroundColor = (Color) background.getFills().get(0).getFill();
        }

        double columnWidth = label.getWidth();
        double rowHeight = label.getHeight();

        mainController.updateCommandsControllerForCell(alignment, textColor, backgroundColor, columnWidth, rowHeight);
    }

    public void resetSelectedCell() {
        selectedCell.set(null);
        clearPreviousMarkings();
        mainController.setActionBarComponentsToStart();
    }

    private void checkIfCellAllowsDynamicAnalysis(String selectedCoordinate) {
        String finalUrl = DYNAMIC_ANALYSIS_URL + "?cellId=" + selectedCoordinate;

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mainController.updateIsDynamicChangeAllowed(false);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        CellDTO cellDTO = Constants.GSON_INSTANCE.fromJson(responseBody, CellDTO.class);
                        String cellValue = cellDTO.getCellOriginalValue();

                        Platform.runLater(() -> {
                            mainController.updateIsDynamicChangeAllowed(true);
                            mainController.setOriginalCellValueInDynamicAnalysis(cellValue);
                        });

                    } else {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> {
                            mainController.updateIsDynamicChangeAllowed(false);
                        });
                    }
                }
            }
        });
    }

    public int[] parseCoordinate(String coordinate) {
        char columnChar = coordinate.charAt(0);
        int columnIndex = columnChar - 'A';

        int rowIndex = Integer.parseInt(coordinate.substring(1)) - 1;

        return new int[]{rowIndex, columnIndex};
    }
}
