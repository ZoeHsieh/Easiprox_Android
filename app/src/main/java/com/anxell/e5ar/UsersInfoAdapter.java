package com.anxell.e5ar;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.anxell.e5ar.custom.FontTextView;
import com.anxell.e5ar.data.UserData;
import com.anxell.e5ar.transport.APPConfig;
import com.anxell.e5ar.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sean on 2017/10/30.
 */

public class UsersInfoAdapter extends RecyclerView.Adapter<UsersInfoAdapter.ViewHolder>implements Filterable {

    private List<UserData> mDataList = new ArrayList<UserData>();
    private List<UserData> mOriginalValues = new ArrayList<UserData>();

    private OnRecyclerViewItemClickListener mOnItemClickListener;

    private String mDeviceType = APPConfig.deviceType_Card;
    public void updateData(List<UserData> dataList) {
		mDataList.clear();
        mOriginalValues.clear();
        for(UserData data:dataList){
            mDataList.add(data);
            mOriginalValues.add(data);
        }
        notifyDataSetChanged();
    }
	public void updateData(UserData datas) {
        mOriginalValues.add(datas);
        mDataList.add(datas);
        Util.debugMessage("user Adapter","o size"+mOriginalValues.size(),true);
        Util.debugMessage("user Adapter","Data size"+mDataList.size(),true);
        notifyDataSetChanged();
    }
    public UsersInfoAdapter(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }
    public UsersInfoAdapter(OnRecyclerViewItemClickListener listener, String DeviceType) {
        mOnItemClickListener = listener;
        mDeviceType = DeviceType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =null;
        if(!APPConfig.deviceType.equals(APPConfig.deviceType_Keypad))
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_card, parent, false);
        else
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserData data = mDataList.get(position);
        String id = data.getId();
        String password = data.getPasswrod();
        String card =data.getCard();

        holder.mIdTV.setText(id);

        holder.mPasswordTV.setText(password);
        holder.mCardTV.setText(card);

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

              

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
                Util.debugMessage("User Adapter","o size"+mOriginalValues.size(),true);
                Util.debugMessage("User Adapter","Data size"+mDataList.size(),true);

                mDataList.clear();
                // perform your search here using the searchConstraint String.
                synchronized(this)
                {
                if(constraint != null && constraint.toString().length() > 0) {


                    constraint = constraint.toString().toLowerCase();

                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String userID = mOriginalValues.get(i).getId();
                        if (userID.toLowerCase().startsWith(constraint.toString())) {
                            mDataList.add(mOriginalValues.get(i));
                        }
                    }
                    results.count = mDataList.size();
                    results.values = mDataList;

                    Log.e("VALUES", "filter size="+mDataList.size());
                    Util.debugMessage("User Adapter","t size="+mDataList.size(),true);
                    Util.debugMessage("User Adapter","o size="+mOriginalValues.size(),true);
                }else
                {  Util.debugMessage("User Adapter","o size"+mOriginalValues.size(),true);
                    Util.debugMessage("User Adapter","Data size"+mDataList.size(),true);

                       for(UserData data:mOriginalValues)
                        mDataList.add(data);
                        results.values = mDataList;
                        results.count = mDataList.size();

                 }
                }
                return results;
            }
        };

        return filter;
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public FontTextView mIdTV;
        public FontTextView mTypeTV;
        public FontTextView mPasswordTV;
        public FontTextView mCardTV;

        public ViewHolder(View v) {
            super(v);
            mIdTV = (FontTextView) v.findViewById(R.id.id);

            mPasswordTV = (FontTextView) v.findViewById(R.id.password);
            if(!mDeviceType.equals(APPConfig.deviceType_Keypad))
            mCardTV = (FontTextView)v.findViewById(R.id.card);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener == null) return;
            mOnItemClickListener.onItemClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            if (mOnItemClickListener == null) return true;
            mOnItemClickListener.onItemLongClick(view, getAdapterPosition());
            return true;
        }


    }

}