package net.aldar.cramello.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import net.aldar.cramello.R;
import net.aldar.cramello.adapter.NotificationRvAdapter;
import net.aldar.cramello.model.response.Notification;
import net.aldar.cramello.model.response.product.Product;
import net.aldar.cramello.services.Utils;
import net.aldar.cramello.view.MainActivity;
import net.aldar.cramello.view.ProductDetailActivity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.aldar.cramello.view.App.KEY_PRODUCT_DATA;
import static net.aldar.cramello.view.App.mMontserratBold;
import static net.aldar.cramello.view.App.mMontserratRegular;

public class NotificationsFragment extends RootFragment implements View.OnClickListener {

    private TextView mTitleTv;
    private LinearLayout mMenuLayout;
    private ImageView mMenuIv;
    private TextView mEmptyTv;

    private RecyclerView mNotificationsRv;
    private NotificationRvAdapter mNotificationRvAdapter;
    private List<Notification> mNotificationsList;

    private MainActivity mMainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        mTitleTv = view.findViewById(R.id.notificationsFragment_title);
        mMenuLayout = view.findViewById(R.id.notificationsFragment_menuLayout);
        mMenuIv = view.findViewById(R.id.notificationsFragment_menuIv);
        mEmptyTv = view.findViewById(R.id.notificationsFragment_emptyTv);

        mNotificationsRv = view.findViewById(R.id.notificationsFragment_notificationsRv);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mMainActivity.mPrefsManger.setUnseenNotificationCount(0);
        mMainActivity.checkUnSeenNotification();

        mTitleTv.setTypeface(mMontserratRegular);
        mMenuLayout.setOnClickListener(this);
        Utils.submitFlip(mMenuIv, mMainActivity.mPrefsManger);

        mEmptyTv.setTypeface(mMontserratBold);
        mEmptyTv.setVisibility(View.GONE);

        getNotifications();
    }

    private void getNotifications() {
        if (Utils.isConnectionOn(mMainActivity)) {
            mMainActivity.mSpinKitLayout.setVisibility(View.VISIBLE);

            Call<List<Notification>> call = mMainActivity.mServiceApi.getNotificationsHistory(mMainActivity.mAuthToken);
            call.enqueue(new Callback<List<Notification>>() {
                @Override
                public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                    mMainActivity.mSpinKitLayout.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            mNotificationsList = response.body();
                            setupNotificationsList();
                        } catch (Exception e) {
                            Log.e("Notification Ex / ", e.getMessage() + "");
                            Utils.makeAToast(mMainActivity, getResources().getString(R.string.getNotificationsFailed));
                        }
                    } else {
                        Log.e("Notification Code / ", "Code not Successful");
                        Utils.makeAToast(mMainActivity, getResources().getString(R.string.getNotificationsFailed));
                    }
                }

                @Override
                public void onFailure(Call<List<Notification>> call, Throwable t) {
                    mMainActivity.mSpinKitLayout.setVisibility(View.GONE);
                    Log.e("Notification Fail / ", t.getMessage() + "");
                    Utils.makeAToast(mMainActivity, getResources().getString(R.string.getNotificationsFailed));
                }
            });
        } else
            Utils.makeAToast(mMainActivity, getResources().getString(R.string.connection_offline));
    }

    private void setupNotificationsList() {
        Collections.sort(mNotificationsList, new Comparator<Notification>() {
            @Override
            public int compare(Notification o1, Notification o2) {
                return o2.getCreated().compareTo(o1.getCreated());
            }
        });

        mNotificationsRv.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        mNotificationRvAdapter = new NotificationRvAdapter(NotificationsFragment.this, mNotificationsList);
        mNotificationsRv.setAdapter(mNotificationRvAdapter);
        mNotificationsRv.addItemDecoration(new DividerItemDecoration(mNotificationsRv.getContext(),
                LinearLayoutManager.VERTICAL));

        if (mNotificationsList == null || mNotificationsList.isEmpty()) {
            mEmptyTv.setVisibility(View.VISIBLE);
            mNotificationsRv.setVisibility(View.GONE);
        } else {
            mEmptyTv.setVisibility(View.GONE);
            mNotificationsRv.setVisibility(View.VISIBLE);
        }
    }

    public void goToProductDetail(int productId) {
        List<Product> products = mMainActivity.mProductsList;
        Product target = null;
        for (int p = 0; p < products.size(); p++) {
            int proId = products.get(p).getId();
            if (productId == proId) {
                target = products.get(p);
                break;
            }
        }

        Intent intent = new Intent(mMainActivity, ProductDetailActivity.class);
        intent.putExtra(KEY_PRODUCT_DATA, new Gson().toJson(target));
        mMainActivity.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.notificationsFragment_menuLayout:
                mMainActivity.openDrawer();
                break;
        }
    }
}
