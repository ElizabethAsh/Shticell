package components.sheetRoomComponents.commands;

import impl.engine.AvailableColumnsForFilterDTO;
import impl.engine.AvailableColumnsForSortDTO;
import javafx.beans.binding.Bindings;
import javafx.stage.Modality;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.alert.AlertUtils;
import components.sheetRoomComponents.commands.filter.FilterController;
import components.sheetRoomComponents.commands.sort.SortController;
import components.sheetRoomComponents.sheetRoom.SheetRoomMainController;
import components.sheetRoomComponents.ranges.rangesController;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import util.constants.Constants;
import util.http.HttpClientUtil;
import java.io.IOException;
import static util.constants.Constants.*;

public class CommandsController {

    @FXML private Button resetCellButton;
    @FXML private TextField rangeField;
    @FXML private Button sortButton;
    @FXML private Button filterButton;
    @FXML private ComboBox<String> alignmentBox;
    @FXML private Spinner<Integer> heightPicker;
    @FXML private Spinner<Integer> widthPicker;
    @FXML private ColorPicker textColorPicker;
    @FXML private ColorPicker backgroundColorPicker;

    private SheetRoomMainController mainController;

    private static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
    private static final Color DEFAULT_BACKGROUND_COLOR = Color.WHITE;

    @FXML
    private void initialize() {
        // alignment box
        ObservableList<String> options =
                FXCollections.observableArrayList( "Center", "Left", "Right" );
        alignmentBox.setItems(options);

        Tooltip alignmentTooltip = new Tooltip("Select text alignment");
        Tooltip.install(alignmentBox, alignmentTooltip);

        // column width picker
        SpinnerValueFactory<Integer> widthValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 200, 100, 1);
        widthPicker.setValueFactory(widthValueFactory);

        Tooltip widthTooltip = new Tooltip("Set column width");
        Tooltip.install(widthPicker, widthTooltip);


