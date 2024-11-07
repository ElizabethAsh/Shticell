package components.sheetRoomComponents.sheetRoom;

import components.main.SheetAppMainController;
import engine.api.Engine;
import engine.impl.EngineImpl;
import exception.InvalidCoordinateException;
import exception.RangeExistsException;
import impl.engine.CellDTO;
import impl.engine.RangeDTO;
import impl.engine.SheetDTO;
import impl.ui.*;
import components.sheetRoomComponents.commands.CommandsController;
import components.sheetRoomComponents.dynamicAnalysis.DynamicAnalysisController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.ColorUtil;
import util.alert.AlertUtils;
import components.sheetRoomComponents.ranges.rangesController;
import components.sheetRoomComponents.sheet.SheetGridBuilder;
import components.sheetRoomComponents.sheet.SheetController;
import components.sheetRoomComponents.sheet.SheetControllerImpl;
import components.sheetRoomComponents.toolBar.ActionBarController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import util.constants.Constants;
import util.http.HttpClientUtil;
import java.io.Closeable;
import java.io.IOException;
import java.util.*;

import static util.constants.Constants.*;


public class SheetRoomMainController implements Closeable {
    public static final String SELECTED_CELL_ID_LABEL = "Selected Cell ID";
    public static final String ORIGINAL_CELL_VALUE_LABEL = "Original Cell Value";
    public static final String LAST_UPDATED_CELL_VERSION_LABEL = "Last Updated Cell Version & username";
    @FXML private GridPane sheetGridPane;
    @FXML private VBox centerPane;
    @FXML private BorderPane root;
    @FXML private VBox actionBarComponent;
    @FXML private ActionBarController actionBarComponentController;
    @FXML private FlowPane commandsComponent;
    @FXML private CommandsController commandsComponentController;
    @FXML private FlowPane rangesComponent;
    @FXML private rangesController rangesComponentController;
    @FXML private HBox dynamicAnalysisComponent;
    @FXML private DynamicAnalysisController dynamicAnalysisComponentController;

    private SimpleStringProperty selectedCellIdProperty;
    private SimpleStringProperty lastUpdateCellVersionProperty;
    private SimpleStringProperty originalCellValueProperty;
    private SimpleBooleanProperty isDynamicChangeAllowed;
    private String originalCellValue;

    private Stage primaryStage;
    private Engine engine = new EngineImpl();
    private SheetController sheetController;
    private SheetAppMainController sheetAppMainController;

    public SheetRoomMainController() {
        selectedCellIdProperty = new SimpleStringProperty();
        lastUpdateCellVersionProperty = new SimpleStringProperty();
        originalCellValueProperty = new SimpleStringProperty();
        sheetController = new SheetControllerImpl();
        isDynamicChangeAllowed = new SimpleBooleanProperty(false);
    }

    @FXML
    public void initialize() {
        if(actionBarComponentController != null) {
            setActionBarComponentsToStart();

            commandsComponentController.setMainController(this);
            actionBarComponentController.setMainController(this);
            rangesComponentController.setMainController(this);
            dynamicAnalysisComponentController.setMainController(this);

            dynamicAnalysisComponentController.bindComponents(isDynamicChangeAllowed);
            actionBarComponentController.getSelectedCellIdLabel().textProperty().bind(selectedCellIdProperty);
            actionBarComponentController.getOriginalCellValueLabel().textProperty().bind(originalCellValueProperty);
            actionBarComponentController.getLastUpdateCellVersionLabel().textProperty().bind(lastUpdateCellVersionProperty);
        }
    }

    public void setActive(){
        actionBarComponentController.startVersionRefresher();
    }

    public Engine getEngine() { return engine; }

    public Stage getPrimaryStage() { return primaryStage; }

    public void setPrimaryStage(Stage primaryStage) { this.primaryStage = primaryStage; }

    public void setEngine(Engine engine) { this.engine = engine; }

    public void updateIsDynamicChangeAllowed(boolean value) { isDynamicChangeAllowed.set(value); }

    public void loadSheet(SheetDTO sheetDTO) {
        initializeSheet(sheetDTO);
    }

