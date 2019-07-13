package net.aldar.cramello.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.aldar.cramello.R;
import net.aldar.cramello.model.response.Order;
import net.aldar.cramello.model.response.product.Product;
import net.aldar.cramello.services.Utils;
import net.aldar.cramello.view.CartActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.aldar.cramello.view.App.KEY_ORDER_NAME;
import static net.aldar.cramello.view.App.KEY_PAYMENT_METHOD;
import static net.aldar.cramello.view.App.KEY_PAYMENT_URL;
import static net.aldar.cramello.view.App.mMontserratRegular;

public class PaymentFragment extends RootFragment implements View.OnClickListener {

    private TextView mTitleTv;
    private LinearLayout mBackLayout;
    private ImageView mBackIv;

    private WebView mPaymentWebView;

    private CartActivity mCartActivity;
    private String mOrderName;
    private String mPaymentMethod;
    private String mPaymentUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCartActivity = (CartActivity) getActivity();

        if (getArguments() != null) {
            mOrderName = getArguments().getString(KEY_ORDER_NAME);
            mPaymentMethod = getArguments().getString(KEY_PAYMENT_METHOD);
            mPaymentUrl = getArguments().getString(KEY_PAYMENT_URL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        mTitleTv = view.findViewById(R.id.payment_fragment_title);
        mBackLayout = view.findViewById(R.id.payment_fragment_backLayout);
        mBackIv = view.findViewById(R.id.payment_activity_backIv);
        mPaymentWebView = view.findViewById(R.id.payment_fragment_webView);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mTitleTv.setTypeface(mMontserratRegular);
        mBackLayout.setOnClickListener(this);
        Utils.submitRotation(mBackIv, mCartActivity.mPrefsManger);

        setupWebView();
    }

    private void setupWebView() {
        mCartActivity.mSpinKitLayout.setVisibility(View.VISIBLE);
        mPaymentWebView.getSettings().setLoadsImagesAutomatically(true);
        mPaymentWebView.getSettings().setJavaScriptEnabled(true);
        mPaymentWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mPaymentWebView.loadUrl(mPaymentUrl);
        mPaymentWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mCartActivity.mSpinKitLayout.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("https://cramello.com/payment")) {
                    int chr = url.lastIndexOf("payment/");
                    String transactionId = url.substring(chr + 8);
                    checkOrder(transactionId);
                }
                view.loadUrl(url);
                return true;
            }
        });
    }

    private void checkOrder(String transactionId) {
        mCartActivity.mSpinKitLayout.setVisibility(View.VISIBLE);

        Call<List<Order>> call = mCartActivity.mServiceApi.getMyOrders(mCartActivity.mAuthToken,
                transactionId, null);
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                mCartActivity.mSpinKitLayout.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    try {
                        List<Order> orders = response.body();

                        Order order = orders.get(0);

                        if (order.getStatus() == 1) {
                            mCartActivity.mPrefsManger.saveCart(new ArrayList<Product>());
                            CheckoutFragment checkoutFragment = (CheckoutFragment) getParentFragment();
                            assert checkoutFragment != null;
                            checkoutFragment.goToOrderScreenAfterPayment(mOrderName, mPaymentMethod);
                        } else {
                            Utils.makeAToast(mCartActivity, getResources().getString(R.string.failedToPay));
                            mCartActivity.backToMainScreen(true, null);
                        }

                    } catch (Exception e) {
                        Log.e("CheckOrder Ex / ", e.getMessage() + "");
                    }
                } else {
                    Log.e("CheckOrder Code / ", "Code not Successful");
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                mCartActivity.mSpinKitLayout.setVisibility(View.GONE);
                Log.e("CheckOrder Fail / ", t.getMessage() + "");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mCartActivity.mSpinKitLayout.setVisibility(View.GONE);
            mPaymentWebView.clearView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.payment_fragment_backLayout:
                mCartActivity.backToMainScreen(true, null);
                break;
        }
    }
}
