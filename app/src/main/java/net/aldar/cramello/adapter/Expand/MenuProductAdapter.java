package net.aldar.cramello.adapter.Expand;

import android.graphics.Paint;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import net.aldar.cramello.App;
import net.aldar.cramello.R;
import net.aldar.cramello.model.response.product.Image;
import net.aldar.cramello.services.Utils;

import java.util.List;

import static net.aldar.cramello.App.mMontserratRegular;
import static net.aldar.cramello.App.mRobotoRegular;

public class MenuProductAdapter extends ChildViewHolder {

    private TextView nameTv;
    private TextView descTv;
    private TextView originalPriceTv;
    private TextView discountedPriceTv;
    private TextView notAvailableTv;
    private ImageView addIv;
    private ImageView itemImage;
    private ProgressBar progressBar;

    private View view;

    public MenuProductAdapter(View itemView) {
        super(itemView);
        nameTv = itemView.findViewById(R.id.product_item_nameTv);
        descTv = itemView.findViewById(R.id.product_item_descTv);
        originalPriceTv = itemView.findViewById(R.id.product_item_originalPriceTv);
        discountedPriceTv = itemView.findViewById(R.id.product_item_discountedPriceTv);
        notAvailableTv = itemView.findViewById(R.id.product_item_notAvailableTv);
        addIv = itemView.findViewById(R.id.product_item_addIv);
        itemImage = itemView.findViewById(R.id.product_item_imageIv);
        progressBar = itemView.findViewById(R.id.product_item_progressBar);

        view = itemView;
    }

    public void setTitle(String name) {
        nameTv.setText(name);
        nameTv.setTypeface(mRobotoRegular);
    }

    public void setDesc(Spanned desc) {
        descTv.setText(desc);
        descTv.setTypeface(mMontserratRegular);
    }

    public void setOriginalPrice(Double originalPrice) {
        originalPriceTv.setText(Utils.makeSureThreeNAfterDot(originalPrice));
        originalPriceTv.setTypeface(mMontserratRegular);
        originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    public void setDiscountedPrice(Double discountedPrice) {
        discountedPriceTv.setText(Utils.makeSureThreeNAfterDot(discountedPrice));
        discountedPriceTv.setTypeface(mMontserratRegular);
    }

    public void setNotAvailableTypeFace() {
        notAvailableTv.setTypeface(mMontserratRegular);
    }

    public View getView() {
        return view;
    }

    public ImageView getAddIv() {
        return addIv;
    }

    public TextView getOriginalPriceTv() {
        return originalPriceTv;
    }

    public TextView getNotAvailableTv() {
        return notAvailableTv;
    }

    public void setImage(List<Image> images) {
        if (images != null && !images.isEmpty()) {
            String imageUrl = images.get(0).getImage();
            if (imageUrl != null && !imageUrl.isEmpty())
                Utils.loadImageWithProgressBar(App.getMenuImageResizeUrl(imageUrl),
                        itemImage, progressBar, R.drawable.menu_item_placeholder);
            else
                itemImage.setImageResource(R.drawable.menu_item_placeholder);
        } else
            itemImage.setImageResource(R.drawable.menu_item_placeholder);
    }
}
