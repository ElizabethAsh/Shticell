package constants;

public class Constants {
    public static final String USERNAME = "username";
    public static final String CURRENT_SHEET_NAME = "current_sheet_name";
    public static final String PERMISSION_TYPE = "permission_type";
    public static final String CELL_ID = "cellId";
    public static final String VERSION_NUMBER = "version_number";
    public static final String FROM_COORDINATE = "from_coordinate";
    public static final String TO_COORDINATE = "to_coordinate";
    public static final String COLUMN = "column";
    public static final String RANGE_NAME = "rangeName";

    public static final String PATH_GET_USER_AVAILABLE_PERMISSIONS = "/getUserAvailablePermissions";
    public static final String PATH_GET_USER_PENDING_REQUESTS = "/getUserPendingPermissionRequests";
    public static final String PATH_GET_SHEET_PERMISSIONS_TABLE = "/getSheetPermissionsTable";
    public static final String PATH_SUBMIT_PERMISSION_REQUEST = "/submitSheetPermissionRequest";
    public static final String PATH_RESPOND_TO_PERMISSION_REQUEST = "/respondToSheetPermissionRequest";

    public static final String PATH_BACKGROUND = "/background";
    public static final String PATH_TEXT = "/text";

    public static final String PATH_GET_SHEET_VERSIONS = "/getSheetVersions";
    public static final String PATH_GET_SHEET_BY_VERSION = "/getSheetByVersion";
    public static final String PATH_IS_NEW_VERSION_AVAILABLE = "/isNewVersionAvailable";

    public static final String ERROR_NO_SHEET_SELECTED = "Error: No sheet is selected. Please select a sheet to view first.";
    public static final String ERROR_UNKNOWN_ACTION = "Unknown action.";
    public static final String ERROR_MISSING_PARAMETERS = "Missing parameters.";
    public static final String ERROR_NO_SHEET_OR_USER_SELECTED = "Error: No sheet or user is selected. Please login and select a sheet.";
    public static final String ERROR_MISSING_CELL_ID = "Error: Missing cellId parameter.";
    public static final String ERROR_INVALID_STYLE_TYPE = "Error: Invalid style type. Must be 'background' or 'text'.";
    public static final String ERROR_MISSING_VERSION_NUMBER = "Error: Missing versionNumber parameter.";
    public static final String ERROR_INVALID_VERSION_NUMBER = "Error: Invalid versionNumber parameter.";
    public static final String ERROR_NO_RANGE_NAME_PROVIDED = "Error: No range name provided.";
    public static final String SUCCESS_RANGE_DELETED = "Range deleted successfully.";

    public static final int INT_PARAMETER_ERROR = Integer.MIN_VALUE;
}
