package net.aldar.cramello;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.aldar.cramello.adapter.CartRvAdapter;
import net.aldar.cramello.apiHandler.BaseApi;
import net.aldar.cramello.apiHandler.BaseApiHandler;
import net.aldar.cramello.fragment.CheckoutFragment;
import net.aldar.cramello.model.Address;
import net.aldar.cramello.model.Basket;
import net.aldar.cramello.model.request.BasketLineRequest;
import net.aldar.cramello.model.request.BasketValidationRequest;
import net.aldar.cramello.model.response.UserData;
import net.aldar.cramello.model.response.basket.BasketLine;
import net.aldar.cramello.model.response.basket.BasketValidation;
import net.aldar.cramello.model.response.basket.Validation;
import net.aldar.cramello.model.response.product.Product;
import net.aldar.cramello.services.LocaleHelper;
import net.aldar.cramello.services.PrefsManger;
import net.aldar.cramello.services.Utils;
import net.aldar.cramello.view.DcDialog;
import net.aldar.cramello.view.listener.OnClickRetryBtn;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.aldar.cramello.App.KEY_ADDRESS_DATA;
import static net.aldar.cramello.App.KEY_OPEN_ORDERS;
import static net.aldar.cramello.App.KEY_PAYMENT_URL;
import static net.aldar.cramello.App.mMontserratRegular;

public class CartActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String CHECK_OUT_FRAGMENT_TAG = "CheckoutFragment";
    public static final String CONG_FRAGMENT_TAG = "CongratulationsFragment";
    public static final String PAYMENT_FRAGMENT_TAG = "PaymentFragment";

    private static final int ERROR_UPDATE_BASKET = 11;
    private static final int ERROR_POST_MODIFY_BASKET_LINES = 22;
    private static final int ERROR_DELETE_BASKET_LINES = 33;
    private static final int ERROR_VALIDATE_BASKET = 44;
    private static final int ERROR_GET_BASKET = 55;

    private double mSubTotal;
