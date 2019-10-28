package com.ub.service.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.bean.OutlineChapterItem;
import com.kloudsync.techexcel2.bean.OutlineChildSectionItem;
import com.kloudsync.techexcel2.bean.OutlineSectionItem;
import com.kloudsync.techexcel2.bean.SyncBook;
import com.kloudsync.techexcel2.bean.SyncbookInfo;
import com.kloudsync.techexcel2.resp.NetworkResponse;
import com.kloudsync.techexcel2.view.viewtree.AndroidTreeView;
import com.kloudsync.techexcel2.view.viewtree.OutlineChapterItemHolder;
import com.kloudsync.techexcel2.view.viewtree.OutlineChildChildSectionItemHolder;
import com.kloudsync.techexcel2.view.viewtree.OutlineChildSectionItemHolder;
import com.kloudsync.techexcel2.view.viewtree.OutlineSectionItemHolder;
import com.kloudsync.techexcel2.view.viewtree.mode.TreeNode;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.util.List;
import retrofit2.Response;

public class SyncBookOutlineManager {

    public Context mContext;
    public int width;
    private View view;
    AndroidTreeView outlineView;
    private ImageView closeImage;
    LinearLayout outlineLayout;
    TextView docNameText;
    private String syncroomId;
    SyncbookInfo syncbookInfo;


    public void prepare(Context mContext,View view,String syncroomId) {
        this.mContext = mContext;
        this.view = view;
        this.syncroomId = syncroomId;
        init();

    }


    private TreeNode parseSyncbook(SyncbookInfo syncbookInfo, TreeNode rootNode) {
        TreeNode root = null;
        List<OutlineChapterItem> chapterItems = syncbookInfo.getChapterItems();
        if (chapterItems != null && chapterItems.size() > 0) {

            for (int chapterIndex = 0; chapterIndex < chapterItems.size(); ++chapterIndex) {
                OutlineChapterItem chapterItem = chapterItems.get(chapterIndex);

                if (chapterItem.getSectionItems() == null || chapterItem.getSectionItems().size() <= 0) {
                    continue;
                }

                int cIndex = chapterIndex <= 0 ? 0 : chapterIndex;
                final int currentChapterIndex = cIndex;

                TreeNode chapterNode = new TreeNode(chapterItem).setViewHolder
                        (new OutlineChapterItemHolder(mContext));
                chapterNode.setClickListener(new TreeNode.TreeNodeClickListener() {
                    @Override
                    public void onClick(TreeNode node, Object value) {
                        if (onChapterClickedListener != null) {
                            onChapterClickedListener.onChapterClicked((OutlineChapterItem) value, currentChapterIndex);
                        }
                    }
                });

                List<OutlineSectionItem> sectionItems = chapterItem.getSectionItems();
                if (sectionItems != null && sectionItems.size() > 0) {

                    for (int i = 0; i < sectionItems.size(); ++i) {

                        TreeNode sectionItemNode = new TreeNode(sectionItems.get(i)).setViewHolder
                                (new OutlineSectionItemHolder(mContext));

                        sectionItemNode.setClickListener(new TreeNode.TreeNodeClickListener() {
                            @Override
                            public void onClick(TreeNode node, Object value) {
                                if (onChapterClickedListener != null) {
                                    onChapterClickedListener.onSectionItemClicked((OutlineSectionItem) value,currentChapterIndex);
                                }
                            }
                        });


                        List<OutlineChildSectionItem> childSectionItems = sectionItems.get(i).getChildSectionItems();
                        if (childSectionItems != null && childSectionItems.size() > 0) {
                            for (OutlineChildSectionItem childSectionItem : childSectionItems) {
                                TreeNode childSectionNode = new TreeNode(childSectionItem).setViewHolder
                                        (new OutlineChildSectionItemHolder(mContext));

                                childSectionNode.setClickListener(new TreeNode.TreeNodeClickListener() {
                                    @Override
                                    public void onClick(TreeNode node, Object value) {
                                        if (onChapterClickedListener != null) {
                                            onChapterClickedListener.onChildSectionItemClicked((OutlineChildSectionItem) value,currentChapterIndex);
                                        }
                                    }
                                });

                                //---

                                List<OutlineChildSectionItem> childChildSectionItems = childSectionItem.getChildSectionItems();
                                if (childChildSectionItems != null &&
                                        childChildSectionItems.size() > 0) {
                                    for (OutlineChildSectionItem childChildSectionItem : childChildSectionItems) {
                                        TreeNode childChildSectionNode = new TreeNode(childChildSectionItem).setViewHolder
                                                (new OutlineChildChildSectionItemHolder(mContext));


                                        childChildSectionNode.setClickListener(new TreeNode.TreeNodeClickListener() {
                                            @Override
                                            public void onClick(TreeNode node, Object value) {
                                                if (onChapterClickedListener != null) {
                                                    onChapterClickedListener.onChildSectionItemClicked((OutlineChildSectionItem) value,currentChapterIndex);
                                                }
                                            }
                                        });
                                        // -----

                                        List<OutlineChildSectionItem> childChildChildSectionItems = childChildSectionItem.getChildSectionItems();
                                        if (childChildChildSectionItems != null && childChildChildSectionItems.size() > 0) {
                                            for (OutlineChildSectionItem childChildChildSectionItem : childChildChildSectionItems) {
                                                TreeNode childChildChildSectionNode = new TreeNode(childChildChildSectionItem).setViewHolder
                                                        (new OutlineChildChildSectionItemHolder(mContext));

                                                childChildChildSectionNode.setClickListener(new TreeNode.TreeNodeClickListener() {
                                                    @Override
                                                    public void onClick(TreeNode node, Object value) {
                                                        if (onChapterClickedListener != null) {
                                                            onChapterClickedListener.onChildSectionItemClicked((OutlineChildSectionItem) value,currentChapterIndex);
                                                        }
                                                    }
                                                });
                                                childChildSectionNode.addChild(childChildChildSectionNode);
                                            }

                                        }

                                        childSectionNode.addChild(childChildSectionNode);

                                    }
                                }

                                sectionItemNode.addChild(childSectionNode);
                            }
                        }

                        chapterNode.addChild(sectionItemNode);
                    }


                }
                rootNode.addChild(chapterNode);
            }

        }

        return root;

    }


