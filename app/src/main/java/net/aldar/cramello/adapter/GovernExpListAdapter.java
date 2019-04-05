package net.aldar.cramello.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import net.aldar.cramello.R;
import net.aldar.cramello.model.response.governorate.Area;
import net.aldar.cramello.model.response.governorate.Governorate;
import net.aldar.cramello.services.PrefsManger;

import java.util.ArrayList;
import java.util.List;

import static net.aldar.cramello.App.mMontserratBold;
import static net.aldar.cramello.App.mMontserratRegular;

public class GovernExpListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private PrefsManger mPrefsManger;

    private ExpandableListView mExpandableListView;

    public List<Governorate> mFilteredGovernorateList;
    private List<Governorate> mOriginalGovernorateList;


    public GovernExpListAdapter(Context context, List<Governorate> governorates, ExpandableListView expandableListView) {
        this.context = context;
        mPrefsManger = new PrefsManger(context);

        mExpandableListView = expandableListView;

        this.mOriginalGovernorateList = new ArrayList<>();
        this.mOriginalGovernorateList.addAll(governorates);

        this.mFilteredGovernorateList = new ArrayList<>();
        this.mFilteredGovernorateList.addAll(governorates);
    }

    @Override
    public Area getChild(int listPosition, int expandedListPosition) {
        return mFilteredGovernorateList.get(listPosition).getAreas().get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        Area childArea = getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.view_area_child_item, null);
        }

        String name;
        if (mPrefsManger.getAppLanguage().contains("ar"))
            name = childArea.getNameAr();
        else
            name = childArea.getNameEn();

        TextView expandedListTextView = convertView
                .findViewById(R.id.child_title);
        expandedListTextView.setTypeface(mMontserratRegular);
        expandedListTextView.setText(name);
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return mFilteredGovernorateList.get(listPosition).getAreas().size();
    }

    @Override
    public Governorate getGroup(int listPosition) {
        return mFilteredGovernorateList.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.mFilteredGovernorateList.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Governorate governorate = getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.view_area_parent_item, null);
        }

        String name;
        if (mPrefsManger.getAppLanguage().contains("ar"))
            name = governorate.getNameAr();
        else
            name = governorate.getNameEn();

        TextView listTitleTextView = convertView
                .findViewById(R.id.parent_title);
        listTitleTextView.setTypeface(mMontserratBold);
        listTitleTextView.setText(name);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    public void filterData(String query) {

        query = query.toLowerCase();
        Log.e("MyListAdapter", String.valueOf(mFilteredGovernorateList.size()));
        mFilteredGovernorateList.clear();

        if (query.isEmpty()) {
            mFilteredGovernorateList.addAll(mOriginalGovernorateList);
        } else {
            for (Governorate governorate : mOriginalGovernorateList) {

                List<Area> areaList = governorate.getAreas();
                List<Area> newList = new ArrayList<>();
                for (Area area : areaList) {

                    String name;
                    if (mPrefsManger.getAppLanguage().contains("ar")) {
                        name = area.getNameAr();
                        name = name.replace("أ", "ا");
                        name = name.replace("إ", "ا");
                        name = name.replace("آ", "ا");
                        query = query.replace("أ", "ا");
                        query = query.replace("إ", "ا");
                        query = query.replace("آ", "ا");
                    } else
                        name = area.getNameEn();

                    if (name.toLowerCase().contains(query)) {
                        newList.add(area);
                    }
                }
                if (newList.size() > 0) {
                    Governorate nGovernorate = new Governorate(governorate.getId(), governorate.getStatus(),
                            governorate.getOrder(), governorate.getNameEn(), governorate.getNameAr(), newList);
                    mFilteredGovernorateList.add(nGovernorate);
                }
            }
        }

        Log.v("MyListAdapter", String.valueOf(mFilteredGovernorateList.size()));
        notifyDataSetChanged();

        int count = getGroupCount();
        for (int i = 0; i < count; i++) {
            mExpandableListView.expandGroup(i);
        }
    }
}
