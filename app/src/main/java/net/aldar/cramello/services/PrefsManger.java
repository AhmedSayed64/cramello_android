package net.aldar.cramello.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.aldar.cramello.model.response.UserData;
import net.aldar.cramello.model.response.governorate.Area;
import net.aldar.cramello.model.response.product.Product;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PrefsManger {
    private static final String FILE_NAME = "Cramello";

    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private Gson mGson;

    private static final String KEY_LANGUAGE = "AppLanguage";
    private static final String KEY_LOGIN = "CramelloUserLogin";
    private static final String KEY_LOGIN_TOKEN = "LoginToken";
    private static final String KEY_USER_DATA = "UserData";
    private static final String KEY_AD_IMAGE = "AdImageName";
    private static final String KEY_CART = "Cart";
    private static final String KEY_SELECTED_AREA = "SelectedArea";
    private static final String KEY_NOTIFICATION_COUNT = "NotificationCount";
    private static final String KEY_FB_TOKEN = "FbToken";

    private static final String DEFAULT_LANGUAGE = Locale.getDefault().getLanguage();
    private static final boolean DEFAULT_LOGIN = false;
    private static final String DEFAULT_LOGIN_TOKEN = null;
    private static final String DEFAULT_USER_DATA = null;
    private static final int DEFAULT_NOTIFICATION_COUNT = 0;
    private static final String DEFAULT_AD_IMAGE = null;
    private static final String DEFAULT_CART = null;
    private static final String DEFAULT_SELECTED_AREA = null;
    private static final String DEFAULT_FB_TOKEN = "Firebase Token";


    public PrefsManger(Context context) {
        this.mContext = context;
        mSharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        mGson = new Gson();
    }

    public void setAppLanguage(String lang) {
        mSharedPreferences.edit().putString(KEY_LANGUAGE, lang).apply();
    }

    public String getAppLanguage() {
        return mSharedPreferences.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE);
    }

    public void setFbToken(String token) {
        mSharedPreferences.edit().putString(KEY_FB_TOKEN, token).apply();
    }

    public String getFbToken() {
        return mSharedPreferences.getString(KEY_FB_TOKEN, DEFAULT_FB_TOKEN);
    }

    public void setUnseenNotificationCount(int newCount) {
        mSharedPreferences.edit().putInt(KEY_NOTIFICATION_COUNT, newCount).apply();
    }

    public int getUnseenNotificationCount() {
        return mSharedPreferences.getInt(KEY_NOTIFICATION_COUNT, DEFAULT_NOTIFICATION_COUNT);
    }

    public void setUserLogin(boolean state) {
        mSharedPreferences.edit().putBoolean(KEY_LOGIN, state).apply();
    }

    public boolean isUserLoggedIn() {
        return mSharedPreferences.getBoolean(KEY_LOGIN, DEFAULT_LOGIN);
    }

    public void setLoginToken(String token) {
        mSharedPreferences.edit().putString(KEY_LOGIN_TOKEN, token).apply();
    }

    public String getLoginToken() {
        return mSharedPreferences.getString(KEY_LOGIN_TOKEN, DEFAULT_LOGIN_TOKEN);
    }

    public void saveUserData(UserData data) {
        String json = mGson.toJson(data);
        mSharedPreferences.edit().putString(KEY_USER_DATA, json).apply();
    }

    public UserData loadUserData() {
        String json = mSharedPreferences.getString(KEY_USER_DATA, DEFAULT_USER_DATA);
        Type type = new TypeToken<UserData>() {
        }.getType();
        return mGson.fromJson(json, type);
    }

    public void setAdImage(String imageName) {
        mSharedPreferences.edit().putString(KEY_AD_IMAGE, imageName).apply();
    }

    public String getAdImage() {
        return mSharedPreferences.getString(KEY_AD_IMAGE, DEFAULT_AD_IMAGE);
    }

    public void saveCart(List<Product> products) {
        String json = mGson.toJson(products);
        mSharedPreferences.edit().putString(KEY_CART, json).apply();
    }

    public List<Product> loadCart() {
        String json = mSharedPreferences.getString(KEY_CART, DEFAULT_CART);
        Type type = new TypeToken<List<Product>>() {
        }.getType();
        List<Product> products = mGson.fromJson(json, type);
        if (products == null)
            return new ArrayList<>();
        else
            return products;
    }

    public void setDeliveryArea(Area selectedArea) {
        String json = mGson.toJson(selectedArea);
        mSharedPreferences.edit().putString(KEY_SELECTED_AREA, json).apply();
    }

    public Area getDeliveryArea() {
        String json = mSharedPreferences.getString(KEY_SELECTED_AREA, DEFAULT_SELECTED_AREA);
        Type type = new TypeToken<Area>() {
        }.getType();
        return mGson.fromJson(json, type);
    }


    public void clearUserData() {
        mSharedPreferences.edit().putBoolean(KEY_LOGIN, DEFAULT_LOGIN).apply();
        mSharedPreferences.edit().putString(KEY_LOGIN_TOKEN, DEFAULT_LOGIN_TOKEN).apply();
        mSharedPreferences.edit().putString(KEY_USER_DATA, DEFAULT_USER_DATA).apply();
        mSharedPreferences.edit().putString(KEY_CART, DEFAULT_CART).apply();
        mSharedPreferences.edit().putString(KEY_SELECTED_AREA, DEFAULT_SELECTED_AREA).apply();
        mSharedPreferences.edit().putInt(KEY_NOTIFICATION_COUNT, DEFAULT_NOTIFICATION_COUNT).apply();
    }
}
