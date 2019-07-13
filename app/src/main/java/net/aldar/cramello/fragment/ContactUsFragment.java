package net.aldar.cramello.fragment;

import android.content.Intent;
import android.os.Bundle;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import net.aldar.cramello.R;
import net.aldar.cramello.model.response.ContactData;
import net.aldar.cramello.services.Utils;
import net.aldar.cramello.view.MainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.aldar.cramello.view.App.mMontserratRegular;
import static net.aldar.cramello.view.MainActivity.BRANCHES_FRAGMENT_TAG;

public class ContactUsFragment extends RootFragment implements View.OnClickListener {

    private TextView mTitleTv;
    private LinearLayout mMenuLayout;
    private ImageView mMenuIv;

    private TextView mHintTitle;
    private TextView mEmailHintTitle;
    private TextView mPhoneHintTitle;

    private TextView mEmailHintTv;
    private TextView mPhoneHintTv;

    private Button mBranchesBtn;

    private SupportMapFragment mSupportMapFragment;

    private MainActivity mMainActivity;
    private ContactData mContactData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);

        mTitleTv = view.findViewById(R.id.contactUsFragment_title);
        mMenuLayout = view.findViewById(R.id.contactUsFragment_menuLayout);
        mMenuIv = view.findViewById(R.id.contactUsFragment_menuIv);

        mHintTitle = view.findViewById(R.id.contactUsFragment_hintTitle);
        mEmailHintTitle = view.findViewById(R.id.contactUsFragment_emailHintTitle);
        mPhoneHintTitle = view.findViewById(R.id.contactUsFragment_phoneHintTitle);

        mEmailHintTv = view.findViewById(R.id.contactUsFragment_emailHintTv);
        mPhoneHintTv = view.findViewById(R.id.contactUsFragment_phoneHintTv);

        mBranchesBtn = view.findViewById(R.id.contactUsFragment_branchesBtn);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mTitleTv.setTypeface(mMontserratRegular);
        mMenuLayout.setOnClickListener(this);
        Utils.submitFlip(mMenuIv, mMainActivity.mPrefsManger);

        mHintTitle.setTypeface(mMontserratRegular);
        mEmailHintTitle.setTypeface(mMontserratRegular);
        mPhoneHintTitle.setTypeface(mMontserratRegular);

        mEmailHintTv.setTypeface(mMontserratRegular);
        mEmailHintTv.setOnClickListener(this);

        mPhoneHintTv.setTypeface(mMontserratRegular);
        mPhoneHintTv.setOnClickListener(this);

        mBranchesBtn.setTypeface(mMontserratRegular);
        mBranchesBtn.setOnClickListener(this);

        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.contactUsFragment_map);

        getContactData();
    }

    private void getContactData() {
        if (Utils.isConnectionOn(mMainActivity)) {
            mMainActivity.mSpinKitLayout.setVisibility(View.VISIBLE);

            Call<ContactData> call = mMainActivity.mServiceApi.getContactData(mMainActivity.mAuthToken);
            call.enqueue(new Callback<ContactData>() {
                @Override
                public void onResponse(Call<ContactData> call, Response<ContactData> response) {
                    mMainActivity.mSpinKitLayout.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            mContactData = response.body();
                            fillInputs();
                        } catch (Exception e) {
                            Log.e("Contact Ex / ", e.getMessage() + "");
                            Utils.makeAToast(mMainActivity, getResources().getString(R.string.failedToGetContactData));
                        }
                    } else {
                        Log.e("Contact Code / ", "Code not Successful");
                        Utils.makeAToast(mMainActivity, getResources().getString(R.string.failedToGetContactData));
                    }
                }

                @Override
                public void onFailure(Call<ContactData> call, Throwable t) {
                    mMainActivity.mSpinKitLayout.setVisibility(View.GONE);
                    Log.e("Contact Fail / ", t.getMessage() + "");
                    Utils.makeAToast(mMainActivity, getResources().getString(R.string.failedToGetContactData));
                }
            });
        } else
            Utils.makeAToast(mMainActivity, getResources().getString(R.string.connection_offline));
    }

    private void fillInputs() {

        double lat = 0;
        double lng = 0;

        if (mContactData.getLatitude() != null && mContactData.getLatitude() != 0)
            lat = mContactData.getLatitude();
        if (mContactData.getLongitude() != null && mContactData.getLongitude() != 0)
            lng = mContactData.getLongitude();

        initializeMap(lat, lng);

        if (mContactData.getEmail() != null && !mContactData.getEmail().isEmpty())
            mEmailHintTv.setText(mContactData.getEmail());
        else
            mEmailHintTitle.setVisibility(View.GONE);

        if (mContactData.getPhone() != null && !mContactData.getPhone().isEmpty())
            mPhoneHintTv.setText(mContactData.getPhone());
        else
            mPhoneHintTitle.setVisibility(View.GONE);

    }

    private void initializeMap(final double lat, final double lng) {
        mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng location = new LatLng(lat, lng);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(location)
                        .zoom(17)
                        .build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.addMarker(new MarkerOptions().position(location));

                googleMap.getUiSettings().setScrollGesturesEnabled(false);
                googleMap.getUiSettings().setZoomGesturesEnabled(false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contactUsFragment_menuLayout:
                mMainActivity.openDrawer();
                break;

            case R.id.contactUsFragment_map:
                Utils.openMapNavigation(mMainActivity, mContactData.getLatitude(), mContactData.getLongitude());
                break;

            case R.id.contactUsFragment_emailHintTv:
                sendEmail();
                break;

            case R.id.contactUsFragment_phoneHintTv:
                Utils.makeACall(mMainActivity, mContactData.getPhone());
                break;

            case R.id.contactUsFragment_branchesBtn:
                Utils.openChildFragment(ContactUsFragment.this, new BranchesFragment(),
                        null, R.id.contactUsFragment_containerLayout, BRANCHES_FRAGMENT_TAG);
                break;
        }
    }

    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mContactData.getEmail()});
        emailIntent.setType("message/rfc822");
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(emailIntent, ""));
    }
}
