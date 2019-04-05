package net.aldar.cramello.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.aldar.cramello.MainActivity;
import net.aldar.cramello.R;
import net.aldar.cramello.adapter.GovernExpListAdapter;
import net.aldar.cramello.entitie.AddressType;
import net.aldar.cramello.model.Address;
import net.aldar.cramello.model.response.governorate.Area;
import net.aldar.cramello.model.response.governorate.Governorate;
import net.aldar.cramello.services.GPSTracker;
import net.aldar.cramello.services.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.aldar.cramello.App.KEY_ADDRESS_DATA;
import static net.aldar.cramello.App.mMontserratBold;
import static net.aldar.cramello.App.mMontserratRegular;

public class AddAddressFragment extends RootFragment implements View.OnClickListener {

    private boolean EDIT = false;
    private String mFullAddress;
    private String mStreet;
    private String mAdminArea;
    private LatLng mChosenLatLng;

    private TextView mTitleTv;
    private LinearLayout mBackLayout;
    private ImageView mBackIv;

    private SupportMapFragment mSupportMapFragment;
    private View mMapView;

    private TextView mHintTv;

    private TextView mAreasTv;
    private List<Governorate> mGovernorateList;
    private Area mSelectedArea;

    private AppCompatSpinner mAddressSpinner;
    private List<AddressType> mAddressTypeList;
    private AddressType mSelectedAddressType;

    private TextInputLayout mBlockInputLayout;
    private AppCompatEditText mBlockInput;

    private TextInputLayout mStreetInputLayout;
    private AppCompatEditText mStreetInput;

    private TextInputLayout mAvenueInputLayout;
    private AppCompatEditText mAvenueInput;

    private TextInputLayout mBuildingInputLayout;
    private AppCompatEditText mBuildingInput;

    private TextInputLayout mFloorInputLayout;
    private AppCompatEditText mFloorInput;

    private TextInputLayout mApartmentInputLayout;
    private AppCompatEditText mApartmentInput;

    private TextInputLayout mExtraInputLayout;
    private AppCompatEditText mExtraInput;

    private Button mSaveBtn;
    private FrameLayout mRootLayout;

    private GPSTracker mGpsTracker;

    private MainActivity mMainActivity;
    private AddressesFragment mAddressesFragment;
    private Address mAddress;

    private GoogleMap mDialogGoogleMap;
    private GoogleMap mScreenGoogleMap;

    private LatLng mKwLocation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
        mMainActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mKwLocation = new LatLng(29.359234, 47.985646);
        if (getArguments() != null) {
            String json = getArguments().getString(KEY_ADDRESS_DATA);
            Type type = new TypeToken<Address>() {
            }.getType();
            mAddress = new Gson().fromJson(json, type);
            EDIT = true;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_address, container, false);

        mTitleTv = view.findViewById(R.id.addAddressFragment_title);
        mBackLayout = view.findViewById(R.id.addAddressFragment_backLayout);
        mBackIv = view.findViewById(R.id.addAddressFragment_backIv);

        mHintTv = view.findViewById(R.id.addAddressFragment_hintTv);

        mMapView = view.findViewById(R.id.addAddressFragment_mapView);

        mAreasTv = view.findViewById(R.id.addAddressFragment_areaTv);
        mAddressSpinner = view.findViewById(R.id.addAddressFragment_addressTypeSpinner);

        mBlockInputLayout = view.findViewById(R.id.addAddressFragment_blockInputLayout);
        mBlockInput = view.findViewById(R.id.addAddressFragment_blockInput);

        mStreetInputLayout = view.findViewById(R.id.addAddressFragment_streetInputLayout);
        mStreetInput = view.findViewById(R.id.addAddressFragment_streetInput);

        mAvenueInputLayout = view.findViewById(R.id.addAddressFragment_avenueInputLayout);
        mAvenueInput = view.findViewById(R.id.addAddressFragment_avenueInput);

        mBuildingInputLayout = view.findViewById(R.id.addAddressFragment_buildingInputLayout);
        mBuildingInput = view.findViewById(R.id.addAddressFragment_buildingInput);

        mFloorInputLayout = view.findViewById(R.id.addAddressFragment_floorInputLayout);
        mFloorInput = view.findViewById(R.id.addAddressFragment_floorInput);

        mApartmentInputLayout = view.findViewById(R.id.addAddressFragment_apartmentNoInputLayout);
        mApartmentInput = view.findViewById(R.id.addAddressFragment_apartmentNoInput);

