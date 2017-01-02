package gholzrib.dbserverchallenge.core.utils;

/**
 * Created by Gunther Ribak on 01/01/2017.
 * For more information contact me
 * through guntherhr@gmail.com
 */

public class Constants {

    public static final String FRG_TAG_RESTAURANTS = "restaurants";

    public static final String REQUEST_RESPONSE_RESULTS = "results";
    public static final String REQUEST_RESPONSE_ERROR_MESSAGE = "error_message";
    public static final String REQUEST_RESPONSE_STATUS = "status";

    public static final String REQUEST_STATUS_OK = "OK";

    public static final String REQUEST_PARAM_LAT = "LAT";
    public static final String REQUEST_PARAM_LNG = "LNG";
    public static final String REQUEST_PARAM_API_KEY = "KEY";

    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
            + REQUEST_PARAM_LAT
            + ","
            + REQUEST_PARAM_LNG
            + "&rankby=distance&type=restaurant&key="
            + REQUEST_PARAM_API_KEY;

    public static final int VISUALIZATION_MODE_MAP = 0;
    public static final int VISUALIZATION_MODE_LIST = 1;

}
