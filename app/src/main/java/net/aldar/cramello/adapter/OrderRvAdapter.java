package net.aldar.cramello.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.aldar.cramello.R;
import net.aldar.cramello.fragment.MyOrdersFragment;
import net.aldar.cramello.model.response.Order;
import net.aldar.cramello.services.Utils;

import java.util.List;

import static net.aldar.cramello.App.mMontserratBold;
import static net.aldar.cramello.App.mMontserratRegular;

public class OrderRvAdapter extends RecyclerView.Adapter<OrderRvAdapter.ViewHolder> {

    public List<Order> mOrderList;
    private Context mContext;
    private MyOrdersFragment mMyOrdersFragment;

    public OrderRvAdapter(Context context, MyOrdersFragment fragment, List<Order> orders) {
        mOrderList = orders;
        mContext = context;
        mMyOrdersFragment = fragment;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public TextView mNameTv;
        public TextView mDateTv;
        public TextView mStatus;
        public TextView mOrderIdTv;
        public TextView mReorderTv;
        private LinearLayout mReorderLayout;


        public ViewHolder(View view) {
            super(view);
            mView = view;

            mNameTv = view.findViewById(R.id.order_item_titleTv);
            mDateTv = view.findViewById(R.id.order_item_dateTv);
            mStatus = view.findViewById(R.id.order_item_statusTv);
            mOrderIdTv = view.findViewById(R.id.order_item_orderIdTv);
            mReorderTv = view.findViewById(R.id.order_item_reorderTv);
            mReorderLayout = view.findViewById(R.id.order_item_reorderLayout);
        }
    }

    public Order getValueAt(int position) {
        return mOrderList.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Order order = getValueAt(position);

        String name;
        if (mMyOrdersFragment.mMainActivity.mAppLanguage.contains("ar"))
            name = order.getLines().get(0).getProduct().getNameAr();
        else
            name = order.getLines().get(0).getProduct().getNameEn();

        holder.mNameTv.setText(name);
        holder.mNameTv.setTypeface(mMontserratBold);

        holder.mStatus.setTypeface(mMontserratRegular);
//        switch (order.getStatus()) {
//            case 0:
//                holder.mStatus.setText(mContext.getResources().getString(R.string.pending));
//                holder.mStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorOrangePeel));
//                break;
//            case 1:
//                holder.mStatus.setText(mContext.getResources().getString(R.string.paid));
//                holder.mStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorGreen));
//                break;
//            case 2:
//                holder.mStatus.setText(mContext.getResources().getString(R.string.canceled));
//                holder.mStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorFreeSpeechRed));
//                break;
//            case 3:
//                holder.mStatus.setText(mContext.getResources().getString(R.string.refunded));
//                holder.mStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorFreeSpeechRed));
//                break;
//        }
        switch (order.getViewStatus()) {
            case 0:
                holder.mStatus.setText(mContext.getResources().getString(R.string.neew));
                holder.mStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorOrangePeel));
                break;
            case 1:
                holder.mStatus.setText(mContext.getResources().getString(R.string.processing));
                holder.mStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorGreen));
                break;
            case 2:
                holder.mStatus.setText(mContext.getResources().getString(R.string.completed));
                holder.mStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorGreen));
                break;
        }

        String date = Utils.dateChangeFormat(order.getCreated(), Utils.ISO_FORMAT, Utils.VIEW_FORMAT);

        holder.mDateTv.setText(date);
        holder.mDateTv.setTypeface(mMontserratRegular);

        holder.mOrderIdTv.setText(mContext.getResources().getString(R.string.orderId)
                .concat(" ")
                .concat(order.getNumber()));
        holder.mOrderIdTv.setTypeface(mMontserratRegular);

        holder.mReorderTv.setTypeface(mMontserratRegular);
        holder.mReorderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMyOrdersFragment.reorder(position);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMyOrdersFragment.openDetailScreen(order);
            }
        });

    }
}
