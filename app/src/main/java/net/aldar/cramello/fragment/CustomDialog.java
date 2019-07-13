package net.aldar.cramello.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.rbrooks.indefinitepagerindicator.IndefinitePagerIndicator;

import net.aldar.cramello.R;
import net.aldar.cramello.adapter.HomeSliderVPAdapter;
import net.aldar.cramello.adapter.VideoRVAdapter;
import net.aldar.cramello.apiHandler.BaseApi;
import net.aldar.cramello.apiHandler.BaseApiHandler;
import net.aldar.cramello.model.response.BranchesMedia;
import net.aldar.cramello.model.response.Image;
import net.aldar.cramello.services.PrefsManger;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CustomDialog extends DialogFragment {

    public static List<String> mSliderList;
    private static int CURRENT = 0;
    private static int NUM_PAGES = 0;
    public BaseApi mServiceApi;
    public PrefsManger mPrefsManger;
    List<Image> images;
    RecyclerView videoRV;
    TextView nodata, branch_name, branch_open, branch_close;
    WebView branch_address;
    FrameLayout root;
    private ViewPager mViewPager;
    private IndefinitePagerIndicator mIndicator;
    private int BranchID;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View mView = inflater.inflate(R.layout.fragment_dialog, null);
        mPrefsManger = new PrefsManger(getActivity());

        mServiceApi = BaseApiHandler.setupBaseApi().create(BaseApi.class);
        videoRV = mView.findViewById(R.id.video_RV);
        nodata = mView.findViewById(R.id.nodata);
        branch_name = mView.findViewById(R.id.branch_name);
        branch_open = mView.findViewById(R.id.branch_time);
        branch_close = mView.findViewById(R.id.close_time);

        branch_address = mView.findViewById(R.id.branch_address);
        Bundle mArgs = getArguments();
        if (mArgs != null)
            BranchID = mArgs.getInt("branchID");

        getImagesAndVideos(BranchID);
        mViewPager = mView.findViewById(R.id.home_fragment_tab_viewPager);
        mIndicator = mView.findViewById(R.id.home_fragment_tab_indicator);
        root = mView.findViewById(R.id.root_pager);
        builder.setView(mView);

        return builder.create();
    }

    private void getImagesAndVideos(int branchID) {

        Call<BranchesMedia> call = mServiceApi.getBranchMedia(branchID);
        call.enqueue(new Callback<BranchesMedia>() {
            @Override
            public void onResponse(@NotNull Call<BranchesMedia> call, @NotNull Response<BranchesMedia> response) {
                if (response.isSuccessful() && response.body() != null) {

                    if (mPrefsManger.getAppLanguage().contains("ar")) {
                        branch_name.setText(response.body().getNameAr());
                        branch_address.loadData(response.body().getAddress_ar(), "text/html; charset=UTF-8", null);

                    } else {
                        branch_name.setText(response.body().getNameEn());
                        branch_address.loadData(response.body().getAddress_en(), "text/html; charset=UTF-8", null);

                    }

                    String open = getResources().getString(R.string.open_at) + " " + response.body().getOpenTime().substring(0, 5) + " AM";
                    String close = getResources().getString(R.string.close_at) + " " + response.body().getCloseTime().substring(0, 5) + " PM";
                    branch_open.setText(open);
                    branch_close.setText(close);
                    if (response.body().getImages() != null && !response.body().getImages().isEmpty()) {
                        images = response.body().getImages();
                        getItems(images);

                    } else {
                        root.setVisibility(View.GONE);
                        Log.d("elseif", "elseif");
                    }

                    if (response.body().getVideos() != null && !response.body().getVideos().isEmpty()) {
                        VideoRVAdapter videoRVAdapter = new VideoRVAdapter(response.body().getVideos(), getActivity());
                        videoRV.setAdapter(videoRVAdapter);
                        videoRV.setLayoutManager(new LinearLayoutManager(getActivity()));

                    } else {
                        videoRV.setVisibility(View.GONE);

                    }
                    if (response.body().getImages() == null && response.body().getVideos() == null) {
                        nodata.setVisibility(View.VISIBLE);
                    }
                    Log.d("CustomDialog", "BranchID = " + response.body().getId());

                }
            }

            @Override
            public void onFailure(@NotNull Call<BranchesMedia> call, @NotNull Throwable t) {

            }
        });


    }

    private void showSlider() {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()), R.style.TransDialog);
        final AlertDialog alertDialog;
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_slider, null);
        aBuilder.setView(dialogView);
        alertDialog = aBuilder.create();

        ViewPager viewPager = dialogView.findViewById(R.id.dialog_slider_viewPager);
        IndefinitePagerIndicator mIndicator = dialogView.findViewById(R.id.dialog_slider_indicator);
        viewPager.setAdapter(new HomeSliderVPAdapter(getActivity(), mSliderList));
        mIndicator.attachToViewPager(viewPager);

        alertDialog.show();
    }

    private void getItems(List<Image> images) {

        mSliderList = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            mSliderList.add(images.get(i).getImage());

        }
        setupVP();

    }

    private void setupVP() {
        mViewPager.setAdapter(new HomeSliderVPAdapter(getActivity(), mSliderList));
        mIndicator.attachToViewPager(mViewPager);
        mViewPager.setOnClickListener(v -> showSlider());
        NUM_PAGES = mSliderList.size();
    }
}
//        final Runnable Update = () -> {
//            if (CURRENT == NUM_PAGES) {
//                CURRENT = 0;
//          