    public void initializeSheet(SheetDTO sheetDTO) {
        String userPermission = sheetDTO.getPermissionType();
        sheetController.setMainController(this);
        GridPane gridPane = sheetController.createDynamicSheet(sheetDTO);
        isDynamicChangeAllowed.set(false);

        sheetGridPane.getChildren().clear();
        sheetGridPane.add(gridPane, 0, 0);

        int selectedStyle = actionBarComponentController.getSelectedStyle();
        changeSkin(selectedStyle);

        setActionBarComponentsToStart();
        commandsComponentController.resetCommands();
        rangesComponentController.updateButtonsState(userPermission);
        actionBarComponentController.bindComponentsCellSelectionAndPermissions(sheetController.getHasSelectedCell(), userPermission);
        commandsComponentController.bindComponentsCellSelectionAndPermissions(sheetController.getHasSelectedCell(), userPermission);
    }

    public void setActionBarComponents(String cellId, String cellOriginalValue, String cellVersion) {
        selectedCellIdProperty.setValue(cellId);
        originalCellValueProperty.setValue(cellOriginalValue);
        lastUpdateCellVersionProperty.setValue(cellVersion);
    }

    public void setActionBarComponentsToStart(){
        setActionBarComponents(SELECTED_CELL_ID_LABEL, ORIGINAL_CELL_VALUE_LABEL, LAST_UPDATED_CELL_VERSION_LABEL);
    }

    public void changeColumnWidth(Integer newValue) {
        sheetController.changeColumnWidth(newValue);
    }

    public void changeRowWidth(Integer newValue) {
        sheetController.changeRowWidth(newValue);
    }

    public void alignCells(Pos pos) {
        sheetController.alignCells(pos);
    }

