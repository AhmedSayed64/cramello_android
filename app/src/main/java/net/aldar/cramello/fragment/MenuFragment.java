package net.aldar.cramello.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thoughtbot.expandablerecyclerview.listeners.GroupExpandCollapseListener;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import net.aldar.cramello.CartActivity;
import net.aldar.cramello.MainActivity;
import net.aldar.cramello.ProductDetailActivity;
import net.aldar.cramello.R;
import net.aldar.cramello.adapter.Expand.MenuRvAdapter;
import net.aldar.cramello.entitie.CategoryAndProducts;
import net.aldar.cramello.model.Address;
import net.aldar.cramello.model.response.product.Product;
import net.aldar.cramello.model.response.product.ProductCategory;
import net.aldar.cramello.services.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static net.aldar.cramello.App.KEY_ADDRESS_DATA;
import static net.aldar.cramello.App.KEY_PRODUCT_DATA;
import static net.aldar.cramello.App.KEY_QTY;
import static net.aldar.cramello.App.mMontserratRegular;
import static net.aldar.cramello.MainActivity.REQUEST_PRODUCT_DETAIL;

public class MenuFragment extends RootFragment implements View.OnClickListener {

    private int CHILD_VIEWED = 2;
    private Handler mHandler;
    private Gson mGson;

    private TextView mTitleTv;
    private LinearLayout mBackLayout;
    private ImageView mBackIv;

    private RelativeLayout mCartLayout;
    private TextView mCartTv;

    private TextView mHintTv;

    private LinearLayout mTotalLayout;
    private TextView mTotalTv;
    private Button mViewCartBtn;

    private RecyclerView mProductsRv;
    private MenuRvAdapter mMenuAdapter;
    private List<CategoryAndProducts> mProductsList;

    private MainActivity mMainActivity;
    private Address mAddress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
        mHandler = new Handler();

