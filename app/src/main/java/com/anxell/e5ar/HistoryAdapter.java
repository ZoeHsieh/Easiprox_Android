package com.anxell.e5ar;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.anxell.e5ar.custom.FontTextView;
import com.anxell.e5ar.data.HistoryData;
import com.anxell.e5ar.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nsdi-monkey on 2017/6/12.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> implements Filterable {

    private List<HistoryData> mDatas = new ArrayList<HistoryData>();
    private List<HistoryData> mOriginalValues = new ArrayList<HistoryData>();
    private OnRecyclerViewItemClickListener mOnItemClickListener;
    private Activity parentActivity;
    public void updateData(List<HistoryData> datas) {
        mDatas.clear();
        mOriginalValues.clear();
        for(HistoryData data:datas){
            mDatas.add(data);
            mOriginalValues.add(data);
        }

        Util.debugMessage("history Adapter","o size"+mOriginalValues.size(),true);
        Util.debugMessage("history Adapter","Data size"+mDatas.size(),true);
        notifyDataSetChanged();
    }
    public void updateData(HistoryData datas) {
        mOriginalValues.add(datas);
        mDatas.add(datas);
        Util.debugMessage("history Adapter","o size"+mOriginalValues.size(),true);
        Util.debugMessage("history Adapter","Data size"+mDatas.size(),true);
        notifyDataSetChanged();
    }


    public HistoryAdapter(OnRecyclerViewItemClickListener listener, Activity activity) {
        mOnItemClickListener = listener;
        parentActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HistoryData data = mDatas.get(position);
        String id = data.getId();
        String dateTime = data.getDateTime();
        String device = data.getDevice();

        holder.mIdTV.setText(id);
        holder.mDateTimeTV.setText(dateTime);
        holder.mDeviceTV.setText(device);
        if(device.equals(parentActivity.getString(R.string.openType_Alarm))){
            holder.mIdTV.setTextColor(Color.RED);
            holder.mDateTimeTV.setTextColor(Color.RED);
            holder.mDeviceTV.setTextColor(Color.RED);

        }
        else if(device.equals(parentActivity.getString(R.string.openType_Button))){
            holder.mIdTV.setTextColor(Color.BLUE);
            holder.mDateTimeTV.setTextColor(Color.BLUE);
            holder.mDeviceTV.setTextColor(Color.BLUE);

        }else{
            holder.mIdTV.setTextColor(parentActivity.getResources().getColor(R.color.green));
            holder.mDateTimeTV.setTextColor(parentActivity.getResources().getColor(R.color.gray4));
            holder.mDeviceTV.setTextColor(Color.BLACK);
        }
    }
    public void clear(){
        mDatas.clear();
        mOriginalValues.clear();
    }
    @Override
    public int getItemCount() {
        return mDatas.size();
    }
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                Log.e("historyAdapter","publishResults");


            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                Util.debugMessage("history Adapter","o size"+mOriginalValues.size(),true);
                Util.debugMessage("history Adapter","Data size"+mDatas.size(),true);

                mDatas.clear();
                // perform your search here using the searchConstraint String.
                synchronized(this)
                {
                if(constraint != null && constraint.toString().length() > 0) {


                    constraint = constraint.toString().toLowerCase();

                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String userID = mOriginalValues.get(i).getId();
                        if (userID.toLowerCase().startsWith(constraint.toString())) {
                            mDatas.add(mOriginalValues.get(i));
                        }
                    }
                    results.count = mDatas.size();
                    results.values = mDatas;

                    Log.e("VALUES", "filter size="+mDatas.size());
                    Util.debugMessage("history Adapter","t size="+mDatas.size(),true);
                    Util.debugMessage("history Adapter","o size="+mOriginalValues.size(),true);
                }else
                {  Util.debugMessage("history Adapter","o size"+mOriginalValues.size(),true);
                    Util.debugMessage("history Adapter","Data size"+mDatas.size(),true);

                       for(HistoryData data:mOriginalValues)
                        mDatas.add(data);
                        results.values = mDatas;
                        results.count = mDatas.size();

                 }
                }
                return results;
            }
        };

        return filter;
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public FontTextView mIdTV;
        public FontTextView mDateTimeTV;
        public FontTextView mDeviceTV;

        public ViewHolder(View v) {
            super(v);
            mIdTV = (FontTextView) v.findViewById(R.id.id);
            mDateTimeTV = (FontTextView) v.findViewById(R.id.date);
            mDeviceTV = (FontTextView) v.findViewById(R.id.found);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener == null) return;
            mOnItemClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}
