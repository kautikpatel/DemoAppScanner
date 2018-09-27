package app.com.Utils;

public class AppConstant {

    public static final String CONNECTION_ERROR_MSG = "No connection found. Please connect & try again.";

    public static final String BASE_URL = "http://kapandroapps.com/";
    public static final String API_DO_LOGIN = "do_login.php?";
    public static final String API_SUBMIT_DATA = "submit_data.php?";
    public static final String API_GET_DATA = "get_data.php?";


    //---------- Request Param------//
    public static final String REQUEST_EMAIL = "email";
    public static final String REQUEST_PASSWORD = "password";
    public static final String REQUEST_CODE = "code";


    //---------- Response Param------//
    public static final String RESPONSE_STATUS = "status";
    public static final String RESPONSE_MESSAGE = "message";

    //------------- Validation ------------------//
    public static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

}