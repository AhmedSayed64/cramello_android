package net.aldar.cramello.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.aldar.cramello.R;
import net.aldar.cramello.model.response.Branch;
import net.aldar.cramello.services.Utils;
import net.aldar.cramello.view.MainActivity;

import java.util.List;

import static net.aldar.cramello.view.App.mMontserratRegular;

public class BranchesRvAdapter extends RecyclerView.Adapter<BranchesRvAdapter.ViewHolder> {

    public List<Branch> mBranchList;
    private MainActivity mMainActivity;
    BranchInterface branchInterface;

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

    public BranchesRvAdapter(MainActivity mainActivity, List<Branch> branches, BranchInterface branchInterface) {
        mMainActivity = mainActivity;
        mBranchList = branches;
        this.branchInterface = branchInterface;
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
        holder.mBranchNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                branchInterface.onClickBranch(branch.getId());

            }
        });
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

    public interface BranchInterface {
        void onClickBranch(int branchID);
    }
}
