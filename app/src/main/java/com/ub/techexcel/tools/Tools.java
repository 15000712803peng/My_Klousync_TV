package com.ub.techexcel.tools;

import android.app.AppOpsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;

import com.kloudsync.techexcel2.dialog.message.GroupMessage;
import com.kloudsync.techexcel2.info.Customer;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;

public class Tools {

    /**
     * dip转为 px
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px 转为 dip
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 判断悬浮窗权限是否打开
     *
     * @param context
     * @return
     */
    public boolean checkAlertWindowPermission(Context context) {
        try {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject = new Object[3];
            arrayOfObject[0] = 24;
            arrayOfObject[1] = Binder.getCallingUid();
            arrayOfObject[2] = context.getPackageName();
            int m = (Integer) method.invoke(object, arrayOfObject);
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    //base64编码
    public static String getBase64(String str) {
        byte[] b = null;
        String s = null;
        try {
            b = str.getBytes("iso-8859-1");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        if (b != null) {
            s = new BASE64Encoder().encode(b);
        }
        return s;
    }


    // base64解码
    public static String getFromBase64(String s) {
        byte[] b = null;
        String result = null;
        if (s != null) {
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                b = decoder.decodeBuffer(s);
                result = new String(b, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }


    public static List<Customer> getUserListByJoinMeeting(JSONArray userlist) {
        List<Customer> customerList = new ArrayList<>();
        try {
            for(int i=0;i<userlist.length();i++){
                JSONObject jsonObject=userlist.getJSONObject(i);
                Customer customer = new Customer();
                customer.setUserID(jsonObject.getString("userId"));
                customer.setName(jsonObject.getString("userName"));
                customer.setUBAOUserID(jsonObject.getString("rongCloudId"));
                customer.setUrl(jsonObject.getString("avatarUrl"));
                customer.setUsertoken(jsonObject.getString("sessionId"));
                customer.setRole(Integer.parseInt(jsonObject.getString("role")));
                customer.setPresenter(jsonObject.getString("presenter").equals("1") ? true : false);
                customer.setOnline(jsonObject.getString("isOnline").equals("1") ? true : false);
                customerList.add(customer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return customerList;
    }

    /**
     * 判断是否有网络连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }


    public static void sendGroupMsg(String firstmsg, String mGroupId) {
        GroupMessage mGroupMessage = new GroupMessage(firstmsg);
        io.rong.imlib.model.Message msg = io.rong.imlib.model.Message.obtain(mGroupId, Conversation.ConversationType.GROUP, mGroupMessage);
        RongIM.getInstance().sendMessage(msg, null, null, new IRongCallback.ISendMessageCallback() {

            @Override
            public void onAttached(io.rong.imlib.model.Message message) {

            }

            @Override
            public void onSuccess(io.rong.imlib.model.Message message) {
                Log.e("lalala", "sendMessage onError");
            }

            @Override
            public void onError(io.rong.imlib.model.Message message, RongIMClient.ErrorCode errorCode) {
                Log.e("lalala", "sendMessage onError");
            }
        });

    }

    public static void openGroup(Context context, String mGroupId) {
        RongIM.getInstance().startGroupChat(context, mGroupId, "");
    }



    public static void sendMessage(final Context context, final MessageContent msg, String mGroupId, String userid) {
        io.rong.imlib.model.Message myMessage = io.rong.imlib.model.Message.obtain(mGroupId, Conversation.ConversationType.GROUP, msg);
        myMessage.setExtra(userid);
        RongIM.getInstance()
                .sendMessage(myMessage, null, null, new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(io.rong.imlib.model.Message message) {

                    }

                    @Override
                    public void onSuccess(io.rong.imlib.model.Message message) {
                        EventBus.getDefault().post(message);
                    }

                    @Override
                    public void onError(io.rong.imlib.model.Message message, RongIMClient.ErrorCode errorCode) {

                    }
                });
    }


    /**
     *
     * @param groupId
     */
    public static void removeGroupMeaage(String groupId) {
        RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, groupId, new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                Log.e("clearMessages", "onSuccess");

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.e("clearMessages", "onError");

            }
        });
    }

    /**
     * 得到资源文件中图片的Uri
     * @param context 上下文对象
     * @param id 资源id
     * @return Uri
     */
    public static Uri getUriFromDrawableRes(Context context, int id) {
        Resources resources = context.getResources();
        String path = ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + resources.getResourcePackageName(id) + "/"
                + resources.getResourceTypeName(id) + "/"
                + resources.getResourceEntryName(id);
        return Uri.parse(path);
    }


    //文件拷贝 copyFileSdCard
    //要复制的目录下的所有非子目录(文件夹)文件拷贝
    public synchronized  static int copyFileSdCard(String fromFile, String toFile) {
        try {
            int byteread = 0;
            File oldfile = new File(fromFile);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(fromFile); //读入原文件
                FileOutputStream fs = new FileOutputStream(toFile);
                byte[] buffer = new byte[1024];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return -1;
    }


}