        // row height picker
        SpinnerValueFactory<Integer> heightValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 100, 40, 1);
        heightPicker.setValueFactory(heightValueFactory);

        Tooltip heightTooltip = new Tooltip("Set row width");
        Tooltip.install(heightPicker, heightTooltip);

        Tooltip textColorTooltip = new Tooltip("Select text color");
        Tooltip.install(textColorPicker, textColorTooltip);

        Tooltip backgroundColorTooltip = new Tooltip("Select background color");
        Tooltip.install(backgroundColorPicker, backgroundColorTooltip);

        Platform.runLater(() -> {
            alignmentBox.getSelectionModel().selectFirst();
            widthPicker.getValueFactory().setValue(50);
            heightPicker.getValueFactory().setValue(40);
            resetColorPickers();
        });

    }

    public void setMainController(SheetRoomMainController mainController) {
        this.mainController = mainController;

        widthPicker.valueProperty().addListener((observable, oldValue, newValue) ->
                mainController.changeColumnWidth(newValue));

        heightPicker.valueProperty().addListener((observable, oldValue, newValue) ->
                mainController.changeRowWidth(newValue));

        textColorPicker.valueProperty().addListener((observable, oldColor, newColor) -> {
                    mainController.changeTextColorListener(newColor);
                });

        backgroundColorPicker.valueProperty().addListener((observable, oldColor, newColor) -> {
                    mainController.changeBackgroundColorListener(newColor);
                });

        rangeField.textProperty().addListener((observable, oldValue, newValue) -> {
            mainController.resetSelectedCell();
        });
    }

    @FXML
    void alignmentSelectionListener(ActionEvent event) {
        int selectedIndex = alignmentBox.getSelectionModel().getSelectedIndex();
        switch (selectedIndex) {
            case 0:
                mainController.alignCells(Pos.CENTER);
                break;
            case 1:
                mainController.alignCells(javafx.geometry.Pos.CENTER_LEFT);
                break;
            case 2:
                mainController.alignCells(Pos.CENTER_RIGHT);
                break;
        }
    }

    @FXML
    void resetCellStyleListener(ActionEvent event) {
        mainController.resetCellStyle();
    }

    public void bindComponentsCellSelectionAndPermissions(SimpleBooleanProperty isCellSelected, String userPermission) {
        boolean isReader = userPermission.equals("READER");

        resetCellButton.disableProperty().bind(
                isCellSelected.not().or(Bindings.createBooleanBinding(() -> isReader))
        );

        alignmentBox.disableProperty().bind(
                isCellSelected.not().or(Bindings.createBooleanBinding(() -> isReader))
        );

        widthPicker.disableProperty().bind(
                isCellSelected.not().or(Bindings.createBooleanBinding(() -> isReader))
        );

        heightPicker.disableProperty().bind(
                isCellSelected.not().or(Bindings.createBooleanBinding(() -> isReader))
        );

        textColorPicker.disableProperty().bind(
                isCellSelected.not().or(Bindings.createBooleanBinding(() -> isReader))
        );

        backgroundColorPicker.disableProperty().bind(
                isCellSelected.not().or(Bindings.createBooleanBinding(() -> isReader))
        );
    }

    public void resetCommands() {
        rangeField.clear();
        resetColorPickers();
    }

    public void setAlignment(Pos alignment) {
        if (alignment == Pos.CENTER) {
            alignmentBox.getSelectionModel().select("Center");
        } else if (alignment == Pos.CENTER_LEFT) {
            alignmentBox.getSelectionModel().select("Left");
        } else if (alignment == Pos.CENTER_RIGHT) {
            alignmentBox.getSelectionModel().select("Right");
        }
    }

    public void setHeightPicker(int height) {
        heightPicker.getValueFactory().setValue(height);
    }

    public void setWidthPicker(int width) {
        widthPicker.getValueFactory().setValue(width);
    }

    public void setTextColor(Color color) {
        textColorPicker.setValue(color);
    }

    public void setBackgroundColor(Color color) {
        backgroundColorPicker.setValue(color);
    }

    public void resetColorPickers() {
        textColorPicker.setValue(DEFAULT_TEXT_COLOR);
        backgroundColorPicker.setValue(DEFAULT_BACKGROUND_COLOR);
    }

    public void SortListener(ActionEvent actionEvent) throws IOException {
        String from, to;

        try {
            String[] boundaries = getRangeToSortAndFilter();
            from = boundaries[0];
            to = boundaries[1];
        } catch (Exception e) {
            AlertUtils.showErrorMessage("Error: " + e.getMessage());
            return;
        }

        mainController.clearPreviousMarkings();
        String finalUrl = Constants.SORT_URL + "?from_coordinate=" + from + "&to_coordinate=" + to;

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to get available columns for sort: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        AvailableColumnsForSortDTO availableColumns = Constants.GSON_INSTANCE.fromJson(responseBody, AvailableColumnsForSortDTO.class);

                        Platform.runLater(() -> {
                            openSortPopup(from, to, FXCollections.observableArrayList(availableColumns.getAvailableColumns()));
                        });
                    } else {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> AlertUtils.showErrorMessage(errorMessage));
                    }
                }
            }
        });
    }

    private void openSortPopup(String from, String to, ObservableList<String> availableColumns){
        if (availableColumns == null || availableColumns.isEmpty()) {
            AlertUtils.showErrorMessage("No numeric columns available",
                    "No Numeric Columns",
                    "There are no numeric columns available for sorting in the selected range.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/sheetRoomComponents/commands/sort/sort.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Sort Table Columns");
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(mainController.getPrimaryStage());

            SortController sortController = loader.getController();
            sortController.setMainController(mainController);
            sortController.setStage(popupStage);
            sortController.setAvailableColumns(availableColumns);
            sortController.setRange(from, to);

            popupStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorMessage("Failed to open permission request popup.");
        }
    }

    public void FilterListener(ActionEvent actionEvent){
        String from, to;

        try {
            String[] boundaries = getRangeToSortAndFilter();
            from = boundaries[0];
            to = boundaries[1];
        } catch (Exception e) {
            AlertUtils.showErrorMessage("Error: " + e.getMessage());
            return;
        }

        mainController.clearPreviousMarkings();
        String finalUrl = FILTER_ACTIONS_URL  + "/getColumns?from_coordinate=" + from + "&to_coordinate=" + to;

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to get available columns for sort: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        AvailableColumnsForFilterDTO availableColumns = Constants.GSON_INSTANCE.fromJson(responseBody, AvailableColumnsForFilterDTO.class);

                        Platform.runLater(() -> {
                            openFilterPopup(from, to, FXCollections.observableArrayList(availableColumns.getAvailableColumns()));
                        });
                    } else {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> AlertUtils.showErrorMessage(errorMessage));
                    }
                }
            }
        });
    }

    private void openFilterPopup(String from, String to, ObservableList<String> availableColumns){
        if (availableColumns == null || availableColumns.isEmpty()) {
            AlertUtils.showErrorMessage("No columns with data available",
                    "All Columns Are Empty",
                    "There are no columns with data in the selected range.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/sheetRoomComponents/commands/filter/filter.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Filter Table Columns");
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(mainController.getPrimaryStage());

            FilterController filterController = loader.getController();
            filterController.setMainController(mainController);
            filterController.setStage(popupStage);
            filterController.setAvailableColumns(availableColumns);
            filterController.setRange(from, to);

            popupStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorMessage("Failed to open permission request popup.");
        }
    }

    private String[] getRangeToSortAndFilter() throws Exception {
        String range = rangeField.getText();

        return rangesController.extractRangeBoundaries(range);
    }

}
