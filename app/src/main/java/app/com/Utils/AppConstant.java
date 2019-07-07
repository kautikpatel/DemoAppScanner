package app.com.Utils;

import java.util.regex.Pattern;

public class AppConstant {

    public static final String CONNECTION_ERROR_MSG = "No connection found. Please connect & try again.";
    //public static final String BASE_URL1 = "https://www.radiofile.host";
    public static final String BASE_URL = /*BASE_URL1+ */"/ws/";
    public static final String API_DO_LOGIN = "login.php?";
    public static final String API_SEARCH_FILE = "search_file.php?";
    public static final String API_GET_DEPT_LIST = "get_deptlist.php?";
    public static final String API_GET_UPDATE_RECORD = "update_record.php?";
    public static final String API_GET_REPORT = "get_report.php?";

    public static final String API_SUBMIT_DATA = "submit_data.php?";
    public static final String API_GET_DATA = "get_data.php?";


    //---------- Request Param------//
    public static final String REQUEST_EMAIL = "email";
    public static final String REQUEST_PASSWORD = "password";
    public static final String REQUEST_BARCODE = "barcode";
    public static final String REQUEST_CODE = "barcode";
    public static final String REQUEST_USER_ID = "user_id";
    public static final String REQUEST_START_DATE = "start_date";
    public static final String REQUEST_END_DATE = "end_date";


    //---------- Response Param------//
    public static final String RESPONSE_STATUS = "status";
    public static final String RESPONSE_MESSAGE = "message";

    //------------- Validation ------------------//
    public static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public static final String[] statusDisplayList = new String[]{"Select Status", "CheckIn", "In Process", "Checkout"};
    public static final String[] statusList = new String[]{"", "checkin", "inprocess", "checkout"};

    public static final Pattern PARTIAl_IP_ADDRESS =
            Pattern.compile("^((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])\\.){0,3}"+
                    "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])){0,1}$");



}