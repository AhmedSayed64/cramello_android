package net.aldar.cramello.adapter.Expand;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import net.aldar.cramello.R;
import net.aldar.cramello.entitie.CategoryAndProducts;

import static net.aldar.cramello.view.App.mMontserratRegular;

public class MenuCategoryAdapter extends GroupViewHolder {

    private TextView mCategoryTitle;
    private ImageView mArrowIv;

    public MenuCategoryAdapter(View itemView) {
        super(itemView);
        mCategoryTitle = itemView.findViewById(R.id.menu_category_item_title);
        mArrowIv = itemView.findViewById(R.id.menu_category_item_arrowIv);
    }

    public void setTitle(ExpandableGroup cat) {
        if (cat instanceof CategoryAndProducts) {
            mCategoryTitle.setText(cat.getTitle());
            mCategoryTitle.setTypeface(mMontserratRegular);
        }
    }

    public ImageView getArrowIv() {
        return mArrowIv;
    }

    @Override
    public void expand() {
        animateExpand();
    }

    @Override
    public void collapse() {
        animateCollapse();
    }

    private void animateExpand() {
        mArrowIv.setImageResource(R.drawable.ic_arrow_up);
    }

    private void animateCollapse() {
        mArrowIv.setImageResource(R.drawable.ic_arrow_down);
    }

}
