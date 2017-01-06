package gholzrib.dbserverchallenge.core.singletons;

import okhttp3.OkHttpClient;

/**
 * Created by Gunther Ribak on 01/01/2017.
 * For more information contact me
 * through guntherhr@gmail.com
 */

public class OkHttpClientSingleton {

    private static OkHttpClient mClient;

    public static OkHttpClient getInstance() {
        if (mClient == null) {
            mClient = new OkHttpClient();
        }
        return mClient;
    }

}
