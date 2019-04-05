package net.aldar.cramello.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.aldar.cramello.CartActivity;
import net.aldar.cramello.R;
import net.aldar.cramello.adapter.CheckoutRvAdapter;
import net.aldar.cramello.model.Address;
import net.aldar.cramello.model.Basket;
import net.aldar.cramello.model.request.CheckoutRequest;
import net.aldar.cramello.model.request.VoucherRequest;
import net.aldar.cramello.model.response.CheckoutResponse;
import net.aldar.cramello.model.response.UserData;
import net.aldar.cramello.model.response.VoucherResponse;
import net.aldar.cramello.model.response.basket.BasketLine;
import net.aldar.cramello.services.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.aldar.cramello.App.CASH;
import static net.aldar.cramello.App.KEY_ORDER_NAME;
import static net.aldar.cramello.App.KEY_PAYMENT_METHOD;
import static net.aldar.cramello.App.KEY_PAYMENT_URL;
import static net.aldar.cramello.App.KNET;
import static net.aldar.cramello.App.VISA;
import static net.aldar.cramello.App.mMontserratBold;
import static net.aldar.cramello.App.mMontserratLight;
import static net.aldar.cramello.App.mMontserratRegular;
import static net.aldar.cramello.App.mRobotoRegular;
import static net.aldar.cramello.CartActivity.CONG_FRAGMENT_TAG;
import static net.aldar.cramello.CartActivity.PAYMENT_FRAGMENT_TAG;

public class CheckoutFragment extends RootFragment implements View.OnClickListener {
    private int mSelectedPaymentMethod;

    private TextView mTitleTv;
    private LinearLayout mBackLayout;
    private ImageView mBackIv;

    private TextView mDeliveryAddressTitle;
    private View mAddressView;

    private TextView mItemsTitle;
    private RecyclerView mItemsRv;
    private CheckoutRvAdapter mCheckoutRvAdapter;

    private TextView mVoucherTitle;
    private EditText mVoucherInput;
    private TextView mRedeemBtn;
    private ImageView mVoucherSubmittedIv;

    private TextView mPayWithTitle;
    private LinearLayout mCashLayout;
    private TextView mCashTv;
    private LinearLayout mKnetLayout;
    private TextView mKnetTv;
    private LinearLayout mVisaLayout;
    private TextView mVisaTv;
    private ImageView mVisaIv;

    private TextView mSubTotalTitle;
    private TextView mSubTotalTv;

    private TextView mDeliveryTitle;
    private TextView mDeliveryTv;

    private LinearLayout mDiscountLayout;
    private TextView mDiscountTitle;
    private TextView mDiscountTv;

    private TextView mTotalAmountTitle;
    private TextView mTotalAmountTv;

    private TextView mDeliveryTimeTitle;
    private TextView mDeliveryTimeTv;

    private Button mPlaceOrderBtn;
    private FrameLayout mRootLayout;

