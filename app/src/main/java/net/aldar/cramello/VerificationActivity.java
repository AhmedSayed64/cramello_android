package net.aldar.cramello;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.aldar.cramello.apiHandler.BaseApi;
import net.aldar.cramello.apiHandler.BaseApiHandler;
import net.aldar.cramello.model.RegistrationData;
import net.aldar.cramello.model.request.ValidateCode;
import net.aldar.cramello.model.request.VerCodeRequest;
import net.aldar.cramello.model.response.DefaultResponse;
import net.aldar.cramello.model.response.UserData;
import net.aldar.cramello.model.response.VerCode;
import net.aldar.cramello.services.LocaleHelper;
import net.aldar.cramello.services.OnSmsReceivedListener;
import net.aldar.cramello.services.PrefsManger;
import net.aldar.cramello.services.Utils;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.aldar.cramello.App.KEY_REG_DATA;
import static net.aldar.cramello.App.mMontserratRegular;

public class VerificationActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, View.OnKeyListener, OnSmsReceivedListener {

    private boolean RESEND_AVAILABLE = true;
    private boolean FIRST_TIME = true;
    private int SMS_CODE = 0;
    private int CALL_CODE = 1;

    private boolean mFirstDigitFlag = false;
    private boolean mSecondDigitFlag = false;
    private boolean mThirdDigitFlag = false;
    private boolean mFourthDigitFlag = false;

    private TextView mActivityTitle;
    private TextView mMsgTitle;
    private TextView mHintTitle;
    private TextView mDidNtTitle;
    private TextView mCounterTv;

    private EditText mDigits_FirstInput;
    private EditText mDigits_SecondInput;
    private EditText mDigits_ThirdInput;
    private EditText mDigits_FourthInput;

    private LinearLayout mResendSmsLayout;
    private ImageView mResendSmsIv;
    private TextView mResendSmsTv;

    private LinearLayout mCallLayout;
    private ImageView mCallIv;
    private TextView mCallTv;

    private Button mContinueBan;

    private LinearLayout mSpinKitLayout;
    private FrameLayout mRootLayout;

    private PrefsManger mPrefsManger;
    private BaseApi mServiceApi;

    private VerCode mVerCode;
    private RegistrationData mRegistrationData;

    public static OnSmsReceivedListener SmsReceivedListener;
    private CountDownTimer mCountDownTimer;