    public void changeTextColorListener(Color newColor) {
        String cellCoordinate = sheetController.getSelectedCellCoordinate();
        String colorHex = ColorUtil.colorToHex(newColor);

        if(cellCoordinate == null) {
            return;
        }

        UpdateCellStyleRequestDTO updateRequest = new UpdateCellStyleRequestDTO(cellCoordinate, colorHex);
        String jsonBody = Constants.GSON_INSTANCE.toJson(updateRequest);

        HttpClientUtil.runAsyncPut(UPDATE_CELL_STYLE_TEXT_URL, RequestBody.create(jsonBody, MediaType.parse("application/json")), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to update text color: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    if (response.isSuccessful()) {
                        Platform.runLater(() -> {
                            sheetController.changeTextColor(newColor);
                        });
                    } else {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> AlertUtils.showErrorMessage("Error: " + errorMessage));
                    }
                }
            }
        });
    }

    public void changeBackgroundColorListener(Color newColor) {
        String cellCoordinate = sheetController.getSelectedCellCoordinate();
        String colorHex = ColorUtil.colorToHex(newColor);

        if(cellCoordinate == null) {
            return;
        }

        UpdateCellStyleRequestDTO updateRequest = new UpdateCellStyleRequestDTO(cellCoordinate, colorHex);
        String jsonBody = Constants.GSON_INSTANCE.toJson(updateRequest);

        HttpClientUtil.runAsyncPut(UPDATE_CELL_STYLE_BACKGROUND_URL, RequestBody.create(jsonBody, MediaType.parse("application/json")), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to update text color: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    if (response.isSuccessful()) {
                        Platform.runLater(() -> {
                            sheetController.changeBackgroundColor(newColor);
                        });
                    } else {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> AlertUtils.showErrorMessage("Error: " + errorMessage));
                    }
                }
            }
        });
    }

    public void resetCellStyle() {
        String cellCoordinate = sheetController.getSelectedCellCoordinate();

        if(cellCoordinate != null) {
            changeTextColorListener(Color.BLACK);
            changeBackgroundColorListener(Color.WHITE);
            sheetController.resetCellStyle();
            commandsComponentController.resetCommands();
        }
    }

    public void updateCommandsControllerForCell(Pos alignment, Color textColor, Color backgroundColor, double columnWidth, double rowHeight) {
        commandsComponentController.setAlignment(alignment);
        commandsComponentController.setTextColor(textColor);

        if (backgroundColor != null) {
            commandsComponentController.setBackgroundColor(backgroundColor);
        } else {
            commandsComponentController.setBackgroundColor(Color.WHITE);
        }

        commandsComponentController.setWidthPicker((int) columnWidth);
        commandsComponentController.setHeightPicker((int) rowHeight);
    }

    public void updateCellValue(String content) {
        String cellCoordinate = sheetController.getSelectedCellCoordinate();
        CellUpdateRequestDTO updateRequest = new CellUpdateRequestDTO(cellCoordinate, content);
        String jsonBody = Constants.GSON_INSTANCE.toJson(updateRequest);

        HttpClientUtil.runAsyncPut(CELL_ACTION_URL, RequestBody.create(jsonBody, MediaType.parse("application/json")), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to update text color: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    if (response.isSuccessful()) {
                        String responseString = response.body().string();
                        SheetDTO sheetDTO = Constants.GSON_INSTANCE.fromJson(responseString, SheetDTO.class);

                        Platform.runLater(() -> {
                            sheetController.updateSheetAfterUpdateCell(sheetDTO);
                        });
                    } else {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> AlertUtils.showErrorMessage("Error: " + errorMessage));
                    }
                }
            }
        });
    }

    public void showSelectedVersionSheet(SheetDTO sheetDTO, int selectedVersion) {
        displayInStage("Sheet Version: " + selectedVersion, sheetDTO, true);
    }

    public void addNewRange(String rangeName, String from, String to, Callback callback) throws InvalidCoordinateException, RangeExistsException {
        AddNewRangeRequestDTO addNewRangeDTO = new AddNewRangeRequestDTO(rangeName, from, to);
        String jsonBody = Constants.GSON_INSTANCE.toJson(addNewRangeDTO);
        HttpClientUtil.runAsyncWithJson(Constants.RANGE_URL, jsonBody, callback, HTTP_POST);
    }

    public void updateSheetAfterAddingRange(SheetDTO sheetDTO) {
        if (sheetDTO == null) {
            System.out.println("Error: sheetDTO is null. Cannot update sheet.");
            return;
        }
        sheetController.updateSheetAfterAddingRange(sheetDTO);
    }

    public void displayRange(String selectedRange, Callback callback) {
        String finalUrl = Constants.RANGE_URL + "?rangeName=" + selectedRange;
        HttpClientUtil.runAsync(finalUrl, callback);
    }

    public void showSelectRange(RangeDTO selectedRange) {
        sheetController.displayRange(selectedRange.getCellsInRange());
    }

    public void deleteRange(String selectedRange, Callback callback){
        RangeNameDTO rangeNameDTO = new RangeNameDTO(selectedRange);
        String jsonBody = Constants.GSON_INSTANCE.toJson(rangeNameDTO);
        HttpClientUtil.runAsyncWithJson(Constants.RANGE_URL, jsonBody, callback, HTTP_DELETE);
    }

    public void clearPreviousMarkings(){
        sheetController.clearPreviousMarkings();
    }

    public void applySort(String from, String to, ArrayList<String> selectedColumns, Callback callback){
        SortRequestDTO sortRequestDTO = new SortRequestDTO(from, to, selectedColumns);
        String jsonBody = Constants.GSON_INSTANCE.toJson(sortRequestDTO);
        HttpClientUtil.runAsyncWithJson(Constants.SORT_URL, jsonBody, callback, HTTP_POST);
    }

    public void displaySortedSheet(SheetDTO sortedSheet) {
        displayInStage("Sorted Sheet", sortedSheet, false);
    }

    public void applyFilter(String from, String to, Map<String, List<String>> selectedColumnValues, Callback callback) {
        FilterRequestDTO filterRequestDTO = new FilterRequestDTO(selectedColumnValues, from, to);
        String jsonBody = Constants.GSON_INSTANCE.toJson(filterRequestDTO);
        String finalUrl = FILTER_ACTIONS_URL + "/filter";
        HttpClientUtil.runAsyncWithJson(finalUrl, jsonBody, callback, HTTP_POST);
    }

    public void displayFilteredSheet(SheetDTO filteredSheet) {
        displayInStage("Filtered Sheet", filteredSheet, false);
    }

    private void displayInStage(String title, SheetDTO dtoFromEngine, boolean isVersionDisplay) {
        Stage stage = new Stage();
        stage.setTitle(title);

        Map<Label, String> coordinates = new HashMap<>();
        GridPane gridPane = SheetGridBuilder.createDynamicSheet(dtoFromEngine, coordinates);

        if (isVersionDisplay) {
            coordinates.keySet().forEach(label -> {
                label.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                label.setTextFill(Color.BLACK);
            });
        }

        gridPane.getStylesheets().add(getClass().getResource("/components/sheetRoomComponents/sheet/style/single-cell.css").toExternalForm());

        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Scene scene = new Scene(scrollPane, 800, 600);
        stage.setScene(scene);
        stage.setWidth(800);
        stage.setHeight(600);
        stage.show();
    }

    public void changeSkin(int skinNumber) {
        SkinManager.applySkin(skinNumber, root, commandsComponent, rangesComponent, centerPane, actionBarComponent);
    }

    public void setAnimationsEnabled(boolean animationsEnabled) {
        sheetController.setAnimationsEnabled(animationsEnabled);
    }

    public void setOriginalCellValueInDynamicAnalysis(String originalCellValue) {
        this.originalCellValue = originalCellValue;
    }

    public void startDynamicAnalysis() {
        blockComponents();
    }

    public void stopDynamicAnalysis() {
        updateCellValueInDynamicAnalysis(originalCellValue);
        unblockComponents();
    }

    public void blockComponents() {
        centerPane.disableProperty().unbind();
        actionBarComponent.disableProperty().unbind();
        commandsComponent.disableProperty().unbind();
        rangesComponent.disableProperty().unbind();

        centerPane.setDisable(true);
        actionBarComponent.setDisable(true);
        commandsComponent.setDisable(true);
        rangesComponent.setDisable(true);
    }

    public void unblockComponents() {
        centerPane.setDisable(false);
        actionBarComponent.setDisable(false);
        commandsComponent.setDisable(false);
        rangesComponent.setDisable(false);
    }

    public void updateCellValueInDynamicAnalysis(String content) {
        String cellCoordinate = sheetController.getSelectedCellCoordinate();
        if (cellCoordinate == null || content == null) {
            AlertUtils.showErrorMessage("Error, Cell coordinate or content is invalid.");
            return;
        }

        CellUpdateRequestDTO updateRequest = new CellUpdateRequestDTO(cellCoordinate, content);
        String jsonBody = Constants.GSON_INSTANCE.toJson(updateRequest);

        HttpClientUtil.runAsyncWithJson(DYNAMIC_ANALYSIS_URL, jsonBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to update cell in dynamic analysis: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        SheetDTO sheetDTO = Constants.GSON_INSTANCE.fromJson(responseBody, SheetDTO.class);

                        Platform.runLater(() -> {
                            sheetController.updateSheetAfterUpdateInDynamicAnalysis(sheetDTO);
                        });

                    } else {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> AlertUtils.showErrorMessage("Error: " + errorMessage));
                    }
                }
            }
        }, "PUT");
    }

    public void displayCellDetails(){
        String cellId = sheetController.getSelectedCellCoordinate();
        String finalUrl = CELL_ACTION_URL + "?cellId=" + cellId;

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to get cell value: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        CellDTO cellDTO = Constants.GSON_INSTANCE.fromJson(responseBody, CellDTO.class);
                        Platform.runLater(() -> {
                            sheetController.displayCellDetails(cellDTO);
                            if(cellDTO != null){
                                setActionBarComponents(cellId, cellDTO.getCellOriginalValue(), String.valueOf(cellDTO.getCellVersion()) + " & " + cellDTO.getLastUpdatedBy());
                            } else{
                                sheetController.resetSelectedCellValue();
                                setActionBarComponents(cellId, "Empty cell", LAST_UPDATED_CELL_VERSION_LABEL);
                            }
                        });
                    } else {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> AlertUtils.showErrorMessage(errorMessage));
                    }
                }
            }
        });
    }

    public void resetSelectedCell() {
        sheetController.resetSelectedCell();
        isDynamicChangeAllowed.set(false);
    }

    public void setSheetAppMainController(SheetAppMainController sheetAppMainController) {
        this.sheetAppMainController = sheetAppMainController;
    }

    public void handleBackToDashboard(ActionEvent actionEvent) {
        if (sheetAppMainController != null) {
            sheetAppMainController.switchToDashboardRoom();
        }
    }

    public void close() {
        if (actionBarComponentController != null) {
            actionBarComponentController.close();
        }
    }
}

