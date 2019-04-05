package net.aldar.cramello.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.aldar.cramello.CartActivity;
import net.aldar.cramello.R;
import net.aldar.cramello.model.Address;
import net.aldar.cramello.model.response.product.Product;
import net.aldar.cramello.services.Utils;

import java.util.ArrayList;

import static net.aldar.cramello.App.KEY_ORDER_NAME;
import static net.aldar.cramello.App.KEY_PAYMENT_METHOD;
import static net.aldar.cramello.App.mMontserratBold;
import static net.aldar.cramello.App.mMontserratRegular;

public class CongratulationFragment extends RootFragment implements View.OnClickListener {

    private TextView mTitleTv;
    private LinearLayout mBackLayout;
    private ImageView mBackIv;

    private TextView mCongTitle;
    private TextView mOnWayTitle;
    private TextView mOnWayTimeTv;
    private TextView mOrderIdTv;

    private TextView mDeliveryAddressTitle;
    private View mAddressView;

    private TextView mPaymentTitle;
    private TextView mPaymentTv;

    private Button mMyOrdersBtn;

    private CartActivity mCartActivity;
    private Address mAddress;
    private String mOrderName;
    private String mPaymentMethod;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCartActivity = (CartActivity) getActivity();
        mAddress = mCartActivity.mAddress;

        if (getArguments() != null) {
            mOrderName = getArguments().getString(KEY_ORDER_NAME);
            mPaymentMethod = getArguments().getString(KEY_PAYMENT_METHOD);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_congratulation, container, false);

        mTitleTv = view.findViewById(R.id.congFragment_title);
        mBackLayout = view.findViewById(R.id.congFragment_backLayout);
        mBackIv = view.findViewById(R.id.congFragment_backIv);

        mCongTitle = view.findViewById(R.id.congFragment_congTitle);
        mOnWayTitle = view.findViewById(R.id.congFragment_onWayTitle);
        mOnWayTimeTv = view.findViewById(R.id.congFragment_onWayTimeTv);
        mOrderIdTv = view.findViewById(R.id.congFragment_orderIdTv);

        mDeliveryAddressTitle = view.findViewById(R.id.congFragment_deliveryAddressTitle);
        mAddressView = view.findViewById(R.id.congFragment_deliveryAddressView);

        mPaymentTitle = view.findViewById(R.id.congFragment_paymentTitle);
        mPaymentTv = view.findViewById(R.id.congFragment_paymentTv);

        mMyOrdersBtn = view.findViewById(R.id.congFragment_myOrdersBtn);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mTitleTv.setTypeface(mMontserratRegular);
        mBackLayout.setOnClickListener(this);
        Utils.submitRotation(mBackIv, mCartActivity.mPrefsManger);

        mCongTitle.setTypeface(mMontserratRegular);
        mOnWayTitle.setTypeface(mMontserratRegular);
        mOrderIdTv.setTypeface(mMontserratRegular);
        mOrderIdTv.setText(getResources().getString(R.string.orderId)
                .concat(" ")
                .concat(mOrderName));

        mDeliveryAddressTitle.setTypeface(mMontserratBold);
        mPaymentTitle.setTypeface(mMontserratBold);
        mPaymentTv.setTypeface(mMontserratRegular);
        mPaymentTv.setText(mPaymentMethod);

        mMyOrdersBtn.setTypeface(mMontserratRegular);
        mMyOrdersBtn.setOnClickListener(this);

        fillAddressView();

        mCartActivity.mPrefsManger.saveCart(new ArrayList<Product>());
    }

    private void fillAddressView() {
        LinearLayout rootLayout = mAddressView.findViewById(R.id.address_item_containerLayout);
        rootLayout.setPadding(0, 0, 0, 0);

        LinearLayout actionsLayout = mAddressView.findViewById(R.id.address_item_actionsLayout);
        actionsLayout.setVisibility(View.GONE);

        LinearLayout arrowLayout = mAddressView.findViewById(R.id.address_item_arrowLayout);
        arrowLayout.setVisibility(View.GONE);

        TextView typeTv = mAddressView.findViewById(R.id.address_item_addressTypeTv);
        if (mAddress.getAddressType() != null) {
            String addressType = null;
            switch (mAddress.getAddressType()) {
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
        if (mAddress.getArea() != null && mAddress.getSelectedArea() != null) {
            String name;
            if (mCartActivity.mAppLanguage.contains("ar"))
                name = mAddress.getSelectedArea().getNameAr();
            else
                name = mAddress.getSelectedArea().getNameEn();

            areaTv.setTypeface(mMontserratRegular);
            areaTv.setText(getResources().getString(R.string.area)
                    .concat(" ").concat(name));
        }

        TextView blockStreetTv = mAddressView.findViewById(R.id.address_item_block_streetTv);
        String blockSt = "";
        if (mAddress.getBlock() != null)
            blockSt = getResources().getString(R.string.block)
                    .concat(" ").concat(mAddress.getBlock());
        if (mAddress.getStreet() != null)
            blockSt = blockSt.concat(", ")
                    .concat(getResources().getString(R.string.street))
                    .concat(": ").concat(mAddress.getStreet());

        blockStreetTv.setTypeface(mMontserratRegular);
        blockStreetTv.setText(blockSt);

        TextView othersTv = mAddressView.findViewById(R.id.address_item_othersTv);
        String others = "";
        if (mAddress.getAvenue() != null)
            others = mAddress.getAvenue();

        if (mAddress.getBuilding() != null) {
            if (others.isEmpty())
                others = mAddress.getBuilding();
            else
                others = others.concat(", ").concat(mAddress.getBuilding());
        }

        if (mAddress.getFloor() != null) {
            if (others.isEmpty())
                others = mAddress.getFloor();
            else
                others = others.concat(", ").concat(mAddress.getFloor());
        }

        if (mAddress.getApartment() != null) {
            if (others.isEmpty())
                others = String.valueOf(mAddress.getApartment());
            else
                others = others.concat(", ").concat(String.valueOf(mAddress.getApartment()));
        }

        othersTv.setTypeface(mMontserratRegular);
        othersTv.setText(others);

        if (mAddress.getSelectedArea().getDeliveryTimeEn() != null)
            mOnWayTimeTv.setText(getResources().getString(R.string.deliveryTime)
                    .concat(" : ").concat(mAddress.getSelectedArea().getDeliveryTimeEn()));
        if (mCartActivity.mAppLanguage.contains("ar")) {
            if (mAddress.getSelectedArea().getDeliveryTimeAr() != null)
                mOnWayTimeTv.setText(getResources().getString(R.string.deliveryTime)
                        .concat(" : ").concat(mAddress.getSelectedArea().getDeliveryTimeAr()));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.congFragment_backLayout:
                mCartActivity.backToMainScreen(false, null);
                break;
            case R.id.congFragment_myOrdersBtn:
                mCartActivity.backToMainScreen(true, null);
                break;
        }
    }

}
