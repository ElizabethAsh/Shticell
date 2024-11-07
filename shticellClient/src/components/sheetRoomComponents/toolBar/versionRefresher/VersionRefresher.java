package components.sheetRoomComponents.toolBar.versionRefresher;

import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import util.alert.AlertUtils;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

import static util.constants.Constants.IS_NEW_SHEET_VERSION_AVAILABLE_URL;

public class VersionRefresher extends TimerTask {
    private final Consumer<Boolean> updateConsumer;

    public VersionRefresher(Consumer<Boolean> updateConsumer) {
        this.updateConsumer = updateConsumer;
    }

    @Override
    public void run() {
        HttpClientUtil.runAsync(IS_NEW_SHEET_VERSION_AVAILABLE_URL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to check for new version: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful()) {
                        String jsonResponse = responseBody.string();
                        boolean isNewVersionAvailable = Boolean.parseBoolean(jsonResponse.trim());

                        Platform.runLater(() -> updateConsumer.accept(isNewVersionAvailable));
                    } else {
                        Platform.runLater(() -> {
                            AlertUtils.showErrorMessage("Failed to check for new version: " + response.message());
                        });
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        AlertUtils.showErrorMessage("Error while processing response: " + e.getMessage());
                    });
                }
            }
        });
    }
}