    private Basket mBasket;
    private Address mSelectedAddress;
    public CartActivity mCartActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCartActivity = (CartActivity) getActivity();
        mBasket = mCartActivity.mBasket;
        mSelectedAddress = mCartActivity.mAddress;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);

        mTitleTv = view.findViewById(R.id.checkoutFragment_title);
        mBackLayout = view.findViewById(R.id.checkoutFragment_backLayout);
        mBackIv = view.findViewById(R.id.checkoutFragment_backIv);

        mDeliveryAddressTitle = view.findViewById(R.id.checkoutFragment_deliveryAddressTitle);
        mAddressView = view.findViewById(R.id.checkoutFragment_deliveryAddressView);

        mItemsTitle = view.findViewById(R.id.checkoutFragment_itemsTitle);
        mItemsRv = view.findViewById(R.id.checkoutFragment_itemsRv);

        mVoucherTitle = view.findViewById(R.id.checkoutFragment_voucherTitle);
        mVoucherInput = view.findViewById(R.id.checkoutFragment_voucherCodeInput);
        mRedeemBtn = view.findViewById(R.id.checkoutFragment_redeemBtn);
        mVoucherSubmittedIv = view.findViewById(R.id.checkoutFragment_voucherDone);
        mVoucherSubmittedIv.setVisibility(View.GONE);

        mPayWithTitle = view.findViewById(R.id.checkoutFragment_payWithTitle);
        mCashLayout = view.findViewById(R.id.checkoutFragment_cashLayout);
        mCashTv = view.findViewById(R.id.checkoutFragment_cashTv);
        mKnetLayout = view.findViewById(R.id.checkoutFragment_knetLayout);
        mKnetTv = view.findViewById(R.id.checkoutFragment_knetTv);
        mVisaLayout = view.findViewById(R.id.checkoutFragment_visaLayout);
        mVisaTv = view.findViewById(R.id.checkoutFragment_visaTv);
        mVisaIv = view.findViewById(R.id.checkoutFragment_visaIv);

        mSubTotalTitle = view.findViewById(R.id.checkoutFragment_subTotalTitle);
        mSubTotalTv = view.findViewById(R.id.checkoutFragment_subTotalTv);

        mDeliveryTitle = view.findViewById(R.id.checkoutFragment_deliveryTitle);
        mDeliveryTv = view.findViewById(R.id.checkoutFragment_deliveryTv);

        mDiscountLayout = view.findViewById(R.id.checkoutFragment_discountLayout);
        mDiscountTitle = view.findViewById(R.id.checkoutFragment_discountTitle);
        mDiscountTv = view.findViewById(R.id.checkoutFragment_discountTv);

        mTotalAmountTitle = view.findViewById(R.id.checkoutFragment_totalAmountTitle);
        mTotalAmountTv = view.findViewById(R.id.checkoutFragment_totalAmountTv);

        mDeliveryTimeTitle = view.findViewById(R.id.checkoutFragment_deliveryTimeTitle);
        mDeliveryTimeTv = view.findViewById(R.id.checkoutFragment_deliveryTimeTv);

        mPlaceOrderBtn = view.findViewById(R.id.checkoutFragment_placeOrderBtn);
        mRootLayout = view.findViewById(R.id.checkoutFragment_containerLayout);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Utils.setupHideKeyboard(mCartActivity, mRootLayout);

        mTitleTv.setTypeface(mMontserratRegular);
        mBackLayout.setOnClickListener(this);
        Utils.submitRotation(mBackIv, mCartActivity.mPrefsManger);

        mDeliveryAddressTitle.setTypeface(mMontserratBold);
        fillAddressView();

        mItemsTitle.setTypeface(mMontserratBold);
        setupItemsRv();

        mVoucherTitle.setTypeface(mMontserratBold);
        mVoucherInput.setTypeface(mRobotoRegular);
        mVoucherInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mRedeemBtn.setVisibility(View.VISIBLE);
                mVoucherSubmittedIv.setVisibility(View.GONE);
            }
        });
        mRedeemBtn.setTypeface(mMontserratRegular);
        mRedeemBtn.setOnClickListener(this);

        mPayWithTitle.setTypeface(mMontserratBold);

        mCashTv.setTypeface(mRobotoRegular);
        mCashLayout.setOnClickListener(this);

        mKnetTv.setTypeface(mRobotoRegular);
        mKnetLayout.setOnClickListener(this);

        mVisaTv.setTypeface(mRobotoRegular);
        mVisaLayout.setOnClickListener(this);
