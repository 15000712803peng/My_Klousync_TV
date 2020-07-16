package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel2.R;
import com.ub.techexcel.bean.LineItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class SyncRoomDocumentPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;
    private TextView adddocument;
    private RecyclerView recycleview;
    private SyncRoomTeamAdapter syncRoomTeamAdapter;

    public void getPopwindow(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
    }

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    LinearLayout upload_linearlayout;
    LinearLayout morell;

    public void initPopuptWindow() {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.syncroom_document_popup, null);

        recycleview = (RecyclerView) view.findViewById(R.id.recycleview);
        adddocument = (TextView) view.findViewById(R.id.adddocument);
        adddocument.setOnClickListener(this);
        recycleview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        upload_linearlayout = (LinearLayout) view.findViewById(R.id.upload_linearlayout);
        morell = (LinearLayout) view.findViewById(R.id.morell);

        RelativeLayout fromTeamDocument = (RelativeLayout) view.findViewById(R.id.fromteamdocument);
        RelativeLayout take_photo = (RelativeLayout) view.findViewById(R.id.take_photo);
        RelativeLayout file_library = (RelativeLayout) view.findViewById(R.id.file_library);
        RelativeLayout save_file = (RelativeLayout) view.findViewById(R.id.save_file);

        fromTeamDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_linearlayout.setVisibility(View.GONE);
                webCamPopupListener.teamDocument();
            }
        });
        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_linearlayout.setVisibility(View.GONE);
                webCamPopupListener.takePhoto();
            }
        });
        file_library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_linearlayout.setVisibility(View.GONE);
                webCamPopupListener.importFromLibrary();
            }
        });
        save_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_linearlayout.setVisibility(View.GONE);
                webCamPopupListener.savedFile();
            }
        });


        RelativeLayout moreshare = (RelativeLayout) view.findViewById(R.id.moreshare);
        RelativeLayout moreedit = (RelativeLayout) view.findViewById(R.id.moreedit);
        RelativeLayout moredelete = (RelativeLayout) view.findViewById(R.id.moredelete);
        moreshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                morell.setVisibility(View.GONE);

            }
        });
        moreedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                morell.setVisibility(View.GONE);
                webCamPopupListener.edit(selectLineItem);

            }
        });
        moredelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                morell.setVisibility(View.GONE);
                webCamPopupListener.delete(selectLineItem);

            }
        });
        mPopupWindow = new Dialog(mContext, R.style.my_dialog);
        mPopupWindow.setContentView(view);
        mPopupWindow.getWindow().setGravity(Gravity.RIGHT);
        WindowManager.LayoutParams params = mPopupWindow.getWindow().getAttributes();
//        DisplayMetrics dm = new DisplayMetrics();
//        (((Activity)mContext).getWindowManager()).getDefaultDisplay().getRealMetrics(dm);
        View root = ((Activity) mContext).getWindow().getDecorView();
        params.height = root.getMeasuredHeight();
        mPopupWindow.getWindow().setAttributes(params);
        mPopupWindow.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mPopupWindow.getWindow().setWindowAnimations(R.style.anination3);


    }


    @SuppressLint("NewApi")
    public void StartPop(View v, List<LineItem> list) {
        if (mPopupWindow != null) {
            webCamPopupListener.open();
            mPopupWindow.show();
            syncRoomTeamAdapter = new SyncRoomTeamAdapter(mContext, list);
            recycleview.setAdapter(syncRoomTeamAdapter);

        }
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }


    public interface WebCamPopupListener {

        void changeOptions(LineItem syncRoomBean, int position);

        void teamDocument();

        void takePhoto();

        void importFromLibrary();

        void savedFile();

        void dismiss();

        void open();

        void delete(LineItem selectLineItem);

        void edit(LineItem selectLineItem);


    }

    public void setWebCamPopupListener(WebCamPopupListener webCamPopupListener) {
        this.webCamPopupListener = webCamPopupListener;
    }

    private WebCamPopupListener webCamPopupListener;


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_closed:
                mPopupWindow.dismiss();
                break;
            case R.id.adddocument:
                if (upload_linearlayout.getVisibility() == View.VISIBLE) {
                    upload_linearlayout.setVisibility(View.GONE);
                } else {
                    upload_linearlayout.setVisibility(View.VISIBLE);
                }
                morell.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }


    private LineItem selectLineItem = new LineItem();

    public class SyncRoomTeamAdapter extends RecyclerView.Adapter<SyncRoomTeamAdapter.RecycleHolder2> {

        private Context context;

        private List<LineItem> list = new ArrayList<>();

        private void setSelectedFile(LineItem file){
            for(LineItem fileItem : list){
                if(file.equals(fileItem)){
                    fileItem.setSelect(true);
                }else {
                    fileItem.setSelect(false);
                }
            }
            notifyDataSetChanged();
        }

        public SyncRoomTeamAdapter(Context context, List<LineItem> list) {
            this.context = context;
            this.list.addAll(list);
        }

        @Override
        public RecycleHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.syncroom_document_popup_item, parent, false);
            RecycleHolder2 holder = new RecycleHolder2(view);
            return holder;
        }


        @Override
        public void onBindViewHolder(RecycleHolder2 holder, final int position) {
            final LineItem lineItem = list.get(position);
            holder.name.setText(lineItem.getFileName());
            holder.mTvCreateUserName.setText(null);
            int syncCount = lineItem.getSyncRoomCount();
            if (syncCount > 0) {
                holder.mLlyYinXiangCount.setVisibility(View.VISIBLE);
                holder.mTvYinXiangCount.setText(String.valueOf(syncCount));
            } else {
                holder.mLlyYinXiangCount.setVisibility(View.GONE);
                holder.mTvYinXiangCount.setText("");
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    morell.setVisibility(View.GONE);
                    setSelectedFile(lineItem);
                    webCamPopupListener.changeOptions(lineItem,position);
                }
            });
            holder.mIvItemDocMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectLineItem = lineItem;
                    if (morell.getVisibility() == View.VISIBLE) {
                        morell.setVisibility(View.GONE);
                    } else {
                        morell.setVisibility(View.VISIBLE);
                    }
                }
            });
            String url = lineItem.getUrl();
            if (!TextUtils.isEmpty(url)) {
                url = url.substring(0, url.lastIndexOf("<")) + "1" + url.substring(url.lastIndexOf("."), url.length());
                Uri imageUri = null;
                if (!TextUtils.isEmpty(url)) {
                    imageUri = Uri.parse(url);
                }
                holder.icon.setImageURI(imageUri);
            }
            holder.headll.setSelected(lineItem.isSelect());

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void setSearchList(List<LineItem> searchList) {
            list.clear();
            list.addAll(searchList);
        }

        class RecycleHolder2 extends RecyclerView.ViewHolder {
            SimpleDraweeView icon;
            TextView name, mTvYinXiangCount, mTvCreateUserName;
            LinearLayout headll, mLlyYinXiangCount;
            ImageView mIvItemDocMore;

            public RecycleHolder2(View itemView) {
                super(itemView);
                icon = itemView.findViewById(R.id.studenticon);
                name = (TextView) itemView.findViewById(R.id.studentname);
                headll = (LinearLayout) itemView.findViewById(R.id.headll);
                mIvItemDocMore = itemView.findViewById(R.id.iv_item_doc_more);
                mTvCreateUserName = itemView.findViewById(R.id.tv_item_create_user_name);
                mLlyYinXiangCount = itemView.findViewById(R.id.lly_item_doc_yin_xiang_count);
                mTvYinXiangCount = itemView.findViewById(R.id.tv_item_yin_xiang_count);
            }
        }
    }
}
