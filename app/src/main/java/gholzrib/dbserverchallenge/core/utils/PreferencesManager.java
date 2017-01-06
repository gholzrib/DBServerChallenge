package gholzrib.dbserverchallenge.core.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gholzrib.dbserverchallenge.core.models.Restaurant;
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
    private static final String WEEKLY_WINNERS = "weekly_winners";
    private static final String FIRST_TIME = "first_time";

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

    private static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences preferences = getDefaultPreferencesManager(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences preferences = getDefaultPreferencesManager(context);
        return preferences.getBoolean(key, defaultValue);
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

    @NonNull
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

        return new User();
    }

    public static Boolean containsUser(Context context){
        return containsValue(context, USER);
    }

    public static void setWeeklyWinners(Context context, ArrayList<Restaurant> restaurants) {
        JSONArray jsonArray = new JSONArray();
        Gson gson = new Gson();
        for (int i = 0; i < restaurants.size(); i++) {
            Restaurant restaurant = restaurants.get(i);
            try {
                jsonArray.put(new JSONObject(gson.toJson(restaurant)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setString(context, WEEKLY_WINNERS, jsonArray.toString());
    }

    @NonNull
    public static ArrayList<Restaurant> getWeeklyWinners(Context context) {
        ArrayList<Restaurant> restaurants = new ArrayList<>();
        Gson gson = new Gson();
        String list = getString(context, WEEKLY_WINNERS, null);
        if (list != null) {
            try {
                JSONArray jsonArray = new JSONArray(list);
                for (int i = 0; i < jsonArray.length(); i++) {
                    restaurants.add(gson.fromJson(jsonArray.getJSONObject(i).toString(), Restaurant.class));
                }
                return restaurants;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    public static void setFirstTime(Context context, boolean firstTime) {
        setBoolean(context, FIRST_TIME, firstTime);
    }

    public static boolean getFirstTime(Context context) {
        return getBoolean(context, FIRST_TIME, true);
    }

}