        mExtraInputLayout = view.findViewById(R.id.addAddressFragment_extraInputLayout);
        mExtraInput = view.findViewById(R.id.addAddressFragment_extraNoInput);

        mRootLayout = view.findViewById(R.id.root_layout);
        mSaveBtn = view.findViewById(R.id.addAddressFragment_saveBtn);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAddressesFragment = (AddressesFragment) getParentFragment();
//        Utils.setupHideKeyboard(mMainActivity, mRootLayout);

        mTitleTv.setTypeface(mMontserratRegular);

        mMapView.setOnClickListener(this);
        mBackLayout.setOnClickListener(this);
        Utils.submitRotation(mBackIv, mMainActivity.mPrefsManger);

        mHintTv.setTypeface(mMontserratBold);

        mAreasTv.setTypeface(mMontserratRegular);
        if (mMainActivity.mAppLanguage.contains("ar"))
            mAreasTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_left, 0, 0, 0);
        else
            mAreasTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right, 0);
        mAreasTv.setOnClickListener(this);

        mBlockInput.setTypeface(mMontserratRegular);
        mBlockInputLayout.setHint(getResources().getString(R.string.block).concat(" *"));

        mStreetInput.setTypeface(mMontserratRegular);
        mStreetInputLayout.setHint(getResources().getString(R.string.street).concat(" *"));

        mAvenueInput.setTypeface(mMontserratRegular);
        mBuildingInput.setTypeface(mMontserratRegular);
        mFloorInput.setTypeface(mMontserratRegular);
        mApartmentInput.setTypeface(mMontserratRegular);

        mSaveBtn.setTypeface(mMontserratRegular);
        mSaveBtn.setOnClickListener(this);

        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.addAddressFragment_map);
        initializeMap();

        mGovernorateList = mMainActivity.mGovernorateList;

        Collections.sort(mGovernorateList, new Comparator<Governorate>() {
            @Override
            public int compare(Governorate o1, Governorate o2) {
                return o1.getOrder() - o2.getOrder();
            }
        });

        initializeAddressTypes();

        if (EDIT)
            fillInputs();
    }

    private void initializeMap() {
        mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mScreenGoogleMap = googleMap;
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(mKwLocation)
                        .zoom(10)
                        .build();
                mScreenGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mScreenGoogleMap.getUiSettings().setScrollGesturesEnabled(false);
                mScreenGoogleMap.getUiSettings().setZoomGesturesEnabled(false);
            }
        });
    }

    private void fillInputs() {

        int addressType = mAddress.getAddressType();
        switch (addressType) {
            case 0:
                mAddressSpinner.setSelection(1);
                mSelectedAddressType = mAddressTypeList.get(1);
                break;
            case 1:
                mAddressSpinner.setSelection(0);
                mSelectedAddressType = mAddressTypeList.get(0);
                break;
            case 2:
                mAddressSpinner.setSelection(2);
                mSelectedAddressType = mAddressTypeList.get(2);
                break;
        }

        if (mAddress.getSelectedArea() != null) {
            String areaName;
            if (mMainActivity.mAppLanguage.contains("ar"))
                areaName = mAddress.getSelectedArea().getNameAr();
            else
                areaName = mAddress.getSelectedArea().getNameEn();

            mAreasTv.setText(areaName);
            mSelectedArea = mAddress.getSelectedArea();
        }

        if (mAddress.getBlock() != null)
            mBlockInput.setText(mAddress.getBlock());

        if (mAddress.getStreet() != null)
            mStreetInput.setText(mAddress.getStreet());

        if (mAddress.getAvenue() != null)
            mAvenueInput.setText(mAddress.getAvenue());

        if (mAddress.getBuilding() != null)
            mBuildingInput.setText(mAddress.getBuilding());

        if (mAddress.getFloor() != null)
            mFloorInput.setText(mAddress.getFloor());

        if (mAddress.getApartment() != null)
            mApartmentInput.setText(String.valueOf(mAddress.getApartment()));

        if (mAddress.getAdditionalNotes() != null)
            mExtraInput.setText(mAddress.getAdditionalNotes());

    }

    private void initializeAddressTypes() {
        mAddressTypeList = new ArrayList<>();
        mAddressTypeList.add(new AddressType(1, getResources().getString(R.string.house)));
        mAddressTypeList.add(new AddressType(0, getResources().getString(R.string.apartment)));
        mAddressTypeList.add(new AddressType(2, getResources().getString(R.string.office)));

        ArrayAdapter<AddressType> brandAdapter = new ArrayAdapter<AddressType>(mMainActivity,
                R.layout.view_spinner_item, mAddressTypeList) {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTypeface(mMontserratRegular);

                int dimen = (int) (getResources().getDimension(R.dimen.default_5x_space)
                        / getResources().getDisplayMetrics().density);

                tv.setPadding(dimen, dimen, dimen, dimen);
                return view;
            }
        };

        mAddressSpinner.setAdapter(brandAdapter);
        mAddressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedAddressType = mAddressTypeList.get(position);
                switch (position) {
                    case 0:
                        mApartmentInputLayout.setHint(getResources().getString(R.string.homeNumber).concat(" *"));
                        mBuildingInputLayout.setVisibility(View.GONE);
                        mFloorInputLayout.setVisibility(View.GONE);
                        break;
                    case 1:
                        mApartmentInputLayout.setHint(getResources().getString(R.string.apartmentNumber).concat(" *"));
                        mBuildingInputLayout.setVisibility(View.VISIBLE);
                        mBuildingInputLayout.setHint(getResources().getString(R.string.building).concat(" *"));
                        mFloorInputLayout.setVisibility(View.VISIBLE);
                        mFloorInputLayout.setHint(getResources().getString(R.string.floor).concat(" *"));
                        break;
                    case 2:
                        mApartmentInputLayout.setHint(getResources().getString(R.string.officeNumber).concat(" *"));
                        mBuildingInputLayout.setVisibility(View.VISIBLE);
                        mBuildingInputLayout.setHint(getResources().getString(R.string.building).concat(" *"));
                        mFloorInputLayout.setVisibility(View.VISIBLE);
                        mFloorInputLayout.setHint(getResources().getString(R.string.floor).concat(" *"));
                        break;
                }
                mApartmentInput.setError(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mAddressSpinner.setSelection(0);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addAddressFragment_backLayout:
                destroy();
                break;
            case R.id.addAddressFragment_mapView:

                mGpsTracker = new GPSTracker(mMainActivity);

                if (!mGpsTracker.canGetLocation()) {
                    mGpsTracker.showSettingsAlert();
                    return;
                }

                if (mGpsTracker.getLatitude() == 0 || mGpsTracker.getLongitude() == 0) {
                    Utils.makeAToast(mMainActivity, getResources().getString(R.string.gps_result_zero));
                    return;
                }
                showMapDialog(mGpsTracker.getLatitude(), mGpsTracker.getLongitude());
                break;
            case R.id.addAddressFragment_areaTv:
                showAreaDialog();
                break;
            case R.id.addAddressFragment_saveBtn:
                submitValidation();
                break;
        }
    }


    private void submitValidation() {

        boolean isThereError = false;
        if (mSelectedArea == null) {
            isThereError = true;
            mAreasTv.setError(getResources().getString(R.string.areaValid));
        }
        if (Utils.isInputEmpty(mBlockInput)) {
            isThereError = true;
            mBlockInput.setError(getResources().getString(R.string.blockValid));
        }
        if (Utils.isInputEmpty(mStreetInput)) {
            isThereError = true;
            mStreetInput.setError(getResources().getString(R.string.streetValid));
        }
        if (mSelectedAddressType.getId() != 1) {
            if (Utils.isInputEmpty(mBuildingInput)) {
                isThereError = true;
                mBuildingInput.setError(getResources().getString(R.string.buildingValid));
            }
            if (Utils.isInputEmpty(mFloorInput)) {
                isThereError = true;
                mFloorInput.setError(getResources().getString(R.string.floorValid));
            }
        }
        if (Utils.isInputEmpty(mApartmentInput)) {
            isThereError = true;

            String errorMsg = null;
            switch (mSelectedAddressType.getId()) {
                case 0:
                    errorMsg = getResources().getString(R.string.homeNumberValid);
                    break;
                case 1:
                    errorMsg = getResources().getString(R.string.apartmentNumberValid);
                    break;
                case 2:
                    errorMsg = getResources().getString(R.string.officeNumberValid);
                    break;
            }

            mApartmentInput.setError(errorMsg);
        }

        if (!isThereError)
            sendRequest();
    }

    private void sendRequest() {
        if (Utils.isConnectionOn(mMainActivity)) {
            mMainActivity.mSpinKitLayout.setVisibility(View.VISIBLE);

            final String successMsg;
            final String errorMsg;

            if (EDIT) {
                successMsg = getResources().getString(R.string.addressUpdatedSuccess);
                errorMsg = getResources().getString(R.string.addressUpdatedFailed);
            } else {
                successMsg = getResources().getString(R.string.addressCreatedSuccess);
                errorMsg = getResources().getString(R.string.addressCreatedFailed);
            }

            Address address = new Address(mSelectedAddressType.getName(),
                    mMainActivity.mUserId, mSelectedAddressType.getId(),
                    mBlockInput.getText().toString().trim(), mStreetInput.getText().toString().trim(),
                    mSelectedArea.getId(), mAvenueInput.getText().toString().trim(),
                    mFloorInput.getText().toString().trim(), mBuildingInput.getText().toString().trim(),
                    mApartmentInput.getText().toString(), mExtraInput.getText().toString().trim());

            Call<Address> call;

            if (EDIT)
                call = mMainActivity.mServiceApi.editAddress(mMainActivity.mAuthToken, mAddress.getId(), address);
            else
                call = mMainActivity.mServiceApi.createNewAddress(mMainActivity.mAuthToken, address);

            call.enqueue(new Callback<Address>() {
                @Override
                public void onResponse(Call<Address> call, Response<Address> response) {
                    mMainActivity.mSpinKitLayout.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            Utils.makeAToast(mMainActivity, successMsg);
                            destroy();

                        } catch (Exception e) {
                            Log.e("A-EAddress Ex / ", e.getMessage() + "");
                        }
                    } else {
                        Log.e("A-EAddress Code / ", "Code not Successful");
                        Utils.makeAToast(mMainActivity, errorMsg);
                    }
                }

                @Override
                public void onFailure(Call<Address> call, Throwable t) {
                    mMainActivity.mSpinKitLayout.setVisibility(View.GONE);
                    Log.e("A-EAddress Fail / ", t.getMessage() + "");
                    Utils.makeAToast(mMainActivity, errorMsg);
                }
            });
        } else
            Utils.makeAToast(mMainActivity, getResources().getString(R.string.connection_offline));
    }

    public void showAreaDialog() {
        final Dialog dialog = new Dialog(mMainActivity, android.R.style.Theme_Black_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialog.setContentView(R.layout.dialog_area);

        TextView cancelTv = dialog.findViewById(R.id.dialogArea_cancelTv);
        cancelTv.setTypeface(mMontserratBold);

        LinearLayout backLayout = dialog.findViewById(R.id.dialogArea_backLayout);
        ImageView backIv = dialog.findViewById(R.id.dialogArea_backIv);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Utils.submitRotation(backIv, mMainActivity.mPrefsManger);

//        TextView title = dialog.findViewById(R.id.dialogArea_titleTv);
//        title.setTypeface(mMontserratRegular);

        final ExpandableListView expandableListView = dialog.findViewById(R.id.dialogArea_expandableListView);
        final GovernExpListAdapter governExpListAdapter = new GovernExpListAdapter(mMainActivity,
                mMainActivity.mGovernorateList, expandableListView);
        expandableListView.setAdapter(governExpListAdapter);
        expandableListView.setGroupIndicator(null);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Governorate governorate = governExpListAdapter.mFilteredGovernorateList.get(groupPosition);
                Area area = governorate.getAreas().get(childPosition);
                updateArea(area);
                dialog.dismiss();
                return false;
            }
        });
