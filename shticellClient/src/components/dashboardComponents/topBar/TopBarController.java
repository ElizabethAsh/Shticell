package components.dashboardComponents.topBar;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import okhttp3.*;
import util.alert.AlertUtils;
import util.http.HttpClientUtil;
import java.io.File;
import java.io.IOException;
import static util.constants.Constants.FILE_UPLOAD_PAGE;

public class TopBarController {

    @FXML private Label userGreetingLabel;

    public void LoadSheetFileButtonListener(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML Files", "*.xml")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            sendFileToServer(selectedFile);
        }
    }

    private void sendFileToServer(File file) {
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file1", file.getName(), RequestBody.create(file, MediaType.parse("application/xml")))
                .build();


        HttpClientUtil.runAsyncWithBody(FILE_UPLOAD_PAGE, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> {
                    AlertUtils.showErrorMessage("Upload failed: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful()) {
                        Platform.runLater(() -> {
                            AlertUtils.showSuccessMessage("File uploaded successfully");
                        });
                    } else {
                        String responseBodyString = responseBody != null ? responseBody.string() : "Unknown error";
                        Platform.runLater(() -> {
                            AlertUtils.showErrorMessage("Failed to upload file: " + responseBodyString);
                        });
                    }
                }
            }
        });
    }

    public void setUserNameProperty(StringProperty userNameProperty) {
        userGreetingLabel.textProperty().bind(Bindings.concat("Hello ", userNameProperty));
    }

}
