package net.aldar.cramello.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import net.aldar.cramello.MainActivity;
import net.aldar.cramello.R;
import net.aldar.cramello.adapter.AddressesRvAdapter;
import net.aldar.cramello.model.Address;
import net.aldar.cramello.model.response.governorate.Area;
import net.aldar.cramello.model.response.governorate.Governorate;
import net.aldar.cramello.model.response.product.Product;
import net.aldar.cramello.services.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.aldar.cramello.App.KEY_ADDRESS_DATA;
import static net.aldar.cramello.App.mMontserratRegular;
import static net.aldar.cramello.MainActivity.ADD_ADDRESS_FRAGMENT_TAG;
import static net.aldar.cramello.MainActivity.MENU_FRAGMENT_TAG;

public class AddressesFragment extends RootFragment implements View.OnClickListener {

    private TextView mTitleTv;
    private LinearLayout mMenuLayout;
    private ImageView mMenuIv;
    private Button mAddBtn;

    private LinearLayout mEmptyLayout;
    private TextView mEmptyTv;
    private Button mEmptyAddBtn;

    private RecyclerView mAddressesRv;
    private AddressesRvAdapter mAddressesRvAdapter;
    private List<Address> mAddressesList;

    public MainActivity mMainActivity;
    private Gson mGson;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
        mGson = new Gson();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addresses, container, false);

        mTitleTv = view.findViewById(R.id.addressesFragment_title);
        mMenuLayout = view.findViewById(R.id.addressesFragment_menuLayout);
        mMenuIv = view.findViewById(R.id.addressesFragment_menuIv);
        mAddBtn = view.findViewById(R.id.addressesFragment_addBtn);

        mEmptyLayout = view.findViewById(R.id.addressesFragment_emptyLayout);
        mEmptyTv = view.findViewById(R.id.addressesFragment_emptyTv);
        mEmptyAddBtn = view.findViewById(R.id.addressesFragment_emptyAddBtn);

        mAddressesRv = view.findViewById(R.id.addressesFragmentRv);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mTitleTv.setTypeface(mMontserratRegular);
        mMenuLayout.setOnClickListener(this);
        Utils.submitFlip(mMenuIv, mMainActivity.mPrefsManger);

        mAddBtn.setTypeface(mMontserratRegular);
        mAddBtn.setOnClickListener(this);

        mEmptyLayout.setOnClickListener(this);
        mEmptyLayout.setVisibility(View.GONE);
        mEmptyTv.setTypeface(mMontserratRegular);
        mEmptyAddBtn.setTypeface(mMontserratRegular);
        mEmptyAddBtn.setOnClickListener(this);

        getAddresses();
    }

    public void getAddresses() {
        if (Utils.isConnectionOn(mMainActivity)) {
            mMainActivity.mSpinKitLayout.setVisibility(View.VISIBLE);

            Call<List<Address>> call = mMainActivity.mServiceApi.getAddresses(mMainActivity.mAuthToken);

            call.enqueue(new Callback<List<Address>>() {
                @Override
                public void onResponse(Call<List<Address>> call, Response<List<Address>> response) {
                    mMainActivity.mSpinKitLayout.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            mAddressesList = response.body();
                            setupAddressesRv();
                        } catch (Exception e) {
                            Log.e("GAddress Ex / ", e.getMessage() + "");
                        }
                    } else {
                        Log.e("GAddress Code / ", "Code not Successful");
                        Utils.makeAToast(mMainActivity, getResources().getString(R.string.addressRequestFailed));
                    }
                }

                @Override
                public void onFailure(Call<List<Address>> call, Throwable t) {
                    mMainActivity.mSpinKitLayout.setVisibility(View.GONE);
                    Log.e("GAddress Fail / ", t.getMessage() + "");
                    Utils.makeAToast(mMainActivity, getResources().getString(R.string.addressRequestFailed));
                }
            });
        } else
            Utils.makeAToast(mMainActivity, getResources().getString(R.string.connection_offline));
    }

    private void setupAddressesRv() {

        Collections.sort(mAddressesList, new Comparator<Address>() {
            @Override
            public int compare(Address o1, Address o2) {
                return o2.getId() - o1.getId();
            }
        });

        for (int a = 0; a < mAddressesList.size(); a++) {
            Address address = mAddressesList.get(a);
            int addressArea = address.getArea();
            for (Governorate governorate : mMainActivity.mGovernorateList) {
                for (int p = 0; p < governorate.getAreas().size(); p++) {
                    Area area = governorate.getAreas().get(p);
                    int areaId = area.getId();
                    if (addressArea == areaId) {
                        address.setSelectedArea(area);
                        break;
                    }
                }
            }
        }

        mAddressesRv.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        mAddressesRvAdapter = new AddressesRvAdapter(getActivity(), this, mAddressesList);
        mAddressesRv.setAdapter(mAddressesRvAdapter);
        mAddressesRv.addItemDecoration(new DividerItemDecoration(mAddressesRv.getContext(),
                LinearLayoutManager.VERTICAL));

        if (mAddressesList == null || mAddressesList.isEmpty()) {
            mEmptyLayout.setVisibility(View.VISIBLE);
            mAddressesRv.setVisibility(View.GONE);
        } else {
            mEmptyLayout.setVisibility(View.GONE);
            mAddressesRv.setVisibility(View.VISIBLE);
        }
    }

    public void recycleItemClicked(int position) {
        Area storedDeliveryArea = mMainActivity.mPrefsManger.getDeliveryArea();
        Area selectedArea = mAddressesList.get(position).getSelectedArea();

        checkCart(selectedArea, position);

//        if (storedDeliveryArea != null) {
//            if (mMainActivity.mPrefsManger.loadCart().size() != 0) {
//                int storedDeliveryAreaId = storedDeliveryArea.getId();
//                int selectedAreaId = selectedArea.getId();
//                if (storedDeliveryAreaId != selectedAreaId)
//                    showDeliveryConfirmationDialog(storedDeliveryArea, position);
//                else
//                    checkCart(selectedArea, position);
//            } else {
//                checkCart(selectedArea, position);
//            }
//        } else {
//            checkCart(selectedArea, position);
//        }
    }

    private void showDeliveryConfirmationDialog(Area storedDeliveryArea, final int position) {
        final Dialog dialog = new Dialog(mMainActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.setCancelable(false);

        TextView title = dialog.findViewById(R.id.confirmation_dialog_head_title_tv);
        title.setTypeface(mMontserratRegular);
        title.setText(getResources().getString(R.string.clearCartTitle));

        String msgTxt = getResources().getString(R.string.clearCartMsg);
        if (mMainActivity.mAppLanguage.contains("ar"))
            msgTxt = msgTxt.concat(" ")
                    .concat(getResources().getString(R.string.store))
                    .concat(" ")
                    .concat(storedDeliveryArea.getNameAr())
                    .concat(" ")
                    .concat(getResources().getString(R.string.clearCartMsg2));
        else
            msgTxt = msgTxt.concat(" ")
                    .concat(storedDeliveryArea.getNameEn())
                    .concat(" ")
                    .concat(getResources().getString(R.string.store))
                    .concat(" ")
                    .concat(getResources().getString(R.string.clearCartMsg2));


        TextView msg = dialog.findViewById(R.id.confirmation_dialog_msg_tv);
        msg.setTypeface(mMontserratRegular);
        msg.setText(msgTxt);

        Button posBtn = dialog.findViewById(R.id.confirmation_dialog_positive_btn);
        posBtn.setTypeface(mMontserratRegular);
        posBtn.setText(getResources().getString(R.string.newOrder));
        posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainActivity.mPrefsManger.saveCart(new ArrayList<Product>());
                openMenuFragment(position);
                dialog.dismiss();
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

    public void editItemClicked(int position) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ADDRESS_DATA, mGson.toJson(mAddressesList.get(position)));
        openAddAddressFragment(bundle);
    }

    public void deleteItemClicked(final int position) {
        final Dialog dialog = new Dialog(mMainActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.setCancelable(false);

        TextView title = dialog.findViewById(R.id.confirmation_dialog_head_title_tv);
        title.setTypeface(mMontserratRegular);
        title.setText(getResources().getString(R.string.deleteConfirm));

        TextView msg = dialog.findViewById(R.id.confirmation_dialog_msg_tv);
        msg.setTypeface(mMontserratRegular);
        msg.setText(getResources().getString(R.string.deleteAddressMsg));

        Button posBtn = dialog.findViewById(R.id.confirmation_dialog_positive_btn);
        posBtn.setTypeface(mMontserratRegular);
        posBtn.setText(getResources().getString(R.string.delete));
        posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                deleteAddress(position);
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

    public void deleteAddress(final int position) {
        if (Utils.isConnectionOn(mMainActivity)) {
            mMainActivity.mSpinKitLayout.setVisibility(View.VISIBLE);

            final Address addressD = mAddressesList.get(position);
            Call<Void> call = mMainActivity.mServiceApi.deleteAddress(addressD.getId(), mMainActivity.mAuthToken);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    mMainActivity.mSpinKitLayout.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        Utils.makeAToast(mMainActivity, getResources().getString(R.string.addressDeletedSuccess));
                        mAddressesList.remove(addressD);
                        mAddressesRvAdapter.notifyDataSetChanged();

                        if (mAddressesList.isEmpty())
                            getAddresses();
                    } else {
                        Log.e("DAddress Code / ", "Code not Successful");
                        Utils.makeAToast(mMainActivity, getResources().getString(R.string.addressDeletedFailed));
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    mMainActivity.mSpinKitLayout.setVisibility(View.GONE);
                    Log.e("DAddress Fail / ", t.getMessage() + "");
                    Utils.makeAToast(mMainActivity, getResources().getString(R.string.addressDeletedFailed));
                }
            });
        } else
            Utils.makeAToast(mMainActivity, getResources().getString(R.string.connection_offline));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addressesFragment_menuLayout:
                mMainActivity.openDrawer();
                break;
            case R.id.addressesFragment_addBtn:
            case R.id.addressesFragment_emptyAddBtn:
                openAddAddressFragment(null);
                break;
        }
    }

    private void openAddAddressFragment(Bundle bundle) {
        Utils.openChildFragment(AddressesFragment.this, new AddAddressFragment(),
                bundle, R.id.addressesFragment_containerLayout, ADD_ADDRESS_FRAGMENT_TAG);
    }

    private void openMenuFragment(int position) {
        Address address = mAddressesList.get(position);
        mMainActivity.mPrefsManger.setDeliveryArea(address.getSelectedArea());

        Bundle bundle = new Bundle();
        bundle.putString(KEY_ADDRESS_DATA, mGson.toJson(address));
        Utils.openChildFragment(AddressesFragment.this, new MenuFragment(), bundle,
                R.id.addressesFragment_containerLayout, MENU_FRAGMENT_TAG);
    }

    private void checkCart(Area selectedArea, int position) {
        List<Product> cart = mMainActivity.mPrefsManger.loadCart();

        List<String> names = new ArrayList<>();
        List<Product> misMatchesProductList = new ArrayList<>();
        boolean misMatchWithArea = false;
        for (int c = 0; c < cart.size(); c++) {
            Product product = cart.get(c);
            boolean areaFoundInProductBranches = false;

            try {
                branchesCombination:
                {
                    if (product.getBranches() != null && !product.getBranches().isEmpty()) {
                        for (Integer productBranch : product.getBranches()) {
                            for (Integer areaBranch : selectedArea.getBranches()) {
                                int productBranchId = productBranch;
                                int areaBranchId = areaBranch;
                                if (productBranchId == areaBranchId) {
                                    areaFoundInProductBranches = true;
                                    break branchesCombination;
                                }
                            }
                        }
                    }
                }

                if (!areaFoundInProductBranches) {
                    misMatchesProductList.add(product);
                    misMatchWithArea = true;
                    String name;
                    if (mMainActivity.mAppLanguage.contains("ar"))
                        name = product.getNameAr();
                    else
                        name = product.getNameEn();

                    names.add(name);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (misMatchWithArea) {
            showDeleteItemsFromCartDialog(misMatchesProductList, names, selectedArea, position);
        } else {
            openMenuFragment(position);
        }
    }

    private void showDeleteItemsFromCartDialog(final List<Product> misMatchesProductList, List<String> names, Area selectedArea, final int position) {
        final Dialog dialog = new Dialog(mMainActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.setCancelable(false);

        TextView title = dialog.findViewById(R.id.confirmation_dialog_head_title_tv);
        title.setTypeface(mMontserratRegular);
        title.setText(getResources().getString(R.string.misMatchCartTitle));

        String productsTitle = "";
        for (int n = 0; n < names.size(); n++) {
            if (productsTitle.isEmpty())
                productsTitle = names.get(n);
            else {
                if (mMainActivity.mAppLanguage.contains("en")) {
                    if (n == names.size() - 1)
                        productsTitle = productsTitle.concat(" ")
                                .concat(getResources().getString(R.string.and)).concat(" ").concat(names.get(n));
                    else
                        productsTitle = productsTitle.concat(", ").concat(names.get(n));
                } else {
                    productsTitle = productsTitle.concat(" ")
                            .concat(getResources().getString(R.string.and)).concat(" ").concat(names.get(n));
                }
            }
        }

        String msgTxt = productsTitle;
        if (mMainActivity.mAppLanguage.contains("ar"))
            msgTxt = msgTxt.concat(" ")
                    .concat(getResources().getString(R.string.misMatchCartMsg1))
                    .concat(" ")
                    .concat(selectedArea.getNameAr())
                    .concat(" , ")
                    .concat(getResources().getString(R.string.misMatchCartMsg2));
        else
            msgTxt = msgTxt.concat(" ")
                    .concat(getResources().getString(R.string.misMatchCartMsg1))
                    .concat(" ")
                    .concat(selectedArea.getNameEn())
                    .concat(" , ")
                    .concat(getResources().getString(R.string.misMatchCartMsg2));


        TextView msg = dialog.findViewById(R.id.confirmation_dialog_msg_tv);
        msg.setTypeface(mMontserratRegular);
        msg.setText(msgTxt);

        Button posBtn = dialog.findViewById(R.id.confirmation_dialog_positive_btn);
        posBtn.setTypeface(mMontserratRegular);
        posBtn.setText(getResources().getString(R.string.continueTitle));
        posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Product> cartList = mMainActivity.mPrefsManger.loadCart();
                for (int c = 0; c < misMatchesProductList.size(); c++) {
                    Product misMatchProduct = misMatchesProductList.get(c);
                    int misMatchProductId = misMatchProduct.getId();
                    for (int l = 0; l < cartList.size(); l++) {
                        Product cartProduct = cartList.get(l);
                        int cartProductId = cartProduct.getId();
                        if (misMatchProductId == cartProductId)
                            cartList.remove(l);
                    }
                }
                mMainActivity.mPrefsManger.saveCart(cartList);
                openMenuFragment(position);
                dialog.dismiss();
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
}
