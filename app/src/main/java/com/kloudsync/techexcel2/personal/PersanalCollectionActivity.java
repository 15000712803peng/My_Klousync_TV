package com.kloudsync.techexcel2.personal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel2.dialog.Document;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.adapter.FavouriteAdapter;
import com.kloudsync.techexcel2.config.AppConfig;
import com.kloudsync.techexcel2.dialog.SaveFavoritesActivity;
import com.kloudsync.techexcel2.dialog.message.SendFileMessage;
import com.kloudsync.techexcel2.help.PopAlbums;
import com.kloudsync.techexcel2.help.PopDeleteFavorite;
import com.kloudsync.techexcel2.help.Popupdate;
import com.kloudsync.techexcel2.info.Favorite;
import com.kloudsync.techexcel2.service.ConnectService;
import com.kloudsync.techexcel2.start.LoginGet;
import com.kloudsync.techexcel2.tool.NetWorkHelp;

import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by pingfan on 2017/7/5.
 */

public class PersanalCollectionActivity extends SwipeBackActivity {

    private TextView tv_back;
    private ImageView img_add;
    private RecyclerView rv_pc;
    private RelativeLayout rl_update;
    private LinearLayout lin_main;
    private ArrayList<Favorite> mlist = new ArrayList<Favorite>();
    private FavouriteAdapter fAdapter;

    private static final int REQUEST_CODE_CAPTURE_MEDIA = 2;

    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(Message msg) {
            rl_update.setVisibility(View.GONE);
            if (puo != null)
                puo.DissmissPop();

            switch (msg.what) {
                case AppConfig.FAILED:
                    String result = (String) msg.obj;
                    Toast.makeText(getApplicationContext(),
                            result,
                            Toast.LENGTH_LONG).show();
                    break;
                case AppConfig.DELETESUCCESS:
                    GetData();
                    break;
                case AppConfig.NO_NETWORK:
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(R.string.No_networking),
                            Toast.LENGTH_SHORT).show();

                    break;
                case AppConfig.NETERROR:
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(R.string.NETWORK_ERROR),
                            Toast.LENGTH_SHORT).show();

                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalcollection);

        initView();
        GetData();
    }

    private void GetData() {
        LoginGet lg = new LoginGet();
        lg.setMyFavoritesGetListener(new LoginGet.MyFavoritesGetListener() {
            @Override
            public void getFavorite(ArrayList<Document> list) {
//                mlist = new ArrayList<Favorite>();
//                mlist.addAll(list);
//                fAdapter.UpdateRV(mlist);
            }
        });
        lg.MyFavoriteRequest(getApplicationContext(), 0);
    }

    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        img_add = (ImageView) findViewById(R.id.img_add);
        rl_update = (RelativeLayout) findViewById(R.id.rl_update);
        lin_main = (LinearLayout) findViewById(R.id.lin_main);
        rv_pc = (RecyclerView) findViewById(R.id.rv_pc);
        tv_back.setOnClickListener(new MyOnclick());
        img_add.setOnClickListener(new MyOnclick());

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_pc.setLayoutManager(manager);
        fAdapter = new FavouriteAdapter(mlist);
        fAdapter.setOnItemLongClickListener(new FavouriteAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PopDeleteFavorite pdf = new PopDeleteFavorite();
                final Favorite fav = mlist.get(position);
                pdf.getPopwindow(getApplicationContext(), fav);
                pdf.setPoPDismissListener(new PopDeleteFavorite.PopUpdateOutDismissListener() {
                    @Override
                    public void PopDismiss(boolean isDelete) {
                        if (isDelete) {
                            DeleteFavorite(fav);
                        }
                        BackChange(1.0f);
                    }
                });
                pdf.StartPop(view);
                BackChange(0.5f);
            }
        });
        fAdapter.setOnItemClickListener(new FavouriteAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent = new Intent(view.getContext(), SaveFavoritesActivity.class);
                Favorite fav = mlist.get(position);
                SendFileMessage sfm = new SendFileMessage();
                sfm.setAttachmentID(fav.getAttachmentID() + "");
                sfm.setFileDownloadURL(fav.getFileDownloadURL() + "");
                sfm.setFileName(fav.getFileName() + "");
                intent.putExtra("sendFileMessage", (Parcelable) sfm);
                view.getContext().startActivity(intent);
            }
        });
        rv_pc.setAdapter(fAdapter);
    }

    private void BackChange(float value) {
        lin_main.animate().alpha(value);
        lin_main.animate().setDuration(500);
    }

    private void DeleteFavorite(final Favorite fav) {
        rl_update.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    JSONObject responsedata = ConnectService.getIncidentDataattachment(
                            AppConfig.URL_PUBLIC +
                                    "EventAttachment/RemoveFavorite?" +
                                    "attachmentIDs=" +
                                    fav.getAttachmentID());
                    Log.e("Removeresponse", responsedata.toString());
                    int retcode = (Integer) responsedata.get("RetCode");
                    msg = new Message();
                    if (0 == retcode) {
                        msg.what = AppConfig.DELETESUCCESS;
                        String result = responsedata.toString();
                        msg.obj = result;
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("errorMessage");
                        msg.obj = ErrorMessage;
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    msg.what = AppConfig.NETERROR;
                } finally {
                    if (!NetWorkHelp.checkNetWorkStatus(getApplicationContext())) {
                        msg.what = AppConfig.NO_NETWORK;
                    }
                    handler.sendMessage(msg);
                }
            }
        }).start();

    }


    protected class MyOnclick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_back:
                    FinishActivityanim();
                    break;
                case R.id.img_add:
                    AddAlbum();
                    break;
                default:
                    break;
            }
        }
    }

    private void AddAlbum() {
        PopAlbums pa = new PopAlbums();
        pa.getPopwindow(getApplicationContext());
        pa.setPoPDismissListener(new PopAlbums.PopAlbumsDismissListener() {
            @Override
            public void PopDismiss(boolean isAdd) {
                if (isAdd) {
                    GetVideo();
                }
                BackChange(1.0f);
            }
        });
        pa.StartPop(img_add);
        BackChange(0.5f);

    }

    private void GetVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_CAPTURE_MEDIA);
    }


    private void FinishActivityanim() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAPTURE_MEDIA && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