//    private double mMinOrder;

    private TextView mTitleTv;
    private LinearLayout mBackLayout;
    private ImageView mBackIv;

    private LinearLayout mEmptyLayout;
    private TextView mEmptyTv;
    private Button mAddItemsBtn;

    private LinearLayout mSummaryLayout;
    private TextView mSubTotalTitle;
    private TextView mSubTotalTv;

    private Button mAddMoreBtn;
    private Button mCheckoutBtn;

    public LinearLayout mSpinKitLayout;

    private RecyclerView mCartRv;
    private CartRvAdapter mCartRvAdapter;
    public List<Product> mCartList;

    public PrefsManger mPrefsManger;
    public BaseApi mServiceApi;
    public String mAppLanguage;
    public String mAuthToken;

    private List<Integer> mBasketLinesIds;
    public Basket mBasket;
    public Address mAddress;
    private List<BasketLineRequest> mBasketLineRequestsList;

    @Override
    protected void attachBaseContext(Context newBase) {
        mPrefsManger = new PrefsManger(newBase);
        super.attachBaseContext(LocaleHelper.wrap(newBase, mPrefsManger.getAppLanguage()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

//        if (getIntent().hasExtra(KEY_MIN_ORDER_VALUE))
//            mMinOrder = getIntent().getExtras().getDouble(KEY_MIN_ORDER_VALUE);

        if (getIntent().hasExtra(KEY_ADDRESS_DATA)) {
            String json = getIntent().getExtras().getString(KEY_ADDRESS_DATA);
            Type type = new TypeToken<Address>() {
            }.getType();
            mAddress = new Gson().fromJson(json, type);
        }
        getCartList();

        mServiceApi = BaseApiHandler.setupBaseApi().create(BaseApi.class);
        mAppLanguage = mPrefsManger.getAppLanguage();
        mAuthToken = "Token ".concat(mPrefsManger.getLoginToken());

        mSpinKitLayout = findViewById(R.id.cart_spinKit_layout);

        mTitleTv = findViewById(R.id.cart_activity_title);
        mTitleTv.setTypeface(mMontserratRegular);

        mBackLayout = findViewById(R.id.cart_activity_backLayout);
        mBackIv = findViewById(R.id.cart_activity_backIv);
        mBackLayout.setOnClickListener(this);
        Utils.submitRotation(mBackIv, mPrefsManger);

        mSummaryLayout = findViewById(R.id.cart_activity_summaryLayout);

        mEmptyLayout = findViewById(R.id.cart_activity_emptyLayout);
        mEmptyTv = findViewById(R.id.cart_activity_emptyTv);
        mEmptyTv.setTypeface(mMontserratRegular);
        mAddItemsBtn = findViewById(R.id.cart_activity_addItemsBtn);
        mEmptyTv.setTypeface(mMontserratRegular);
        mAddItemsBtn.setOnClickListener(this);
        mAddItemsBtn.setTypeface(mMontserratRegular);
        mEmptyLayout.setVisibility(View.GONE);

        mSubTotalTitle = findViewById(R.id.cart_activity_totalTitle);
        mSubTotalTitle.setTypeface(mMontserratRegular);
        mSubTotalTv = findViewById(R.id.cart_activity_totalTv);
        mSubTotalTv.setTypeface(mMontserratRegular);

        mAddMoreBtn = findViewById(R.id.cart_activity_addMoreBtn);
        mAddMoreBtn.setTypeface(mMontserratRegular);
        mAddMoreBtn.setOnClickListener(this);

        mCheckoutBtn = findViewById(R.id.cart_activity_checkOutBtn);
        mCheckoutBtn.setTypeface(mMontserratRegular);
        mCheckoutBtn.setOnClickListener(this);

        mCartRv = findViewById(R.id.cart_activity_itemsRv);
        setupCartRv();
    }

    private void setupCartRv() {
        mCartRv.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        mCartRvAdapter = new CartRvAdapter(this, mCartList);
        mCartRv.setAdapter(mCartRvAdapter);
        mCartRv.setItemAnimator(new DefaultItemAnimator());

        checkEmptyList();
        updateSubTotal();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cart_activity_backLayout:
            case R.id.cart_activity_addItemsBtn:
            case R.id.cart_activity_addMoreBtn:
                mPrefsManger.saveCart(mCartList);
                finish();
                break;

            case R.id.cart_activity_checkOutBtn:
                if (mSubTotal < Double.parseDouble(mAddress.getSelectedArea().getArea_minimum_order())) {
                    String text = getResources().getString(R.string.minOrderValid)
                            .concat(" ")
                            .concat(getResources().getString(R.string.kd))
                            .concat(" ")
                            .concat(Utils.makeSureThreeNAfterDot(Double.parseDouble(mAddress.getSelectedArea().getArea_minimum_order())));
                    Utils.makeAToast(CartActivity.this, text);
                    return;
                }
                mPrefsManger.saveCart(mCartList);
                updateBasketArea();
                break;
        }
    }

    private void updateBasketArea() {
        if (Utils.isConnectionOn(this)) {
            mSpinKitLayout.setVisibility(View.VISIBLE);

            UserData userData = mPrefsManger.loadUserData();

            Basket basketRequested = new Basket();
            basketRequested.setOwner(userData.getId());
            basketRequested.setId(userData.getBasketId());
            basketRequested.setArea(mPrefsManger.getDeliveryArea().getId());
            basketRequested.setVoucher("");

            Call<Basket> call = mServiceApi.updateBasketInfo(mAuthToken, userData.getBasketId(), basketRequested);
            call.enqueue(new Callback<Basket>() {
                @Override
                public void onResponse(Call<Basket> call, Response<Basket> response) {
                    mSpinKitLayout.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            mBasket = response.body();
                            if (mBasket.getLines() != null && !mBasket.getLines().isEmpty()) {
                                mBasketLinesIds = new ArrayList<>();
                                for (int l = 0; l < mBasket.getLines().size(); l++) {
                                    mBasketLinesIds.add(mBasket.getLines().get(l).getId());
                                }
                            }
                            reBuildProductLines();
                        } catch (Exception e) {
                            showDcDialog(ERROR_UPDATE_BASKET);
                            Log.e("UBasket Ex / ", e.getMessage() + "");
                        }
                    } else {
                        Log.e("UBasket Code / ", "Code not Successful");
                        showDcDialog(ERROR_UPDATE_BASKET);
                    }
                }

                @Override
                public void onFailure(Call<Basket> call, Throwable t) {
                    mSpinKitLayout.setVisibility(View.GONE);
                    Log.e("UBasket Fail / ", t.getMessage() + "");
                    showDcDialog(ERROR_UPDATE_BASKET);
                }
            });
        } else
            Utils.makeAToast(CartActivity.this, getResources().getString(R.string.connection_offline));
    }

    private void reBuildProductLines() {
        mBasket.setLines(new ArrayList<BasketLine>());
        mBasketLineRequestsList = new ArrayList<>();
        for (int c = 0; c < mCartList.size(); c++) {
            Product product = mCartList.get(c);
            BasketLineRequest basketLine = new BasketLineRequest(
                    mBasket.getId(), product.getId(), product.getDiscountedPrice(),
                    product.getQuantity(), product.getSpecialHint());
            mBasketLineRequestsList.add(basketLine);
        }
        sendBasketLineRequest(mBasketLineRequestsList.size() - 1);
    }

    private void sendBasketLineRequest(final int index) {
        mSpinKitLayout.setVisibility(View.VISIBLE);

        final BasketLineRequest basketLine = mBasketLineRequestsList.get(index);

        if (!basketLine.isSynced()) {
            Call<BasketLineRequest> call;
            if (mBasketLinesIds != null && !mBasketLinesIds.isEmpty())
                call = mServiceApi.modifyBasketLine(mAuthToken, mBasketLinesIds.get(0), basketLine);
            else
                call = mServiceApi.createBasketLine(mAuthToken, basketLine);

            call.enqueue(new Callback<BasketLineRequest>() {
                @Override
                public void onResponse(Call<BasketLineRequest> call, Response<BasketLineRequest> response) {
                    mSpinKitLayout.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            BasketLineRequest line = response.body();

                            List<BasketLine> basketLines = mBasket.getLines();

                            BasketLine confirmedBasketLine = new BasketLine(line.getId(), line.getBasket(),
                                    line.getAmount(), line.getQuantity(), line.getVoucherDiscount(), line.getComment());

                            for (int c = 0; c < mCartList.size(); c++) {
                                Product product = mCartList.get(c);
                                int cartProductId = product.getId();
                                int confirmedLineProductId = line.getProduct();

                                if (confirmedLineProductId == cartProductId) {
                                    confirmedBasketLine.setProduct(product);
                                    break;
                                }
                            }

                            basketLines.add(confirmedBasketLine);
                            mBasket.setLines(basketLines);

                            if (line.getVoucherDiscount() != null && line.getVoucherDiscount() != 0)
                                mBasket.setVoucherDiscount(line.getVoucherDiscount());

                            if (mBasketLinesIds != null && !mBasketLinesIds.isEmpty())
                                mBasketLinesIds.remove(0);

                            basketLine.setSynced(true);

                            if (index != 0)
                                sendBasketLineRequest((index - 1));
                            else {
                                if (mBasketLinesIds != null && !mBasketLinesIds.isEmpty())
                                    deleteTheRestOfBasketLines();
                                else
                                    getMyBasket();
                            }
                        } catch (Exception e) {
                            showDcDialog(ERROR_POST_MODIFY_BASKET_LINES);
                            Log.e("C-M-BasketLine Ex / ", e.getMessage() + "");
                        }
                    } else {
                        Log.e("C-M-BasketLine Code / ", "Code not Successful");
                        showDcDialog(ERROR_POST_MODIFY_BASKET_LINES);
                    }
                }

                @Override
                public void onFailure(Call<BasketLineRequest> call, Throwable t) {
                    mSpinKitLayout.setVisibility(View.GONE);
                    Log.e("C-M-BasketLine Fail / ", t.getMessage() + "");
                    showDcDialog(ERROR_POST_MODIFY_BASKET_LINES);
                }
            });
        } else {
            if (index != 0)
                sendBasketLineRequest((index - 1));
            else {
                if (mBasketLinesIds != null && !mBasketLinesIds.isEmpty())
                    deleteTheRestOfBasketLines();
                else
                    validateBasket();
            }
        }
    }

    private void deleteTheRestOfBasketLines() {
        mSpinKitLayout.setVisibility(View.VISIBLE);

        if (mBasketLinesIds != null && !mBasketLinesIds.isEmpty()) {

            Call<Void> call = mServiceApi.deleteBasketLine(mAuthToken, mBasketLinesIds.get(0));
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    mSpinKitLayout.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            mBasketLinesIds.remove(0);
                            if (mBasketLinesIds != null && !mBasketLinesIds.isEmpty())
                                deleteTheRestOfBasketLines();
                            else
                                getMyBasket();
                        } catch (Exception e) {
                            showDcDialog(ERROR_DELETE_BASKET_LINES);
                            Log.e("DBasketLine Ex / ", e.getMessage() + "");
                        }
                    } else {
                        Log.e("DBasketLine Code / ", "Code not Successful");
                        showDcDialog(ERROR_DELETE_BASKET_LINES);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    mSpinKitLayout.setVisibility(View.GONE);
                    Log.e("DBasketLine Fail / ", t.getMessage() + "");
                    showDcDialog(ERROR_DELETE_BASKET_LINES);
                }
            });
        }
    }

    private void getMyBasket() {
        mSpinKitLayout.setVisibility(View.VISIBLE);

        Call<List<Basket>> call = mServiceApi.getMyBasket(mAuthToken, mPrefsManger.loadUserData().getId(), mBasket.getId());
        call.enqueue(new Callback<List<Basket>>() {
            @Override
            public void onResponse(Call<List<Basket>> call, Response<List<Basket>> response) {
                mSpinKitLayout.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        mBasket = response.body().get(0);
                        validateBasket();
                    } catch (Exception e) {
                        showDcDialog(ERROR_GET_BASKET);
                        Log.e("GetBasket Ex / ", e.getMessage() + "");
                    }
                } else {
                    Log.e("GetBasket Code / ", "Code not Successful");
                    showDcDialog(ERROR_GET_BASKET);
                }
            }

            @Override
            public void onFailure(Call<List<Basket>> call, Throwable t) {
                mSpinKitLayout.setVisibility(View.GONE);
                Log.e("GetBasket Fail / ", t.getMessage() + "");
                showDcDialog(ERROR_GET_BASKET);
            }
        });
    }

    private void validateBasket() {
        mSpinKitLayout.setVisibility(View.VISIBLE);

        Call<BasketValidation> call = mServiceApi.validateBasket(mAuthToken, mAppLanguage, mAppLanguage,
                new BasketValidationRequest(mBasket.getId()));
        call.enqueue(new Callback<BasketValidation>() {
            @Override
            public void onResponse(Call<BasketValidation> call, Response<BasketValidation> response) {
                mSpinKitLayout.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    try {
                        BasketValidation validation = response.body();
                        if (validation.getErrors() != 0)
                            showValidationsErrorDialog(validation.getValidation());
                        else
                            goToCheckoutScreen();
                    } catch (Exception e) {
                        showDcDialog(ERROR_VALIDATE_BASKET);
                        Log.e("VBasket Ex / ", e.getMessage() + "");
                    }
                } else {
                    Log.e("VBasket Code / ", "Code not Successful");
                    showDcDialog(ERROR_VALIDATE_BASKET);
                }
            }

            @Override
            public void onFailure(Call<BasketValidation> call, Throwable t) {
                mSpinKitLayout.setVisibility(View.GONE);
                Log.e("VBasket Fail / ", t.getMessage() + "");
                showDcDialog(ERROR_VALIDATE_BASKET);
            }
        });
    }

    private void showValidationsErrorDialog(List<Validation> validationList) {

        String errorMsg = "";

        for (int v = 0; v < validationList.size(); v++) {
            Validation validation = validationList.get(v);
            if (validation.getErrors() != null && !validation.getErrors().isEmpty()) {
                for (int l = 0; l < mBasket.getLines().size(); l++) {
                    BasketLine line = mBasket.getLines().get(l);
                    int lineId = line.getId();
                    int validationLineId = validation.getLine();

                    if (validationLineId == lineId) {
                        String productName;
                        if (mAppLanguage.contains("ar"))
                            productName = line.getProduct().getNameAr();
                        else
                            productName = line.getProduct().getNameEn();

                        String validationErrors = "";
                        for (int e = 0; e < validation.getErrors().size(); e++) {
                            String valError = validation.getErrors().get(e);

                            if (validationErrors.isEmpty()) {
                                validationErrors = valError;
                            } else {
                                validationErrors = validationErrors.concat("\n").concat(valError);
                            }
                        }
                        String lineError = productName.concat(" ").concat(validationErrors);
                        if (errorMsg.isEmpty()) {
                            errorMsg = lineError;
                        } else {
                            errorMsg = errorMsg.concat("\n").concat(lineError);
                        }
                    }
                }
            }
        }

        Utils.showAlertCustomMsg(CartActivity.this, errorMsg);
    }

    private void goToCheckoutScreen() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.cart_activity_container_layout, new CheckoutFragment(), CHECK_OUT_FRAGMENT_TAG).commit();
    }

    @Override
    public void onBackPressed() {
        try {
            CheckoutFragment checkoutFragment = (CheckoutFragment) CartActivity.this
                    .getSupportFragmentManager().findFragmentByTag(CHECK_OUT_FRAGMENT_TAG);
            if (checkoutFragment != null) {
                if (checkoutFragment.getChildFragmentManager().getFragments().size() != 0) {
                    backToMainScreen(false, null);
                    return;
                }
            }
        } catch (Exception e) {
            mPrefsManger.saveCart(mCartList);
            super.onBackPressed();
        }
        mPrefsManger.saveCart(mCartList);
        super.onBackPressed();
    }

    public void updateSubTotal() {
        mSubTotal = 0;
        for (int t = 0; t < mCartList.size(); t++) {
            Product product = mCartList.get(t);
            mSubTotal = mSubTotal + (product.getQuantity() * product.getDiscountedPrice());
        }
        mSubTotalTv.setText(getResources().getString(R.string.kd)
                .concat(" ")
                .concat(Utils.makeSureThreeNAfterDot(mSubTotal)));
    }

    public void deleteProduct(int position) {
        mCartList.remove(position);
        if (mCartList.isEmpty()) {
            mCartRvAdapter.mCartList = new ArrayList<>();
            checkEmptyList();
        }
        mCartRvAdapter.notifyDataSetChanged();
        updateSubTotal();
    }

    private void checkEmptyList() {
        if (mCartList.isEmpty()) {
            mEmptyLayout.setVisibility(View.VISIBLE);
            mCartRv.setVisibility(View.GONE);
            mSummaryLayout.setVisibility(View.GONE);
        } else {
            mEmptyLayout.setVisibility(View.GONE);
            mCartRv.setVisibility(View.VISIBLE);
            mSummaryLayout.setVisibility(View.VISIBLE);
        }
    }

    public void getCartList() {
        mCartList = mPrefsManger.loadCart();
    }

    public void showDcDialog(final int type) {
        DcDialog dcDialog = new DcDialog(this);
        dcDialog.setOnClickRetryBtnListener(new OnClickRetryBtn() {
            @Override
            public void retry() {
                switch (type) {
                    case ERROR_UPDATE_BASKET:
                        updateBasketArea();
                        break;
                    case ERROR_POST_MODIFY_BASKET_LINES:
                        mBasket.setLines(new ArrayList<BasketLine>());
                        sendBasketLineRequest(0);
                        break;
                    case ERROR_DELETE_BASKET_LINES:
                        deleteTheRestOfBasketLines();
                        break;
                    case ERROR_GET_BASKET:
                        getMyBasket();
                        break;
                    case ERROR_VALIDATE_BASKET:
                        validateBasket();
                        break;
                }
            }
        });
    }

    public void backToMainScreen(boolean myOrdersScreen, String paymentUrl) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        if (myOrdersScreen && paymentUrl == null)
            intent.putExtra(KEY_OPEN_ORDERS, myOrdersScreen);
        else
            intent.putExtra(KEY_PAYMENT_URL, paymentUrl);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
