package net.aldar.cramello.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import net.aldar.cramello.R;
import net.aldar.cramello.model.response.UserData;
import net.aldar.cramello.services.Utils;
import net.aldar.cramello.view.MainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.aldar.cramello.view.App.mMontserratBold;
import static net.aldar.cramello.view.App.mMontserratRegular;
import static net.aldar.cramello.view.MainActivity.CHANGE_PW_FRAGMENT_TAG;

public class MyProfileFragment extends RootFragment implements View.OnClickListener, TextWatcher {


    private TextView mTitleTv;
    private LinearLayout mMenuLayout;
    private ImageView mMenuIv;

    private AppCompatEditText mFirstNameInput;
    private AppCompatEditText mFamilyNameInput;
    private AppCompatEditText mEmailInput;
    private AppCompatEditText mPhoneInput;

    private Button mSaveBtn;
    private TextView mChangePwBtn;

    private MainActivity mMainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mTitleTv = view.findViewById(R.id.profileFragment_title);
        mMenuLayout = view.findViewById(R.id.profileFragment_menuLayout);
        mMenuIv = view.findViewById(R.id.profileFragment_menuIv);

        mFirstNameInput = view.findViewById(R.id.profileFragment_firstNameInput);
        mFamilyNameInput = view.findViewById(R.id.profileFragment_familyNameInput);
        mEmailInput = view.findViewById(R.id.profileFragment_emailInput);
        mPhoneInput = view.findViewById(R.id.profileFragment_mobileInput);

        mSaveBtn = view.findViewById(R.id.profileFragment_saveBtn);
        mChangePwBtn = view.findViewById(R.id.profileFragment_changePwBtn);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mMainActivity = (MainActivity) getActivity();

        mTitleTv.setTypeface(mMontserratRegular);
        mMenuLayout.setOnClickListener(this);
        Utils.submitFlip(mMenuIv, mMainActivity.mPrefsManger);

        mFirstNameInput.setTypeface(mMontserratRegular);
        mFamilyNameInput.setTypeface(mMontserratRegular);

        mEmailInput.setTypeface(mMontserratRegular);
        mEmailInput.addTextChangedListener(this);

        mPhoneInput.setTypeface(mMontserratRegular);
        mPhoneInput.setFocusable(false);
        mPhoneInput.setEnabled(false);

        mSaveBtn.setTypeface(mMontserratRegular);
        mSaveBtn.setOnClickListener(this);

        mChangePwBtn.setTypeface(mMontserratBold);
        mChangePwBtn.setOnClickListener(this);

        setCachedProfileData();
    }

    private void setCachedProfileData() {
        fillInputs();
        getProfileData();
    }

    private void fillInputs() {
        UserData userData = mMainActivity.mPrefsManger.loadUserData();
        mFirstNameInput.setText(userData.getFirstName());
        mFamilyNameInput.setText(userData.getLastName());
        mEmailInput.setText(userData.getEmail());
        mPhoneInput.setText(userData.getPhone());
    }

    private void getProfileData() {
        if (Utils.isConnectionOn(mMainActivity)) {
            mMainActivity.mSpinKitLayout.setVisibility(View.VISIBLE);

            Call<UserData> call = mMainActivity.mServiceApi.getUserData(mMainActivity.mAuthToken, mMainActivity.mUserId);
            call.enqueue(new Callback<UserData>() {
                @Override
                public void onResponse(Call<UserData> call, Response<UserData> response) {
                    mMainActivity.mSpinKitLayout.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            UserData userData = response.body();
                            mMainActivity.mPrefsManger.saveUserData(userData);
                            fillInputs();
                        } catch (Exception e) {
                            Log.e("GProfile Ex / ", e.getMessage() + "");
                            Utils.makeAToast(mMainActivity, getResources().getString(R.string.getProfileFailed));
                        }
                    } else {
                        Log.e("GProfile Code / ", "Code not Successful");
                        Utils.makeAToast(mMainActivity, getResources().getString(R.string.getProfileFailed));
                    }
                }

                @Override
                public void onFailure(Call<UserData> call, Throwable t) {
                    mMainActivity.mSpinKitLayout.setVisibility(View.GONE);
                    Log.e("GProfile Fail / ", t.getMessage() + "");
                    Utils.makeAToast(mMainActivity, getResources().getString(R.string.getProfileFailed));
                }
            });
        } else
            Utils.makeAToast(mMainActivity, getResources().getString(R.string.connection_offline));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profileFragment_menuLayout:
                mMainActivity.openDrawer();
                break;
            case R.id.profileFragment_saveBtn:
                inputsValidations();
                break;
            case R.id.profileFragment_changePwBtn:
                openChangePasswordScreen();
                break;

        }
    }

    private void inputsValidations() {
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

        if (!isThereError)
            updateProfile();
    }

    private void updateProfile() {
        if (Utils.isConnectionOn(mMainActivity)) {
            mMainActivity.mSpinKitLayout.setVisibility(View.VISIBLE);

            UserData updatedUserData = new UserData();
            updatedUserData.setFirstName(mFirstNameInput.getText().toString().trim());
            updatedUserData.setLastName(mFamilyNameInput.getText().toString().trim());
            updatedUserData.setEmail(mEmailInput.getText().toString().trim());

            Call<UserData> call = mMainActivity.mServiceApi.updateProfile(mMainActivity.mAuthToken,
                    mMainActivity.mUserId, updatedUserData);
            call.enqueue(new Callback<UserData>() {
                @Override
                public void onResponse(Call<UserData> call, Response<UserData> response) {
                    mMainActivity.mSpinKitLayout.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            UserData userData = response.body();
                            mMainActivity.mPrefsManger.saveUserData(userData);

                            Utils.makeAToast(mMainActivity, getResources().getString(R.string.profileUpdatedSuccess));

                        } catch (Exception e) {
                            Log.e("UProfile Ex / ", e.getMessage() + "");
                            Utils.makeAToast(mMainActivity, getResources().getString(R.string.profileUpdatedFailed));
                        }
                    } else {
                        Log.e("UProfile Code / ", "Code not Successful");
                        Utils.makeAToast(mMainActivity, getResources().getString(R.string.profileUpdatedFailed));
                    }
                }

                @Override
                public void onFailure(Call<UserData> call, Throwable t) {
                    mMainActivity.mSpinKitLayout.setVisibility(View.GONE);
                    Log.e("UProfile Fail / ", t.getMessage() + "");
                    Utils.makeAToast(mMainActivity, getResources().getString(R.string.profileUpdatedFailed));
                }
            });
        } else
            Utils.makeAToast(mMainActivity, getResources().getString(R.string.connection_offline));
    }

    private void openChangePasswordScreen() {
        Utils.openChildFragment(MyProfileFragment.this, new ChangePwFragment(), null,
                R.id.profileFragment_container_layout, CHANGE_PW_FRAGMENT_TAG);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        if (s == mEmailInput.getEditableText()) {
            if (!Utils.isValidEmail(s.toString())) {
                mEmailInput.setError(getResources().getString(R.string.emailValid));
            } else
                mEmailInput.setError(null);
        }
    }
}