//            int videoId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
            // 视频名称：MediaStore.Audio.Media.TITLE
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
            // 视频路径：MediaStore.Audio.Media.DATA
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            // 视频时长：MediaStore.Audio.Media.DURATION
//            int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
            // 视频大小：MediaStore.Audio.Media.SIZE
//            long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
            cursor.close();
            title = path.substring(path.lastIndexOf("/") + 1);
            UpdateVideo(path, title);
        }
    }

    private Popupdate puo;

    private void UpdateVideo(String path, String title) {
        Log.e("video", path + " : " +title);
        puo = new Popupdate();
        puo.getPopwindow(PersanalCollectionActivity.this, title);
        puo.setPoPDismissListener(new Popupdate.PopDismissListener() {
            @Override
            public void PopDismiss() {
                rl_update.setVisibility(View.GONE);

            }
        });
        RequestParams params = new RequestParams();
        params.setHeader("UserToken", AppConfig.UserToken);

        params.addBodyParameter("Content-Type", "video/mpeg4");// 设定传送的内容类型
        // params.setContentType("application/octet-stream");
        File file = new File(path);
        if (file.exists()) {
            rl_update.setVisibility(View.VISIBLE);
            params.addBodyParameter(title, file);
            String url = null;
            try {
                url = AppConfig.URL_PUBLIC + "EventAttachment/AddNewFavoriteMultipart?FileName="
                        + URLEncoder.encode(LoginGet.getBase64Password(title), "UTF-8")
                        + "&Guid=" + AppConfig.DEVICE_ID + System.currentTimeMillis()
                        +"&Total=1&Index=1";
                Log.e("hahaha", url + ":" + title);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Log.e("url", url);
            HttpUtils http = new HttpUtils();
            http.configResponseTextCharset("UTF-8");
            final HttpHandler hh = http.send(HttpRequest.HttpMethod.POST, url, params,
                    new RequestCallBack<String>() {
                        @Override
                        public void onStart() {
                            puo.StartPop(img_add);
                        }

                        @Override
                        public void onLoading(final long total, final long current,
                                              boolean isUploading) {
                            puo.setProgress(total, current);
                        }

                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            Log.e("hahaha", responseInfo + "");
                            Message message = new Message();
                            message.what = AppConfig.DELETESUCCESS;
                            handler.sendEmptyMessage(message.what);
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            Log.e("error", msg.toString());
                            Message message = new Message();
                            message.what = AppConfig.FAILED;
                            handler.sendEmptyMessage(message.what);
                        }
                    });
            puo.setPopCancelListener(new Popupdate.PopCancelListener() {
                @Override
                public void Cancel() {
                    /*if (hh != null) {
                        hh.cancel();
                    }*/
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.nofile),
                    Toast.LENGTH_LONG).show();
        }

    }
}
