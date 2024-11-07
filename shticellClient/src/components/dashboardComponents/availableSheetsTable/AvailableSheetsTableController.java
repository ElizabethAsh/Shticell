package components.dashboardComponents.availableSheetsTable;

import components.dashboardComponents.dashboardRoom.DashboardController;
import impl.engine.SheetDetailsDTO;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import java.io.Closeable;
import java.util.List;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import  javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Timer;
import java.util.TimerTask;
import static util.constants.Constants.REFRESH_RATE;

public class AvailableSheetsTableController implements Closeable {
    private Timer timer;
    private TimerTask sheetOverviewTableRefresher;
    private DashboardController mainController;
    private final IntegerProperty tableVersion;
    private SheetDetailsDTO selectedSheet;

    @FXML private TableColumn<SheetDetailsDTO, String> usernameOfUploaderColumn;
    @FXML private TableColumn<SheetDetailsDTO, String> sheetNameColumn;
    @FXML private TableColumn<SheetDetailsDTO, String> sheetSizeColumn;
    @FXML private TableColumn<SheetDetailsDTO, String> permissionTypeColumn;
    @FXML private TableView<SheetDetailsDTO> sheetsOverviewTable;

    public AvailableSheetsTableController() {
        tableVersion = new SimpleIntegerProperty(0);
    }

    public void initialize() {
        sheetNameColumn.setCellValueFactory(new PropertyValueFactory<>("sheetName"));
        usernameOfUploaderColumn.setCellValueFactory(new PropertyValueFactory<>("usernameNameOfUploader"));
        sheetSizeColumn.setCellValueFactory(new PropertyValueFactory<>("sheetSize"));
        permissionTypeColumn.setCellValueFactory(cellData -> cellData.getValue().userPermissionProperty());

        sheetsOverviewTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        sheetsOverviewTable.setRowFactory(tv -> {
            TableRow<SheetDetailsDTO> row = new TableRow<>() {
                @Override
                protected void updateItem(SheetDetailsDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setStyle("");
                    } else if (isSelected()) {
                        setStyle("-fx-background-color: #AED6F1;");
                    } else {
                        setStyle("");
                    }
                }
            };

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    selectedSheet = row.getItem();
                    sheetsOverviewTable.getSelectionModel().select(row.getIndex());
                    mainController.showPermissionsTable(selectedSheet.getSheetName());
                }
            });

            return row;
        });
    }

    public void setMainController(DashboardController mainController) { this.mainController = mainController;}

    public SheetDetailsDTO getSelectedSheet() {
        return selectedSheet;
    }

    public void startSheetsRefresher() {
        sheetOverviewTableRefresher = new AvailableSheetsTableRefresher(
                mainController.getCurrentUserName(),
                this::updateSheetsTableView);
        timer = new Timer();
        timer.schedule(sheetOverviewTableRefresher, REFRESH_RATE, REFRESH_RATE);
    }


    private void updateSheetsTableView(List<SheetDetailsDTO> sheets) {
        if(sheets.size() != tableVersion.getValue()) {
            SheetDetailsDTO previouslySelectedSheet = sheetsOverviewTable.getSelectionModel().getSelectedItem();

            Platform.runLater(() -> {
                tableVersion.setValue(sheets.size());
                ObservableList<SheetDetailsDTO> items = sheetsOverviewTable.getItems();
                items.clear();
                items.addAll(sheets);

                if (previouslySelectedSheet != null) {
                    for (SheetDetailsDTO sheet : items) {
                        if (sheet.getSheetName().equals(previouslySelectedSheet.getSheetName())) {
                            sheetsOverviewTable.getSelectionModel().select(sheet);
                            selectedSheet = sheet;
                            mainController.showPermissionsTable(selectedSheet.getSheetName());
                            break;
                        }
                    }
                }
            });

        } else {
            Platform.runLater(() -> {
                ObservableList<SheetDetailsDTO> items = sheetsOverviewTable.getItems();

                for (SheetDetailsDTO updatedPermission : sheets) {
                    for (SheetDetailsDTO existingSheet : items) {
                        if (existingSheet.getSheetName().equals(updatedPermission.getSheetName())) {
                            existingSheet.setUserPermission(updatedPermission.getUserPermission());
                            break;
                        }
                    }
                }
            });
        }
    }

    @Override
    public void close(){
        tableVersion.set(0);

        if (sheetOverviewTableRefresher != null) {
            sheetOverviewTableRefresher.cancel();
        }
        if (timer != null) {
            timer.cancel();
        }
    }
}
