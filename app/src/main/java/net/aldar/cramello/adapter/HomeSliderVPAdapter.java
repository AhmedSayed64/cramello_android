package net.aldar.cramello.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.rbrooks.indefinitepagerindicator.IndefinitePagerIndicator;
import com.squareup.picasso.Picasso;

import net.aldar.cramello.R;

import java.util.List;

import static net.aldar.cramello.fragment.CustomDialog.mSliderList;


public class HomeSliderVPAdapter extends PagerAdapter {
    private List<String> mImages;
    private LayoutInflater inflater;
    private Context mContext;

    public HomeSliderVPAdapter(Context context, List<String> images) {
        mContext = context;
        mImages = images;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, int position) {
        View layout = inflater.inflate(R.layout.view_slider_item, view, false);

        final String item = mImages.get(position);


        Log.d("url_tag", item);
        assert layout != null;
        ImageView imageView = layout.findViewById(R.id.slide_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("mViewPager", "onClick()");
                showSlider();
            }
        });
        Picasso.get().load(item).placeholder(R.drawable.placeholder).fit().into(imageView);
        view.addView(layout, 0);

        return layout;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(@NonNull Parcelable state, @NonNull ClassLoader loader) {
    }

    private void showSlider() {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(mContext, R.style.TransDialog);
        final AlertDialog alertDialog;
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_slider, null);
        aBuilder.setView(dialogView);
        alertDialog = aBuilder.create();
        ViewPager viewPager = dialogView.findViewById(R.id.dialog_slider_viewPager);
        IndefinitePagerIndicator mIndicator = dialogView.findViewById(R.id.dialog_slider_indicator);
        viewPager.setAdapter(new HomeSliderVPAdapter(mContext, mSliderList));
        mIndicator.attachToViewPager(viewPager);
        alertDialog.show();
    }

    @NonNull
    @Override
    public Parcelable saveState() {
        return null;
    }

}
