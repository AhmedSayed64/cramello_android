package net.aldar.cramello.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.aldar.cramello.MainActivity;
import net.aldar.cramello.R;
import net.aldar.cramello.model.response.Branch;
import net.aldar.cramello.services.Utils;

import java.util.List;

import static net.aldar.cramello.App.mMontserratRegular;

public class BranchesRvAdapter extends RecyclerView.Adapter<BranchesRvAdapter.ViewHolder> {

    public List<Branch> mBranchList;
    private MainActivity mMainActivity;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public TextView mBranchNameTv;
        public TextView mBranchPhoneTv;
        public LinearLayout mMapLayout;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mBranchNameTv = view.findViewById(R.id.view_branch_nameTv);
            mBranchPhoneTv = view.findViewById(R.id.view_branch_phoneTv);
            mMapLayout = view.findViewById(R.id.view_branch_mapLayout);
        }
    }

    public Branch getValueAt(int position) {
        return mBranchList.get(position);
    }

    public BranchesRvAdapter(MainActivity mainActivity, List<Branch> branches) {
        mMainActivity = mainActivity;
        mBranchList = branches;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_branch_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mBranchList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Branch branch = getValueAt(position);

        String name;
        if (mMainActivity.mAppLanguage.contains("ar"))
            name = branch.getNameAr();
        else
            name = branch.getNameEn();

        holder.mBranchNameTv.setText(name);
        holder.mBranchNameTv.setTypeface(mMontserratRegular);

        if (branch.getPhone() != null) {
            holder.mBranchPhoneTv.setText(branch.getPhone());
            holder.mBranchPhoneTv.setTypeface(mMontserratRegular);
            holder.mBranchPhoneTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (branch.getPhone() != null && !branch.getPhone().isEmpty())
                        Utils.makeACall(mMainActivity, branch.getPhone());
                }
            });
        }

        holder.mMapLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (branch.getLatitude() != null && branch.getLatitude() != 0 &&
                        branch.getLongitude() != null && branch.getLongitude() != 0)
                    Utils.openMapNavigation(mMainActivity, branch.getLatitude(), branch.getLongitude());
            }
        });
    }
}
