package gholzrib.dbserverchallenge.core.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

import gholzrib.dbserverchallenge.R;

public class CheckConnection {

    public static boolean hasInternetConnection(Context context, boolean shouldShowToast) {

        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null
                    && cm.getActiveNetworkInfo().isAvailable()
                    && cm.getActiveNetworkInfo().isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (shouldShowToast) {
            Toast.makeText(context, context.getResources().getString(R.string.warning_no_internet_connection), Toast.LENGTH_SHORT).show();
        }

        return false;
    }

}
