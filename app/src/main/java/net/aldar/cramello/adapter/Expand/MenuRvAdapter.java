package net.aldar.cramello.adapter.Expand;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import net.aldar.cramello.R;
import net.aldar.cramello.entitie.CategoryAndProducts;
import net.aldar.cramello.fragment.MenuFragment;
import net.aldar.cramello.model.response.product.Product;

import java.util.List;

public class MenuRvAdapter extends ExpandableRecyclerViewAdapter<MenuCategoryAdapter, MenuProductAdapter> {

    private Context mContext;
    private String mAppLanguage;
    private MenuFragment mMenuFragment;

    public MenuRvAdapter(Context context, MenuFragment fragment,
                         String lang, List<? extends ExpandableGroup> groups) {
        super(groups);
        mContext = context;
        mMenuFragment = fragment;
        mAppLanguage = lang;
    }

    @Override
    public MenuCategoryAdapter onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_menu_category_item, parent, false);
        return new MenuCategoryAdapter(view);
    }

    @Override
    public MenuProductAdapter onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_menu_product_item, parent, false);
        return new MenuProductAdapter(view);
    }

    @Override
    public void onBindChildViewHolder(MenuProductAdapter holder, int flatPosition,
                                      ExpandableGroup group, int childIndex) {

        final Product product = ((CategoryAndProducts) group).getItems().get(childIndex);
        final String name;
        String desc;
        if (mAppLanguage.contains("ar")) {
            name = product.getNameAr();
            desc = product.getDescriptionAr();
        } else {
            name = product.getNameEn();
            desc = product.getDescriptionEn();
        }
        Spanned descSpanned = Html.fromHtml(desc);
        holder.setTitle(name);
        holder.setDesc(descSpanned);
        holder.setOriginalPrice(product.getPrice());
        holder.setDiscountedPrice(product.getDiscountedPrice());
        holder.setNotAvailableTypeFace();
        holder.setImage(product.getImages());
        if (product.getStock() == 0) {
            holder.getAddIv().setVisibility(View.GONE);
            holder.getNotAvailableTv().setVisibility(View.VISIBLE);
        } else {
            holder.getAddIv().setVisibility(View.VISIBLE);
            holder.getNotAvailableTv().setVisibility(View.GONE);
        }
        if (product.hasAvailableOffer())
            holder.getOriginalPriceTv().setVisibility(View.VISIBLE);
        else
            holder.getOriginalPriceTv().setVisibility(View.GONE);
        holder.getAddIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMenuFragment.addQuantity(name, product, 1);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMenuFragment.goToProductDetailScreen(product);
            }
        });
    }

    @Override
    public boolean onGroupClick(int flatPos) {
        return super.onGroupClick(flatPos);
    }

    @Override
    public void onBindGroupViewHolder(MenuCategoryAdapter holder, int flatPosition,
                                      ExpandableGroup group) {
        holder.setTitle(group);
        if (MenuRvAdapter.this.isGroupExpanded(flatPosition)) {
            holder.getArrowIv().setImageResource(R.drawable.ic_arrow_up);
        } else
            holder.getArrowIv().setImageResource(R.drawable.ic_arrow_down);
    }
}