//        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                return true;
//            }
//        });

        int count = governExpListAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            expandableListView.expandGroup(i);
        }
        expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Utils.hideKeyboardFrom(mMainActivity, expandableListView);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        final EditText searchInput = dialog.findViewById(R.id.dialogArea_searchInput);
        searchInput.setTypeface(mMontserratRegular);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                governExpListAdapter.filterData(s.toString().trim());
            }
        });

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchInput.getText().toString().trim().isEmpty())
                    searchInput.getText().clear();
            }
        });

        dialog.show();
    }

    public void showMapDialog(final double lat, final double lng) {
        final Dialog dialog = new Dialog(mMainActivity, android.R.style.Theme_Black_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_map);

        LinearLayout backLayout = dialog.findViewById(R.id.dialogMap_backLayout);
        ImageView backIv = dialog.findViewById(R.id.dialogMap_backIv);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Utils.submitRotation(backIv, mMainActivity.mPrefsManger);

        TextView title = dialog.findViewById(R.id.dialogMap_titleTv);
        title.setTypeface(mMontserratRegular);

        TextView hint = dialog.findViewById(R.id.dialogMap_hintTv);
        hint.setTypeface(mMontserratRegular);

        Button confirmBtn = dialog.findViewById(R.id.dialogMap_confirmBtn);
        confirmBtn.setTypeface(mMontserratRegular);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog(dialog);
            }
        });

        ImageView getLocationIv = dialog.findViewById(R.id.dialogMap_getLocationIv);
        getLocationIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng currentLocation = new LatLng(lat, lng);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(currentLocation)
                        .zoom(17)
                        .build();
                mDialogGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) mMainActivity
                .getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mDialogGoogleMap = googleMap;
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(mKwLocation)
                        .zoom(10)
                        .build();
                mDialogGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                mDialogGoogleMap.setMyLocationEnabled(false);

                mDialogGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                // Showing / hiding your current location
                mDialogGoogleMap.setMyLocationEnabled(false);
                // Enable / Disable zooming controls
                mDialogGoogleMap.getUiSettings().setZoomControlsEnabled(false);
                // Enable / Disable my location button
                mDialogGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                // Enable / Disable Compass icon
                mDialogGoogleMap.getUiSettings().setCompassEnabled(true);
                // Enable / Disable Rotate gesture`enter code here`
                mDialogGoogleMap.getUiSettings().setRotateGesturesEnabled(true);
                // Enable / Disable zooming functionality
                mDialogGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
                mDialogGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        mChosenLatLng = cameraPosition.target;
                        Geocoder geocoder = new Geocoder(mMainActivity, Locale.getDefault());
                        try {
                            List<android.location.Address> addresses;
                            try {
                                addresses = geocoder.getFromLocation(mChosenLatLng.latitude, mChosenLatLng.longitude, 1);
                                mFullAddress = addresses.get(0).getAddressLine(0);
                                mAdminArea = addresses.get(0).getAdminArea();
                                mStreet = addresses.get(0).getThoroughfare();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Fragment mFragment = mMainActivity.getSupportFragmentManager().findFragmentById(R.id.map);
                FragmentTransaction mFragmentTransaction = mMainActivity.getSupportFragmentManager().beginTransaction();
                mFragmentTransaction.remove(mFragment);
                mFragmentTransaction.commit();
            }
        });

        dialog.show();
    }

    private void showConfirmationDialog(final Dialog firstDialog) {
        final Dialog dialog = new Dialog(mMainActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.setCancelable(false);

        TextView title = dialog.findViewById(R.id.confirmation_dialog_head_title_tv);
        title.setTypeface(mMontserratRegular);
        title.setText(getResources().getString(R.string.confirmLocationTitle));

        TextView msg = dialog.findViewById(R.id.confirmation_dialog_msg_tv);
        msg.setTypeface(mMontserratRegular);
        msg.setText(getResources().getString(R.string.confirmLocationMsg));

        Button posBtn = dialog.findViewById(R.id.confirmation_dialog_positive_btn);
        posBtn.setTypeface(mMontserratRegular);
        posBtn.setText(getResources().getString(R.string.ok));
        posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAddressInfoFromMap();
                dialog.dismiss();
                firstDialog.dismiss();
            }
        });

        Button negBtn = dialog.findViewById(R.id.confirmation_dialog_negative_btn);
        negBtn.setTypeface(mMontserratRegular);
        negBtn.setText(getResources().getString(R.string.cancel));
        negBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void destroy() {
        mMainActivity.onBackPressed();
        mAddressesFragment.getAddresses();
    }

    public void updateAddressInfoFromMap() {
        if (mChosenLatLng != null) {
            LatLng location = new LatLng(mChosenLatLng.latitude, mChosenLatLng.longitude);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(location)
                    .zoom(15)
                    .build();
            mScreenGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mScreenGoogleMap.addMarker(new MarkerOptions().position(location));
        }
//            Utils.loadImage(Utils.getStaticMap(String.valueOf(mChosenLatLng.latitude),
//                    String.valueOf(mChosenLatLng.longitude),
//                    getResources().getString(R.string.google_maps_Api_Key)),
//                    null, mMapIv, R.drawable.map_place_holder);

        if (mStreet != null) {
            mStreetInput.setText(mStreet);
            mStreetInput.setError(null);
        }
    }

    private void updateArea(Area area) {
        mSelectedArea = area;

        String name;
        if (mMainActivity.mPrefsManger.getAppLanguage().contains("ar"))
            name = mSelectedArea.getNameAr();
        else
            name = mSelectedArea.getNameEn();


        mAreasTv.setText(name);
        mAreasTv.setError(null);
    }
}
