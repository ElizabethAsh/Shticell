package components.dashboardComponents.PermissionsTable;

import impl.engine.UserPermissionDTO;
import javafx.application.Platform;
import okhttp3.*;
import util.alert.AlertUtils;
import util.http.HttpClientUtil;
import java.io.IOException;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static util.constants.Constants.*;

public class PermissionsTableRefresher extends TimerTask {
    private final String sheetName;
    private final Consumer<List<UserPermissionDTO>> updateConsumer;

    public PermissionsTableRefresher(String sheetName, Consumer<List<UserPermissionDTO>> updateConsumer) {
        this.sheetName = sheetName;
        this.updateConsumer = updateConsumer;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl.parse(GET_SHEET_PERMISSIONS_TABLE_URL).newBuilder()
                .addQueryParameter("current_sheet_name", sheetName)
                .build().toString();


        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> {
                    AlertUtils.showErrorMessage("Failed to load permissions table: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful()) {
                        String jsonResponse = responseBody.string();
                        List<UserPermissionDTO> permissions = GSON_INSTANCE.fromJson(jsonResponse,
                                new com.google.gson.reflect.TypeToken<List<UserPermissionDTO>>() {}.getType());

                        updateConsumer.accept(permissions);
                    } else {
                        Platform.runLater(() -> {
                            AlertUtils.showErrorMessage("Failed to load permissions table: " + response.message());
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
