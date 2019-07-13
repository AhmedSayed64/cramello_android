package net.aldar.cramello.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import net.aldar.cramello.R;
import net.aldar.cramello.apiHandler.BaseApi;
import net.aldar.cramello.apiHandler.BaseApiHandler;
import net.aldar.cramello.model.RegistrationData;
import net.aldar.cramello.services.LocaleHelper;
import net.aldar.cramello.services.PrefsManger;
import net.aldar.cramello.services.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.aldar.cramello.services.Utils.PW_MAX_CHAR_NUMBER;
import static net.aldar.cramello.services.Utils.PW_MIN_CHAR_NUMBER;
import static net.aldar.cramello.view.App.KEY_REG_DATA;
import static net.aldar.cramello.view.App.mMontserratRegular;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private int KW_PHONE_COUNT = 8;

    private FrameLayout mRootLayout;

    private TextView mRegisterTitle;
    private TextView mRegisterHintTv;

    private EditText mFirstNameInput;
    private EditText mFamilyNameInput;
    private EditText mEmailInput;
    private EditText mPhoneInput;
    private EditText mPasswordInput;
    private EditText mConfirmPasswordInput;

    private Button mRegisterBtn;

    private LinearLayout mSpinKitLayout;

    private PrefsManger mPrefsManger;
    private BaseApi mServiceApi;

    @Override
    protected void attachBaseContext(Context newBase) {
        mPrefsManger = new PrefsManger(newBase);
        super.attachBaseContext(LocaleHelper.wrap(newBase, mPrefsManger.getAppLanguage()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mServiceApi = BaseApiHandler.setupBaseApi().create(BaseApi.class);

        mRootLayout = findViewById(R.id.root_layout);
        Utils.setupHideKeyboard(this, mRootLayout);

        mSpinKitLayout = findViewById(R.id.register_spinKit_layout);
        mSpinKitLayout.setVisibility(View.GONE);

        mRegisterTitle = findViewById(R.id.register_activity_title);
        mRegisterTitle.setTypeface(mMontserratRegular);

        mRegisterHintTv = findViewById(R.id.register_activity_hintTv);
        mRegisterHintTv.setTypeface(mMontserratRegular);

        mFirstNameInput = findViewById(R.id.register_activity_firstNameInput);
        mFirstNameInput.setTypeface(mMontserratRegular);

        mFamilyNameInput = findViewById(R.id.register_activity_familyNameInput);
        mFamilyNameInput.setTypeface(mMontserratRegular);

        mEmailInput = findViewById(R.id.register_activity_emailInput);
        mEmailInput.setTypeface(mMontserratRegular);
        mEmailInput.addTextChangedListener(this);

        mPhoneInput = findViewById(R.id.register_activity_phoneInput);
        mPhoneInput.setTypeface(mMontserratRegular);
        mPhoneInput.addTextChangedListener(this);

        mPasswordInput = findViewById(R.id.register_activity_pwInput);
        mPasswordInput.setTypeface(mMontserratRegular);
        mPasswordInput.addTextChangedListener(this);

        mConfirmPasswordInput = findViewById(R.id.register_activity_confirmPwInput);
        mConfirmPasswordInput.setTypeface(mMontserratRegular);
        mConfirmPasswordInput.addTextChangedListener(this);

        mRegisterBtn = findViewById(R.id.register_activity_continueBtn);
        mRegisterBtn.setTypeface(mMontserratRegular);
        mRegisterBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.register_activity_continueBtn:
                registerValidation();
                break;
        }

    }

    private void registerValidation() {

        boolean isThereError = false;
        if (Utils.isInputEmpty(mFirstNameInput)) {
            mFirstNameInput.setError(getResources().getString(R.string.firstNameValid));
            isThereError = true;
        }
        if (Utils.isInputEmpty(mFamilyNameInput)) {
            mFamilyNameInput.setError(getResources().getString(R.string.familyNameValid));
            isThereError = true;
        }
        if (!Utils.isValidEmail(mEmailInput.getText().toString().trim())) {
            mEmailInput.setError(getResources().getString(R.string.emailValid));
            isThereError = true;
        }
        if (!Utils.isValidPhone(mPhoneInput.getText().toString().trim(), KW_PHONE_COUNT)) {
            mPhoneInput.setError(getResources().getString(R.string.mobileNoValid)
                    .concat("( ")
                    .concat(String.valueOf(KW_PHONE_COUNT))
                    .concat(" ")
                    .concat(getResources().getString(R.string.number))
                    .concat(" )"));
            isThereError = true;
        }
        if (!Utils.isValidPw(mPasswordInput.getText().toString().trim())) {
            mPasswordInput.setError(getResources().getString(R.string.pwValid)
                    .concat("( ")
                    .concat(String.valueOf(PW_MIN_CHAR_NUMBER))
                    .concat("-")
                    .concat(String.valueOf(PW_MAX_CHAR_NUMBER))
                    .concat(" ")
                    .concat(getResources().getString(R.string.chars))
                    .concat(" )"));
            isThereError = true;
        }
        if (!mPasswordInput.getText().toString().trim().equals(mConfirmPasswordInput.getText().toString().trim())) {
            mConfirmPasswordInput.setError(getResources().getString(R.string.pw_notMatchValid));
            isThereError = true;
        }

        if (!isThereError)
            sendRegisterValidationRequest();
    }

    private void sendRegisterValidationRequest() {
        if (Utils.isConnectionOn(RegisterActivity.this)) {
            mSpinKitLayout.setVisibility(View.VISIBLE);

            final RegistrationData registrationData = new RegistrationData(
                    mEmailInput.getText().toString().trim(),
                    mFirstNameInput.getText().toString().trim(),
                    mFamilyNameInput.getText().toString().trim(),
                    mPhoneInput.getText().toString().trim(),
                    mPasswordInput.getText().toString().trim());

            Call<RegistrationData> call = mServiceApi.validateRegisterData(registrationData);

            call.enqueue(new Callback<RegistrationData>() {
                @Override
                public void onResponse(Call<RegistrationData> call, Response<RegistrationData> response) {
                    mSpinKitLayout.setVisibility(View.GONE);

                    if (response.isSuccessful()) {
                        try {
                            Intent intent = new Intent(RegisterActivity.this, VerificationActivity.class);
                            intent.putExtra(KEY_REG_DATA, new Gson().toJson(registrationData));
                            startActivity(intent);
                        } catch (Exception e) {
                            Log.e("Validation Ex / ", e.getMessage() + "");
                        }
                    } else {
                        Log.e("Validation Code / ", "Code not Successful");
                        if (response.code() == 400) {
                            try {
                                ResponseBody errorBody = response.errorBody();
                                JSONObject jsonObject = new JSONObject(new String(errorBody.bytes()));

                                if (jsonObject.has("phone"))
                                    mPhoneInput.setError(getResources().getString(R.string.phone_duplicated));
                                if (jsonObject.has("email"))
                                    mEmailInput.setError(getResources().getString(R.string.email_duplicated));

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else
                            Utils.makeAToast(RegisterActivity.this,
                                    getResources().getString(R.string.server_register_failed));
                    }
                }

                @Override
                public void onFailure(Call<RegistrationData> call, Throwable t) {
                    mSpinKitLayout.setVisibility(View.GONE);
                    Log.e("Validation Fail / ", t.getMessage() + "");
                    Utils.makeAToast(RegisterActivity.this,
                            getResources().getString(R.string.server_register_failed));
                }
            });
        } else
            Utils.makeAToast(RegisterActivity.this, getResources().getString(R.string.connection_offline));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        if (s == mPhoneInput.getEditableText()) {
            if (s.toString().trim().length() != KW_PHONE_COUNT) {
                mPhoneInput.setError(getResources().getString(R.string.mobileNoValid)
                        .concat("( ")
                        .concat(String.valueOf(KW_PHONE_COUNT))
                        .concat(" ")
                        .concat(getResources().getString(R.string.number))
                        .concat(" )"));
            } else
                mPhoneInput.setError(null);

        } else if (s == mPasswordInput.getEditableText()) {
            if (!Utils.isValidPw(s.toString())) {
                mPasswordInput.setError(getResources().getString(R.string.pwValid)
                        .concat("( ")
                        .concat(String.valueOf(PW_MIN_CHAR_NUMBER))
                        .concat("-")
                        .concat(String.valueOf(PW_MAX_CHAR_NUMBER))
                        .concat(" ")
                        .concat(getResources().getString(R.string.chars))
                        .concat(" )"));
            } else
                mPasswordInput.setError(null);

        } else if (s == mConfirmPasswordInput.getEditableText()) {
            if (!s.toString().equals(mPasswordInput.getText().toString())) {
                mConfirmPasswordInput.setError(getResources().getString(R.string.pw_notMatchValid));
            } else
                mConfirmPasswordInput.setError(null);

        } else if (s == mEmailInput.getEditableText()) {
            if (!Utils.isValidEmail(s.toString())) {
                mEmailInput.setError(getResources().getString(R.string.emailValid));
            } else
                mEmailInput.setError(null);
        }
    }
}
