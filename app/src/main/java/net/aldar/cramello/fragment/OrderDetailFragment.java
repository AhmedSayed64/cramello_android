package net.aldar.cramello.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.aldar.cramello.R;
import net.aldar.cramello.adapter.CheckoutRvAdapter;
import net.aldar.cramello.model.Address;
import net.aldar.cramello.model.response.Order;
import net.aldar.cramello.model.response.governorate.Area;
import net.aldar.cramello.model.response.governorate.Governorate;
import net.aldar.cramello.services.Utils;
import net.aldar.cramello.view.MainActivity;

import java.lang.reflect.Type;

import static net.aldar.cramello.view.App.CASH;
import static net.aldar.cramello.view.App.KEY_ORDER_DATA;
import static net.aldar.cramello.view.App.KNET;
import static net.aldar.cramello.view.App.VISA;
import static net.aldar.cramello.view.App.mMontserratBold;
import static net.aldar.cramello.view.App.mMontserratLight;
import static net.aldar.cramello.view.App.mMontserratRegular;

public class OrderDetailFragment extends RootFragment implements View.OnClickListener {

    private TextView mTitleTv;
    private LinearLayout mBackLayout;
    private ImageView mBackIv;

    private TextView mDeliveryAddressTitle;
    private View mAddressView;

    private TextView mItemsTitle;
    private RecyclerView mItemsRv;
    private CheckoutRvAdapter mItemsRvAdapter;

    private TextView mPaymentMethodTitle;
    private TextView mPaymentMethodTv;

    private TextView mSubTotalTitle;
    private TextView mSubTotalTv;

    private TextView mDeliveryTitle;
    private TextView mDeliveryTv;

    private LinearLayout mDiscountLayout;
    private TextView mDiscountTitle;
    private TextView mDiscountTv;

    private TextView mTotalAmountTitle;
    private TextView mTotalAmountTv;

    private MainActivity mMainActivity;
    private Order mOrder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
        if (getArguments() != null) {
            String json = getArguments().getString(KEY_ORDER_DATA);
            Type type = new TypeToken<Order>() {
            }.getType();
            mOrder = new Gson().fromJson(json, type);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);

        mTitleTv = view.findViewById(R.id.orderDetailFragment_title);
        mBackLayout = view.findViewById(R.id.orderDetailFragment_backLayout);
        mBackIv = view.findViewById(R.id.orderDetailFragment_backIv);

        mDeliveryAddressTitle = view.findViewById(R.id.orderDetailFragment_deliveryAddressTitle);
        mAddressView = view.findViewById(R.id.orderDetailFragment_deliveryAddressView);

        mItemsTitle = view.findViewById(R.id.orderDetailFragment_itemsTitle);
        mItemsRv = view.findViewById(R.id.orderDetailFragment_itemsRv);

        mPaymentMethodTitle = view.findViewById(R.id.orderDetailFragment_paymentMethodTitle);
        mPaymentMethodTv = view.findViewById(R.id.orderDetailFragment_paymentMethodTv);

        mSubTotalTitle = view.findViewById(R.id.orderDetailFragment_subTotalTitle);
        mSubTotalTv = view.findViewById(R.id.orderDetailFragment_subTotalTv);

        mDeliveryTitle = view.findViewById(R.id.orderDetailFragment_deliveryTitle);
        mDeliveryTv = view.findViewById(R.id.orderDetailFragment_deliveryTv);

        mDiscountLayout = view.findViewById(R.id.orderDetailFragment_discountLayout);
        mDiscountTitle = view.findViewById(R.id.orderDetailFragment_discountTitle);
        mDiscountTv = view.findViewById(R.id.orderDetailFragment_discountTv);

        mTotalAmountTitle = view.findViewById(R.id.orderDetailFragment_totalAmountTitle);
        mTotalAmountTv = view.findViewById(R.id.orderDetailFragment_totalAmountTv);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mTitleTv.setTypeface(mMontserratRegular);
        mBackLayout.setOnClickListener(this);
        Utils.submitRotation(mBackIv, mMainActivity.mPrefsManger);

        mDeliveryAddressTitle.setTypeface(mMontserratBold);
        fillAddressView();

        mItemsTitle.setTypeface(mMontserratBold);
        setupItemsRv();

        mPaymentMethodTitle.setTypeface(mMontserratLight);
        mPaymentMethodTv.setTypeface(mMontserratLight);

        mSubTotalTitle.setTypeface(mMontserratLight);
        mSubTotalTv.setTypeface(mMontserratLight);

        mDeliveryTitle.setTypeface(mMontserratLight);
        mDeliveryTv.setTypeface(mMontserratLight);

        mDiscountTitle.setTypeface(mMontserratLight);
        mDiscountTv.setTypeface(mMontserratLight);

        mTotalAmountTitle.setTypeface(mMontserratBold);
        mTotalAmountTv.setTypeface(mMontserratBold);

