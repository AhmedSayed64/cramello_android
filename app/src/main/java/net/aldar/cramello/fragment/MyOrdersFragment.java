package net.aldar.cramello.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import net.aldar.cramello.MainActivity;
import net.aldar.cramello.R;
import net.aldar.cramello.adapter.OrderRvAdapter;
import net.aldar.cramello.model.response.Order;
import net.aldar.cramello.model.response.basket.BasketLine;
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

import static net.aldar.cramello.App.KEY_ORDER_DATA;
import static net.aldar.cramello.App.mMontserratBold;
import static net.aldar.cramello.App.mMontserratRegular;
import static net.aldar.cramello.MainActivity.ORDER_DETAIL_FRAGMENT_TAG;

public class MyOrdersFragment extends RootFragment implements View.OnClickListener {

    private TextView mTitleTv;
    private LinearLayout mMenuLayout;
    private ImageView mMenuIv;
    private TextView mEmptyTv;

    private RecyclerView mOrdersRv;
    private OrderRvAdapter mOrderRvAdapter;
    private List<Order> mOrdersList;

    public MainActivity mMainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        mTitleTv = view.findViewById(R.id.myOrdersFragment_title);
        mMenuLayout = view.findViewById(R.id.myOrdersFragment_menuLayout);
        mMenuIv = view.findViewById(R.id.myOrdersFragment_menuIv);
        mEmptyTv = view.findViewById(R.id.myOrdersFragment_emptyTv);

        mOrdersRv = view.findViewById(R.id.myOrdersFragment_ordersRv);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mTitleTv.setTypeface(mMontserratRegular);
        mMenuLayout.setOnClickListener(this);
        Utils.submitFlip(mMenuIv, mMainActivity.mPrefsManger);

        mEmptyTv.setTypeface(mMontserratBold);
        mEmptyTv.setVisibility(View.GONE);

        getOrders();
    }

    private void getOrders() {
        if (Utils.isConnectionOn(mMainActivity)) {
            mMainActivity.mSpinKitLayout.setVisibility(View.VISIBLE);

            Call<List<Order>> call = mMainActivity.mServiceApi.getMyOrders(mMainActivity.mAuthToken,
                    null, mMainActivity.mUserId);
            call.enqueue(new Callback<List<Order>>() {
                @Override
                public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                    mMainActivity.mSpinKitLayout.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            mOrdersList = response.body();
                            setupOrdersList();
                        } catch (Exception e) {
                            Log.e("GOrders Ex / ", e.getMessage() + "");
                            Utils.makeAToast(mMainActivity, getResources().getString(R.string.getOrdersFailed));
                        }
                    } else {
                        Log.e("GOrders Code / ", "Code not Successful");
                        Utils.makeAToast(mMainActivity, getResources().getString(R.string.getOrdersFailed));
                    }
                }

                @Override
                public void onFailure(Call<List<Order>> call, Throwable t) {
                    mMainActivity.mSpinKitLayout.setVisibility(View.GONE);
                    Log.e("GOrders Fail / ", t.getMessage() + "");
                    Utils.makeAToast(mMainActivity, getResources().getString(R.string.getOrdersFailed));
                }
            });
        } else
            Utils.makeAToast(mMainActivity, getResources().getString(R.string.connection_offline));
    }

    private void setupOrdersList() {

        Collections.sort(mOrdersList, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return o2.getCreated().compareTo(o1.getCreated());
            }
        });


        mOrdersRv.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        mOrderRvAdapter = new OrderRvAdapter(getActivity(), this, mOrdersList);
        mOrdersRv.setAdapter(mOrderRvAdapter);
        mOrdersRv.setItemAnimator(new DefaultItemAnimator());

        if (mOrdersList == null || mOrdersList.isEmpty()) {
            mEmptyTv.setVisibility(View.VISIBLE);
            mOrdersRv.setVisibility(View.GONE);
        } else {
            mEmptyTv.setVisibility(View.GONE);
            mOrdersRv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myOrdersFragment_menuLayout:
                mMainActivity.openDrawer();
                break;
        }
    }

    public void reorder(int position) {
        List<Product> mCartList = mMainActivity.mPrefsManger.loadCart();

        Order selectedOrder = mOrdersList.get(position);
        Area storedDeliveryArea = mMainActivity.mPrefsManger.getDeliveryArea();
        Area orderArea = null;

        for (Governorate governorate : mMainActivity.mGovernorateList) {
            for (int p = 0; p < governorate.getAreas().size(); p++) {
                Area area = governorate.getAreas().get(p);
                int areaId = area.getId();
                if (selectedOrder.getShippingAddress().getArea() == areaId) {
                    orderArea = area;
                    break;
                }
            }
        }

        if (storedDeliveryArea != null) {
            if (mMainActivity.mPrefsManger.loadCart().size() != 0) {
                int storedDeliveryAreaId = storedDeliveryArea.getId();
                int selectedAreaId = orderArea.getId();
                if (storedDeliveryAreaId != selectedAreaId)
                    mCartList = new ArrayList<>();
            }
        }

        for (int c = 0; c < selectedOrder.getLines().size(); c++) {
            BasketLine basketLine = selectedOrder.getLines().get(c);

            Product product = basketLine.getProduct();
            product.setQuantity(basketLine.getQuantity());
            product.setSpecialHint(basketLine.getComment());

            mCartList.add(product);
        }

        mMainActivity.mPrefsManger.saveCart(mCartList);
        mMainActivity.mPrefsManger.setDeliveryArea(orderArea);

        Utils.makeAToast(mMainActivity, getResources().getString(R.string.reorderAddedToCart));
    }

    public void openDetailScreen(Order order) {

        Bundle bundle = new Bundle();
        bundle.putString(KEY_ORDER_DATA, new Gson().toJson(order));

        Utils.openChildFragment(MyOrdersFragment.this, new OrderDetailFragment(), bundle,
                R.id.myOrdersFragment_containerLayout, ORDER_DETAIL_FRAGMENT_TAG);

    }
}
