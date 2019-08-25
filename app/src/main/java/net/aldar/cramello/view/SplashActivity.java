package net.aldar.cramello.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import net.aldar.cramello.R;
import net.aldar.cramello.notification.MyFirebaseMessagingService;
import net.aldar.cramello.services.LocaleHelper;
import net.aldar.cramello.services.PrefsManger;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private int REQUEST_PERMISSION = 101;

    private String[] mPermissions = {Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.RECEIVE_SMS
    };

    private static int SPLASH_TIME_OUT = 1200;

    private ImageView mLogoTv;
    private Animation animation;

    private PrefsManger mPrefsManger;
    private Class activity;

    @Override
    protected void attachBaseContext(Context newBase) {
        mPrefsManger = new PrefsManger(newBase);
        super.attachBaseContext(LocaleHelper.wrap(newBase, mPrefsManger.getAppLanguage()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startService(new Intent(this, MyFirebaseMessagingService.class));

        mLogoTv = findViewById(R.id.logo);

        checkPermissions();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (arePermissionsGranted()) {
                fadeIn();
            } else {
                requestPermissions();
            }
        } else
            fadeIn();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean arePermissionsGranted() {
        for (String permission : mPermissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermissions() {
        List<String> remainingPermissions = new ArrayList<>();
        for (String permission : mPermissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                remainingPermissions.add(permission);
            }
        }
        requestPermissions(remainingPermissions.toArray(new String[remainingPermissions.size()]), REQUEST_PERMISSION);
    }

    private void fadeIn() {
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fadeOut();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mLogoTv.startAnimation(animation);
    }

    private void fadeOut() {
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeout);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                goToActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mLogoTv.startAnimation(animation);
    }

    private void goToActivity() {
        if (mPrefsManger.isUserLoggedIn())
            activity = MainActivity.class;
        else
            activity = LoginActivity.class;

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, activity);
            startActivity(intent);
            finish();
        }, SPLASH_TIME_OUT);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    if (shouldShowRequestPermissionRationale(permissions[i])) {
                        new AlertDialog.Builder(this)
                                .setMessage(getResources().getString(R.string.permission_denied_msg))
                                .setPositiveButton(getResources().getString(R.string.allow), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissions();
                                    }
                                })
                                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        finish();
                                    }
                                })
                                .create()
                                .show();
                    }
                    return;
                }
            }
            fadeIn();
        }
    }
}
