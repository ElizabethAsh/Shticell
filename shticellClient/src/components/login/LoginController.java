package components.login;

import components.main.SheetAppMainController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;
import util.constants.Constants;

import java.io.IOException;

public class LoginController {
    @FXML public TextField userNameTextField;
    @FXML public Label errorMessageLabel;

    private SheetAppMainController sheetAppMainController;

    private final StringProperty errorMessageProperty = new SimpleStringProperty();

    @FXML
    public void initialize() {
        errorMessageLabel.textProperty().bind(errorMessageProperty);
    }

    @FXML
    private void loginButtonClicked(ActionEvent event) {

        String userName = userNameTextField.getText();
        if (userName.isEmpty()) {
            errorMessageProperty.set("User name is empty. You can't login with empty user name");
            return;
        }


        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .build()
                .toString();


        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorMessageProperty.set("Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.code() != 200) {
                        String responseText = responseBody != null ? responseBody.string() : "Unknown error";
                        Platform.runLater(() ->
                                errorMessageProperty.set("Something went wrong: " + responseText)
                        );
                    } else {
                        Platform.runLater(() -> {
                            sheetAppMainController.updateUserName(userName);
                            sheetAppMainController.switchToDashboardRoom();
                        });
                    }
                }
            }
        });
    }

    @FXML
    private void userNameKeyTyped(KeyEvent event) {
        errorMessageProperty.set("");
    }

    public void setSheetAppMainController(SheetAppMainController sheetAppMainController) {
        this.sheetAppMainController = sheetAppMainController;
    }
}