        if (getArguments() != null) {
            mGson = new Gson();
            String json = getArguments().getString(KEY_ADDRESS_DATA);
            Type type = new TypeToken<Address>() {
            }.getType();
            mAddress = mGson.fromJson(json, type);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        mTitleTv = view.findViewById(R.id.menuFragment_title);
        mBackLayout = view.findViewById(R.id.menuFragment_backLayout);
        mBackIv = view.findViewById(R.id.menuFragment_backIv);

        mCartLayout = view.findViewById(R.id.menuFragment_cartLayout);
        mCartTv = view.findViewById(R.id.menuFragment_cartTv);

        mHintTv = view.findViewById(R.id.menuFragment_hintTv);

        mTotalLayout = view.findViewById(R.id.menuFragment_totalLayout);
        mTotalTv = view.findViewById(R.id.menuFragment_totalTv);
        mViewCartBtn = view.findViewById(R.id.menuFragment_viewCartBtn);

        mProductsRv = view.findViewById(R.id.menuFragment_productsRv);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mTitleTv.setTypeface(mMontserratRegular);
        mBackLayout.setOnClickListener(this);
        Utils.submitRotation(mBackIv, mMainActivity.mPrefsManger);

        mCartTv.setTypeface(mMontserratRegular);
        mCartLayout.setOnClickListener(this);

        mHintTv.setTypeface(mMontserratRegular);
        mHintTv.setText(getResources().getString(R.string.minOrder)
                .concat(" ")
                .concat(getResources().getString(R.string.kd))
                .concat(" ")
                .concat(Utils.makeSureThreeNAfterDot(Double.parseDouble(mAddress.getSelectedArea().getArea_minimum_order()))));

        mTotalTv.setTypeface(mMontserratRegular);
        mViewCartBtn.setTypeface(mMontserratRegular);
        mViewCartBtn.setOnClickListener(this);
        mTotalLayout.setVisibility(View.GONE);

        updateCartCounter();
        setupProductsRv();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menuFragment_backLayout:
                mMainActivity.onBackPressed();
                break;
            case R.id.menuFragment_cartLayout:
            case R.id.menuFragment_viewCartBtn:
                openCartScreen();
                break;
        }
    }

    private void openCartScreen() {
        Intent intent = new Intent(mMainActivity, CartActivity.class);
//        intent.putExtra(KEY_MIN_ORDER_VALUE, mMainActivity.mMinOrder);
        intent.putExtra(KEY_ADDRESS_DATA, mGson.toJson(mAddress));
        startActivity(intent);
    }

    private void setupProductsRv() {

        mProductsList = new ArrayList<>();
        for (ProductCategory category : mMainActivity.mCategoryList) {
            int categoryId = category.getId();
            List<Product> products = new ArrayList<>();
            for (Product product : mMainActivity.mProductsList) {
                int productCatId = product.getCategory().getId();
                if (categoryId == productCatId) {
                    boolean branchFound = false;
                    branchesComparing:
                    {
                        for (int ab = 0; ab < mAddress.getSelectedArea().getBranches().size(); ab++) {
                            int areaBranchId = mAddress.getSelectedArea().getBranches().get(ab);
                            for (int pa = 0; pa < product.getBranches().size(); pa++) {
                                int productBranchId = product.getBranches().get(pa);
                                if (areaBranchId == productBranchId) {
                                    branchFound = true;
                                    break branchesComparing;
                                }
                            }
                        }
                    }
                    if (branchFound)
                        products.add(product);
                }
            }
            if (!products.isEmpty()) {
                String name;
                if (mMainActivity.mAppLanguage.contains("ar"))
                    name = category.getNameAr();
                else
                    name = category.getNameEn();

                Collections.sort(products, new Comparator<Product>() {
                    @Override
                    public int compare(Product s1, Product s2) {
                        if (mMainActivity.mAppLanguage.equals("ar"))
                            return s1.getNameAr().compareToIgnoreCase(s2.getNameAr());
                        else
                            return s1.getNameEn().compareToIgnoreCase(s2.getNameEn());
                    }
                });

                mProductsList.add(new CategoryAndProducts(name, category, products));
            }
        }

        mProductsRv.setLayoutManager(new LinearLayoutManager(mMainActivity, LinearLayoutManager.VERTICAL, false));
        mMenuAdapter = new MenuRvAdapter(mMainActivity, MenuFragment.this, mMainActivity.mAppLanguage, mProductsList);
        mMenuAdapter.setOnGroupExpandCollapseListener(new GroupExpandCollapseListener() {
            @Override
            public void onGroupExpanded(ExpandableGroup group) {
                ExpandableGroup expGroup = mProductsList.get(mProductsList.size() - 1);
                if (expGroup.equals(group)) {
                    mProductsRv.smoothScrollToPosition(mProductsList.size() - 1 + CHILD_VIEWED);
                } else {
                    CHILD_VIEWED += group.getItemCount();
                }
            }

            @Override
            public void onGroupCollapsed(ExpandableGroup group) {
                ExpandableGroup expGroup = mProductsList.get(mProductsList.size() - 1);
                if (!expGroup.equals(group)) {
                    CHILD_VIEWED -= group.getItemCount();
                }
            }
        });
        mProductsRv.setAdapter(mMenuAdapter);
        mProductsRv.addItemDecoration(new DividerItemDecoration(mProductsRv.getContext(),
                LinearLayoutManager.VERTICAL));
    }

    public void goToProductDetailScreen(Product item) {
        Intent intent = new Intent(mMainActivity, ProductDetailActivity.class);
        intent.putExtra(KEY_ADDRESS_DATA, mGson.toJson(mAddress));
        intent.putExtra(KEY_PRODUCT_DATA, mGson.toJson(item));
//        intent.putExtra(KEY_MIN_ORDER_VALUE, mMainActivity.mMinOrder);
        mMainActivity.startActivityForResult(intent, REQUEST_PRODUCT_DETAIL);
    }

    public void addQuantity(String name, Product item, int qty) {

//        List<Product> products = getCartList();
//
//        if (!products.isEmpty()) {
//            boolean found = false;
//            for (int p = 0; p < products.size(); p++) {
//                Product product = products.get(p);
//                int productId = product.getId();
//                int itemId = item.getId();
//                if (itemId == productId) {
//                    found = true;
//                    product.setQuantity(product.getQuantity() + qty);
//                    if (item.getSpecialHint() != null && !item.getSpecialHint().isEmpty())
//                        product.setSpecialHint(item.getSpecialHint());
//                    break;
//                }
//            }
//            if (!found) {
//                item.setQuantity(qty);
//                products.add(item);
//            }
//        } else {
//            item.setQuantity(qty);
//            products.add(item);
//        }
//
//        mMainActivity.mPrefsManger.saveCart(products);
//        Utils.makeAToast(mMainActivity, name.concat(" ")
//                .concat(getResources().getString(R.string.itemAddedSuccess)));
        Utils.addQuantityToCart(mMainActivity, mMainActivity.mPrefsManger, name, item, qty);
        flashTheTotal();
        updateCartCounter();
    }

    private void flashTheTotal() {
        List<Product> products = getCartList();

        mHandler.removeCallbacksAndMessages(null);
        double total = 0;

        for (int p = 0; p < products.size(); p++) {
            Product product = products.get(p);
            double priceXQuantity = product.getQuantity() * product.getDiscountedPrice();
            total = total + priceXQuantity;
        }

        mTotalTv.setText(getResources().getString(R.string.total)
                .concat(" ")
                .concat(Utils.makeSureThreeNAfterDot(total)));

        mTotalLayout.setVisibility(View.VISIBLE);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTotalLayout.setVisibility(View.GONE);
            }
        }, 2000);

    }

    public void updateCartCounter() {
        int cartSize = getCartList().size();
        mCartTv.setText(String.valueOf(cartSize));
    }

    private List<Product> getCartList() {
        return mMainActivity.mPrefsManger.loadCart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PRODUCT_DETAIL && data != null && data.getExtras() != null) {
                if (data.hasExtra(KEY_QTY) && data.hasExtra(KEY_PRODUCT_DATA)) {
                    String json = data.getExtras().getString(KEY_PRODUCT_DATA);
                    Type type = new TypeToken<Product>() {
                    }.getType();
                    Product product = mGson.fromJson(json, type);

                    String name;
                    if (mMainActivity.mAppLanguage.contains("ar"))
                        name = product.getNameAr();
                    else
                        name = product.getNameEn();

                    int qty = data.getExtras().getInt(KEY_QTY);

                    addQuantity(name, product, qty);
                }
            }
        }
    }
}
