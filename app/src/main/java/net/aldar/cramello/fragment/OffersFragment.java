package net.aldar.cramello.fragment;

import android.content.Intent;
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

import net.aldar.cramello.R;
import net.aldar.cramello.adapter.OffersRvAdapter;
import net.aldar.cramello.model.response.product.Product;
import net.aldar.cramello.services.Utils;
import net.aldar.cramello.view.MainActivity;
import net.aldar.cramello.view.ProductDetailActivity;

import java.util.ArrayList;
import java.util.List;

import static net.aldar.cramello.view.App.KEY_PRODUCT_DATA;
import static net.aldar.cramello.view.App.mMontserratBold;
import static net.aldar.cramello.view.App.mMontserratRegular;

public class OffersFragment extends RootFragment implements View.OnClickListener {

    private TextView mTitleTv;
    private LinearLayout mMenuLayout;
    private ImageView mMenuIv;
    private TextView mEmptyTv;

    private RecyclerView mOffersRv;
    private OffersRvAdapter mOffersRvAdapter;
    private List<Product> mOffersList;

    public MainActivity mMainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offers, container, false);

        mTitleTv = view.findViewById(R.id.offersFragment_title);
        mMenuLayout = view.findViewById(R.id.offersFragment_menuLayout);
        mMenuIv = view.findViewById(R.id.offersFragment_menuIv);
        mEmptyTv = view.findViewById(R.id.offersFragment_emptyTv);

        mOffersRv = view.findViewById(R.id.offersFragment_offersRv);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mTitleTv.setTypeface(mMontserratRegular);
        mMenuLayout.setOnClickListener(this);
        Utils.submitFlip(mMenuIv, mMainActivity.mPrefsManger);

        mEmptyTv.setTypeface(mMontserratBold);
        mEmptyTv.setVisibility(View.GONE);

        setupOffers();
    }

    private void setupOffers() {
        mOffersList = new ArrayList<>();
        List<Product> products = mMainActivity.mProductsList;

        for (int p = 0; p < products.size(); p++) {
            if (products.get(p).hasAvailableOffer())
                mOffersList.add(products.get(p));
        }

        mOffersRv.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL, false));
        mOffersRvAdapter = new OffersRvAdapter(OffersFragment.this, mOffersList);
        mOffersRv.setAdapter(mOffersRvAdapter);
        mOffersRv.setItemAnimator(new DefaultItemAnimator());

        if (mOffersList == null || mOffersList.isEmpty()) {
            mEmptyTv.setVisibility(View.VISIBLE);
            mOffersRv.setVisibility(View.GONE);
        } else {
            mEmptyTv.setVisibility(View.GONE);
            mOffersRv.setVisibility(View.VISIBLE);
        }
    }

    public void goToProductDetail(Product product) {
        Intent intent = new Intent(mMainActivity, ProductDetailActivity.class);
        intent.putExtra(KEY_PRODUCT_DATA, new Gson().toJson(product));
        mMainActivity.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.offersFragment_menuLayout:
                mMainActivity.openDrawer();
                break;
        }
    }
}