//        mVisaTv.setTextColor(ContextCompat.getColor(mCartActivity, R.color.colorNobel));
//        mVisaIv.setColorFilter(ContextCompat.getColor(mCartActivity, R.color.colorNobel), PorterDuff.Mode.SRC_ATOP);

        mSubTotalTitle.setTypeface(mMontserratLight);
        mSubTotalTv.setTypeface(mMontserratLight);

        mDeliveryTitle.setTypeface(mMontserratLight);
        mDeliveryTv.setTypeface(mMontserratLight);

        mDiscountTitle.setTypeface(mMontserratLight);
        mDiscountTv.setTypeface(mMontserratLight);

        if (mBasket.getVoucherCode() != null && !mBasket.getVoucherCode().isEmpty())
            mDiscountLayout.setVisibility(View.VISIBLE);

        mTotalAmountTitle.setTypeface(mMontserratBold);
        mTotalAmountTv.setTypeface(mMontserratBold);

        mDeliveryTimeTitle.setTypeface(mMontserratBold);
        mDeliveryTimeTv.setTypeface(mMontserratBold);

        fillReceiptFields();

        mPlaceOrderBtn.setTypeface(mMontserratRegular);
        mPlaceOrderBtn.setOnClickListener(this);

    }

    private void fillReceiptFields() {
        double total = 0.0;
        double subTotal = 0.0;
        double delivery = mSelectedAddress.getSelectedArea().getFee();

        for (int t = 0; t < mBasket.getLines().size(); t++) {
            BasketLine line = mBasket.getLines().get(t);
            subTotal = subTotal + line.getAmount();
        }

        if (mBasket.getBasketTotalNoDiscount() != null && mBasket.getBasketTotalNoDiscount() != 0)
            if (mBasket.getBasketTotalNoDiscount() != subTotal)
                subTotal = mBasket.getBasketTotalNoDiscount();

        if (mBasket.getVoucherDiscount() != null && mBasket.getVoucherDiscount() != 0) {
            mDiscountTv.setText(getResources().getString(R.string.kd)
                    .concat(" ")
                    .concat(Utils.makeSureThreeNAfterDot(mBasket.getVoucherDiscount())));
            total = subTotal - mBasket.getVoucherDiscount();
        }

        total = total + delivery;

        try {
            String totalInString = String.valueOf(total);
            String beforeInString = totalInString.substring(0, totalInString.indexOf('.'));
            String afterInString = totalInString.substring(totalInString.indexOf('.'), totalInString.length());

            double before = Double.valueOf(beforeInString);
            double after = Double.valueOf(afterInString);

            if (after > 0 && after < 0.25)
                after = 0;
            else if (after > 0.25 && after < 0.50)
                after = 0.25;
            else if (after > 0.50 && after < 0.75)
                after = 0.50;
            else if (after > 0.75)
                after = 0.75;

            total = before + after;

            if (mBasket.getBasketTotal() != null && mBasket.getBasketTotal() != 0)
                if (mBasket.getBasketTotal() != total)
                    total = mBasket.getBasketTotal();

        } catch (Exception e) {
            total = mBasket.getBasketTotal();
        }

        mSubTotalTv.setText(getResources().getString(R.string.kd)
                .concat(" ")
                .concat(Utils.makeSureThreeNAfterDot(subTotal)));

        mDeliveryTv.setText(getResources().getString(R.string.kd)
                .concat(" ")
                .concat(Utils.makeSureThreeNAfterDot(delivery)));

        mTotalAmountTv.setText(getResources().getString(R.string.kd)
                .concat(" ")
                .concat(Utils.makeSureThreeNAfterDot(total)));

        if (mSelectedAddress.getSelectedArea().getDeliveryTimeEn() != null)
            mDeliveryTimeTv.setText(mSelectedAddress.getSelectedArea().getDeliveryTimeEn());
        if (mCartActivity.mAppLanguage.contains("ar")) {
            if (mSelectedAddress.getSelectedArea().getDeliveryTimeAr() != null)
                mDeliveryTimeTv.setText(mSelectedAddress.getSelectedArea().getDeliveryTimeAr());
        }
    }

    private void fillAddressView() {
        LinearLayout rootLayout = mAddressView.findViewById(R.id.address_item_containerLayout);
        rootLayout.setPadding(0, 0, 0, 0);

        LinearLayout actionsLayout = mAddressView.findViewById(R.id.address_item_actionsLayout);
        actionsLayout.setVisibility(View.GONE);

        LinearLayout arrowLayout = mAddressView.findViewById(R.id.address_item_arrowLayout);
        arrowLayout.setVisibility(View.GONE);

        TextView typeTv = mAddressView.findViewById(R.id.address_item_addressTypeTv);
        if (mSelectedAddress.getAddressType() != null) {
            String addressType = null;
            switch (mSelectedAddress.getAddressType()) {
                case 0:
                    addressType = getResources().getString(R.string.apartment);
                    break;
                case 1:
                    addressType = getResources().getString(R.string.house);
                    break;
                case 2:
                    addressType = getResources().getString(R.string.office);
                    break;
            }

            typeTv.setTypeface(mMontserratRegular);
            typeTv.setText(addressType);
        }

        TextView areaTv = mAddressView.findViewById(R.id.address_item_areaTv);
        if (mSelectedAddress.getArea() != null && mSelectedAddress.getSelectedArea() != null) {
            String name;
            if (mCartActivity.mAppLanguage.contains("ar"))
                name = mSelectedAddress.getSelectedArea().getNameAr();
            else
                name = mSelectedAddress.getSelectedArea().getNameEn();

            areaTv.setTypeface(mMontserratRegular);
            areaTv.setText(getResources().getString(R.string.area)
                    .concat(" ").concat(name));
        }

        TextView blockStreetTv = mAddressView.findViewById(R.id.address_item_block_streetTv);
        String blockSt = "";
        if (mSelectedAddress.getBlock() != null)
            blockSt = getResources().getString(R.string.block)
                    .concat(" ").concat(mSelectedAddress.getBlock());
        if (mSelectedAddress.getStreet() != null)
            blockSt = blockSt.concat(", ")
                    .concat(getResources().getString(R.string.street))
                    .concat(": ").concat(mSelectedAddress.getStreet());

        blockStreetTv.setTypeface(mMontserratRegular);
        blockStreetTv.setText(blockSt);

        TextView othersTv = mAddressView.findViewById(R.id.address_item_othersTv);
        String others = "";
        if (mSelectedAddress.getAvenue() != null)
            others = mSelectedAddress.getAvenue();

        if (mSelectedAddress.getBuilding() != null) {
            if (others.isEmpty())
                others = mSelectedAddress.getBuilding();
            else
                others = others.concat(", ").concat(mSelectedAddress.getBuilding());
        }

        if (mSelectedAddress.getFloor() != null) {
            if (others.isEmpty())
                others = mSelectedAddress.getFloor();
            else
                others = others.concat(", ").concat(mSelectedAddress.getFloor());
        }

        if (mSelectedAddress.getApartment() != null) {
            if (others.isEmpty())
                others = String.valueOf(mSelectedAddress.getApartment());
            else
                others = others.concat(", ").concat(String.valueOf(mSelectedAddress.getApartment()));
        }

        othersTv.setTypeface(mMontserratRegular);
        othersTv.setText(others);

    }

    private void setupItemsRv() {
        mItemsRv.setLayoutManager(new LinearLayoutManager(mCartActivity,
                LinearLayoutManager.VERTICAL, false));
        mCheckoutRvAdapter = new CheckoutRvAdapter(mCartActivity, mCartActivity.mAppLanguage, mBasket.getLines());
        mItemsRv.setAdapter(mCheckoutRvAdapter);
        mItemsRv.setNestedScrollingEnabled(false);
        mItemsRv.setItemAnimator(new DefaultItemAnimator());
    }

    private void managePaymentSelection(LinearLayout selectedPayment, LinearLayout other1, LinearLayout other2, int code) {
        mSelectedPaymentMethod = code;
        selectedPayment.setBackground(ContextCompat.getDrawable(mCartActivity, R.drawable.bg_button_trans_border_street_green));
        other1.setBackground(null);
        other2.setBackground(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkoutFragment_backLayout:
                mCartActivity.onBackPressed();
                break;
            case R.id.checkoutFragment_redeemBtn:
                validateVoucherCode();
                break;
            case R.id.checkoutFragment_cashLayout:
                managePaymentSelection(mCashLayout, mKnetLayout, mVisaLayout, CASH);
                break;
            case R.id.checkoutFragment_knetLayout:
                managePaymentSelection(mKnetLayout, mCashLayout, mVisaLayout, KNET);
                break;
            case R.id.checkoutFragment_visaLayout:
                managePaymentSelection(mVisaLayout, mCashLayout, mKnetLayout, VISA);
                break;
            case R.id.checkoutFragment_placeOrderBtn:
                checkoutOrder();
                break;
        }
    }

    private void validateVoucherCode() {
        if (Utils.isInputEmpty(mVoucherInput)) {
            mVoucherInput.setError(getResources().getString(R.string.voucherCodeVaild));
            return;
        }
        mVoucherInput.setError(null);
        checkVoucherCodeAvailability();
    }

    private void checkVoucherCodeAvailability() {
        if (Utils.isConnectionOn(mCartActivity)) {
            mCartActivity.mSpinKitLayout.setVisibility(View.VISIBLE);

            Call<VoucherResponse> call = mCartActivity.mServiceApi
                    .validateVoucherCode(mCartActivity.mAuthToken, new VoucherRequest(mVoucherInput.getText().toString().trim()));
            call.enqueue(new Callback<VoucherResponse>() {
                @Override
                public void onResponse(Call<VoucherResponse> call, Response<VoucherResponse> response) {
                    mCartActivity.mSpinKitLayout.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            VoucherResponse voucherResponse = response.body();

                            if (voucherResponse.getStatus())
                                addVoucherToBasket(voucherResponse.getId());
                            else
                                mVoucherInput.setError(getResources().getString(R.string.voucherCodeExpire));

                        } catch (Exception e) {
                            Log.e("AvailVoucher Ex / ", e.getMessage() + "");
                            Utils.makeAToast(mCartActivity, getResources().getString(R.string.failedToGetData));
                        }
                    } else {
                        if (response.code() == 404)
                            mVoucherInput.setError(getResources().getString(R.string.voucherCodeWrong));
                        else {
                            Log.e("AvailVoucher Code / ", "Code not Successful");
                            Utils.makeAToast(mCartActivity, getResources().getString(R.string.failedToGetData));
                        }
                    }
                }

                @Override
                public void onFailure(Call<VoucherResponse> call, Throwable t) {
                    mCartActivity.mSpinKitLayout.setVisibility(View.GONE);
                    Log.e("AvailVoucher Fail / ", t.getMessage() + "");
                    Utils.makeAToast(mCartActivity, getResources().getString(R.string.failedToGetData));
                }
            });
        } else
            Utils.makeAToast(mCartActivity, getResources().getString(R.string.connection_offline));
    }

    private void addVoucherToBasket(Integer voucherId) {
        mCartActivity.mSpinKitLayout.setVisibility(View.VISIBLE);

        Basket basketRequested = new Basket();
        basketRequested.setId(mBasket.getId());
        basketRequested.setVoucher(String.valueOf(voucherId));

        Call<Basket> call = mCartActivity.mServiceApi.updateBasketInfo(mCartActivity.mAuthToken, mBasket.getId(), basketRequested);
        call.enqueue(new Callback<Basket>() {
            @Override
            public void onResponse(Call<Basket> call, Response<Basket> response) {
                mCartActivity.mSpinKitLayout.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    try {
                        mBasket = response.body();
                        mRedeemBtn.setVisibility(View.GONE);
                        mVoucherSubmittedIv.setVisibility(View.VISIBLE);
                        Utils.makeAToast(mCartActivity, getResources().getString(R.string.voucherCodeAddedSuccess));
                    } catch (Exception e) {
                        Log.e("UBasket Ex / ", e.getMessage() + "");
                        Utils.makeAToast(mCartActivity, getResources().getString(R.string.voucherCodeAddedFailed));
                    }
                } else {
                    Log.e("UBasket Code / ", "Code not Successful");
                    Utils.makeAToast(mCartActivity, getResources().getString(R.string.voucherCodeAddedFailed));
                }
            }

            @Override
            public void onFailure(Call<Basket> call, Throwable t) {
                mCartActivity.mSpinKitLayout.setVisibility(View.GONE);
                Log.e("UBasket Fail / ", t.getMessage() + "");
                Utils.makeAToast(mCartActivity, getResources().getString(R.string.voucherCodeAddedFailed));
            }
        });
    }

    private void checkoutOrder() {
        if (Utils.isConnectionOn(mCartActivity)) {
            mCartActivity.mSpinKitLayout.setVisibility(View.VISIBLE);

            Address selectedAddress = new Address(mSelectedAddress.getTitle(),
                    mSelectedAddress.getOwner(),
                    mSelectedAddress.getAddressType(),
                    mSelectedAddress.getBlock(),
                    mSelectedAddress.getStreet(),
                    mSelectedAddress.getArea(),
                    mSelectedAddress.getAvenue(),
                    mSelectedAddress.getFloor(),
                    mSelectedAddress.getBuilding(),
                    mSelectedAddress.getApartment(),
                    mSelectedAddress.getAdditionalNotes());

            final CheckoutRequest checkoutRequest = new CheckoutRequest(mBasket.getId(),
                    mSelectedPaymentMethod, selectedAddress);

            Call<CheckoutResponse> call = mCartActivity.mServiceApi.postCheckout(mCartActivity.mAuthToken, checkoutRequest);
            call.enqueue(new Callback<CheckoutResponse>() {
                @Override
                public void onResponse(Call<CheckoutResponse> call, Response<CheckoutResponse> response) {
                    mCartActivity.mSpinKitLayout.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            CheckoutResponse checkoutResponse = response.body();

                            UserData userData = mCartActivity.mPrefsManger.loadUserData();
                            userData.setBasketId(checkoutResponse.getBasket());
                            mCartActivity.mPrefsManger.saveUserData(userData);
//                            mCartActivity.mPrefsManger.saveCart(new ArrayList<Product>());

                            if (mSelectedPaymentMethod == CASH)
                                goToOrderScreen(checkoutResponse.getOrder(), checkoutResponse.getOrderNumber());
                            else
                                goToPaymentScreen(checkoutResponse.getOrder(), checkoutResponse.getOrderNumber(),
                                        checkoutResponse.getPayment_url());

                        } catch (Exception e) {
                            Log.e("checkout Ex / ", e.getMessage() + "");
                            Utils.makeAToast(mCartActivity, getResources().getString(R.string.checkoutFailed));
                        }
                    } else {
                        Log.e("checkout Code / ", "Code not Successful");
                        Utils.makeAToast(mCartActivity, getResources().getString(R.string.checkoutFailed));
                    }
                }

                @Override
                public void onFailure(Call<CheckoutResponse> call, Throwable t) {
                    mCartActivity.mSpinKitLayout.setVisibility(View.GONE);
                    Log.e("checkout Fail / ", t.getMessage() + "");
                    Utils.makeAToast(mCartActivity, getResources().getString(R.string.checkoutFailed));
                }
            });
        } else
            Utils.makeAToast(mCartActivity, getResources().getString(R.string.connection_offline));
    }

    private void goToOrderScreen(Integer orderId, String orderNumber) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ORDER_NAME, orderNumber);
        bundle.putString(KEY_PAYMENT_METHOD, getResources().getString(R.string.cash));
        Utils.openChildFragment(CheckoutFragment.this,
                new CongratulationFragment(), bundle, R.id.checkoutFragment_containerLayout, CONG_FRAGMENT_TAG);
    }

    private void goToPaymentScreen(Integer orderId, String orderNumber, String paymentUrl) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ORDER_NAME, orderNumber);
        bundle.putString(KEY_PAYMENT_URL, paymentUrl);
        bundle.putString(KEY_PAYMENT_METHOD, getResources().getString(R.string.knet));
        Utils.openChildFragment(CheckoutFragment.this,
                new PaymentFragment(), bundle, R.id.checkoutFragment_containerLayout, PAYMENT_FRAGMENT_TAG);
    }

    public void goToOrderScreenAfterPayment(String orderNumber, String paymentMethod) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ORDER_NAME, orderNumber);
        bundle.putString(KEY_PAYMENT_METHOD, paymentMethod);
        Utils.openChildFragment(CheckoutFragment.this,
                new CongratulationFragment(), bundle, R.id.checkoutFragment_containerLayout, CONG_FRAGMENT_TAG);
    }

//    private void goToPaymentScreen(String payment_url) {
//        mCartActivity.backToMainScreen(false, payment_url);
//    }
}
