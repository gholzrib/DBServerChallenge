package gholzrib.dbserverchallenge.core.requests;

import android.support.annotation.NonNull;

import gholzrib.dbserverchallenge.core.handlers.RequestsHandler;
import gholzrib.dbserverchallenge.core.listeners.RequestsListener;
import gholzrib.dbserverchallenge.core.utils.Constants;

/**
 * Created by Gunther Ribak on 01/01/2017.
 * For more information contact me
 * through guntherhr@gmail.com
 */

public class RequestRestaurants extends RequestsHandler {

    public RequestRestaurants(@NonNull RequestsListener requestsListener, int operation) {
        super(requestsListener, operation);
    }

    @Override
    public void doRequest(String[] additionalParams) {
        String url = Constants.BASE_URL
                .replace(Constants.REQUEST_PARAM_LAT, additionalParams[0])
                .replace(Constants.REQUEST_PARAM_LNG, additionalParams[1])
                .replace(Constants.REQUEST_PARAM_API_KEY, additionalParams[2]);
        doRequestByUsingGet(url);
    }
}
