package net.aldar.cramello.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.aldar.cramello.R;
import net.aldar.cramello.model.response.product.Product;
import net.aldar.cramello.services.Utils;
import net.aldar.cramello.view.App;
import net.aldar.cramello.view.CartActivity;

import java.util.List;

import static net.aldar.cramello.view.App.mMontserratRegular;
import static net.aldar.cramello.view.App.mRobotoRegular;

public class CartRvAdapter extends RecyclerView.Adapter<CartRvAdapter.ViewHolder> {

    public List<Product> mCartList;
    private CartActivity mCartActivity;

    public CartRvAdapter(CartActivity cartActivity, List<Product> cart) {
        mCartList = cart;
        mCartActivity = cartActivity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public ImageView mImageIv;
        public ProgressBar mProgressBar;

        public TextView mNameTv;
        public TextView mOriginalPriceTv;
        public TextView mDiscountedPriceTv;

        public TextView mSubQtyTv;
        public TextView mIncQtyTv;
        public TextView mRequestedQtyTv;

        public TextView mTotalTv;
        public ImageView mDeleteIv;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mImageIv = view.findViewById(R.id.cart_item_imageIv);
            mProgressBar = view.findViewById(R.id.cart_item_progressBar);

            mNameTv = view.findViewById(R.id.cart_item_nameTv);
            mOriginalPriceTv = view.findViewById(R.id.cart_item_originalPriceTv);
            mDiscountedPriceTv = view.findViewById(R.id.cart_item_discountedPriceTv);

            mSubQtyTv = view.findViewById(R.id.cart_item_subBtn);
            mIncQtyTv = view.findViewById(R.id.cart_item_incBtn);
            mRequestedQtyTv = view.findViewById(R.id.cart_item_requestQtyTv);

            mTotalTv = view.findViewById(R.id.cart_item_totalCostTv);
            mDeleteIv = view.findViewById(R.id.cart_item_deleteIv);
        }
    }

    public Product getValueAt(int position) {
        return mCartList.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mCartList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Product product = getValueAt(position);

        String name;

        if (mCartActivity.mPrefsManger.getAppLanguage().contains("ar"))
            name = product.getNameAr();
        else
            name = product.getNameEn();

        holder.mNameTv.setText(name);
        holder.mNameTv.setTypeface(mRobotoRegular);

        holder.mOriginalPriceTv.setText(mCartActivity.getResources().getString(R.string.kd)
                .concat(" ")
                .concat(Utils.makeSureThreeNAfterDot(product.getPrice())));
        holder.mOriginalPriceTv.setPaintFlags(holder.mOriginalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.mOriginalPriceTv.setTypeface(mMontserratRegular);

        holder.mDiscountedPriceTv.setText(mCartActivity.getResources().getString(R.string.kd)
                .concat(" ")
                .concat(Utils.makeSureThreeNAfterDot(product.getDiscountedPrice())));
        holder.mDiscountedPriceTv.setTypeface(mMontserratRegular);

        if (product.hasAvailableOffer())
            holder.mOriginalPriceTv.setVisibility(View.VISIBLE);
        else
            holder.mOriginalPriceTv.setVisibility(View.GONE);

        holder.mRequestedQtyTv.setText(String.valueOf(product.getQuantity()));
        holder.mRequestedQtyTv.setTypeface(mMontserratRegular);

        updateItemTotal(holder.mTotalTv, product);
        holder.mTotalTv.setTypeface(mMontserratRegular);

        holder.mSubQtyTv.setTypeface(mMontserratRegular);
        holder.mSubQtyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.valueOf(holder.mRequestedQtyTv.getText().toString()) > 1) {
                    holder.mRequestedQtyTv.setText((String.valueOf(Integer.valueOf(holder.mRequestedQtyTv.getText().toString()) - 1)));
                } else if (Integer.valueOf(holder.mRequestedQtyTv.getText().toString()) == 1) {
                    holder.mRequestedQtyTv.setText("1");
                }
                product.setQuantity(Integer.parseInt(holder.mRequestedQtyTv.getText().toString()));
                updateItemTotal(holder.mTotalTv, product);
                mCartActivity.updateSubTotal();
            }
        });

        holder.mIncQtyTv.setTypeface(mMontserratRegular);
        holder.mIncQtyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mRequestedQtyTv.setText(String.valueOf(Integer.valueOf(holder.mRequestedQtyTv.getText().toString()) + 1));
                product.setQuantity(Integer.parseInt(holder.mRequestedQtyTv.getText().toString()));
                updateItemTotal(holder.mTotalTv, product);
                mCartActivity.updateSubTotal();
            }
        });

        holder.mDeleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCartActivity.deleteProduct(position);
            }
        });

        holder.mProgressBar.setVisibility(View.GONE);
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            String imageUrl = product.getImages().get(0).getImage();
            if (imageUrl != null && !imageUrl.isEmpty())
                Utils.loadImageWithProgressBar(App.getMenuImageResizeUrl(imageUrl), holder.mImageIv, holder.mProgressBar, R.drawable.menu_item_placeholder);
            else
                holder.mImageIv.setImageResource(R.drawable.menu_item_placeholder);
        } else
            holder.mImageIv.setImageResource(R.drawable.menu_item_placeholder);
    }

    private void updateItemTotal(TextView totalTv, Product product) {
        totalTv.setText(mCartActivity.getResources().getString(R.string.total)
                .concat(": ")
                .concat(mCartActivity.getResources().getString(R.string.kd)
                        .concat(" ")
                        .concat(Utils.makeSureThreeNAfterDot(product.getDiscountedPrice() * product.getQuantity()))));
    }
}
