package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.info.Favorite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/9/18.
 */

public class FavoritePopup {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private List<Favorite> list = new ArrayList<Favorite>();
    private DocumentAdapter mDocumentAdapter;
    private ListView listView;
    private View view;

    private ImageView addsavefile;
    private int  type;

    public void getPopwindow(Context context, List<Favorite> list,int type) {
        this.mContext = context;
        this.list = list;

        this.type=type;
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

    private static FavoritePoPListener mFavoritePoPListener;

    public interface FavoritePoPListener {

        void selectFavorite(int position);

        void uploadFile();
        void dismiss();

        void open();
    }

    public void setFavoritePoPListener(FavoritePoPListener documentPoPListener) {
        this.mFavoritePoPListener = documentPoPListener;
    }

    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.popup_document, null);
        listView = (ListView) view.findViewById(R.id.listview);
        addsavefile= (ImageView) view.findViewById(R.id.addsavefile);
        if(type==2){
            addsavefile.setVisibility(View.VISIBLE);
        }else if(type==1){
            addsavefile.setVisibility(View.GONE);
        }

        mDocumentAdapter = new DocumentAdapter(mContext, list,
                R.layout.popup_document_item);
        listView.setAdapter(mDocumentAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mFavoritePoPListener.selectFavorite(position);
            }
        });
        addsavefile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFavoritePoPListener.uploadFile();
            }
        });
        mPopupWindow = new PopupWindow(view, width / 2,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.getWidth();
        mPopupWindow.getHeight();
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mFavoritePoPListener.dismiss();
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }


    @SuppressLint("NewApi")
    public void StartPop(View v) {
        if (mPopupWindow != null) {
            mFavoritePoPListener.open();
            mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        }
    }


    public void dismiss() {
        if (mPopupWindow != null) {
            mFavoritePoPListener.open();
            mPopupWindow.dismiss();
        }
    }

    public class DocumentAdapter extends BaseAdapter {
        private Context context;
        private List<Favorite> mDatas;
        private int itemLayoutId;

        public DocumentAdapter(Context context, List<Favorite> mDatas,
                               int itemLayoutId) {
            this.context = context;
            this.mDatas = mDatas;
            this.itemLayoutId = itemLayoutId;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        itemLayoutId, null);
                holder.values = (TextView) convertView
                        .findViewById(R.id.document_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.values.setText(mDatas.get(position).getTitle());
            return convertView;
        }

        class ViewHolder {
            TextView values;
        }
    }

}
