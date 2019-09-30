package com.kloudsync.techexcel2.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.config.AppConfig;
import com.kloudsync.techexcel2.info.Customer;
import com.kloudsync.techexcel2.start.LoginGet;
import com.kloudsync.user.techexcel.pi.tools.MemberBean;
import com.kloudsync.user.techexcel.pi.tools.UserGet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class AddFileFromFavoriteDialog implements View.OnClickListener {
    public Context mContext;
    public int width;
    public int heigth;
    public Dialog dialog;
    private View view;
    private ImageView closeImage;
    private TextView cancelText;
    private TextView saveText;

    public interface OnFavoriteDocSelectedListener {
        void onFavoriteDocSelected(String docId);
    }

    OnFavoriteDocSelectedListener onFavoriteDocSelectedListener;


    public void setOnFavoriteDocSelectedListener(OnFavoriteDocSelectedListener onFavoriteSelectedListener) {
        this.onFavoriteDocSelectedListener = onFavoriteSelectedListener;
    }


    public AddFileFromFavoriteDialog(Context context) {
        mContext = context;
        initDialog();
        getDocumentList();
    }

    public void initDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.dialog_add_file_from_favorite, null);
        dialog = new Dialog(mContext, R.style.my_dialog);
        documentList = (RecyclerView) view.findViewById(R.id.list_document);
        documentList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        closeImage = (ImageView) view.findViewById(R.id.image_close);
        closeImage.setOnClickListener(this);
        cancelText = (TextView) view.findViewById(R.id.cancel);
        saveText = (TextView) view.findViewById(R.id.save);
        saveText.setOnClickListener(this);
        cancelText.setOnClickListener(this);
        width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * (0.85f));
        heigth = (int) (mContext.getResources().getDisplayMetrics().heightPixels * (0.80f));
        dialog.setContentView(view);
        dialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = width;
        lp.height = heigth;
        dialog.getWindow().setAttributes(lp);


    }


    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_close:
                dismiss();
                break;

            case R.id.save:
                if (onFavoriteDocSelectedListener != null) {
                    if (documentAdapter != null) {
                        Document document = documentAdapter.getSelectFile();
                        if (document != null) {
                            onFavoriteDocSelectedListener.onFavoriteDocSelected(document.getItemID());
                            dismiss();
                        } else {
                            Toast.makeText(mContext, "no file selected", Toast.LENGTH_SHORT).show();
                        }
                    }

                }

                break;

            case R.id.cancel:
                dismiss();
                break;

        }
    }

    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    DocumentAdapter documentAdapter;
    RecyclerView documentList;

    private void getDocumentList() {

        AppConfig.SACN_USER_TOKEN = "ee2951d6-7b3e-4f22-984c-c95300b2dcc5";
        UserGet userget = new UserGet();
        userget.setDetailListener(new UserGet.DetailListener() {

            @Override
            public void getUser(Customer user) {

                LoginGet loginGet = new LoginGet();
                loginGet.setMyFavoritesGetListener(new LoginGet.MyFavoritesGetListener() {
                    @Override
                    public void getFavorite(ArrayList<Document> list) {
                        Log.e("getDocumentList","list:" + list.size());
                        documentAdapter = new DocumentAdapter(mContext, list);
                        documentList.setAdapter(documentAdapter);
                    }
                });
//                loginGet.MyFavoriteRequest(mContext, 1, user.getUsertoken());
                loginGet.MyFavoriteRequest(mContext, 1, AppConfig.SACN_USER_TOKEN);

            }

            @Override
            public void getMember(MemberBean member) {

            }
        });
        userget.CustomerDetailRequest(mContext, AppConfig.BINDUSERID);

    }

    class DocHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView time;
        TextView size;
        ImageView imageview;

        public DocHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            size = (TextView) itemView
                    .findViewById(R.id.filesize);
            time = (TextView) itemView
                    .findViewById(R.id.totalTime);
            imageview = (ImageView) itemView
                    .findViewById(R.id.imageview);
        }
    }


    public class DocumentAdapter extends RecyclerView.Adapter<DocHolder> {
        private Context mContext;
        private List<Document> mDatas;
        int selectPosition = -1;


        public DocumentAdapter(Context context, List<Document> mDatas) {
            this.mContext = context;
            this.mDatas = mDatas;

        }


        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        public Document getSelectFile() {
            if (selectPosition == -1) {
                return null;
            }
            return mDatas.get(selectPosition);
        }

        @Override
        public DocHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DocHolder(LayoutInflater.from(mContext).inflate(R.layout.popup_video_item, parent, false));
        }

        @Override
        public void onBindViewHolder(DocHolder holder, final int position) {
            holder.name.setText(mDatas.get(position).getTitle());
            holder.time.setText("123kb");
            String create = mDatas.get(position).getCreatedDate();
            String createdate = "";
            if (!TextUtils.isEmpty(create)) {
                long dd = Long.parseLong(create);
                createdate = new SimpleDateFormat("yyyy.MM.dd").format(dd);
            }
            holder.size.setText(createdate);
            if (selectPosition == position) {
                holder.imageview.setImageResource(R.drawable.finish_a);
            } else {
                holder.imageview.setImageResource(R.drawable.finish_d);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectPosition = position;
                    notifyDataSetChanged();
                }
            });

        }


    }
}