        fillAmounts();

    }

    private void fillAddressView() {
        Address selectedAddress = mOrder.getShippingAddress();
        LinearLayout rootLayout = mAddressView.findViewById(R.id.address_item_containerLayout);
        rootLayout.setPadding(0, 0, 0, 0);

        LinearLayout actionsLayout = mAddressView.findViewById(R.id.address_item_actionsLayout);
        actionsLayout.setVisibility(View.GONE);

        LinearLayout arrowLayout = mAddressView.findViewById(R.id.address_item_arrowLayout);
        arrowLayout.setVisibility(View.GONE);

        TextView typeTv = mAddressView.findViewById(R.id.address_item_addressTypeTv);
        if (selectedAddress.getAddressType() != null) {
            String addressType = null;
            switch (selectedAddress.getAddressType()) {
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
        if (selectedAddress.getArea() != null) {
            int selectedAreaId = selectedAddress.getArea();
            Area selectedArea = null;
            for (Governorate governorate : mMainActivity.mGovernorateList) {
                for (int p = 0; p < governorate.getAreas().size(); p++) {
                    Area area = governorate.getAreas().get(p);
                    int areaId = area.getId();
                    if (selectedAreaId == areaId) {
                        selectedArea = area;
                        break;
                    }
                }
            }
            if (selectedArea != null) {
                String name;
                if (mMainActivity.mAppLanguage.contains("ar"))
                    name = selectedArea.getNameAr();
                else
                    name = selectedArea.getNameEn();

                areaTv.setTypeface(mMontserratRegular);
                areaTv.setText(getResources().getString(R.string.area)
                        .concat(" ").concat(name));
            }
        }

        TextView blockStreetTv = mAddressView.findViewById(R.id.address_item_block_streetTv);
        String blockSt = "";
        if (selectedAddress.getBlock() != null)
            blockSt = getResources().getString(R.string.block)
                    .concat(" ").concat(selectedAddress.getBlock());
        if (selectedAddress.getStreet() != null)
            blockSt = blockSt.concat(", ")
                    .concat(getResources().getString(R.string.street))
                    .concat(": ").concat(selectedAddress.getStreet());

        blockStreetTv.setTypeface(mMontserratRegular);
        blockStreetTv.setText(blockSt);

        TextView othersTv = mAddressView.findViewById(R.id.address_item_othersTv);
        String others = "";
        if (selectedAddress.getAvenue() != null)
            others = selectedAddress.getAvenue();

        if (selectedAddress.getBuilding() != null) {
            if (others.isEmpty())
                others = selectedAddress.getBuilding();
            else
                others = others.concat(", ").concat(selectedAddress.getBuilding());
        }

        if (selectedAddress.getFloor() != null) {
            if (others.isEmpty())
                others = selectedAddress.getFloor();
            else
                others = others.concat(", ").concat(selectedAddress.getFloor());
        }

        if (selectedAddress.getApartment() != null) {
            if (others.isEmpty())
                others = selectedAddress.getApartment();
            else
                others = others.concat(", ").concat(String.valueOf(selectedAddress.getApartment()));
        }

        othersTv.setTypeface(mMontserratRegular);
        othersTv.setText(others);

    }

    private void setupItemsRv() {
        mItemsRv.setLayoutManager(new LinearLayoutManager(mMainActivity,
                LinearLayoutManager.VERTICAL, false));
        mItemsRvAdapter = new CheckoutRvAdapter(mMainActivity, mMainActivity.mAppLanguage, mOrder.getLines());
        mItemsRv.setAdapter(mItemsRvAdapter);
        mItemsRv.setNestedScrollingEnabled(false);
        mItemsRv.setItemAnimator(new DefaultItemAnimator());
    }

    private void fillAmounts() {
        String paymentMethod = null;

        switch (mOrder.getPaymentMethod()) {
            case CASH:
                paymentMethod = getResources().getString(R.string.cash);
                break;
            case KNET:
                paymentMethod = getResources().getString(R.string.knet);
                break;
            case VISA:
                paymentMethod = getResources().getString(R.string.visaMaster);
                break;
        }

        mPaymentMethodTv.setText(paymentMethod);

        mSubTotalTv.setText(Utils.makeSureThreeNAfterDot(mOrder.getTotalExclTax()));
        mDeliveryTv.setText(Utils.makeSureThreeNAfterDot(mOrder.getShippingPrice()));
        if (mOrder.getVoucherDiscount() != null && mOrder.getVoucherDiscount() != 0) {
            mDiscountLayout.setVisibility(View.VISIBLE);
            mDiscountTv.setText(Utils.makeSureThreeNAfterDot(mOrder.getVoucherDiscount()));
        }
        mTotalAmountTv.setText(Utils.makeSureThreeNAfterDot(mOrder.getTotalInclTax()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.orderDetailFragment_backLayout:
                mMainActivity.onBackPressed();
                break;
        }
    }
}
