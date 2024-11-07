package components.sheetRoomComponents.toolBar;
import components.sheetRoomComponents.sheetRoom.SheetRoomMainController;
import components.sheetRoomComponents.toolBar.versionRefresher.VersionRefresher;
import components.sheetRoomComponents.toolBar.versionSelector.VersionSelectorController;
import impl.engine.NumOfVersionsDTO;
import impl.engine.SheetDTO;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.alert.AlertUtils;
import util.constants.Constants;
import util.http.HttpClientUtil;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static util.constants.Constants.*;

public class ActionBarController implements Closeable{
    // skin and animation bonus
    @FXML private ComboBox<String> animationSelectorComboBox;
    @FXML private ComboBox<String> skinSelectorComboBox;

    // actions
    @FXML private Label lastUpdateCellVersionLabel;
    @FXML private Label originalCellValueLabel;
    @FXML private Label selectedCellIdLabel;
    @FXML private Button updateValueButton;
    @FXML private Button updateToLatestVersionButton;

    private SheetRoomMainController mainController;
    private Timer timer;
    private TimerTask versionRefresher;


    @FXML
    private void initialize() {

        ObservableList<String> skinOptions =
                FXCollections.observableArrayList( "STYLE1", "STYLE2", "STYLE3");
        skinSelectorComboBox.setItems(skinOptions);
        skinSelectorComboBox.setValue("STYLE1");

        ObservableList<String> animationOptions =
                FXCollections.observableArrayList( "ANIMATION", "NO ANIMATION");
        animationSelectorComboBox.setItems(animationOptions);
        animationSelectorComboBox.setValue("ANIMATION");
    }

    public Label getLastUpdateCellVersionLabel() { return lastUpdateCellVersionLabel; }

    public Label getOriginalCellValueLabel() { return originalCellValueLabel; }

    public Label getSelectedCellIdLabel() { return selectedCellIdLabel; }

    @FXML
    void updateValueButtonListener(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Update Cell Value");
        dialog.setHeaderText("Enter new value for the selected cell:");
        dialog.setContentText("New value:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(newValue -> {
            String newCellValue = newValue;
            mainController.updateCellValue(newCellValue);
        });

    }

    @FXML
    void versionSelectorListener(ActionEvent event) {
        displayVersions();
    }

    public void setMainController(SheetRoomMainController mainController) {
        this.mainController = mainController;
    }

    public void bindComponentsCellSelectionAndPermissions(SimpleBooleanProperty isCellSelected, String userPermission) {
        boolean isReader = userPermission.equals("READER");

        updateValueButton.disableProperty().bind(
                isCellSelected.not().or(Bindings.createBooleanBinding(() -> isReader))
        );
        lastUpdateCellVersionLabel.disableProperty().bind(isCellSelected.not());
        originalCellValueLabel.disableProperty().bind(isCellSelected.not());
        selectedCellIdLabel.disableProperty().bind(isCellSelected.not());
    }

    public void displayVersions(){
        HttpClientUtil.runAsync(GET_SHEET_VERSIONS_URL, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to get versions: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        NumOfVersionsDTO versionsDTO = Constants.GSON_INSTANCE.fromJson(responseBody, NumOfVersionsDTO.class);

                        Platform.runLater(() -> {
                            openVersionSelectorPopup(versionsDTO.getVersions());
                        });
                    } else {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> AlertUtils.showErrorMessage(errorMessage));
                    }
                }
            }
        });
    }

    private void openVersionSelectorPopup(Set<Integer> versions){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/sheetRoomComponents/toolBar/versionSelector/version-selector.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(mainController.getPrimaryStage());

            VersionSelectorController versionController = loader.getController();
            versionController.setStage(popupStage);
            versionController.setVersions(versions);
            versionController.setMainController(mainController);

            popupStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorMessage("Failed to open permission request popup.");
        }
    }

    public void skinSelectorListener(ActionEvent actionEvent) {
        int selectedIndex = skinSelectorComboBox.getSelectionModel().getSelectedIndex();
        mainController.changeSkin(selectedIndex + 1);
    }

    public int getSelectedStyle() {
        return skinSelectorComboBox.getSelectionModel().getSelectedIndex() + 1;
    }

    public void animationSelectorListener(ActionEvent actionEvent) {
        String selectedOption = animationSelectorComboBox.getValue();
        boolean animationsEnabled = selectedOption.equals("ANIMATION");

        mainController.setAnimationsEnabled(animationsEnabled);
    }

    @FXML
    public void updateToLatestVersionListener(ActionEvent actionEvent) {
        HttpClientUtil.runAsync(VIEW_SHEET_URL, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to update to the latest version: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        SheetDTO sheetDTO = Constants.GSON_INSTANCE.fromJson(responseBody, SheetDTO.class);

                        Platform.runLater(() -> {
                            mainController.loadSheet(sheetDTO);

                            updateToLatestVersionButton.setDisable(true);
                            updateToLatestVersionButton.setText("Update to Latest Version");
                            updateToLatestVersionButton.getStyleClass().remove("new-version");
                        });
                    } else {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> AlertUtils.showErrorMessage("Error: " + errorMessage));
                    }
                }
            }
        });
    }

    public void startVersionRefresher() {
        versionRefresher = new VersionRefresher(isNewVersionAvailable -> Platform.runLater(() -> {
            if (isNewVersionAvailable) {
                updateToLatestVersionButton.setDisable(false);
                updateToLatestVersionButton.setText("New Version Available!");
                updateToLatestVersionButton.getStyleClass().add("new-version");
            } else {
                updateToLatestVersionButton.setDisable(true);
                updateToLatestVersionButton.setText("Update to Latest Version");
                updateToLatestVersionButton.getStyleClass().remove("new-version");
            }
        }));

        timer = new Timer(true);
        timer.schedule(versionRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    @Override
    public void close() {
        if (versionRefresher != null) {
            versionRefresher.cancel();
        }

        if (timer != null) {
            timer.cancel();
        }
    }
}
