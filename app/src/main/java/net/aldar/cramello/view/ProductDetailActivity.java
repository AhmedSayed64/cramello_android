package net.aldar.cramello.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rbrooks.indefinitepagerindicator.IndefinitePagerIndicator;

import net.aldar.cramello.R;
import net.aldar.cramello.adapter.SliderPVAdapter;
import net.aldar.cramello.model.Address;
import net.aldar.cramello.model.response.product.Product;
import net.aldar.cramello.services.LocaleHelper;
import net.aldar.cramello.services.PrefsManger;
import net.aldar.cramello.services.Utils;

import java.lang.reflect.Type;

import static net.aldar.cramello.view.App.KEY_ADDRESS_DATA;
import static net.aldar.cramello.view.App.KEY_PRODUCT_DATA;
import static net.aldar.cramello.view.App.KEY_QTY;
import static net.aldar.cramello.view.App.mMontserratRegular;
import static net.aldar.cramello.view.App.mRobotoRegular;

public class ProductDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTitleTv;
    private LinearLayout mBackLayout;
    private ImageView mBackIv;

    private RelativeLayout mCartLayout;
    private TextView mCartTv;

    private LinearLayout mShareLayout;

    private ImageView mImageIv;
    private ProgressBar mProgressBar;

    private TextView mItemNameTv;
    private TextView mItemDescTv;
    private TextView mNotAvailableTv;
    private TextView mItemOriginalPriceTv;
    private TextView mItemDiscountedPriceTv;

    private CardView mSpecialCard;
    private TextView mSpecialTv;
    private TextView mOptionalTv;
    private EditText mSpecialInput;

    private CardView mQtyCard;
    private TextView mQtyTv;
    private TextView mSubQtyTv;
    private TextView mIncQtyTv;
    private TextView mRequestedQtyTv;

    private Button mAddBtn;
    private FrameLayout mRootLayout;

    private PrefsManger mPrefsManger;
    private Product mProduct;
    private Gson mGson;

    //    private double mMinOrder;
    private Address mAddress;

    @Override
    protected void attachBaseContext(Context newBase) {
        mPrefsManger = new PrefsManger(newBase);
        super.attachBaseContext(LocaleHelper.wrap(newBase, mPrefsManger.getAppLanguage()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        mGson = new Gson();

        if (getIntent().hasExtra(KEY_ADDRESS_DATA)) {
            String json = getIntent().getExtras().getString(KEY_ADDRESS_DATA);
            Type type = new TypeToken<Address>() {
            }.getType();
            mAddress = new Gson().fromJson(json, type);
        }
        if (getIntent().hasExtra(KEY_PRODUCT_DATA)) {
            String json = getIntent().getExtras().getString(KEY_PRODUCT_DATA);
            Type type = new TypeToken<Product>() {
            }.getType();
            mProduct = mGson.fromJson(json, type);
        }
//        if (getIntent().hasExtra(KEY_MIN_ORDER_VALUE))
//            mMinOrder = getIntent().getExtras().getDouble(KEY_MIN_ORDER_VALUE);

        mRootLayout = findViewById(R.id.root_layout);
        Utils.setupHideKeyboard(this, mRootLayout);

        mTitleTv = findViewById(R.id.productDetail_activity_title);
        mTitleTv.setTypeface(mMontserratRegular);

        mBackLayout = findViewById(R.id.productDetail_activity_backLayout);
        mBackIv = findViewById(R.id.productDetail_activity_backIv);
        mBackLayout.setOnClickListener(this);
        Utils.submitRotation(mBackIv, mPrefsManger);

        mCartLayout = findViewById(R.id.productDetail_activity_cartLayout);
        mCartTv = findViewById(R.id.productDetail_activity_cartTv);
        mCartTv.setTypeface(mMontserratRegular);
        mCartLayout.setOnClickListener(this);
        mCartTv.setText(String.valueOf(mPrefsManger.loadCart().size()));

        mShareLayout = findViewById(R.id.productDetail_activity_shareLayout);
        mShareLayout.setOnClickListener(this);

        mImageIv = findViewById(R.id.productDetail_activity_itemImage);
        mImageIv.setOnClickListener(this);
        mProgressBar = findViewById(R.id.productDetail_activity_progressBar);
        mProgressBar.setVisibility(View.GONE);

        mItemNameTv = findViewById(R.id.productDetail_activity_nameTv);
        mItemNameTv.setTypeface(mRobotoRegular);

        mItemDescTv = findViewById(R.id.productDetail_activity_descTv);
        mItemDescTv.setTypeface(mMontserratRegular);

        mNotAvailableTv = findViewById(R.id.productDetail_activity_notAvailableTv);
        mNotAvailableTv.setTypeface(mMontserratRegular);

        mItemOriginalPriceTv = findViewById(R.id.productDetail_activity_originalPriceTv);
        mItemOriginalPriceTv.setTypeface(mRobotoRegular);

        mItemDiscountedPriceTv = findViewById(R.id.productDetail_activity_discountedPriceTv);
        mItemDiscountedPriceTv.setTypeface(mRobotoRegular);

        mSpecialCard = findViewById(R.id.productDetail_activity_specialCardView);
        mSpecialTv = findViewById(R.id.productDetail_activity_specialRequestTitle);
        mSpecialTv.setTypeface(mRobotoRegular);
        mOptionalTv = findViewById(R.id.productDetail_activity_optionalTitle);
        mOptionalTv.setTypeface(mRobotoRegular);

        mSpecialInput = findViewById(R.id.productDetail_activity_specialRequestInput);
        mSpecialInput.setTypeface(mMontserratRegular);

        mQtyCard = findViewById(R.id.productDetail_activity_qtyCardView);
        mQtyTv = findViewById(R.id.productDetail_activity_qtyTitle);
        mQtyTv.setTypeface(mRobotoRegular);

        mSubQtyTv = findViewById(R.id.productDetail_activity_subBtn);
        mSubQtyTv.setTypeface(mRobotoRegular);
        mSubQtyTv.setOnClickListener(this);

        mIncQtyTv = findViewById(R.id.productDetail_activity_incBtn);
        mIncQtyTv.setTypeface(mRobotoRegular);
        mIncQtyTv.setOnClickListener(this);

        mRequestedQtyTv = findViewById(R.id.productDetail_activity_requestQtyTv);
        mRequestedQtyTv.setTypeface(mRobotoRegular);

        mAddBtn = findViewById(R.id.productDetail_activity_addBtn);
        mAddBtn.setTypeface(mRobotoRegular);
        mAddBtn.setOnClickListener(this);

        fillProductInfo();
    }

    private void fillProductInfo() {
        String name;
        Spanned descSpanned;

        if (mPrefsManger.getAppLanguage().contains("ar")) {
            name = mProduct.getNameAr();
            descSpanned = Html.fromHtml(mProduct.getDescriptionAr());
        } else {
            name = mProduct.getNameEn();
            descSpanned = Html.fromHtml(mProduct.getDescriptionEn());
        }

        mItemNameTv.setText(name);
        mItemDescTv.setText(descSpanned);

        if (mProduct.getStock() == 0) {
            mSpecialCard.setVisibility(View.GONE);
            mQtyCard.setVisibility(View.GONE);
            mAddBtn.setVisibility(View.GONE);
            mNotAvailableTv.setVisibility(View.VISIBLE);
        } else {
            mSpecialCard.setVisibility(View.VISIBLE);
            mQtyCard.setVisibility(View.VISIBLE);
            mAddBtn.setVisibility(View.VISIBLE);
            mNotAvailableTv.setVisibility(View.GONE);
        }

        if (mProduct.hasAvailableOffer()) {
            mItemOriginalPriceTv.setText(getResources().getString(R.string.kd)
                    .concat(" ")
                    .concat(Utils.makeSureThreeNAfterDot(mProduct.getPrice())));
            mItemOriginalPriceTv.setPaintFlags(mItemDiscountedPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else
            mItemOriginalPriceTv.setVisibility(View.GONE);


        mItemDiscountedPriceTv.setText(getResources().getString(R.string.kd)
                .concat(" ")
                .concat(Utils.makeSureThreeNAfterDot(mProduct.getDiscountedPrice())));

        if (mProduct.getImages() != null && !mProduct.getImages().isEmpty()) {
            String imageUrl = mProduct.getImages().get(0).getImage();
            if (imageUrl != null && !imageUrl.isEmpty())
                Utils.loadImageWithProgressBar(imageUrl, mImageIv, mProgressBar, R.drawable.menu_item_placeholder);
            else
                mImageIv.setImageResource(R.drawable.menu_item_placeholder);
        } else
            mImageIv.setImageResource(R.drawable.menu_item_placeholder);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.productDetail_activity_backLayout:
                finish();
                break;

            case R.id.productDetail_activity_cartLayout:
                if (mAddress != null) {
                    Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
//                intent.putExtra(KEY_MIN_ORDER_VALUE, mMinOrder);
                    intent.putExtra(KEY_ADDRESS_DATA, mGson.toJson(mAddress));
                    startActivity(intent);
                    finish();
                } else
                    Utils.makeAToast(ProductDetailActivity.this, getResources().getString(R.string.selectAddressFirst));
                break;

            case R.id.productDetail_activity_shareLayout:
                if (mProduct.getFirstImage() != null)
                    Utils.shareProduct(ProductDetailActivity.this, mProduct.getFirstImage());
                break;

            case R.id.productDetail_activity_itemImage:
                showSlider();
                break;

            case R.id.productDetail_activity_subBtn:
                subQty();
                break;

            case R.id.productDetail_activity_incBtn:
                increaseQty();
                break;

            case R.id.productDetail_activity_addBtn:
                addProductQty();
                break;
        }
    }

    private void showSlider() {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(this, R.style.TransDialog);
        final AlertDialog alertDialog;
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_slider, null);
        aBuilder.setView(dialogView);
        alertDialog = aBuilder.create();

        ViewPager viewPager = dialogView.findViewById(R.id.dialog_slider_viewPager);
        IndefinitePagerIndicator mIndicator = dialogView.findViewById(R.id.dialog_slider_indicator);
        viewPager.setAdapter(new SliderPVAdapter(this, mProduct.getImages()));
        mIndicator.attachToViewPager(viewPager);

        alertDialog.show();
    }


    private void subQty() {
        if (Integer.valueOf(mRequestedQtyTv.getText().toString()) > 1) {
            mRequestedQtyTv.setText((String.valueOf(Integer.valueOf(mRequestedQtyTv.getText().toString()) - 1)));
        } else if (Integer.valueOf(mRequestedQtyTv.getText().toString()) == 1) {
            mRequestedQtyTv.setText("1");
        }
    }

    private void increaseQty() {
        mRequestedQtyTv.setText(String.valueOf(Integer.valueOf(mRequestedQtyTv.getText().toString()) + 1));
    }

    private void addProductQty() {
        mProduct.setSpecialHint(mSpecialInput.getText().toString().trim());

        if (mAddress != null) {
            Intent intent = new Intent();
            intent.putExtra(KEY_QTY, Integer.parseInt(mRequestedQtyTv.getText().toString()));
            intent.putExtra(KEY_PRODUCT_DATA, mGson.toJson(mProduct));
            setResult(AppCompatActivity.RESULT_OK, intent);
        } else {
            Utils.addQuantityToCart(ProductDetailActivity.this, mPrefsManger, mItemNameTv.getText().toString(),
                    mProduct, Integer.parseInt(mRequestedQtyTv.getText().toString()));
        }

        finish();
    }
}