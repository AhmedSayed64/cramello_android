package net.aldar.cramello.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.aldar.cramello.R;
import net.aldar.cramello.adapter.BranchesRvAdapter;
import net.aldar.cramello.model.response.Branch;
import net.aldar.cramello.services.Utils;
import net.aldar.cramello.view.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.aldar.cramello.view.App.mMontserratRegular;

public class BranchesFragment extends RootFragment implements View.OnClickListener, BranchesRvAdapter.BranchInterface {


    private TextView mTitleTv;
    private LinearLayout mBackLayout;
    private ImageView mBackIv;
    private TextView mBranchTv;
    private TextView mPhoneTv;
    private TextView mLocationTv;
    private RecyclerView mBranchesRv;
    private BranchesRvAdapter mBranchesRvAdapter;
    private List<Branch> mBranchesList;
    public MainActivity mMainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_branches, container, false);

        mTitleTv = view.findViewById(R.id.branchesFragment_title);
        mBackLayout = view.findViewById(R.id.branchesFragment_backLayout);
        mBackIv = view.findViewById(R.id.branchesFragment_backIv);
        mBranchTv = view.findViewById(R.id.branchesFragment_branchNameTitle);
        mPhoneTv = view.findViewById(R.id.branchesFragment_phoneNumberTitle);
        mLocationTv = view.findViewById(R.id.branchesFragment_locationTitle);
        mBranchesRv = view.findViewById(R.id.branchesFragment_branchesRv);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mTitleTv.setTypeface(mMontserratRegular);
        mBackLayout.setOnClickListener(this);
        Utils.submitRotation(mBackIv, mMainActivity.mPrefsManger);
        mBranchTv.setTypeface(mMontserratRegular);
        mPhoneTv.setTypeface(mMontserratRegular);
        mLocationTv.setTypeface(mMontserratRegular);
        getBranches();
    }

    private void getBranches() {
        if (Utils.isConnectionOn(mMainActivity)) {
            mMainActivity.mSpinKitLayout.setVisibility(View.VISIBLE);
            Call<List<Branch>> call = mMainActivity.mServiceApi.getBranches(mMainActivity.mAuthToken);
            call.enqueue(new Callback<List<Branch>>() {
                @Override
                public void onResponse(@NotNull Call<List<Branch>> call, @NotNull Response<List<Branch>> response) {
                    mMainActivity.mSpinKitLayout.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            mBranchesList = response.body();
                            setupBranchesRv();
                        } catch (Exception e) {
                            Log.e("Branches Ex / ", e.getMessage() + "");
                            Utils.makeAToast(mMainActivity, getResources().getString(R.string.getBranchesFailed));
                        }
                    } else {
                        Log.e("Branches Code / ", "Code not Successful");
                        Utils.makeAToast(mMainActivity, getResources().getString(R.string.getBranchesFailed));
                    }
                }

                @Override
                public void onFailure(Call<List<Branch>> call, Throwable t) {
                    mMainActivity.mSpinKitLayout.setVisibility(View.GONE);
                    Log.e("Branches Fail / ", t.getMessage() + "");
                    Utils.makeAToast(mMainActivity, getResources().getString(R.string.getBranchesFailed));
                }
            });
        } else
            Utils.makeAToast(mMainActivity, getResources().getString(R.string.connection_offline));
    }

    private void setupBranchesRv() {
        mBranchesRv.setLayoutManager(new LinearLayoutManager(mMainActivity,
                LinearLayoutManager.VERTICAL, false));
        mBranchesRvAdapter = new BranchesRvAdapter(mMainActivity, mBranchesList, this);
        mBranchesRv.setAdapter(mBranchesRvAdapter);
        mBranchesRv.addItemDecoration(new DividerItemDecoration(mBranchesRv.getContext(),
                LinearLayoutManager.VERTICAL));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.branchesFragment_backLayout:
                mMainActivity.onBackPressed();
                break;
        }
    }


    @Override
    public void onClickBranch(int branchID) {

        Bundle args = new Bundle();
        args.putInt("branchID", branchID);

        Log.d("BranchesFragment", String.valueOf(branchID));
        FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        CustomDialog dialog = new CustomDialog();
        dialog.setArguments(args);
        dialog.show(fm, "dialog");

    }
}