    @Override
    protected void attachBaseContext(Context newBase) {
        mPrefsManger = new PrefsManger(newBase);
        super.attachBaseContext(LocaleHelper.wrap(newBase, mPrefsManger.getAppLanguage()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        if (getIntent().hasExtra(KEY_REG_DATA)) {
            String json = getIntent().getExtras().getString(KEY_REG_DATA);
            Type type = new TypeToken<RegistrationData>() {
            }.getType();
            mRegistrationData = new Gson().fromJson(json, type);
        }

        mServiceApi = BaseApiHandler.setupBaseApi().create(BaseApi.class);
        SmsReceivedListener = this;

        mSpinKitLayout = findViewById(R.id.ver_spinKit_layout);
        mRootLayout = findViewById(R.id.root_layout);
        Utils.setupHideKeyboard(this, mRootLayout);

        mActivityTitle = findViewById(R.id.ver_activity_title);
        mActivityTitle.setTypeface(mMontserratRegular);

        mMsgTitle = findViewById(R.id.ver_activity_msgTv);
        mMsgTitle.setTypeface(mMontserratRegular);

        mHintTitle = findViewById(R.id.ver_activity_hintTitle);
        mHintTitle.setTypeface(mMontserratRegular);

        mDidNtTitle = findViewById(R.id.ver_activity_didNotTitle);
        mDidNtTitle.setTypeface(mMontserratRegular);

        mCounterTv = findViewById(R.id.ver_activity_counterTv);
        mCounterTv.setTypeface(mMontserratRegular);

        mResendSmsLayout = findViewById(R.id.ver_activity_reSmsLayout);
        mResendSmsTv = findViewById(R.id.ver_activity_reSmsTv);
        mResendSmsTv.setTypeface(mMontserratRegular);
        mResendSmsLayout.setOnClickListener(this);
        mResendSmsIv = findViewById(R.id.ver_activity_reSmsIv);
//        mResendSmsIv.setColorFilter(ContextCompat.getColor(this, R.color.colorGreen));

        mCallLayout = findViewById(R.id.ver_activity_callLayout);
        mCallTv = findViewById(R.id.ver_activity_callTv);
        mCallTv.setTypeface(mMontserratRegular);
        mCallLayout.setOnClickListener(this);
        mCallIv = findViewById(R.id.ver_activity_callIv);
//        mCallIv.setColorFilter(ContextCompat.getColor(this, R.color.colorMaroon));

        mDigits_FirstInput = findViewById(R.id.ver_activity_firstNoInput);
        mDigits_FirstInput.setTypeface(mMontserratRegular);
        mDigits_FirstInput.addTextChangedListener(this);
        mDigits_FirstInput.setOnKeyListener(this);

        mDigits_SecondInput = findViewById(R.id.ver_activity_secNoInput);
        mDigits_SecondInput.setTypeface(mMontserratRegular);
        mDigits_SecondInput.addTextChangedListener(this);
        mDigits_SecondInput.setOnKeyListener(this);

        mDigits_ThirdInput = findViewById(R.id.ver_activity_thirdNoInput);
        mDigits_ThirdInput.setTypeface(mMontserratRegular);
        mDigits_ThirdInput.addTextChangedListener(this);
        mDigits_ThirdInput.setOnKeyListener(this);

        mDigits_FourthInput = findViewById(R.id.ver_activity_fourthNoInput);
        mDigits_FourthInput.setTypeface(mMontserratRegular);
        mDigits_FourthInput.addTextChangedListener(this);
        mDigits_FourthInput.setOnKeyListener(this);

        mContinueBan = findViewById(R.id.ver_activity_continueBtn);
        mContinueBan.setTypeface(mMontserratRegular);
        mContinueBan.setOnClickListener(this);

        initializeTimer();
        requestVerCode(SMS_CODE);
    }

    private void requestVerCode(final int type) {
        mSpinKitLayout.setVisibility(View.VISIBLE);

        final String successMsg;
        final String errorMsg;
        if (type == SMS_CODE) {
            successMsg = getResources().getString(R.string.smsSentSuccess);
            errorMsg = getResources().getString(R.string.smsSentFailed);
        } else {
            successMsg = getResources().getString(R.string.callsentSuccess);
            errorMsg = getResources().getString(R.string.callsentFailed);
        }

        VerCodeRequest codeRequest = new VerCodeRequest(
                getResources().getString(R.string.kwPhoneCode).concat(mRegistrationData.getPhone()),
                type);

        Call<VerCode> call = mServiceApi.requestVerCode(codeRequest);

        call.enqueue(new Callback<VerCode>() {
            @Override
            public void onResponse(Call<VerCode> call, Response<VerCode> response) {
                mSpinKitLayout.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    try {
                        mVerCode = response.body();
                        Utils.makeAToast(VerificationActivity.this, successMsg);

                        if (!FIRST_TIME)
                            startTimer();
                        else
                            FIRST_TIME = false;

                    } catch (Exception e) {
                        Log.e("VerCode Ex / ", e.getMessage() + "");
                    }
                } else {
                    Log.e("VerCode Code / ", "Code not Successful");
                    Utils.makeAToast(VerificationActivity.this, errorMsg);
                }
            }

            @Override
            public void onFailure(Call<VerCode> call, Throwable t) {
                mSpinKitLayout.setVisibility(View.GONE);
                Log.e("VerCode Fail / ", t.getMessage() + "");
                Utils.makeAToast(VerificationActivity.this, errorMsg);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ver_activity_reSmsLayout:
                if (RESEND_AVAILABLE)
                    requestVerCode(SMS_CODE);
                break;

            case R.id.ver_activity_callLayout:
                if (RESEND_AVAILABLE)
                    requestVerCode(CALL_CODE);
                break;

            case R.id.ver_activity_continueBtn:
                proceed();
                break;
        }
    }

    private void proceed() {
        if (TextUtils.isEmpty(mDigits_FirstInput.getText().toString()) ||
                TextUtils.isEmpty(mDigits_SecondInput.getText().toString()) ||
                TextUtils.isEmpty(mDigits_ThirdInput.getText().toString()) ||
                TextUtils.isEmpty(mDigits_FourthInput.getText().toString())) {
            Utils.makeAToast(VerificationActivity.this, getResources().getString(R.string.verCodeValidation));
            return;
        }

        String code = mDigits_FirstInput.getText().toString()
                .concat(mDigits_SecondInput.getText().toString())
                .concat(mDigits_ThirdInput.getText().toString())
                .concat(mDigits_FourthInput.getText().toString());

        validateCode(code);

//        String decodedBase64 = Utils.decodeBase64(mVerCode.getActivationCode());

//        if (code.equals(decodedBase64))
//            sendRegisterRequest();
//        else
//            Utils.makeAToast(VerificationActivity.this, getResources().getString(R.string.codeNotCorrect));
    }

    private void validateCode(String code) {
        if (Utils.isConnectionOn(VerificationActivity.this)) {
            mSpinKitLayout.setVisibility(View.VISIBLE);

            ValidateCode validateCode = new ValidateCode(
                    getResources().getString(R.string.kwPhoneCode).concat(mRegistrationData.getPhone()),
                    code);

            Call<DefaultResponse> call = mServiceApi.validateVerCode(mVerCode.getActivationCode(), validateCode);
            call.enqueue(new Callback<DefaultResponse>() {
                @Override
                public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                    mSpinKitLayout.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            DefaultResponse data = response.body();

                            if (data.getStatus() == 1)
                                sendRegisterRequest();
                            else
                                Utils.makeAToast(VerificationActivity.this, getResources().getString(R.string.codeNotCorrect));

                        } catch (Exception e) {
                            Log.e("ValidateCode Ex / ", e.getMessage() + "");
                        }
                    } else {
                        Log.e("ValidateCode Code / ", "Code not Successful");
                        Utils.makeAToast(VerificationActivity.this,
                                getResources().getString(R.string.failedSendCode));
                    }
                }

                @Override
                public void onFailure(Call<DefaultResponse> call, Throwable t) {
                    mSpinKitLayout.setVisibility(View.GONE);
                    Log.e("ValidateCode Fail / ", t.getMessage() + "");
                    Utils.makeAToast(VerificationActivity.this,
                            getResources().getString(R.string.failedSendCode));
                }
            });
        } else
            Utils.makeAToast(VerificationActivity.this, getResources().getString(R.string.connection_offline));
    }

