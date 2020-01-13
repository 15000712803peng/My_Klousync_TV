package com.kloudsync.techexcel2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.bean.AgoraMember;
import com.kloudsync.techexcel2.httpgetimage.ImageLoader;
import com.kloudsync.techexcel2.view.CircleImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wang on 2018/8/8.
 */

public class AgoraCameraAdapter extends RecyclerView.Adapter<AgoraCameraAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<AgoraMember> users;
    private Context mContext;
    private ImageLoader imageLoader;
    public List<AgoraMember> getUsers() {
        return users;
    }



    public AgoraCameraAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
        users = new ArrayList<>();
        imageLoader = new ImageLoader(context);
    }

    private OnCameraOptionsListener onCameraOptionsListener;

    public void setOnCameraOptionsListener(OnCameraOptionsListener onCameraOptionsListener) {
        this.onCameraOptionsListener = onCameraOptionsListener;
    }

    public interface OnCameraOptionsListener {
        void onCameraFrameClick(AgoraMember member);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.small_camera_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ViewHolder myHolder = holder;
        final AgoraMember user = users.get(position);

        holder.vedioFrame.removeAllViews();
        if (holder.vedioFrame.getChildCount() == 0) {
            View d = inflater.inflate(R.layout.framelayout_head, null);
            TextView videoname = (TextView) d.findViewById(R.id.videoname);
            ViewParent parent = videoname.getParent();
            if (parent != null) {
                ((RelativeLayout) parent).removeView(videoname);
            }
            holder.vedioFrame.addView(videoname);
            SurfaceView target = user.getSurfaceView();
            if (!user.isMuteVideo()) {
                target.setVisibility(View.VISIBLE);
                stripSurfaceView(target);
                holder.vedioFrame.addView(target, 0, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } else {
                Log.e("onBindViewHolder","target_gone");
                stripSurfaceView(target);
                target.setVisibility(View.INVISIBLE);
            }
            myHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCameraOptionsListener != null) {
                        onCameraOptionsListener.onCameraFrameClick(user);
                    }
                }
            });

            //---
            if(!TextUtils.isEmpty(user.getUserName())){
                holder.nameText.setText(user.getUserName());
            }else {
                holder.nameText.setText("");
            }

            if(user.isMuteAudio()){
                holder.audioStatusImage.setImageResource(R.drawable.icon_command_mic_disable);
            }else {
                holder.audioStatusImage.setImageResource(R.drawable.icon_command_mic_enabel);
            }

            if(TextUtils.isEmpty(user.getIconUrl())){
                holder.iconImage.setImageResource(R.drawable.hello);
            }else {
                imageLoader.DisplayImage(user.getIconUrl(), holder.iconImage);
            }

            if(user.isMuteVideo()){
                holder.vedioStatusImage.setImageResource(R.drawable.icon_command_webcam_disable);
                holder.iconImage.setVisibility(View.VISIBLE);

            }else {
                holder.vedioStatusImage.setImageResource(R.drawable.icon_command_webcam_enable);
                holder.iconImage.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public AgoraMember getItem(int position) {
        return users.get(position);
    }

    protected final void stripSurfaceView(SurfaceView view) {
        ViewParent parent = view.getParent();
        if (parent != null) {
            ((FrameLayout) parent).removeView(view);
        }
    }

    public void muteOrOpenCamera(int myId, boolean isMute) {
        int index = users.indexOf(new AgoraMember(myId));
        if (index >= 0 && index < users.size()) {
            users.get(index).setMuteVideo(isMute);
            Log.e("muteOrOpenCamera","isMute:" + isMute);
            notifyDataSetChanged();
        }
    }

    public void setMembers(List<AgoraMember> users) {
        if (users != null) {
            this.users.clear();
            this.users.addAll(users);
            Collections.sort(users);
        }
        notifyDataSetChanged();
    }

    public void addUser(AgoraMember user) {

        if (users.contains(user)) {
            return;
        }
        this.users.add(user);
        Collections.sort(users);
        notifyItemInserted(this.users.indexOf(user));
    }

    public void removeUser(AgoraMember user) {
        int index = users.indexOf(user);
        if(index >= 0){
            this.users.remove(user);
//            Collections.sort(users);
            notifyItemRemoved(index);

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
            vedioFrame = view.findViewById(R.id.video_view_container);
            nameText = view.findViewById(R.id.txt_name);
            audioStatusImage = view.findViewById(R.id.image_audio_status);
            vedioStatusImage = view.findViewById(R.id.image_vedio_status);
            iconImage = view.findViewById(R.id.member_icon);
        }

        public FrameLayout vedioFrame;
        public TextView nameText;
        public ImageView audioStatusImage;
        public ImageView vedioStatusImage;
        public CircleImageView iconImage;
    }

   public void reset(){
       for (AgoraMember member : this.users) {
           SurfaceView surfaceView = member.getSurfaceView();
           stripSurfaceView(surfaceView);
       }
       this.users.clear();
       notifyDataSetChanged();
   }

   public void muteVideo(AgoraMember member,boolean isMute){
       int index = this.users.indexOf(member);
       if(index >= 0){
           this.users.get(index).setMuteVideo(isMute);
           notifyItemChanged(index);
       }
   }

    public void muteAudio(AgoraMember member,boolean isMute){
        int index = this.users.indexOf(member);
        Log.e("AgoraCameraAdapter","muteAudio:" + isMute + ",index:" + index);
        if(index >= 0){
            this.users.get(index).setMuteAudio(isMute);
            notifyItemChanged(index);
        }
    }

}
