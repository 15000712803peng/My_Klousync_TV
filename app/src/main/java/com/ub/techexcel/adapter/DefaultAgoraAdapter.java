package com.ub.techexcel.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ub.service.activity.WatchCourseActivity2;
import com.ub.service.activity.WatchCourseActivity3;
import com.ub.techexcel.bean.AgoraBean;
import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.tool.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import io.agora.openlive.ui.VideoViewEventListener;

/**
 * Created by wang on 2018/8/8.
 */

public class DefaultAgoraAdapter extends RecyclerView.Adapter<DefaultAgoraAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<AgoraBean> mDatas;
    private Context mContext;
    private VideoViewEventListener mListener;

    public void setItemEventHandler(VideoViewEventListener listener) {
        this.mListener = listener;
    }

    public DefaultAgoraAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
        mDatas = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.video_view_container, parent, false);
        return new ViewHolder(view);
    }

    int flag = 7;

    public void setHeightByCount(int flag) {
        this.flag = flag;
        notifyDataSetChanged();
    }


    private boolean isHidden;

    public void hiddenText(boolean isHidden) {
        this.isHidden = isHidden;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ViewHolder myHolder = holder;
        final AgoraBean agoraBean = mDatas.get(position);

        FrameLayout holderView = (FrameLayout) myHolder.itemView;
        holderView.removeAllViews();

        int height = mContext.getResources().getDisplayMetrics().heightPixels;
        Rect frame = new Rect();
        ((Activity) mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int notifiheight = frame.top;
        height -= notifiheight;
        int framelayoutHeight = height;
        if (holderView.getChildCount() == 0) {
            View d = inflater.inflate(R.layout.framelayout_head, null);
            TextView videoname = (TextView) d.findViewById(R.id.videoname);
            if (isHidden) {
                videoname.setText("");
            } else {
                videoname.setText(agoraBean.getUserName());
            }
            ViewParent parent = videoname.getParent();
            if (parent != null) {
                ((RelativeLayout) parent).removeView(videoname);
            }
            holderView.addView(videoname);

            int typex = getItemViewType(position);
            if (agoraBean.isMuteVideo() == false) {
                SurfaceView target = agoraBean.getSurfaceView();
                stripSurfaceView(target);
                if (1 == typex) {
                    holderView.addView(target, 0, new FrameLayout.LayoutParams(DensityUtil.dp2px(mContext, 201), framelayoutHeight / 4));
                } else {
                    holderView.addView(target, 0, new FrameLayout.LayoutParams(DensityUtil.dp2px(mContext, 201 / 3), framelayoutHeight / 12));
                }
            }
            if (1 == typex) {
                holderView.setLayoutParams(new FrameLayout.LayoutParams(DensityUtil.dp2px(mContext, 201), framelayoutHeight / 4));
            } else {
                holderView.setLayoutParams(new FrameLayout.LayoutParams(DensityUtil.dp2px(mContext, 201 / 3), framelayoutHeight / 12));
            }
        }

        holderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    if (WatchCourseActivity2.watch2instance) {
                        if (WatchCourseActivity2.mViewType == 0) {
                            mListener.onItemDoubleClick(agoraBean);
                        } else if (WatchCourseActivity2.mViewType == 2) {
                            mListener.onSwitchVideo(agoraBean);
                        }
                    } else if (WatchCourseActivity3.watch3instance) {
                        if (WatchCourseActivity3.mViewType == 0) {
                            mListener.onItemDoubleClick(agoraBean);
                        } else if (WatchCourseActivity3.mViewType == 2) {
                            mListener.onSwitchVideo(agoraBean);
                        }
                    }
                }
            }
        });

    }


    public List<AgoraBean> getmDatas() {
        return mDatas;
    }

    private int HAHA(int position) {
        if (mDatas.size() < 4) {
            return 1;
        } else if (mDatas.size() < 13) {
            if (position < 3) {
                return 1;
            } else {
                return 2;
            }
        } else if (mDatas.size() < 21) {
            if (position < 2) {
                return 1;
            } else {
                return 2;
            }
        } else if (mDatas.size() < 29) {
            if (position < 1) {
                return 1;
            } else {
                return 2;
            }
        } else {
            return 2;
        }

    }

    @Override
    public int getItemCount() {
        int sizeLimit = mDatas.size();
        return sizeLimit;
    }

    public AgoraBean getItem(int position) {
        return mDatas.get(position);
    }


    public void setData(List<AgoraBean> datas, String teacherId) {
        if (datas != null) {
            mDatas.clear();
            for (AgoraBean mData : datas) {
                SurfaceView surfaceView = mData.getSurfaceView();
                surfaceView.setVisibility(View.VISIBLE);
                surfaceView.setZOrderOnTop(true);
                surfaceView.setZOrderMediaOverlay(true);
                AgoraBean agoraBean = new AgoraBean();
                agoraBean.setuId(mData.getuId());
                agoraBean.setSurfaceView(surfaceView);
                agoraBean.setMuteAudio(mData.isMuteAudio());
                agoraBean.setMuteVideo(mData.isMuteVideo());
                agoraBean.setUserName(mData.getUserName());
                if (TextUtils.isEmpty(teacherId)) {
                    mDatas.add(agoraBean);
                } else {
                    if (teacherId.equals(agoraBean.getuId() + "")) {
                        mDatas.add(0, agoraBean);
                    } else {
                        mDatas.add(agoraBean);
                    }
                }
            }
        } else {
            for (AgoraBean mData : mDatas) {
                SurfaceView surfaceView = mData.getSurfaceView();   //SurfaceView单独在一个Window之上，不和父控件在一个View树中
                stripSurfaceView(surfaceView);
            }
            mDatas.clear();
        }

        notifyDataSetChanged();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 1000);
    }


    protected final void stripSurfaceView(SurfaceView view) {
        ViewParent parent = view.getParent();
        if (parent != null) {
            ((FrameLayout) parent).removeView(view);
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View a) {
            super(a);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return HAHA(position);
    }
}
