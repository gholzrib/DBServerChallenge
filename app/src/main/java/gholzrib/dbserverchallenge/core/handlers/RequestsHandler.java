package gholzrib.dbserverchallenge.core.handlers;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.lang.ref.WeakReference;

import gholzrib.dbserverchallenge.core.listeners.RequestsListener;
import gholzrib.dbserverchallenge.core.singletons.OkHttpClientSingleton;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public abstract class RequestsHandler {

    private WeakReference<RequestsListener> mApiListener = null;
    private WeakReference<Call> mCurrentCall;
    private int mCurrentOperation;

    public RequestsHandler(@NonNull RequestsListener requestsListener, int operation) {
        mApiListener = new WeakReference<>(requestsListener);
        mCurrentOperation = operation;
    }

    public abstract void doRequest(String[] additionalParams);

    protected void doRequestByUsingGet(final String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        mCurrentCall = new WeakReference<>(OkHttpClientSingleton.getInstance().newCall(request));
        ApiTask apiTask = new ApiTask();
        apiTask.execute(mCurrentCall.get());
    }

    public void cancelRequest() {
        if (mCurrentCall.get() != null && !mCurrentCall.get().isExecuted()) {
            mCurrentCall.get().cancel();
        }
    }

    private class ApiTask extends AsyncTask<Call, Integer, RequestResponse> {

        @Override
        protected void onPreExecute() {
            mApiListener.get().onPreExecute();
        }

        @Override
        protected RequestResponse doInBackground(Call... params) {
            Call mCall = params[0];
            if (mCall != null) {
                try {
                    Response response =  mCall.execute();
                    RequestResponse requestResponse = new RequestResponse();
                    requestResponse.setSuccessful(response.isSuccessful());
                    requestResponse.setBody(response.body().string());
                    return  requestResponse;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(RequestResponse requestResponse) {
            if (requestResponse != null) {
                mApiListener.get().onRequestEnds(mCurrentOperation,
                        requestResponse.isSuccessful(),
                        requestResponse.getBody());
            }
        }
    }

    /**
     * Model created to use the request response into the main thread
     */
    private class RequestResponse {

        boolean isSuccessful;
        String body;

        public boolean isSuccessful() {
            return isSuccessful;
        }

        public void setSuccessful(boolean successful) {
            isSuccessful = successful;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }
}