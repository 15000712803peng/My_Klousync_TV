package com.ub.techexcel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ub.techexcel.bean.ServiceBean;
import com.kloudsync.techexcel2.R;

import java.util.List;

/**
 * Created by wang on 2018/7/30.
 */

public class NotifyRecyclerAdapter extends RecyclerView.Adapter<NotifyHolder> {

    private List<ServiceBean> serviceList;
    private Context context;

    public NotifyRecyclerAdapter(Context context, List<ServiceBean> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }

    @Override
    public NotifyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_item, parent, false);
        NotifyHolder holder = new NotifyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(NotifyHolder holder, int position) {
        final ServiceBean bean = serviceList.get(position);
        holder.klsId.setText("Klassroom "+bean.getId());
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }
}

class NotifyHolder extends RecyclerView.ViewHolder {

    TextView klsId;

    public NotifyHolder(View itemView) {
        super(itemView);
        klsId = (TextView) itemView.findViewById(R.id.klsid);
    }

}





