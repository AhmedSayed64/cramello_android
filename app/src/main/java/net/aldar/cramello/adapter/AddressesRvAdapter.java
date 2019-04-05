package net.aldar.cramello.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.aldar.cramello.R;
import net.aldar.cramello.fragment.AddressesFragment;
import net.aldar.cramello.model.Address;

import java.util.List;

import static net.aldar.cramello.App.mMontserratBold;
import static net.aldar.cramello.App.mMontserratRegular;

public class AddressesRvAdapter extends RecyclerView.Adapter<AddressesRvAdapter.ViewHolder> {

    private List<Address> mAddressesList;
    private Context mContext;
    private AddressesFragment mAddressesFragment;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public TextView mTypeTv;
        public TextView mAreaTv;
        public TextView mBlockStreetTv;
        public TextView mOthersTv;

        public LinearLayout mEditLayout;
        public TextView mEditTv;
        public LinearLayout mDeleteLayout;
        public TextView mDeleteTv;

        public ImageView mArrowIv;


        public ViewHolder(View view) {
            super(view);
            mView = view;

            mTypeTv = view.findViewById(R.id.address_item_addressTypeTv);
            mAreaTv = view.findViewById(R.id.address_item_areaTv);
            mBlockStreetTv = view.findViewById(R.id.address_item_block_streetTv);
            mOthersTv = view.findViewById(R.id.address_item_othersTv);

            mEditLayout = view.findViewById(R.id.address_item_editLayout);
            mEditTv = view.findViewById(R.id.address_item_editTv);
            mDeleteLayout = view.findViewById(R.id.address_item_deleteLayout);
            mDeleteTv = view.findViewById(R.id.address_item_deleteTv);

            mArrowIv = view.findViewById(R.id.address_item_arrowIv);
        }
    }

    public Address getValueAt(int position) {
        return mAddressesList.get(position);
    }

    public AddressesRvAdapter(Context context, AddressesFragment fragment, List<Address> addresses) {
        mContext = context;
        mAddressesFragment = fragment;
        mAddressesList = addresses;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_address_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mAddressesList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Address address = getValueAt(position);

        if (address.getAddressType() != null) {
            String addressType = null;
            switch (address.getAddressType()) {
                case 0:
                    addressType = mContext.getResources().getString(R.string.apartment);
                    break;
                case 1:
                    addressType = mContext.getResources().getString(R.string.house);
                    break;
                case 2:
                    addressType = mContext.getResources().getString(R.string.office);
                    break;
            }

            holder.mTypeTv.setTypeface(mMontserratBold);
            holder.mTypeTv.setText(mContext.getResources().getString(R.string.addressType)
                    .concat(": ").concat(addressType));
        }

        if (address.getArea() != null && address.getSelectedArea() != null) {
            String name;
            if (mAddressesFragment.mMainActivity.mAppLanguage.contains("ar"))
                name = address.getSelectedArea().getNameAr();
            else
                name = address.getSelectedArea().getNameEn();

            holder.mAreaTv.setTypeface(mMontserratRegular);
            holder.mAreaTv.setText(mContext.getResources().getString(R.string.area)
                    .concat(" ").concat(name));
        }

        String blockSt = "";
        if (address.getBlock() != null)
            blockSt = mContext.getResources().getString(R.string.block)
                    .concat(" ").concat(address.getBlock());
        if (address.getStreet() != null)
            blockSt = blockSt.concat(", ")
                    .concat(mContext.getResources().getString(R.string.street))
                    .concat(": ").concat(address.getStreet());

        holder.mBlockStreetTv.setTypeface(mMontserratRegular);
        holder.mBlockStreetTv.setText(blockSt);

        String others = "";
        if (address.getAvenue() != null && !address.getAvenue().isEmpty())
            others = address.getAvenue();

        if (address.getBuilding() != null && !address.getBuilding().isEmpty()) {
            if (others.isEmpty())
                others = address.getBuilding();
            else
                others = others.concat(", ").concat(address.getBuilding());
        }

        if (address.getFloor() != null && !address.getFloor().isEmpty()) {
            if (others.isEmpty())
                others = address.getFloor();
            else
                others = others.concat(", ").concat(address.getFloor());
        }

        if (address.getApartment() != null && !address.getApartment().isEmpty()) {
            if (others.isEmpty())
                others = String.valueOf(address.getApartment());
            else
                others = others.concat(", ").concat(String.valueOf(address.getApartment()));
        }

        holder.mOthersTv.setTypeface(mMontserratRegular);
        holder.mOthersTv.setText(others);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddressesFragment.recycleItemClicked(position);
            }
        });

        holder.mEditTv.setTypeface(mMontserratRegular);
        holder.mEditLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddressesFragment.editItemClicked(position);
            }
        });

        holder.mDeleteTv.setTypeface(mMontserratRegular);
        holder.mDeleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddressesFragment.deleteItemClicked(position);
            }
        });

        if (mAddressesFragment.mMainActivity.mAppLanguage.contains("ar"))
            holder.mArrowIv.setImageResource(R.drawable.ic_arrow_left);
        else
            holder.mArrowIv.setImageResource(R.drawable.ic_arrow_right);

    }
}
