package net.aldar.cramello.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.aldar.cramello.R;
import net.aldar.cramello.fragment.NotificationsFragment;
import net.aldar.cramello.model.response.Notification;
import net.aldar.cramello.services.Utils;
import net.aldar.cramello.view.App;

import java.util.List;

import static net.aldar.cramello.view.App.mMontserratRegular;
import static net.aldar.cramello.view.App.mRobotoRegular;

public class NotificationRvAdapter extends RecyclerView.Adapter<NotificationRvAdapter.ViewHolder> {

    public List<Notification> mNotificationsList;
    private NotificationsFragment mNotificationsFragment;

    public NotificationRvAdapter(NotificationsFragment fragment, List<Notification> notifications) {
        mNotificationsList = notifications;
        mNotificationsFragment = fragment;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public TextView mTitleTv;
        public TextView mContentTv;
        public FrameLayout mProductLayout;
        public ImageView mProductIv;
        public ImageView mBigImageIv;
        public ProgressBar mProductProgressBar;
        public ProgressBar mImageProgressBar;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mTitleTv = view.findViewById(R.id.notification_item_titleTv);
            mContentTv = view.findViewById(R.id.notification_item_contentTv);
            mProductLayout = view.findViewById(R.id.notification_item_productLayout);
            mProductIv = view.findViewById(R.id.notification_item_productImageIv);
            mBigImageIv = view.findViewById(R.id.notification_item_imageIv);
            mProductProgressBar = view.findViewById(R.id.notification_item_productProgressBar);
            mImageProgressBar = view.findViewById(R.id.notification_item_imageProgressBar);
        }
    }

    public Notification getValueAt(int position) {
        return mNotificationsList.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mNotificationsList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Notification notification = getValueAt(position);

        holder.mTitleTv.setText(notification.getTitle());
        holder.mTitleTv.setTypeface(mRobotoRegular);

        holder.mContentTv.setText(notification.getBody());
        holder.mContentTv.setTypeface(mMontserratRegular);

        holder.mProductProgressBar.setVisibility(View.GONE);
        holder.mImageProgressBar.setVisibility(View.GONE);

        if (notification.getProduct() != null) {
            holder.mProductIv.setVisibility(View.VISIBLE);
            if (notification.getProductFirstImage() != null && !notification.getProductFirstImage().isEmpty()) {
                Utils.loadImageWithProgressBar(App.getMenuImageResizeUrl(notification.getProductFirstImage()),
                        holder.mProductIv, holder.mProductProgressBar, R.drawable.menu_item_placeholder);
            } else
                holder.mProductIv.setImageResource(R.drawable.menu_item_placeholder);
        } else
            holder.mProductLayout.setVisibility(View.GONE);

        if (notification.getBigImage() != null) {
            holder.mBigImageIv.setVisibility(View.VISIBLE);
            if (notification.getBigImage() != null && !notification.getBigImage().isEmpty()) {
                Utils.loadImageWithProgressBar(notification.getBigImage(),
                        holder.mBigImageIv, holder.mImageProgressBar, R.drawable.menu_item_placeholder);
            } else
                holder.mBigImageIv.setImageResource(R.drawable.menu_item_placeholder);
        } else
            holder.mBigImageIv.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification.getProduct() != null) {
                    mNotificationsFragment.goToProductDetail(notification.getProduct());
                }
            }
        });
    }
}
