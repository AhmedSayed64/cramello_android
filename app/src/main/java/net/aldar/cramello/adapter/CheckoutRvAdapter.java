package net.aldar.cramello.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.aldar.cramello.R;
import net.aldar.cramello.model.response.basket.BasketLine;
import net.aldar.cramello.services.Utils;

import java.util.List;

import static net.aldar.cramello.view.App.mRobotoRegular;

public class CheckoutRvAdapter extends RecyclerView.Adapter<CheckoutRvAdapter.ViewHolder> {

    public List<BasketLine> mCheckoutList;
    private String mAppLang;
    private Context mContext;

    public CheckoutRvAdapter(Context context, String lang, List<BasketLine> lines) {
        mCheckoutList = lines;
        mAppLang = lang;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public TextView mNameTv;
        public TextView mTotalCostTv;


        public ViewHolder(View view) {
            super(view);
            mView = view;

            mNameTv = view.findViewById(R.id.checkout_product_item_nameTv);
            mTotalCostTv = view.findViewById(R.id.checkout_product_item_totalPriceTv);
        }
    }

    public BasketLine getValueAt(int position) {
        return mCheckoutList.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_checkout_product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mCheckoutList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final BasketLine basketLine = getValueAt(position);

        String name;

        if (mAppLang.contains("ar"))
            name = basketLine.getProduct().getNameAr();
        else
            name = basketLine.getProduct().getNameEn();

        holder.mNameTv.setText(String.valueOf(basketLine.getQuantity())
                .concat(mContext.getResources().getString(R.string.x)
                        .concat(" ")
                        .concat(name)));
        holder.mNameTv.setTypeface(mRobotoRegular);

        holder.mTotalCostTv.setText(Utils.makeSureThreeNAfterDot(basketLine.getAmount()));
        holder.mTotalCostTv.setTypeface(mRobotoRegular);

    }
}