    private void sendRegisterRequest() {
        if (Utils.isConnectionOn(VerificationActivity.this)) {
            mSpinKitLayout.setVisibility(View.VISIBLE);

            Call<RegistrationData> call = mServiceApi.registerNewAccount(mRegistrationData);
            call.enqueue(new Callback<RegistrationData>() {
                @Override
                public void onResponse(Call<RegistrationData> call, Response<RegistrationData> response) {
                    mSpinKitLayout.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            RegistrationData data = response.body();
                            getUserData(data);
                        } catch (Exception e) {
                            Log.e("Register Ex / ", e.getMessage() + "");
                        }
                    } else {
                        Log.e("Register Code / ", "Code not Successful");
                        Utils.makeAToast(VerificationActivity.this,
                                getResources().getString(R.string.server_register_failed));
                    }
                }

                @Override
                public void onFailure(Call<RegistrationData> call, Throwable t) {
                    mSpinKitLayout.setVisibility(View.GONE);
                    Log.e("Register Fail / ", t.getMessage() + "");
                    Utils.makeAToast(VerificationActivity.this,
                            getResources().getString(R.string.server_register_failed));
                }
            });
        } else
            Utils.makeAToast(VerificationActivity.this, getResources().getString(R.string.connection_offline));
    }

    private void getUserData(final RegistrationData registrationData) {
        mSpinKitLayout.setVisibility(View.VISIBLE);

        String auth = "Token ".concat(registrationData.getToken());

        Call<UserData> call = mServiceApi.getUserData(auth, registrationData.getId());
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                mSpinKitLayout.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    try {
                        UserData userData = response.body();

                        mPrefsManger.setLoginToken(registrationData.getToken());
                        mPrefsManger.saveUserData(userData);
                        mPrefsManger.setUserLogin(true);
                        Utils.makeAToast(VerificationActivity.this,
                                getResources().getString(R.string.registerSuccess));

                        Intent intent = new Intent(getApplicationContext(), AdActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    } catch (Exception e) {
                        Log.e("UserData Ex / ", e.getMessage() + "");
                    }
                } else {
                    Log.e("UserData Code / ", "Code not Successful");
                    Utils.makeAToast(VerificationActivity.this,
                            getResources().getString(R.string.server_register_failed));
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                mSpinKitLayout.setVisibility(View.GONE);
                Log.e("UserData Fail / ", t.getMessage() + "");
                Utils.makeAToast(VerificationActivity.this,
                        getResources().getString(R.string.server_register_failed));
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int count, int after) {

    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_DEL) {
            controlBackSign(view);
            return true;
        }
        return false;
    }

    @Override
    public void afterTextChanged(Editable editable) {

        if (editable == mDigits_FirstInput.getEditableText()) {

            if (!mDigits_FirstInput.getText().toString().trim().isEmpty()) {
                mFirstDigitFlag = true;
                if (mDigits_FirstInput.getText().toString().length() == 1)     //size as per your requirement
                    mDigits_SecondInput.requestFocus();
            }

        } else if (editable == mDigits_SecondInput.getEditableText()) {

            if (!mDigits_SecondInput.getText().toString().trim().isEmpty()) {
                mSecondDigitFlag = true;
                if (mDigits_SecondInput.getText().toString().length() == 1)     //size as per your requirement
                    mDigits_ThirdInput.requestFocus();
            }

        } else if (editable == mDigits_ThirdInput.getEditableText()) {

            if (!mDigits_ThirdInput.getText().toString().trim().isEmpty()) {
                mThirdDigitFlag = true;
                if (mDigits_ThirdInput.getText().toString().length() == 1)     //size as per your requirement
                    mDigits_FourthInput.requestFocus();
            }

        } else if (editable == mDigits_FourthInput.getEditableText()) {
            mFourthDigitFlag = true;
        }
    }

    private void controlBackSign(View view) {
        EditText eView = (EditText) view;

        if (eView.getText().toString().trim().isEmpty()) {
            if (eView.getId() == R.id.ver_activity_secNoInput) {
                if (!mSecondDigitFlag)
                    mDigits_FirstInput.requestFocus();
                else
                    mSecondDigitFlag = false;
            } else if (eView.getId() == R.id.ver_activity_thirdNoInput) {
                if (!mThirdDigitFlag)
                    mDigits_SecondInput.requestFocus();
                else
                    mThirdDigitFlag = false;
            } else if (eView.getId() == R.id.ver_activity_fourthNoInput) {
                if (!mFourthDigitFlag)
                    mDigits_ThirdInput.requestFocus();
                else
                    mFourthDigitFlag = false;
            }
        }
    }

    @Override
    public void onSmsReceived(String sender, String code) {
        Log.e("Code Re", code + "");

        for (int c = 0; c < code.length(); c++) {
            switch (c) {
                case 0:
                    mDigits_FirstInput.setText(String.valueOf(code.charAt(c)));
                    mFirstDigitFlag = true;
                    break;

                case 1:
                    mDigits_SecondInput.setText(String.valueOf(code.charAt(c)));
                    mSecondDigitFlag = true;
                    break;

                case 2:
                    mDigits_ThirdInput.setText(String.valueOf(code.charAt(c)));
                    mThirdDigitFlag = true;
                    break;

                case 3:
                    mDigits_FourthInput.setText(String.valueOf(code.charAt(c)));
                    mFourthDigitFlag = true;
                    break;
            }
        }

        proceed();
    }

    private void initializeTimer() {
        mCountDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                int time = (int) (millisUntilFinished / 1000);

                String timeInString = "00:".concat(String.valueOf(time));
                if (time < 10)
                    timeInString = "00:0".concat(String.valueOf(time));

                mCounterTv.setText(timeInString);
            }

            public void onFinish() {
                mCounterTv.setText("");
                RESEND_AVAILABLE = true;
            }

        };
    }

    private void startTimer() {
        RESEND_AVAILABLE = false;
        mCountDownTimer.start();
    }
}
