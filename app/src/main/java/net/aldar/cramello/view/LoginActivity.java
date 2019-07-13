package net.aldar.cramello.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import net.aldar.cramello.R;
import net.aldar.cramello.apiHandler.BaseApi;
import net.aldar.cramello.apiHandler.BaseApiHandler;
import net.aldar.cramello.model.request.ForgetPwRequest;
import net.aldar.cramello.model.request.LoginRequest;
import net.aldar.cramello.model.response.LoginData;
import net.aldar.cramello.model.response.UserData;
import net.aldar.cramello.services.LocaleHelper;
import net.aldar.cramello.services.PrefsManger;
import net.aldar.cramello.services.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.aldar.cramello.view.App.mMontserratRegular;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FrameLayout mRootLayout;

    private TextView mLoginTitle;
    private TextView mAlreadyHaveAccTitle;
    private TextView mForgetPWTitle;

    private EditText mPhoneOrEmailInput;
    private EditText mPasswordInput;

    private Button mCreateNewAccBtn;
    private Button mContinueBtn;

    private LinearLayout mSpinKitLayout;

    private BaseApi mServiceApi;
    private PrefsManger mPrefsManger;

    @Override
    protected void attachBaseContext(Context newBase) {
        mPrefsManger = new PrefsManger(newBase);
        super.attachBaseContext(LocaleHelper.wrap(newBase, mPrefsManger.getAppLanguage()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mServiceApi = BaseApiHandler.setupBaseApi().create(BaseApi.class);

        mRootLayout = findViewById(R.id.root_layout);
        Utils.setupHideKeyboard(this, mRootLayout);

        mSpinKitLayout = findViewById(R.id.login_spinKit_layout);

        mLoginTitle = findViewById(R.id.login_activity_title);
        mLoginTitle.setTypeface(mMontserratRegular);

        mAlreadyHaveAccTitle = findViewById(R.id.login_activity_alreadyAccTitle);
        mAlreadyHaveAccTitle.setTypeface(mMontserratRegular);

        mForgetPWTitle = findViewById(R.id.login_activity_forgetPwBtn);
        mForgetPWTitle.setTypeface(mMontserratRegular);
        mForgetPWTitle.setOnClickListener(this);

        mCreateNewAccBtn = findViewById(R.id.login_activity_newAccBtn);
        mCreateNewAccBtn.setTypeface(mMontserratRegular);
        mCreateNewAccBtn.setOnClickListener(this);

        mPhoneOrEmailInput = findViewById(R.id.login_activity_emailPhoneInput);
        mPhoneOrEmailInput.setTypeface(mMontserratRegular);

        mPasswordInput = findViewById(R.id.login_activity_pwInput);
        mPasswordInput.setTypeface(mMontserratRegular);

        mContinueBtn = findViewById(R.id.login_activity_continueBtn);
        mContinueBtn.setTypeface(mMontserratRegular);
        mContinueBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.login_activity_forgetPwBtn:
                showForgetPwDialog();
                break;

            case R.id.login_activity_newAccBtn:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;

            case R.id.login_activity_continueBtn:
                loginValidation();
                break;
        }
    }

    private void showForgetPwDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_forget_pw, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog mAlertDialog = dialogBuilder.create();

        final LinearLayout spinKitLayout = dialogView.findViewById(R.id.dialog_forget_spinKit_layout);

        TextView title = dialogView.findViewById(R.id.dialog_forget__title);
        title.setTypeface(mMontserratRegular);

        final EditText email = dialogView.findViewById(R.id.dialog_forget_input);
        email.setTypeface(mMontserratRegular);

        Button sendBtn = dialogView.findViewById(R.id.dialog_forget_continueBtn);
        sendBtn.setTypeface(mMontserratRegular);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendForgetPwRequest(mAlertDialog, email, spinKitLayout);
            }
        });

        mAlertDialog.show();
    }

    private void loginValidation() {

        boolean isThereError = false;

        if (Utils.isInputEmpty(mPhoneOrEmailInput)) {
            mPhoneOrEmailInput.setError(getResources().getString(R.string.mobileOrEmailValid));
            isThereError = true;
        }

        if (Utils.isInputEmpty(mPasswordInput)) {
            mPasswordInput.setError(getResources().getString(R.string.pwValid));
            isThereError = true;
        }
        if (!isThereError)
            sendLoginRequest();
    }

    private void sendLoginRequest() {
        if (Utils.isConnectionOn(LoginActivity.this)) {
            mSpinKitLayout.setVisibility(View.VISIBLE);

            Call<LoginData> call = mServiceApi.login(new LoginRequest(mPhoneOrEmailInput.getText().toString().trim(),
                    mPasswordInput.getText().toString().trim()));
            call.enqueue(new Callback<LoginData>() {
                @Override
                public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                    mSpinKitLayout.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            LoginData loginResponse = response.body();
                            getUserData(loginResponse);
                        } catch (Exception e) {
                            Log.e("Login Ex / ", e.getMessage() + "");
                        }
                    } else {
                        Log.e("Login Code / ", "Code not Successful");
                        if (response.code() == 400)
                            Utils.makeAToast(LoginActivity.this,
                                    getResources().getString(R.string.login_wrong_data));
                        else
                            Utils.makeAToast(LoginActivity.this,
                                    getResources().getString(R.string.server_login_failed));
                    }
                }

                @Override
                public void onFailure(Call<LoginData> call, Throwable t) {
                    mSpinKitLayout.setVisibility(View.GONE);
                    Log.e("Login Fail / ", t.getMessage() + "");
                    Utils.makeAToast(LoginActivity.this,
                            getResources().getString(R.string.server_login_failed));
                }
            });
        } else
            Utils.makeAToast(LoginActivity.this, getResources().getString(R.string.connection_offline));
    }

    private void getUserData(final LoginData loginResponse) {
        mSpinKitLayout.setVisibility(View.VISIBLE);

        String auth = "Token ".concat(loginResponse.getToken());

        Call<UserData> call = mServiceApi.getUserData(auth, loginResponse.getUserId());
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                mSpinKitLayout.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    try {
                        UserData userData = response.body();

                        mPrefsManger.setLoginToken(loginResponse.getToken());
                        mPrefsManger.saveUserData(userData);
                        mPrefsManger.setUserLogin(true);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();

                    } catch (Exception e) {
                        Log.e("UserData Ex / ", e.getMessage() + "");
                    }
                } else {
                    Log.e("UserData Code / ", "Code not Successful");
                    Utils.makeAToast(LoginActivity.this,
                            getResources().getString(R.string.server_login_failed));
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                mSpinKitLayout.setVisibility(View.GONE);
                Log.e("UserData Fail / ", t.getMessage() + "");
                Utils.makeAToast(LoginActivity.this,
                        getResources().getString(R.string.server_login_failed));
            }
        });
    }

    private void sendForgetPwRequest(final AlertDialog alertDialog, final EditText email, final LinearLayout spinKitLayout) {
        if (Utils.isConnectionOn(LoginActivity.this)) {
            spinKitLayout.setVisibility(View.VISIBLE);

            Call<ForgetPwRequest> call = mServiceApi.forgetPw(new ForgetPwRequest(email.getText().toString().trim()));

            call.enqueue(new Callback<ForgetPwRequest>() {
                @Override
                public void onResponse(Call<ForgetPwRequest> call, Response<ForgetPwRequest> response) {
                    spinKitLayout.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            Utils.makeAToast(LoginActivity.this, getResources().getString(R.string.resetPwSuccess));
                            alertDialog.dismiss();

                        } catch (Exception e) {
                            Log.e("ForgetPw Ex / ", e.getMessage() + "");
                        }
                    } else {
                        Log.e("ForgetPw Code / ", "Code not Successful");
                        if (response.code() == 400)
                            email.setError(getResources().getString(R.string.emailNotRegistered));
                    }
                }

                @Override
                public void onFailure(Call<ForgetPwRequest> call, Throwable t) {
                    spinKitLayout.setVisibility(View.GONE);
                    Log.e("ForgetPw Fail / ", t.getMessage() + "");
                    Utils.makeAToast(LoginActivity.this,
                            getResources().getString(R.string.resetPwFailed));
                }
            });
        } else
            Utils.makeAToast(LoginActivity.this, getResources().getString(R.string.connection_offline));
    }
}
