package components.dashboardComponents.availableSheetsTable;

import impl.engine.SheetDetailsDTO;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;
import java.io.IOException;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static util.constants.Constants.AVAILABLE_SHEETS_UPDATE_URL;
import static util.constants.Constants.GSON_INSTANCE;

public class AvailableSheetsTableRefresher extends TimerTask {

    private final Consumer<List<SheetDetailsDTO>> updateConsumer;
    public String name;

    public AvailableSheetsTableRefresher(String currentUserName, Consumer<List<SheetDetailsDTO>> updateConsumer) {
        this.updateConsumer = updateConsumer;
        this.name = currentUserName;
    }

    @Override
    public void run() {
        HttpClientUtil.runAsync(AVAILABLE_SHEETS_UPDATE_URL, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    String responseString = responseBody.string();
                    if(!responseString.isEmpty()){
                        SheetDetailsDTO[] sheetDetailsArray = GSON_INSTANCE.fromJson(responseString, SheetDetailsDTO[].class);
                        updateConsumer.accept(List.of(sheetDetailsArray));
                    } else {}
                }
            }
        });
    }
}
