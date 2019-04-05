package net.aldar.cramello.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.aldar.cramello.MainActivity;
import net.aldar.cramello.R;
import net.aldar.cramello.model.request.ChangePasswordRequest;
import net.aldar.cramello.services.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.aldar.cramello.App.mMontserratLight;
import static net.aldar.cramello.App.mMontserratRegular;
import static net.aldar.cramello.services.Utils.PW_MAX_CHAR_NUMBER;
import static net.aldar.cramello.services.Utils.PW_MIN_CHAR_NUMBER;

public class ChangePwFragment extends RootFragment implements View.OnClickListener, TextWatcher {

    private TextView mTitleTv;
    private LinearLayout mBackLayout;
    private ImageView mBackIv;

    private AppCompatEditText mCurrentPwInput;
    private AppCompatEditText mNewPwInput;
    private AppCompatEditText mConfirmNewPwInput;

    private Button mSaveBtn;
    private FrameLayout mRootLayout;

    private MainActivity mMainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        mTitleTv = view.findViewById(R.id.changePwFragment_title);
        mBackLayout = view.findViewById(R.id.changePwFragment_backLayout);
        mBackIv = view.findViewById(R.id.changePwFragment_backIv);

        mCurrentPwInput = view.findViewById(R.id.changePwFragment_currentPwInput);
        mNewPwInput = view.findViewById(R.id.changePwFragment_newPwInput);
        mConfirmNewPwInput = view.findViewById(R.id.changePwFragment_confirmNewPwInput);

        mSaveBtn = view.findViewById(R.id.changePwFragment_saveBtn);
        mRootLayout = view.findViewById(R.id.root_layout);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mTitleTv.setTypeface(mMontserratRegular);
        mBackLayout.setOnClickListener(this);
        Utils.setupHideKeyboard(mMainActivity, mRootLayout);
        Utils.submitRotation(mBackIv, mMainActivity.mPrefsManger);

        mCurrentPwInput.setTypeface(mMontserratRegular);

        mNewPwInput.setTypeface(mMontserratRegular);
        mNewPwInput.addTextChangedListener(this);

        mConfirmNewPwInput.setTypeface(mMontserratRegular);
        mConfirmNewPwInput.addTextChangedListener(this);

        mSaveBtn.setTypeface(mMontserratLight);
        mSaveBtn.setOnClickListener(this);
    }

    private void passwordValidations() {

        boolean isThereError = false;

        if (Utils.isInputEmpty(mCurrentPwInput)) {
            mCurrentPwInput.setError(getResources().getString(R.string.pwValid));
            isThereError = true;
        }

        if (!Utils.isValidPw(mNewPwInput.getText().toString().trim())) {
            mNewPwInput.setError(getResources().getString(R.string.pwValid)
                    .concat("( ")
                    .concat(String.valueOf(PW_MIN_CHAR_NUMBER))
                    .concat("-")
                    .concat(String.valueOf(PW_MAX_CHAR_NUMBER))
                    .concat(" ")
                    .concat(getResources().getString(R.string.chars))
                    .concat(" )"));
            isThereError = true;
        }

        if (!mConfirmNewPwInput.getText().toString().equals(mNewPwInput.getText().toString())) {
            mConfirmNewPwInput.setError(getResources().getString(R.string.pw_notMatchValid));
            isThereError = true;
        }

        if (!isThereError)
            changePassword();
    }

    private void changePassword() {
        if (Utils.isConnectionOn(mMainActivity)) {
            mMainActivity.mSpinKitLayout.setVisibility(View.VISIBLE);

            ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
                    mCurrentPwInput.getText().toString().trim(),
                    mNewPwInput.getText().toString().trim(),
                    mConfirmNewPwInput.getText().toString().trim());

            Call<Void> call = mMainActivity.mServiceApi.changePassword(mMainActivity.mAuthToken, changePasswordRequest);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    mMainActivity.mSpinKitLayout.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            Utils.makeAToast(mMainActivity, getResources().getString(R.string.changePasswordSuccess));
                            mMainActivity.onBackPressed();
                        } catch (Exception e) {
                            Log.e("CPassword Ex / ", e.getMessage() + "");
                            Utils.makeAToast(mMainActivity, getResources().getString(R.string.changePasswordFailed));
                        }
                    } else {
                        if (response.code() == 400) {
                            try {
                                ResponseBody errorBody = response.errorBody();
                                JSONObject jsonObject = new JSONObject(new String(errorBody.bytes()));

                                if (jsonObject.has("old_password"))
                                    mCurrentPwInput.setError(getResources().getString(R.string.currentPasswordIncorrect));

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.e("CPassword Code / ", "Code not Successful");
                            Utils.makeAToast(mMainActivity, getResources().getString(R.string.changePasswordFailed));
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    mMainActivity.mSpinKitLayout.setVisibility(View.GONE);
                    Log.e("CPassword Fail / ", t.getMessage() + "");
                    Utils.makeAToast(mMainActivity, getResources().getString(R.string.changePasswordFailed));
                }
            });
        } else
            Utils.makeAToast(mMainActivity, getResources().getString(R.string.connection_offline));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.changePwFragment_backLayout:
                mMainActivity.onBackPressed();
                break;

            case R.id.changePwFragment_saveBtn:
                passwordValidations();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s == mNewPwInput.getEditableText()) {
            if (!Utils.isValidPw(s.toString())) {
                mNewPwInput.setError(getResources().getString(R.string.pwValid)
                        .concat("( ")
                        .concat(String.valueOf(PW_MIN_CHAR_NUMBER))
                        .concat("-")
                        .concat(String.valueOf(PW_MAX_CHAR_NUMBER))
                        .concat(" ")
                        .concat(getResources().getString(R.string.chars))
                        .concat(" )"));
            } else
                mNewPwInput.setError(null);

        } else if (s == mConfirmNewPwInput.getEditableText()) {
            if (!s.toString().equals(mNewPwInput.getText().toString())) {
                mConfirmNewPwInput.setError(getResources().getString(R.string.pw_notMatchValid));
            } else
                mConfirmNewPwInput.setError(null);

        }
    }
}
