package net.aldar.cramello.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import net.aldar.cramello.R;
import net.aldar.cramello.apiHandler.BaseApi;
import net.aldar.cramello.apiHandler.BaseApiHandler;
import net.aldar.cramello.model.FirebaseData;
import net.aldar.cramello.model.response.product.Product;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;


public class Utils {

    public static final int PW_MIN_CHAR_NUMBER = 8;
    public static final int PW_MAX_CHAR_NUMBER = 16;

    public static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String VIEW_FORMAT = "MMMM dd, yyyy";

    public static Boolean isConnectionOn(Context context) {
        ConnectivityManager CManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo NInfo = CManager.getActiveNetworkInfo();
        if (NInfo != null && NInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static int getDrawerGravity(PrefsManger mPrefsManger) {
        Integer mDrawerGravity;

        if (mPrefsManger.getAppLanguage().contains("ar")) {
            mDrawerGravity = Gravity.RIGHT;
        } else {
            mDrawerGravity = Gravity.LEFT;
        }

        return mDrawerGravity;
    }

    public static boolean isInputEmpty(EditText input) {
        return TextUtils.isEmpty(input.getText().toString().trim());
    }

    public static boolean isValidPw(String pw) {
        return (pw.length() >= PW_MIN_CHAR_NUMBER && pw.length() <= PW_MAX_CHAR_NUMBER);
    }

    public static boolean isValidPhone(String phone, Integer phoneNumberChars) {
        return phone.length() == phoneNumberChars;
    }

    public static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static void makeAToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void setupHideKeyboard(final Activity activity, final View view) {

        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(activity);
                    view.requestFocus();
                    return false;
                }
            });
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupHideKeyboard(activity, innerView);
            }
        }
    }

    private static void hideKeyboard(Activity activity) {
        try {
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideKeyboardFrom(Context context, View view) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String dateChangeFormat(String incomingDate, String fromFormat, String toFormat) {
        String viewDate = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fromFormat, Locale.ENGLISH);
            Date date = simpleDateFormat.parse(incomingDate);
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(toFormat, Locale.ENGLISH);
            viewDate = simpleDateFormat2.format(date);
        } catch (Exception e) {
            Log.e("date", e.getMessage() + "");
        }
        return viewDate;
    }

    private static Bitmap rotateImageView(Bitmap bitmap, int rotationAngleDegree) {

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int newW = w, newH = h;
        if (rotationAngleDegree == 90 || rotationAngleDegree == 270) {
            newW = h;
            newH = w;
        }
        Bitmap rotatedBitmap = Bitmap.createBitmap(newW, newH, bitmap.getConfig());
        Canvas canvas = new Canvas(rotatedBitmap);

        Rect rect = new Rect(0, 0, newW, newH);
        Matrix matrix = new Matrix();
        float px = rect.exactCenterX();
        float py = rect.exactCenterY();
        matrix.postTranslate(-bitmap.getWidth() / 2, -bitmap.getHeight() / 2);
        matrix.postRotate(rotationAngleDegree);
        matrix.postTranslate(px, py);
        canvas.drawBitmap(bitmap, matrix, new Paint(Paint.ANTI_ALIAS_FLAG |
                Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG));
        matrix.reset();

        return rotatedBitmap;
    }

    private static Bitmap flip(Bitmap src) {
        // create new matrix for transformation
        Matrix matrix = new Matrix();
        // if vertical
//            matrix.preScale(1.0f, -1.0f);
        // if horizonal
        matrix.preScale(-1.0f, 1.0f);

        // return transformed image
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    public static void submitRotation(ImageView imageView, PrefsManger prefsManger) {
        if (prefsManger.getAppLanguage().equals("ar")) {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            imageView.setImageBitmap(rotateImageView(bitmap, 180));
        }
    }

    public static void submitFlip(ImageView imageView, PrefsManger prefsManger) {
        if (prefsManger.getAppLanguage().equals("ar")) {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            imageView.setImageBitmap(flip(bitmap));
        }
    }

    public static void loadImage(String link, File target, ImageView imageView, int placeHolder) {
        if (link != null)
            Picasso.get()
                    .load(link)
                    .placeholder(placeHolder)
                    .error(placeHolder)
                    .into(imageView);
        else
            Picasso.get()
                    .load(target)
                    .placeholder(placeHolder)
                    .error(placeHolder)
                    .into(imageView);
    }

    public static void loadImageWithProgressBar(String link, final ImageView imageView,
                                                final ProgressBar bar, final int placeHolder) {
        if (link != null) {
            bar.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            Picasso.get()
                    .load(link)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            bar.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            bar.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);
                            imageView.setImageResource(placeHolder);
                        }
                    });
        }
    }

    public static String getStaticMap(String latitude, String longitude, String key) {
        String staticMap = "https://maps.googleapis.com/maps/api/staticmap?markers=color:red%7C"
                + latitude
                + ","
                + longitude
                + "&zoom=17&size=480x320&sensor=true&key=" + key;
        return staticMap;
    }

    public static void openChildFragment(Fragment parentFragment, Fragment childFragment,
                                         Bundle bundle, int container, String tag) {
        childFragment.setArguments(bundle);
        FragmentTransaction transaction = parentFragment.getChildFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(container, childFragment, tag).commit();
    }

    public static String decodeBase64(String coded) {
        byte[] valueDecoded = new byte[0];
        try {
            valueDecoded = Base64.decode(coded.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
        }
        return new String(valueDecoded);
    }

    public static String makeSureThreeNAfterDot(double number) {
        return String.format(Locale.ENGLISH, "%.3f", number);
    }

    public static void showAlertCustomMsg(Context context, String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        alertDialog.setMessage(msg);
        alertDialog.show();
    }

    public static void makeACall(Activity activity, String phone) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:".concat(phone)));
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(callIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openMapNavigation(Activity activity, double lat, double lng) {
        try {
            Uri gmmIntentUri = Uri.parse("google.navigation:q="
                    + lat + "," + lng);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            activity.startActivity(mapIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void shareProduct(Activity activity, String message) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);

        activity.startActivity(Intent.createChooser(share, "Share"));
    }

    public static void addQuantityToCart(Context context, PrefsManger prefsManger, String name, Product item, int qty) {
        List<Product> products = prefsManger.loadCart();

        if (!products.isEmpty()) {
            boolean found = false;
            for (int p = 0; p < products.size(); p++) {
                Product product = products.get(p);
                int productId = product.getId();
                int itemId = item.getId();
                if (itemId == productId) {
                    found = true;
                    product.setQuantity(product.getQuantity() + qty);
                    if (item.getSpecialHint() != null && !item.getSpecialHint().isEmpty())
                        product.setSpecialHint(item.getSpecialHint());
                    break;
                }
            }
            if (!found) {
                item.setQuantity(qty);
                products.add(item);
            }
        } else {
            item.setQuantity(qty);
            products.add(item);
        }

        prefsManger.saveCart(products);
        Utils.makeAToast(context, name.concat(" ").concat(context.getResources().getString(R.string.itemAddedSuccess)));
    }

    public static void sendTokenToServer(Context context, final String token) {
        final PrefsManger prefsManger = new PrefsManger(context);
        if (!prefsManger.getFbToken().equals(token)) {
            BaseApi baseApi = BaseApiHandler.setupBaseApi().create(BaseApi.class);
            FirebaseData firebaseData = new FirebaseData(Build.BRAND.concat(" ").concat(Build.MODEL),
                    token, android.os.Build.ID, "android");

            Call<FirebaseData> call = baseApi.registerNewToken(firebaseData);
            call.enqueue(new retrofit2.Callback<FirebaseData>() {
                @Override
                public void onResponse(@NonNull Call<FirebaseData> call, @NonNull Response<FirebaseData> response) {
                    if (response.isSuccessful()) {
                        try {
                            prefsManger.setFbToken(token);
                        } catch (Exception e) {
                            Log.e("NewDeviceToken Ex / ", e.getMessage() + "");
                        }
                    } else {
                        Log.e("NewDeviceToken Code / ", "Code not Successful");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<FirebaseData> call, Throwable t) {
                    Log.e("NewDeviceToken Fail / ", t.getMessage() + "");
                }
            });
        }
    }
}
