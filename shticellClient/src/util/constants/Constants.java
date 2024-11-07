package util.constants;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Constants {
    public static final String COMMANDS_STYLE_1 = "/components/sheetRoomComponents/commands/style/commands.css";
    public static final String COMMANDS_STYLE_2 = "/components/sheetRoomComponents/commands/style/second-commands.css";
    public static final String COMMANDS_STYLE_3 = "/components/sheetRoomComponents/commands/style/third-commands.css";

    public static final String RANGES_STYLE_1 = "/components/sheetRoomComponents/ranges/style/ranges.css";
    public static final String RANGES_STYLE_2 = "/components/sheetRoomComponents/ranges/style/second-ranges.css";
    public static final String RANGES_STYLE_3 = "/components/sheetRoomComponents/ranges/style/third-ranges.css";

    public static final String SHEET_STYLE_1 = "/components/sheetRoomComponents/sheet/style/single-cell.css";
    public static final String SHEET_STYLE_2 = "/components/sheetRoomComponents/sheet/style/second-single-cell.css";
    public static final String SHEET_STYLE_3 = "/components/sheetRoomComponents/sheet/style/third-single-cell.css";

    public static final String TOOL_BAR_STYLE_1 = "/components/sheetRoomComponents/toolBar/style/action-bar.css";
    public static final String TOOL_BAR_STYLE_2 = "/components/sheetRoomComponents/toolBar/style/second-action-bar.css";
    public static final String TOOL_BAR_STYLE_3 = "/components/sheetRoomComponents/toolBar/style/third-action-bar.css";


    public final static String ANONYMOUS = "<Anonymous>";
    public final static int REFRESH_RATE = 2000;


    public final static String MAIN_PAGE_FXML_RESOURCE_LOCATION = "/components/main/sheet-app-main.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/components/login/login.fxml";
    public final static String SHEET_ROOM_FXML_RESOURCE_LOCATION = "/components/sheetRoomComponents/sheetRoom/sheet-room-main.fxml";
    public final static String DASH_BOARD_PAGE_FXML_RESOURCE_LOCATION = "/components/dashboardComponents/dashboardRoom/dash-board.fxml";


    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/shticellWebApp_war";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/loginShortResponse";
    public final static String FILE_UPLOAD_PAGE = FULL_SERVER_PATH + "/upload-file";
    public final static String AVAILABLE_SHEETS_UPDATE_URL = FULL_SERVER_PATH + "/availableSheets/update";;
    public final static String VIEW_SHEET_URL = FULL_SERVER_PATH + "/view/sheet";;
    public final static String PERMISSION_BASE_URL = FULL_SERVER_PATH + "/permissions";
    public final static String GET_USER_AVAILABLE_PERMISSIONS_URL = PERMISSION_BASE_URL + "/getUserAvailablePermissions";
    public final static String GET_USER_PENDING_PERMISSIONS_URL = PERMISSION_BASE_URL + "/getUserPendingPermissionRequests";
    public final static String GET_SHEET_PERMISSIONS_TABLE_URL = PERMISSION_BASE_URL + "/getSheetPermissionsTable";
    public final static String SUBMIT_SHEET_PERMISSION_REQUEST_URL = PERMISSION_BASE_URL + "/submitSheetPermissionRequest";
    public final static String RESPOND_TO_SHEET_PERMISSION_REQUEST_URL = PERMISSION_BASE_URL + "/respondToSheetPermissionRequest";

    public final static String GET_AVAILABLE_RANGES_URL = FULL_SERVER_PATH + "/getRanges";;
    public final static String RANGE_URL = FULL_SERVER_PATH + "/range";
    public final static String UPDATE_CELL_STYLE_URL = FULL_SERVER_PATH + "/updateCellStyle";;
    public static final String UPDATE_CELL_STYLE_TEXT_URL = UPDATE_CELL_STYLE_URL + "/text";
    public static final String UPDATE_CELL_STYLE_BACKGROUND_URL = UPDATE_CELL_STYLE_URL + "/background";

    public static final String CELL_ACTION_URL = FULL_SERVER_PATH + "/cell";
    public final static String DYNAMIC_ANALYSIS_URL = FULL_SERVER_PATH + "/dynamic-analysis";;
    public final static String SORT_URL = FULL_SERVER_PATH + "/sort";;
    public final static String FILTER_ACTIONS_URL = FULL_SERVER_PATH + "/filter";;
    public final static String VERSION_ACTIONS_URL = FULL_SERVER_PATH + "/versionActions";;
    public final static String GET_SHEET_VERSIONS_URL = VERSION_ACTIONS_URL + "/getSheetVersions";;
    public final static String GET_SHEET_BY_VERSION_URL = VERSION_ACTIONS_URL + "/getSheetByVersion";;
    public static final String IS_NEW_SHEET_VERSION_AVAILABLE_URL = VERSION_ACTIONS_URL + "/isNewVersionAvailable";

    public static final String HTTP_POST = "POST";
    public static final String HTTP_DELETE = "DELETE";

    public static final String CURRENT_SHEET_NAME = "current_sheet_name";
    public static final String PERMISSION_TYPE = "permission_type";

    public final static Gson GSON_INSTANCE = new GsonBuilder()
            .serializeSpecialFloatingPointValues()
            .create();}
