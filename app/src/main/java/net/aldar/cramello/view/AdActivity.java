package net.aldar.cramello.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import net.aldar.cramello.R;
import net.aldar.cramello.apiHandler.BaseApi;
import net.aldar.cramello.apiHandler.BaseApiHandler;
import net.aldar.cramello.model.response.Ad;
import net.aldar.cramello.services.DownloadFile;
import net.aldar.cramello.services.LocaleHelper;
import net.aldar.cramello.services.PrefsManger;
import net.aldar.cramello.services.Utils;

import java.io.File;

import ch.halcyon.squareprogressbar.SquareProgressBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.aldar.cramello.view.App.mAppDirFile;

public class AdActivity extends AppCompatActivity {

    private ImageView mAdsIv;
    private SquareProgressBar mSkipBtn;

    private PrefsManger mPrefsManger;
    private BaseApi mServiceApi;

    private CountDownTimer mCountDownTimer;

    public String mAuthToken;

    @Override
    protected void attachBaseContext(Context newBase) {
        mPrefsManger = new PrefsManger(newBase);
        super.attachBaseContext(LocaleHelper.wrap(newBase, mPrefsManger.getAppLanguage()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);

        mServiceApi = BaseApiHandler.setupBaseApi().create(BaseApi.class);
        mAuthToken = "Token ".concat(mPrefsManger.getLoginToken());

        mAdsIv = findViewById(R.id.ad_activity_adIv);

        mSkipBtn = findViewById(R.id.ad_activity_skipBtn);
        mSkipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHomeScreen();
            }
        });

        int drawable;

        if (mPrefsManger.getAppLanguage().contains("ar"))
            drawable = R.drawable.skip_ar;
        else
            drawable = R.drawable.skip_en;

        mSkipBtn.setImage(drawable);
        mSkipBtn.setProgress(0.0);
        mSkipBtn.setWidth(3);

        mCountDownTimer = new CountDownTimer(10000, 100) {

            public void onTick(long millisUntilFinished) {
                double progress = 10000 - millisUntilFinished;
                double progressD = progress / 100;
                mSkipBtn.setProgress(progressD);
            }

            public void onFinish() {
                mSkipBtn.setProgress(100);
                goToHomeScreen();
            }
        }.start();

        getFBToken();
        setupSavedAdImage();
        getAdImage();
    }

    private void getFBToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(AdActivity.this,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String newToken = instanceIdResult.getToken();
                        Log.e("newToken", newToken);
                        Utils.sendTokenToServer(AdActivity.this, newToken);
                    }
                });
    }

    private void setupSavedAdImage() {
        if (mPrefsManger.getAdImage() != null) {
            try {
                File img = new File(mAppDirFile + "/" + mPrefsManger.getAdImage());
                if (img.exists())
                    Utils.loadImage(null, img, mAdsIv, R.drawable.menu_item_placeholder);
                else
                    mPrefsManger.setAdImage(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getAdImage() {
        Call<Ad> call = mServiceApi.getAdImage(mAuthToken);
        call.enqueue(new Callback<Ad>() {
            @Override
            public void onResponse(Call<Ad> call, Response<Ad> response) {
                if (response.isSuccessful()) {
                    try {
                        Ad ad = response.body();
                        if (ad.getImage() != null)
                            checkFile(ad.getImage());
                    } catch (Exception e) {
                        Log.e("Ad Ex / ", e.getMessage() + "");
                    }
                } else {
                    Log.e("Ad Code / ", "Code not Successful");
                }
            }

            @Override
            public void onFailure(Call<Ad> call, Throwable t) {
                Log.e("Ad Fail / ", t.getMessage() + "");
            }
        });
    }

    private void goToHomeScreen() {
        mCountDownTimer.cancel();
        startActivity(new Intent(AdActivity.this, MainActivity.class));
        finish();
    }

    private void checkFile(String fileLink) {

        String fileName = fileLink.substring(fileLink.lastIndexOf("/") + 1);
        File myFile = new File(mAppDirFile + "/" + fileName);

        if (myFile.exists()) {
            mPrefsManger.setAdImage(fileName);
            Utils.loadImage(null, myFile, mAdsIv, R.drawable.menu_item_placeholder);
        } else {
            DownloadFile.downloadImage(mServiceApi, mPrefsManger, fileLink, fileName, mAdsIv, R.drawable.menu_item_placeholder);
        }
    }

}
