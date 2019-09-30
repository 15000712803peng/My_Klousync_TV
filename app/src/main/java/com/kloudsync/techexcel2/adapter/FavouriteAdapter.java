package com.kloudsync.techexcel2.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.info.Favorite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pingfan on 2017/12/11.
 */

public class FavouriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnLongClickListener, View.OnClickListener {


    private List<Favorite> mlist = new ArrayList<>();



    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    private OnRecyclerViewItemLongClickListener mOnItemLongClickListener = null;

    public static interface OnRecyclerViewItemLongClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnItemLongClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemLongClickListener.onItemClick(v, (Integer) v.getTag());
        }
        return false;
    }

    public FavouriteAdapter(List<Favorite> mlist) {
        this.mlist = mlist;
    }

    public void UpdateRV(List<Favorite> mlist) {
        this.mlist = mlist;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favour, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.tv_favour.setText(mlist.get(position).getFileName());
        holder.lin_favour.setOnLongClickListener(this);
        holder.lin_favour.setOnClickListener(this);
        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_favour;
        LinearLayout lin_favour;

        ViewHolder(View view) {
            super(view);
            tv_favour = (TextView) view.findViewById(R.id.tv_favour);
            lin_favour = (LinearLayout) view.findViewById(R.id.lin_favour);
        }
    }
}
