package gholzrib.dbserverchallenge.core.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import gholzrib.dbserverchallenge.core.models.User;

/**
 * Created by Gunther Ribak on 01/01/2017.
 * For more information contact me
 * through guntherhr@gmail.com
 */
public class PreferencesManager {

    private static final String EMAIL_KEY = "email_key";
    private static final String PASSWORD_KEY = "password_key";
    private static final String USER = "user";

    private static SharedPreferences getDefaultPreferencesManager(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static void setString(Context context, String key, String value) {
        SharedPreferences preferences = getDefaultPreferencesManager(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static String getString(Context context, String key, String defaultValue) {
        SharedPreferences preferences = getDefaultPreferencesManager(context);
        return preferences.getString(key, defaultValue);
    }

    private static Boolean containsValue(Context context, String key){
        SharedPreferences preferences = getDefaultPreferencesManager(context);
        return preferences.contains(key);
    }

    public static void clearPreferences(Context context){
        SharedPreferences preferences = getDefaultPreferencesManager(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void setEmail(Context context, String email){
        setString(context, EMAIL_KEY, email);
    }

    public static String getEmail(Context context){
        return getString(context, EMAIL_KEY, "");
    }

    public static Boolean containsEmail(Context context){
        return containsValue(context, EMAIL_KEY);
    }

    public static void setPassword(Context context, String password){
        setString(context, PASSWORD_KEY, password);
    }

    public static String getPassword(Context context){
        return getString(context, PASSWORD_KEY, "");
    }

    public static Boolean containsPassword(Context context){
        return containsValue(context, PASSWORD_KEY);
    }

    public static void setUser(Context context, User user){
        String userJson = null;
        if (user != null) {
            Gson gson = new Gson();
            userJson = gson.toJson(user);
        }
        setString(context, USER, userJson);
    }

    public static User getUser(Context context){
        Gson gson = new Gson();
        String json = getString(context, USER, null);
        if (json != null) {
            try {
                return gson.fromJson(json, User.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static Boolean containsUser(Context context){
        return containsValue(context, USER);
    }
}