    public void init() {
        outlineLayout = (LinearLayout) view.findViewById(R.id.layout_outline_inside);
        docNameText = (TextView) view.findViewById(R.id.txt_doc_name);
        closeImage = (ImageView)view.findViewById(R.id.image_close);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setVisibility(View.GONE);
                if(onOutlineCloseListener != null){
                    onOutlineCloseListener.onOutlineClose();
                }
            }
        });
        if(this.syncbookInfo == null){
            new GetOutlineTask().execute();
        }

    }


    public void setOnOutlineCloseListener(OnOutlineCloseListener onOutlineCloseListener) {
        this.onOutlineCloseListener = onOutlineCloseListener;
    }

    public interface OnOutlineCloseListener{
        void onOutlineClose();
    }

    OnOutlineCloseListener onOutlineCloseListener;




    private class GetOutlineTask extends AsyncTask<Void, Void, SyncbookInfo> {

        @Override
        protected SyncbookInfo doInBackground(Void... params) {

            try {
              Response<NetworkResponse<SyncBook>> response = ServiceInterfaceTools.getinstance().getSyncbookOutline(syncroomId).execute();
              if(response != null && response.isSuccessful() && response.body() != null){

                  try {
                      JSONObject jsonObject = new JSONObject(response.body().getRetData().getOutlineInfo());
                      return new Gson().fromJson(jsonObject.getJSONObject("BookInfo").toString(),SyncbookInfo.class);
                  } catch (JSONException e) {
                      e.printStackTrace();
                      return null;
                  }

              }else {
                  return null;
              }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(SyncbookInfo syncbookInfo) {
            super.onPostExecute(syncbookInfo);
            TreeNode rootNode = TreeNode.root();

            Log.e("SyncbookInfo", "" + syncbookInfo);
            if (syncbookInfo != null) {
                SyncBookOutlineManager.this.syncbookInfo = syncbookInfo;
                parseSyncbook(syncbookInfo, rootNode);
                outlineView = new AndroidTreeView(mContext, rootNode);
                outlineView.setDefaultAnimation(true);
                outlineView.setUse2dScroll(true);
                outlineLayout.addView(outlineView.getView());
                docNameText.setText(syncbookInfo.getBookTitle());
            }


        }
    }


    public interface OnChapterClickedListener {
        void onChapterClicked(OutlineChapterItem chapterItem, int chapterIndex);

        void onSectionItemClicked(OutlineSectionItem sectionItem, int chapterIndex);

        void onChildSectionItemClicked(OutlineChildSectionItem childSectionItem, int chapterIndex);
    }

    private OnChapterClickedListener onChapterClickedListener;


    public void setOnChapterClickedListener(OnChapterClickedListener onChapterClickedListener) {
        this.onChapterClickedListener = onChapterClickedListener;
    }


}
