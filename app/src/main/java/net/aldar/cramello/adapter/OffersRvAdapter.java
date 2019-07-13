package net.aldar.cramello.adapter;

import android.graphics.Paint;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.aldar.cramello.R;
import net.aldar.cramello.fragment.OffersFragment;
import net.aldar.cramello.model.response.product.Product;
import net.aldar.cramello.services.Utils;

import java.util.List;

import static net.aldar.cramello.view.App.mMontserratRegular;
import static net.aldar.cramello.view.App.mRobotoRegular;

public class OffersRvAdapter extends RecyclerView.Adapter<OffersRvAdapter.ViewHolder> {

    public List<Product> mOffersList;
    private OffersFragment mOffersFragment;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public ImageView mProductImage;
        public ProgressBar mProgressBar;
        public TextView mNameTv;
        public TextView mDescTv;
        public TextView mOriginalPriceTv;
        public TextView mDiscountedPriceTv;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mProductImage = view.findViewById(R.id.offer_item_imageIv);
            mProgressBar = view.findViewById(R.id.offer_item_progressBar);

            mNameTv = view.findViewById(R.id.offer_item_nameTv);
            mDescTv = view.findViewById(R.id.offer_item_descTv);
            mOriginalPriceTv = view.findViewById(R.id.offer_item_originalPriceTv);
            mDiscountedPriceTv = view.findViewById(R.id.offer_item_discountedPriceTv);
        }
    }

    public Product getValueAt(int position) {
        return mOffersList.get(position);
    }

    public OffersRvAdapter(OffersFragment offersFragment, List<Product> products) {
        mOffersFragment = offersFragment;
        mOffersList = products;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_offer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mOffersList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Product product = getValueAt(position);

        final String name;
        String desc;
        if (mOffersFragment.mMainActivity.mAppLanguage.contains("ar")) {
            name = product.getNameAr();
            desc = product.getDescriptionAr();
        } else {
            name = product.getNameEn();
            desc = product.getDescriptionEn();
        }
        Spanned descSpanned = Html.fromHtml(desc);

        holder.mNameTv.setText(name);
        holder.mNameTv.setTypeface(mRobotoRegular);

        holder.mDescTv.setText(descSpanned);
        holder.mDescTv.setTypeface(mMontserratRegular);

        holder.mOriginalPriceTv.setText(Utils.makeSureThreeNAfterDot(product.getPrice()));
        holder.mOriginalPriceTv.setTypeface(mMontserratRegular);
        holder.mOriginalPriceTv.setPaintFlags(holder.mOriginalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.mDiscountedPriceTv.setText(Utils.makeSureThreeNAfterDot(product.getDiscountedPrice()));
        holder.mDiscountedPriceTv.setTypeface(mMontserratRegular);

        holder.mProgressBar.setVisibility(View.GONE);

        if (product.getFirstImage() != null)
            Utils.loadImageWithProgressBar(product.getFirstImage(), holder.mProductImage,
                    holder.mProgressBar, R.drawable.menu_item_placeholder);
        else
            holder.mProductImage.setImageResource(R.drawable.menu_item_placeholder);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOffersFragment.goToProductDetail(product);
            }
        });
    }
}
