package net.aldar.cramello.view;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Environment;

import androidx.multidex.MultiDex;

import com.google.firebase.FirebaseApp;

import net.aldar.cramello.R;

import java.io.File;

public class App extends Application {

    public static final String API_BASE_URL = "https://api.cramello.com/";

    public static String KEY_REG_DATA = "registrationData";
    public static String KEY_ADDRESS_DATA = "AddressData";
    public static String KEY_PRODUCT_DATA = "ProductData";
    public static String KEY_QTY = "Quantity";
    public static String KEY_MIN_ORDER_VALUE = "MinOrderValue";
    public static String KEY_ORDER_NAME = "OrderName";
    public static String KEY_OPEN_ORDERS = "OpenOrdersScreen";
    public static String KEY_PAYMENT_URL = "PaymentUrl";
    public static String KEY_OPEN_NOTIFICATIONS = "OpenNotifications";
    public static String KEY_ORDER_DATA = "OrderData";
    public static String KEY_PAYMENT_METHOD = "PaymentMethod";

    public static final int CASH = 0;
    public static final int KNET = 1;
    public static final int VISA = 2;

    public static Typeface mRobotoRegular;
    public static Typeface mMontserratRegular;
    public static Typeface mMontserratLight;
    public static Typeface mMontserratBold;

    public static File mAppDirFile;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        FirebaseApp.initializeApp(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mAppDirFile = new File(Environment.getExternalStorageDirectory() + "/" +
                getResources().getString(R.string.app_name));

        if (!mAppDirFile.exists())
            mAppDirFile.mkdirs();

        mRobotoRegular = Typeface.createFromAsset(this.getApplicationContext().getAssets(),
                "Roboto-Regular.ttf");
        mMontserratRegular = Typeface.createFromAsset(this.getApplicationContext().getAssets(),
                "Montserrat-Regular.ttf");
        mMontserratLight = Typeface.createFromAsset(this.getApplicationContext().getAssets(),
                "Montserrat-Light.ttf");
        mMontserratBold = Typeface.createFromAsset(this.getApplicationContext().getAssets(),
                "Montserrat-Bold.ttf");
    }

    public static String getMenuImageResizeUrl(String imageUrl) {
        String mediaUrl = imageUrl.substring(imageUrl.indexOf("media"));
        return "https://api.cramello.com/api/v1/image-resizer?url=/" + mediaUrl + "&width=300&height=300";
    }
}
