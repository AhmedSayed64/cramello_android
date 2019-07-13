package net.aldar.cramello.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import net.aldar.cramello.R;
import net.aldar.cramello.model.response.product.Image;
import net.aldar.cramello.services.Utils;

import java.util.List;

public class SliderPVAdapter extends PagerAdapter {
    private List<Image> imageSliderArrayList;
    private LayoutInflater inflater;
    private Context mContext;

    public SliderPVAdapter(Context context, List<Image> imageSliderArrayList) {
        this.mContext = context;
        this.imageSliderArrayList = imageSliderArrayList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return imageSliderArrayList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.view_item_slider, view, false);

        assert imageLayout != null;
        final ImageView imageView = imageLayout.findViewById(R.id.image);

        Utils.loadImage(imageSliderArrayList.get(position).getImage(), null,
                imageView, R.drawable.menu_item_placeholder);

        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}
