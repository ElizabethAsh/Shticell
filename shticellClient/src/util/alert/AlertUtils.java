package util.alert;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

public class AlertUtils {

    public static Stage showProgressWindow(Window owner, Task<?> task) {
        Stage progressStage = new Stage();

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(400);
        progressBar.progressProperty().bind(task.progressProperty());

        VBox progressBox = new VBox(10, new Label("Loading file..."), progressBar);
        progressBox.setAlignment(Pos.CENTER);

        Scene progressScene = new Scene(progressBox, 500, 150);
        progressStage.setScene(progressScene);
        progressStage.setTitle("File Loading");
        progressStage.initOwner(owner);

        progressStage.show();
        return progressStage;
    }

    public static void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);

        String contentText = message;
        if (message.length() > 80) {
            contentText = message.substring(0, 80) + "...\n" + message.substring(80);
        }

        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setMinWidth(400);
        dialogPane.setMinHeight(200);

        alert.showAndWait();
    }

    public static void showErrorMessage(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);

        String contentText = message;
        if (message.length() > 80) {
            contentText = message.substring(0, 80) + "...\n" + message.substring(80);
        }

        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setMinWidth(400);
        dialogPane.setMinHeight(200);

        alert.showAndWait();
    }

    public static void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
