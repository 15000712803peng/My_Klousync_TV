package com.kloudsync.techexcel2.start;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.config.AppConfig;
import com.kloudsync.techexcel2.dialog.ConversationActivity;
import com.kloudsync.techexcel2.dialog.Document;
import com.kloudsync.techexcel2.help.SideBarSortHelp;
import com.kloudsync.techexcel2.info.CommonUse;
import com.kloudsync.techexcel2.info.Customer;
import com.kloudsync.techexcel2.info.Favorite;
import com.kloudsync.techexcel2.info.GroupInfo;
import com.kloudsync.techexcel2.info.Sex;
import com.kloudsync.techexcel2.info.Uploadao;
import com.kloudsync.techexcel2.tool.NetWorkHelp;
import com.kloudsync.techexcel2.tool.PingYinUtil;
import com.kloudsync.techexcel2.ui.MainActivity;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class LoginGet {

    private static String URL_LOGIN;
    private static String URL_CHECKCODE;
    private static String URL_RONGTOKEN;
    private static String URL_CUSTOMER;
    private static String URL_MEMBER;
    private static String URL_CH;
    private static String URL_USEFULEX;
    private static String URL_CUSTOMERDETAIL;
    private static String URL_SEX;
    private static String URL_CHECKC_MOBILE;
    private static String URL_MEMBERDETAIL;
    private static String URL_FRIENDS;
    private static String URL_QUITGROUP;
    private static String URL_GROUPDETAIL;
    private static String URL_GETGROUPS;
    private static String URL_DISMISSGROUP;
    private static String URL_MYFAVOURTES;
    private static Context mContext;

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static ArrayList<Customer> cus_list = new ArrayList<Customer>();
    private static ArrayList<Customer> mb_list = new ArrayList<Customer>();
    private static ArrayList<Customer> fi_list = new ArrayList<Customer>();
    private static ArrayList<Customer> gm_list = new ArrayList<Customer>();

    private static ArrayList<CommonUse> com_list = new ArrayList<CommonUse>();
    private static ArrayList<Sex> sex_list = new ArrayList<Sex>();
    private static ArrayList<GroupInfo> gi_list = new ArrayList<GroupInfo>();

    private static Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case AppConfig.LOGIN:
                    String result = (String) msg.obj;
                    Log.e("LOGIN", result + "");
                    if (result != null) {
                        mJson(result);
                    } else {
                        Toast.makeText(
                                mContext,
                                mContext.getResources().getString(R.string.No_Data),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case AppConfig.CHECKCODE:
                    result = (String) msg.obj;
                    Log.e("CHECKCODE", "" + result);
                    if (result != null) {
                        mJsonCheckCode(result);
                    } else {
                        Toast.makeText(
                                mContext,
                                mContext.getResources().getString(R.string.No_Data),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case AppConfig.RONGUSERTOKEN:
                    result = (String) msg.obj;
                    Log.e("RONGUSERTOKEN", result + "");
                    if (result != null) {
                        mJsonRongToken(result);
                    } else {
                        Toast.makeText(
                                mContext,
                                mContext.getResources().getString(R.string.No_Data),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case AppConfig.GETCUSTOMER:
                    result = (String) msg.obj;
                    Log.e("GETCUSTOMER", result + "");
                    if (result != null) {
                        mJsonCustomer(result);
                    } else {
                        Toast.makeText(
                                mContext,
                                mContext.getResources().getString(R.string.No_Data),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case AppConfig.GETMEMBER:
                    result = (String) msg.obj;
                    Log.e("GETMEMBER", result + "");
                    if (result != null) {
                        mJsonMember(result);
                    } else {
                        Toast.makeText(
                                mContext,
                                mContext.getResources().getString(R.string.No_Data),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case AppConfig.CONCERNHIERARCHY:
                    result = (String) msg.obj;
                    Log.e("CONCERNHIERARCHY", result + "");
                    if (result != null) {
                        mJsonCH(result);
                    } else {
                        Toast.makeText(
                                mContext,
                                mContext.getResources().getString(R.string.No_Data),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case AppConfig.USEFULEXPRESSION:
                    result = (String) msg.obj;
                    Log.e("USEFULEXPRESSION", result + "");
                    if (result != null) {
                        mJsonUseFulE(result);
                    } else {
                        Toast.makeText(
                                mContext,
                                mContext.getResources().getString(R.string.No_Data),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case AppConfig.CUSTOMERDETAIL:
                    result = (String) msg.obj;
                    Log.e("CUSTOMERDETAIL", result + "");
                    if (result != null) {
                        mJsonCusDetail(result);
                    } else {
                        Toast.makeText(
                                mContext,
                                mContext.getResources().getString(R.string.No_Data),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case AppConfig.MEMBERDETAIL:
                    result = (String) msg.obj;
                    Log.e("MEMBERDETAIL", result + "");
                    if (result != null) {
                        mJsonMemDetail(result);
                    } else {
                        Toast.makeText(
                                mContext,
                                mContext.getResources().getString(R.string.No_Data),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case AppConfig.SEX:
                    result = (String) msg.obj;
                    Log.e("SEX", result + "");
                    if (result != null) {
                        mJsonSex(result);
                    } else {
                        Toast.makeText(
                                mContext,
                                mContext.getResources().getString(R.string.No_Data),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case AppConfig.CHECKC_MOBILE:
                    result = (String) msg.obj;
                    Log.e("CHECKC_MOBILE", result + "");
                    if (result != null) {
                        mJsonCCM(result);
                    } else {
                        Toast.makeText(
                                mContext,
                                mContext.getResources().getString(R.string.No_Data),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case AppConfig.FRIENDSINFO:
                    result = (String) msg.obj;
                    Log.e("FRIENDSINFO", result + "");
                    if (result != null) {
                        mJsonFI(result);
                    } else {
                        Toast.makeText(
                                mContext,
                                mContext.getResources().getString(R.string.No_Data),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case AppConfig.QUIT_GROUP:
                    result = (String) msg.obj;
                    Log.e("QUIT_GROUP", result + "");
                    if (result != null) {
                        mJsonQG(result);
                    } else {
                        Toast.makeText(
                                mContext,
                                mContext.getResources().getString(R.string.No_Data),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case AppConfig.GROUP_DETAIL:
                    result = (String) msg.obj;
                    Log.e("GROUP_DETAIL", result + "");
                    if (result != null) {
                        mJsonGD(result);
                    } else {
                        Toast.makeText(
                                mContext,
                                mContext.getResources().getString(R.string.No_Data),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case AppConfig.GETGROUPS:
                    result = (String) msg.obj;
                    Log.e("GETGROUPS", result + "");
                    if (result != null) {
                        mJsonGGS(result);
                    } else {
                        Toast.makeText(
                                mContext,
                                mContext.getResources().getString(R.string.No_Data),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case AppConfig.DISMISS_GROUP:
                    result = (String) msg.obj;
                    Log.e("DISMISS_GROUP", result + "");
                    if (result != null) {
                        mJsonDisG(result);
                    } else {
                        Toast.makeText(
                                mContext,
                                mContext.getResources().getString(R.string.No_Data),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case AppConfig.MY_FAVOURITES:
                    result = (String) msg.obj;
                    Log.e("MY_FAVOURITES", result + "");
                    if (result != null) {
                        mJsonMyFavor(result);
                    } else {
                        Toast.makeText(
                                mContext,
                                mContext.getResources().getString(R.string.No_Data),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case AppConfig.prepareUploading:
                    result = (String) msg.obj;
                    Log.e("prepareUploading", result + "");
                    if (result != null) {
                        mJsonPU(result);
                    } else {
                        Toast.makeText(
                                mContext,
                                mContext.getResources().getString(R.string.No_Data),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case AppConfig.NO_NETWORK:
                    Toast.makeText(
                            mContext,
                            mContext.getResources().getString(R.string.No_networking),
                            Toast.LENGTH_SHORT).show();
                    GoToLogin();
                    break;
                case AppConfig.NETERROR:
                    Toast.makeText(
                            mContext,
                            mContext.getResources().getString(R.string.NETWORK_ERROR),
                            Toast.LENGTH_SHORT).show();
                    GoToLogin();
                    break;

                default:
                    break;
            }


        }


    };

    private static void GoToLogin() {
        if (mContext != null && mContext.getClass().equals(StartUbao.class)) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            mContext.startActivity(intent);
            ((Activity) mContext).finish();
        }
    }

    private static LoginGetListener loggetlistener;

    public interface LoginGetListener {
        void getCustomer(ArrayList<Customer> list);

        void getMember(ArrayList<Customer> list);
    }

    public void setLoginGetListener(LoginGetListener loggetlistener) {
        LoginGet.loggetlistener = loggetlistener;
    }

    private static DialogGetListener dialoggetlistener;

    public interface DialogGetListener {
        void getCH(ArrayList<CommonUse> list);

        void getUseful(ArrayList<CommonUse> list);
    }

    public void setDialogGetListener(DialogGetListener dialoggetlistener) {
        LoginGet.dialoggetlistener = dialoggetlistener;
    }

    private static DetailGetListener detailgetlistener;

    public interface DetailGetListener {
        void getUser(Customer user);

        void getMember(Customer member);
    }

    public void setDetailGetListener(DetailGetListener detailgetlistener) {
        LoginGet.detailgetlistener = detailgetlistener;
    }

    private static UserGetListener usergetlistener;

    public interface UserGetListener {
        void getSex(ArrayList<Sex> sex_list);

        void getMobile(String retcode, String ErrorMessage);
    }

    public void setUserGetListener(UserGetListener usergetlistener) {
        LoginGet.usergetlistener = usergetlistener;
    }

    private static FriendGetListener friendgetlistener;

    public interface FriendGetListener {
        void getFriends(ArrayList<Customer> cus_list);
    }

    public void setFriendGetListener(FriendGetListener friendgetlistener) {
        LoginGet.friendgetlistener = friendgetlistener;
    }

    private static GroupGetListener groupgetlistener;

    public interface GroupGetListener {
        void getGroupDetail(GroupInfo gi);

        void getGDMember(ArrayList<Customer> list);
    }

    public void setGroupGetListener(GroupGetListener groupgetlistener) {
        LoginGet.groupgetlistener = groupgetlistener;
    }

    private static GroupChatsGetListener groupchatsgetlistener;

    public interface GroupChatsGetListener {
        void getGroups(ArrayList<GroupInfo> list);
    }

    public void setGroupsGetListener(GroupChatsGetListener groupchatsgetlistener) {
        LoginGet.groupchatsgetlistener = groupchatsgetlistener;
    }

    private static MyFavoritesGetListener myFavoritesGetListener;

    public interface MyFavoritesGetListener {
        void getFavorite(ArrayList<Document> list);
    }

    public void setMyFavoritesGetListener(MyFavoritesGetListener myFavoritesGetListener) {
        LoginGet.myFavoritesGetListener = myFavoritesGetListener;
    }

    private static prepareUploadingGetListener prepareUploadingGetListener;

    public interface prepareUploadingGetListener {
        void getUD(Uploadao ud);
    }

    public void setprepareUploadingGetListener(prepareUploadingGetListener prepareUploadingGetListener) {
        LoginGet.prepareUploadingGetListener = prepareUploadingGetListener;
    }


    /**
     * 登录信息获取
     *
     * @param context
     * @param name
     * @param passsword
     * @param role
     * @param s
     * @param e
     */
    public static void LoginRequest(Context context, String name,
                                    String passsword, int role, SharedPreferences s,
                                    SharedPreferences.Editor e) {
        mContext = context;
        sharedPreferences = s;
        editor = e;
        try {
            URL_LOGIN = AppConfig.URL_PUBLIC + "Login?login="
                    + URLEncoder.encode(name, "UTF-8") + "&password="
                    + URLEncoder.encode(getBase64Password(passsword), "UTF-8")
                    + "&role=" + role + "&deviceID=" + AppConfig.DEVICE_ID
                    + "&deviceType=1";
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Log.e("login", URL_LOGIN);

        newThreadGetResult(URL_LOGIN, AppConfig.LOGIN);
    }

    /**
     * 验证码获取
     *
     * @param context
     * @param mobile
     */
    public static void CheckCodeRequest(Context context, String mobile) {
        mContext = context;
        URL_CHECKCODE = AppConfig.URL_PUBLIC + "CheckCode/Send?mobile=" + mobile;
        newThreadGetResult(URL_CHECKCODE, AppConfig.CHECKCODE);
    }

    /**
     * 用户列表获取
     *
     * @param context
     */
    public void CustomerRequest(Context context) {
        mContext = context;
//		URL_CUSTOMER = AppConfig.URL_PUBLIC + "Friend/CustomerList";
        URL_CUSTOMER = AppConfig.URL_PUBLIC + "Friend/FriendList";
        newThreadGetResultBytoken(URL_CUSTOMER, AppConfig.GETCUSTOMER);
    }

    /**
     * 会员列表获取
     *
     * @param context
     */
    public void MemberRequest(Context context, int type) {
        mContext = context;
        URL_MEMBER = AppConfig.URL_PUBLIC + "Friend/MemberList?actionType=" + type;
        newThreadGetResultBytoken(URL_MEMBER, AppConfig.GETMEMBER);
    }

    /**
     * Get health concern and its categories
     *
     * @param context
     */
    public void ConcernHierarchyRequest(Context context) {
        mContext = context;
        URL_CH = AppConfig.URL_PUBLIC + "ConcernHierarchy";
        newThreadGetResultBytoken(URL_CH, AppConfig.CONCERNHIERARCHY);
    }

    /**
     * 常用语获取
     *
     * @param context
     */
    public void UsefulExpressionRequest(Context context, int largeId, int smallId, int focusId) {
        mContext = context;
        URL_USEFULEX = AppConfig.URL_PUBLIC + "UsefulExpression/List?cat="
                + (largeId < 0 ? 0 : largeId) + "&subcat="
                + (smallId < 0 ? 0 : smallId) + "&concern="
                + (focusId < 0 ? 0 : focusId);
        newThreadGetResultBytoken(URL_USEFULEX, AppConfig.USEFULEXPRESSION);
    }

    /**
     * 用户详情获取
     *
     * @param context
     */
    public void CustomerDetailRequest(Context context, String UserID) {
        mContext = context;
        URL_CUSTOMERDETAIL = AppConfig.URL_PUBLIC + "User/Customer?UserID=" + UserID;
        newThreadGetResultBytoken(URL_CUSTOMERDETAIL, AppConfig.CUSTOMERDETAIL);
    }

    /**
     * 会员详情获取
     *
     * @param context
     */
    public void MemberDetailRequest(Context context, String UserID) {
        mContext = context;
        URL_MEMBERDETAIL = AppConfig.URL_PUBLIC + "User/Member?UserID=" + UserID;
        newThreadGetResultBytoken(URL_MEMBERDETAIL, AppConfig.MEMBERDETAIL);
    }

    /**
     * 性别获取
     *
     * @param context
     */
    public void SexRequest(Context context) {
        mContext = context;
        URL_SEX = AppConfig.URL_PUBLIC + "User/Choices?ChoiceTypeID=1";
        newThreadGetResultBytoken(URL_SEX, AppConfig.SEX);
    }

    /**
     * 手机号验证
     *
     * @param context
     */
    public void MobileRequest(Context context, String mobile) {
        mContext = context;
        try {
            URL_CHECKC_MOBILE = AppConfig.URL_PUBLIC
                    + "User/Check4CreateCustomer?mobile="
                    + URLEncoder.encode(mobile, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        newThreadGetResultBytoken(URL_CHECKC_MOBILE, AppConfig.CHECKC_MOBILE);
    }

    /**
     * 好友获取
     *
     * @param context
     */
    public void FriendsRequest(Context context, String ids) {
        mContext = context;
        URL_FRIENDS = AppConfig.URL_PUBLIC + "Friend/ToApproveList?rongCloudIDs=" + ids;
        newThreadGetResultBytoken(URL_FRIENDS, AppConfig.FRIENDSINFO);
    }

    /**
     * 退出群聊
     *
     * @param context
     */
    public void QuitGroupRequest(Context context, String GroupID) {
        mContext = context;
        URL_QUITGROUP = AppConfig.URL_PUBLIC + "ChatGroup/QuitGroup?GroupID=" + GroupID;
        newThreadGetResultBytoken(URL_QUITGROUP, AppConfig.QUIT_GROUP);
    }

    /**
     * 获取群聊detail
     *
     * @param context
     */
    public void GroupDetailRequest(Context context, String GroupID) {
        mContext = context;
        URL_GROUPDETAIL = AppConfig.URL_PUBLIC + "ChatGroup/GetGroupDetail?GroupID=" + GroupID;
        newThreadGetResultBytoken(URL_GROUPDETAIL, AppConfig.GROUP_DETAIL);
    }

    /**
     * 获取所有自己的群组消息
     *
     * @param context
     */
    public void GetGroupsRequest(Context context) {
        mContext = context;
        URL_GETGROUPS = AppConfig.URL_PUBLIC + "ChatGroup/GetMyGroups";
        newThreadGetResultBytoken(URL_GETGROUPS, AppConfig.GETGROUPS);
    }

    /**
     * 解散群组
     *
     * @param context
     */
    public void DismissGroupRequest(Context context, String GroupID) {
        mContext = context;
        URL_DISMISSGROUP = AppConfig.URL_PUBLIC + "ChatGroup/DismissGroup?GroupID=" + GroupID;
        newThreadGetResultBytoken(URL_DISMISSGROUP, AppConfig.DISMISS_GROUP);
    }

    /**
     * 获取收藏
     * type 1普通  2 Video  0是全部
     *
     * @param context
     */
    public void MyFavoriteRequest(Context context, int type) {
        mContext = context;
        URL_MYFAVOURTES = AppConfig.URL_PUBLIC + "EventAttachment/MyFavoriteAttachments?type=" + type;
        URL_MYFAVOURTES = AppConfig.URL_PUBLIC + "FavoriteAttachment/MyFavoriteAttachments";
        newThreadGetResultBytoken(URL_MYFAVOURTES, AppConfig.MY_FAVOURITES);
    }

    public void MyFavoriteRequest(Context context, int type,String scanToken) {
        mContext = context;
//        URL_MYFAVOURTES = AppConfig.URL_PUBLIC + "EventAttachment/MyFavoriteAttachments?type=" + type;
//        URL_MYFAVOURTES = AppConfig.URL_PUBLIC + "FavoriteAttachment/MyFavoriteAttachments";
        URL_MYFAVOURTES = AppConfig.URL_PUBLIC + "FavoriteAttachment/MyFavoriteAttachmentsNew?type=" + type;
        newThreadGetResultBytoken(URL_MYFAVOURTES, AppConfig.MY_FAVOURITES,scanToken);
    }

    /**
     * @param context
     */
    public void GetprepareUploading(Context context) {
        mContext = context;
        String url = AppConfig.URL_LIVEDOC + "prepareUploading?clientIp=";
        newThreadLiveGetResult(url, AppConfig.prepareUploading);
    }

    @SuppressLint("NewApi")
    public static String getBase64Password(String passsword) {

        String enToStr = Base64.encodeToString(passsword.getBytes(), Base64.DEFAULT);
        return enToStr;

    }

    /**
     * 解密密码
     *
     * @param passsword
     * @return
     */
    public static String DecodeBase64Password(String passsword) {
        byte[] decodeBytes = Base64.decode(passsword, Base64.DEFAULT);
        return new String(decodeBytes);

    }

    public static synchronized void newThreadLiveGetResult(final String URL, final int config) {
        new Thread(new Runnable() {

            @Override
            public void run() {

                String result = null;
                Message msg = new Message();
                try {
                    java.net.URL url = new URL(URL);
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setReadTimeout(5 * 1000);
                    conn.setConnectTimeout(5 * 1000);
                    conn.addRequestProperty("Authorization", "Bearer " +AppConfig.liveToken);
                    conn.setRequestMethod("GET");
                    if (conn.getResponseCode() == 200) {
                        //先将服务器得到的流对象 包装 存入缓冲区，忽略了正在缓冲时间
                        InputStream in = new BufferedInputStream(conn.getInputStream());
                        // 得到servlet写入的头信息，response.setHeader("year", "2013");
//						String year = conn.getHeaderField("year");
//						System.out.println("year="+year);
//						byte[] bytes = readFromInput(in);	//封装的一个方法，通过指定的输入流得到其字节数据
                        result = NetWorkHelp.InputStreamTOString(in);
                        in.close();
                        conn.disconnect();
                    }
                    msg.obj = result;
                    msg.what = config;

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    msg.what = AppConfig.NETERROR;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    msg.what = AppConfig.NETERROR;
                } finally {
                    if (!NetWorkHelp.checkNetWorkStatus(mContext)) {
                        msg.what = AppConfig.NO_NETWORK;
                    }
                    handler.sendMessage(msg);
                }

            }
        }).start();
    }

    public static synchronized void newThreadGetResult(final String URL, final int config) {
        new Thread(new Runnable() {

            @Override
            public void run() {

                String result = null;
                Message msg = new Message();
                try {
                    java.net.URL url = new URL(URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5 * 1000);
                    conn.setConnectTimeout(5 * 1000);
                    conn.setRequestMethod("GET");
                    if (conn.getResponseCode() == 200) {
                        //先将服务器得到的流对象 包装 存入缓冲区，忽略了正在缓冲时间
                        InputStream in = new BufferedInputStream(conn.getInputStream());
                        // 得到servlet写入的头信息，response.setHeader("year", "2013");
//						String year = conn.getHeaderField("year");
//						System.out.println("year="+year);
//						byte[] bytes = readFromInput(in);	//封装的一个方法，通过指定的输入流得到其字节数据
                        result = NetWorkHelp.InputStreamTOString(in);
                        in.close();
                        conn.disconnect();
                    }
                    msg.obj = result;
                    msg.what = config;

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    msg.what = AppConfig.NETERROR;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    msg.what = AppConfig.NETERROR;
                } finally {
                    if (!NetWorkHelp.checkNetWorkStatus(mContext)) {
                        msg.what = AppConfig.NO_NETWORK;
                    }
                    handler.sendMessage(msg);
                }

            }
        }).start();
    }

    public static synchronized void newThreadGetResultBytoken(final String URL, final int config) {
        new Thread(new Runnable() {

            @Override
            public void run() {

                String result = null;
                Message msg = new Message();
                try {
                    java.net.URL url = new URL(URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5 * 1000);
                    conn.setConnectTimeout(5 * 1000);
                    conn.addRequestProperty("UserToken", AppConfig.UserToken);
                    conn.setRequestMethod("GET");
                    if (conn.getResponseCode() == 200) {
                        //先将服务器得到的流对象 包装 存入缓冲区，忽略了正在缓冲时间
                        InputStream in = new BufferedInputStream(conn.getInputStream());
                        // 得到servlet写入的头信息，response.setHeader("year", "2013");
//						String year = conn.getHeaderField("year");
//						System.out.println("year="+year);
//						byte[] bytes = readFromInput(in);	//封装的一个方法，通过指定的输入流得到其字节数据
                        result = NetWorkHelp.InputStreamTOString(in);
                        in.close();
                        conn.disconnect();
                    }
                    msg.obj = result;
                    msg.what = config;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    msg.what = AppConfig.NETERROR;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    msg.what = AppConfig.NETERROR;
                } finally {
                    if (!NetWorkHelp.checkNetWorkStatus(mContext)) {
                        msg.what = AppConfig.NO_NETWORK;
                    }
                    handler.sendMessage(msg);
                }

            }
        }).start();

    }

    public static synchronized void newThreadGetResultBytoken(final String URL, final int config,final String token) {
        new Thread(new Runnable() {

            @Override
            public void run() {

                String result = null;
                Message msg = new Message();
                try {
                    java.net.URL url = new URL(URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5 * 1000);
                    conn.setConnectTimeout(5 * 1000);
                    conn.addRequestProperty("UserToken", token);
                    conn.setRequestMethod("GET");
                    if (conn.getResponseCode() == 200) {
                        //先将服务器得到的流对象 包装 存入缓冲区，忽略了正在缓冲时间
                        InputStream in = new BufferedInputStream(conn.getInputStream());
                        // 得到servlet写入的头信息，response.setHeader("year", "2013");
//						String year = conn.getHeaderField("year");
//						System.out.println("year="+year);
//						byte[] bytes = readFromInput(in);	//封装的一个方法，通过指定的输入流得到其字节数据
                        result = NetWorkHelp.InputStreamTOString(in);
                        in.close();
                        conn.disconnect();
                    }
                    msg.obj = result;
                    msg.what = config;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    msg.what = AppConfig.NETERROR;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    msg.what = AppConfig.NETERROR;
                } finally {
                    if (!NetWorkHelp.checkNetWorkStatus(mContext)) {
                        msg.what = AppConfig.NO_NETWORK;
                    }
                    handler.sendMessage(msg);
                }

            }
        }).start();

    }


    /**
     * HttpGet超时设置
     */
    private static int TIME_OUT_DELAY = 5000;

    public static HttpClient initHttp() {
        HttpClient client = new DefaultHttpClient();
        client.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT,
                TIME_OUT_DELAY); // 超时设置
        client.getParams().setIntParameter(
                HttpConnectionParams.CONNECTION_TIMEOUT, TIME_OUT_DELAY);// 连接超时
        return client;
    }


    protected static void mJson(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            String RetCode = obj.getString("RetCode");
            String ErrorMessage = obj.getString("ErrorMessage");
            String DetailMessage = obj.getString("DetailMessage");
            if (RetCode.equals(AppConfig.RIGHT_RETCODE)) {
                JSONObject RetData = obj.getJSONObject("RetData");
                String UserToken = RetData.getString("UserToken");
                int UserID = RetData.getInt("UserID");
                String Name = RetData.getString("Name");
                String ExpirationDate = RetData.getString("ExpirationDate");
                String ClassRoomID = RetData.getString("ClassRoomID");
                String Mobile = RetData.getString("Mobile");
                int Role = RetData.getInt("Role");
                int SchoolID = RetData.getInt("SchoolID");

                Log.i("UserToken", UserToken);
                Log.i("UserID", UserID + "");
                Log.i("Name", Name);
                AppConfig.UserToken = UserToken;
                AppConfig.UserID = UserID + "";
                AppConfig.UserName = Name + "";
                AppConfig.SchoolID = SchoolID;
                AppConfig.Role = Role;
                AppConfig.UserExpirationDate = ExpirationDate;
                AppConfig.ClassRoomID = ClassRoomID;
                AppConfig.Mobile = Mobile;

                editor.putInt("UserID", UserID);
                editor.putBoolean("isLogIn", true);
//				editor.putString("UserToken", UserToken);
                editor.putString("Name", Name);
                editor.commit();
				
				/*Intent intent = new Intent(mContext, MainActivity.class);
				mContext.startActivity(intent);
				((Activity) mContext).finish();*/
                URL_RONGTOKEN = AppConfig.URL_PUBLIC + "RongCloudUserToken";
                newThreadGetResultBytoken(URL_RONGTOKEN, AppConfig.RONGUSERTOKEN);

            } else {
                Toast.makeText(mContext, ErrorMessage, 1000).show();
                GoToLogin();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    protected static void mJsonCheckCode(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            String RetCode = obj.getString("RetCode");
            if (RetCode.equals(AppConfig.RIGHT_RETCODE)) {
                String RetData = obj.getString("RetData");
//				loggetlistener.getCheckCode(RetData);
            } else {
                String ErrorMessage = obj.getString("ErrorMessage");
                String DetailMessage = obj.getString("DetailMessage");
                Toast.makeText(mContext, ErrorMessage, 1000).show();
//				loggetlistener.getCheckFalse();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected static void mJsonRongToken(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            String RetCode = obj.getString("RetCode");
            String ErrorMessage = obj.getString("ErrorMessage");
            String DetailMessage = obj.getString("DetailMessage");
            if (RetCode.equals(AppConfig.RIGHT_RETCODE)) {
                JSONObject RetData = obj.getJSONObject("RetData");
                String UserToken = RetData.getString("UserToken");
                String RongUserID = RetData.getString("RongCloudUserID");
                AppConfig.RongUserToken = UserToken;
                AppConfig.RongUserID = RongUserID;

                Intent intent = new Intent(mContext, MainActivity.class);
                mContext.startActivity(intent);
                ((Activity) mContext).finish();
                ((Activity) mContext).overridePendingTransition(R.anim.tran_in_null, R.anim.tran_out_null);


            } else {
                Toast.makeText(mContext, ErrorMessage, Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected static void mJsonCustomer(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            String RetCode = obj.getString("RetCode");
            cus_list = new ArrayList<Customer>();
            if (RetCode.equals(AppConfig.RIGHT_RETCODE)) {
                JSONArray RetDatas = obj.getJSONArray("RetData");
                for (int i = 0; i < RetDatas.length(); i++) {
                    JSONObject RetData = RetDatas.getJSONObject(i);
                    String UserID = RetData.getString("UserID");
                    String UBAOUserID = RetData.getString("RongCloudUserID");
                    String Name = RetData.getString("Name");
//					String Sex = RetData.getString("Sex");
//					String Age = RetData.getString("Age");
                    String AvatarUrl = RetData.getString("AvatarUrl");
//					AvatarUrl= AvatarUrl.replace("4443", "120").replace("https", "http");

                    String sortLetter = null;
                    if (Name == null
                            || (Name.length() > 0 && Name.substring(0, 1)
                            .equals(" ")) || Name.equals("")) {
                        sortLetter = "";
                    } else {
                        sortLetter = PingYinUtil.getPingYin(Name)
                                .substring(0, 1).toUpperCase();
                    }
                    sortLetter = SideBarSortHelp.getAlpha(sortLetter);
					/*String CurrentPosition = RetData.getString("CurrentPosition");
					if(null == CurrentPosition || CurrentPosition.equals("null")){
						CurrentPosition = "";
					}*/
//					String Symptom = RetData.getString("Symptom");
                    String Phone = RetData.getString("Phone");
//					String PersonalComment = RetData.getString("PersonalComment");
//					String SelfDescription = RetData.getString("SelfDescription");
//					int VIPLevel = RetData.getInt("VIPLevel");
//					int ServiceCount = RetData.getInt("ServiceCount");
//					boolean IsNewCustomer = RetData.getBoolean("IsNewCustomer");

					/*ArrayList<CommonUse> hclist = new ArrayList<CommonUse>();
					JSONArray HealthConcerns = RetData.getJSONArray("FocusPoints");
					for (int j = 0; j < HealthConcerns.length(); j++){
						String hName = HealthConcerns.getString(j);
						
						CommonUse cu = new CommonUse();
						cu.setName(hName);
						hclist.add(cu);
						
					}*/

					/*Customer cus = new Customer(UserID, UBAOUserID, Name, Sex,
							CurrentPosition, sortLetter, Age);*/
                    Customer cus = new Customer();
                    cus.setUserID(UserID);
                    cus.setUBAOUserID(UBAOUserID);
                    cus.setName(Name);
                    cus.setSortLetters(sortLetter);
					/*cus.setSymptom(Symptom);
					cus.setPhone(Phone);
					cus.setVIPLevel(VIPLevel);
					cus.setServiceCount(ServiceCount);
					cus.setNew(IsNewCustomer);
					cus.setSelfDescription(SelfDescription);
					cus.setPersonalComment(PersonalComment);
					cus.setHealthConcerns(hclist);*/
                    cus.setUrl(AvatarUrl);
                    cus.setRole(1);

                    cus_list.add(cus);
                }
                loggetlistener.getCustomer(cus_list);

            } else {
                String ErrorMessage = obj.getString("ErrorMessage");
                String DetailMessage = obj.getString("DetailMessage");
                Toast.makeText(mContext, ErrorMessage, Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected static void mJsonMember(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            String RetCode = obj.getString("RetCode");
            mb_list = new ArrayList<Customer>();
            if (RetCode.equals(AppConfig.RIGHT_RETCODE)) {
                JSONArray RetDatas = obj.getJSONArray("RetData");
                for (int i = 0; i < RetDatas.length(); i++) {
                    JSONObject RetData = RetDatas.getJSONObject(i);
                    String UserID = RetData.getString("UserID");
                    String UBAOUserID = RetData.getString("RongCloudUserID");
                    String Name = RetData.getString("Name");
                    String Sex = RetData.getString("Sex");
                    String Age = RetData.getString("Age");
                    int Type = RetData.getInt("Type");
                    String AvatarUrl = RetData.getString("AvatarUrl");
                    String sortLetter = null;
                    if (Name == null
                            || (Name.length() > 0 && Name.substring(0, 1)
                            .equals(" ")) || Name.equals("")) {
                        sortLetter = "";
                    } else {
                        sortLetter = PingYinUtil.getPingYin(Name)
                                .substring(0, 1).toUpperCase();
                    }
                    sortLetter = SideBarSortHelp.getAlpha(sortLetter);
                    String CurrentPosition = RetData.getString("CurrentPosition");
                    if (null == CurrentPosition || CurrentPosition.equals("null")) {
                        CurrentPosition = "";
                    }
                    String Summary = RetData.getString("SkilledFields");
                    String Title = RetData.getString("Title");
                    int StarLevel = RetData.getInt("StarLevel");
                    Customer cus = new Customer(UserID, UBAOUserID, Name, Sex,
                            CurrentPosition, sortLetter, Age);
                    cus.setVIPLevel(StarLevel);
                    cus.setSummary(Summary);
                    cus.setTitle(Title);
                    cus.setUrl(AvatarUrl);
                    cus.setRole(2);
                    cus.setType(Type);

                    mb_list.add(cus);
                }

            } else {
                String ErrorMessage = obj.getString("ErrorMessage");
                String DetailMessage = obj.getString("DetailMessage");
                Toast.makeText(mContext, ErrorMessage, 1000).show();
            }
            loggetlistener.getMember(mb_list);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected static void mJsonCH(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            String RetCode = obj.getString("RetCode");
            if (RetCode.equals(AppConfig.RIGHT_RETCODE)) {
                com_list = new ArrayList<CommonUse>();
                JSONArray RetDatas = obj.getJSONArray("RetData");
                for (int i = 0; i < RetDatas.length(); i++) {
                    JSONObject RetData = RetDatas.getJSONObject(i);
                    String Name = RetData.getString("Name");
                    int ID = RetData.getInt("ID");
                    int NodeType = RetData.getInt("NodeType");
                    CommonUse cu = new CommonUse(NodeType, ID, Name);
                    if (2 != NodeType) {
                        JSONArray cs = RetData.getJSONArray("ChildSelections");

                        int ChildSelections[] = new int[cs.length()];
                        for (int j = 0; j < cs.length(); j++) {
                            int ChildSelection = cs.getInt(j);
                            ChildSelections[j] = ChildSelection;
                        }
                        cu.setChildSelections(ChildSelections);
                    }

                    com_list.add(cu);
                }
                dialoggetlistener.getCH(com_list);

            } else {
                String ErrorMessage = obj.getString("ErrorMessage");
                String DetailMessage = obj.getString("DetailMessage");
                Toast.makeText(mContext, ErrorMessage, 1000).show();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    protected static void mJsonUseFulE(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            String RetCode = obj.getString("RetCode");
            if (RetCode.equals(AppConfig.RIGHT_RETCODE)) {
                com_list = new ArrayList<CommonUse>();
                JSONArray RetData = obj.getJSONArray("RetData");
                for (int i = 0; i < RetData.length(); i++) {
                    String Name = RetData.getString(i);
                    CommonUse cu = new CommonUse();
                    cu.setName(Name);
                    cu.setID(i);

                    com_list.add(cu);
                }
                dialoggetlistener.getUseful(com_list);

            } else {
                String ErrorMessage = obj.getString("ErrorMessage");
                String DetailMessage = obj.getString("DetailMessage");
                Toast.makeText(mContext, ErrorMessage, 1000).show();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected static void mJsonCusDetail(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            String RetCode = obj.getString("RetCode");
            if (RetCode.equals(AppConfig.RIGHT_RETCODE)) {
                JSONObject RetData = obj.getJSONObject("RetData");
                String UserID = RetData.getString("UserID");
                String RongCloudUserID = RetData.getString("RongCloudUserID");
                String Name = RetData.getString("Name");
                String Sex = RetData.getString("Sex");
                String Birthday = RetData.getString("Birthday");
                String Age = RetData.getString("Age");
                String Phone = RetData.getString("Phone");
                String Address = RetData.getString("Address");
                String AvatarUrl = RetData.getString("AvatarUrl");
                String Height = RetData.getString("Height");
                String Weight = RetData.getString("Weight");
//				AvatarUrl= AvatarUrl.replace("4443", "120").replace("https", "http");
                String PersonalComment = RetData.getString("PersonalComment");
                String SelfDescription = RetData.getString("SelfDescription");


                Customer cus = new Customer(UserID, RongCloudUserID, Name, Sex, null, "A", Age);
                cus.setBirthday(Birthday);
                cus.setPhone(Phone);
                cus.setAddress(Address);
                cus.setUrl(AvatarUrl);
                cus.setPersonalComment(PersonalComment);
                cus.setSelfDescription(SelfDescription);
                cus.setHeight(Height);
                cus.setWeight(Weight);

                int UBAOPersonID = RetData.getInt("UBAOPersonID");
                if (UBAOPersonID > 0) {
                    String UBAOPersonName = RetData.getString("UBAOPersonName");
                    cus.setUBAOPersonName(UBAOPersonName);
                    cus.setUBAOPersonID(UBAOPersonID);
                }
                ArrayList<CommonUse> hclist = new ArrayList<CommonUse>();

                JSONArray HealthConcerns = RetData.getJSONArray("HealthConcerns");
                for (int i = 0; i < HealthConcerns.length(); i++) {
                    JSONObject HealthConcern = HealthConcerns.getJSONObject(i);
                    String hName = HealthConcern.getString("Name");
                    int hID = HealthConcern.getInt("ID");

                    CommonUse cu = new CommonUse();
                    cu.setID(hID);
                    cu.setName(hName);
                    hclist.add(cu);

                }
                cus.setHealthConcerns(hclist);

                detailgetlistener.getUser(cus);

            } else {
                detailgetlistener.getUser(new Customer());
                String ErrorMessage = obj.getString("ErrorMessage");
                String DetailMessage = obj.getString("DetailMessage");
                Toast.makeText(mContext, ErrorMessage, 1000).show();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected static void mJsonMemDetail(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            String RetCode = obj.getString("RetCode");
            if (RetCode.equals(AppConfig.RIGHT_RETCODE)) {
                JSONObject RetData = obj.getJSONObject("RetData");
                String UserID = RetData.getString("UserID");
                String RongCloudUserID = RetData.getString("RongCloudUserID");
                String Name = RetData.getString("Name");
                String Title = RetData.getString("Title");
                String Sex = RetData.getString("Sex");
                String Mobile = RetData.getString("Mobile");
                String Birthday = RetData.getString("Birthday");
                String State = RetData.getString("State");
                String City = RetData.getString("City");
                String Address = RetData.getString("Address");
                String SkilledFields = RetData.getString("SkilledFields");
                String Summary = RetData.getString("Summary");
                String AvatarUrl = RetData.getString("AvatarUrl");
                String ExpirationDate = RetData.getString("ExpirationDate");
                int Type = RetData.getInt("Type");
                int ArticleCount = RetData.getInt("ArticleCount");

                Customer cus = new Customer(UserID, RongCloudUserID, Name, Sex, null, "A", "");
                cus.setBirthday(Birthday);
                cus.setPhone(Mobile);
                cus.setAddress(Address);
                cus.setTitle(Title);
                cus.setState(State);
                cus.setCity(City);
                cus.setSummary(Summary);
                cus.setUrl(AvatarUrl);
                cus.setType(Type);
                cus.setExpirationDate(ExpirationDate);
                cus.setSkilledFields(SkilledFields);
                cus.setArticleCount(ArticleCount);

                detailgetlistener.getMember(cus);

            } else {
                detailgetlistener.getMember(new Customer());
                String ErrorMessage = obj.getString("ErrorMessage");
                String DetailMessage = obj.getString("DetailMessage");
                Toast.makeText(mContext, ErrorMessage, Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected static void mJsonSex(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            String RetCode = obj.getString("RetCode");
            if (RetCode.equals(AppConfig.RIGHT_RETCODE)) {
                sex_list = new ArrayList<Sex>();
                JSONArray RetDatas = obj.getJSONArray("RetData");
                for (int i = 0; i < RetDatas.length(); i++) {
                    JSONObject RetData = RetDatas.getJSONObject(i);
                    String Name = RetData.getString("Name");
                    String ID = RetData.getString("ID");

                    Sex sex = new Sex(ID, Name);

                    sex_list.add(sex);
                }
            }
            usergetlistener.getSex(sex_list);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected static void mJsonCCM(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            String RetCode = obj.getString("RetCode");
            String ErrorMessage = "";
            if (!RetCode.equals(AppConfig.RIGHT_RETCODE)) {
                ErrorMessage = obj.getString("ErrorMessage");
            }
            usergetlistener.getMobile(RetCode, ErrorMessage);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected static void mJsonFI(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            String RetCode = obj.getString("RetCode");
            if (RetCode.equals(AppConfig.RIGHT_RETCODE)) {
                fi_list = new ArrayList<Customer>();
                JSONArray RetDatas = obj.getJSONArray("RetData");
                for (int i = 0; i < RetDatas.length(); i++) {
                    JSONObject RetData = RetDatas.getJSONObject(i);
                    String Name = RetData.getString("Name");
                    String UserID = RetData.getString("UserID");
                    String RongCloudUserID = RetData.getString("RongCloudUserID");
                    String Phone = RetData.getString("Phone");
                    String AvatarUrl = RetData.getString("AvatarUrl");

                    Customer cus = new Customer();
                    cus.setName(Name);
                    cus.setPhone(Phone);
                    cus.setUserID(UserID);
                    cus.setUBAOUserID(RongCloudUserID);
                    cus.setUrl(AvatarUrl);

                    fi_list.add(cus);
                }
                friendgetlistener.getFriends(fi_list);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected static void mJsonQG(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            String RetCode = obj.getString("RetCode");
            if (RetCode.equals(AppConfig.RIGHT_RETCODE)) {
                AppConfig.isDeleteGroup = true;
                ConversationActivity.instance.finish();
                ((Activity) mContext).finish();
            } else {
                String ErrorMessage = obj.getString("ErrorMessage");
                Toast.makeText(mContext, ErrorMessage, Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected static void mJsonGD(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            String RetCode = obj.getString("RetCode");
            gm_list = new ArrayList<Customer>();
            GroupInfo gi = new GroupInfo();
            if (RetCode.equals(AppConfig.RIGHT_RETCODE)) {
                JSONObject RetData = obj.getJSONObject("RetData");
                String GroupID = RetData.getString("GroupID");
                String GroupName = RetData.getString("GroupName");
                String GroupAdminID = RetData.getString("GroupAdminID");
//				String GroupTempName = RetData.getString("GroupTempName");
                gi = new GroupInfo(GroupID, GroupName, GroupAdminID);
//				gi.setGroupTempName(GroupTempName);
                JSONArray GroupMembers = RetData.getJSONArray("GroupMembers");
                for (int i = 0; i < GroupMembers.length(); i++) {
                    JSONObject GroupMember = GroupMembers.getJSONObject(i);
                    String RongCloudUserID = GroupMember.getString("RongCloudUserID");
                    int Role = GroupMember.getInt("Role");
                    String UserID = GroupMember.getString("UserID");
                    String Name = GroupMember.getString("Name");
                    String AvatarUrl = GroupMember.getString("AvatarUrl");


                    Customer cus = new Customer();
                    cus.setName(Name);
                    cus.setRole(Role);
                    cus.setUserID(UserID);
                    cus.setUBAOUserID(RongCloudUserID);
                    cus.setUrl(AvatarUrl);

                    gm_list.add(cus);
                }
            } else {
                String ErrorMessage = obj.getString("ErrorMessage");
                Toast.makeText(mContext, ErrorMessage, Toast.LENGTH_LONG).show();
            }
            groupgetlistener.getGDMember(gm_list);
            groupgetlistener.getGroupDetail(gi);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected static void mJsonGGS(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            String RetCode = obj.getString("RetCode");
            gi_list = new ArrayList<GroupInfo>();
            if (RetCode.equals(AppConfig.RIGHT_RETCODE)) {
                JSONArray RetDatas = obj.getJSONArray("RetData");
                for (int i = 0; i < RetDatas.length(); i++) {
                    JSONObject RetData = RetDatas.getJSONObject(i);
                    String GroupID = RetData.getString("GroupID");
                    String GroupName = RetData.getString("GroupName");
                    String GroupAdminID = RetData.getString("GroupAdminID");
                    String GroupTempName = RetData.getString("GroupTempName");
                    GroupInfo gi = new GroupInfo(GroupID, GroupName, GroupAdminID);
                    gi.setGroupTempName(GroupTempName);

                    gi_list.add(gi);
                }
            } else {
                String ErrorMessage = obj.getString("ErrorMessage");
                Toast.makeText(mContext, ErrorMessage, Toast.LENGTH_LONG).show();
            }
            groupchatsgetlistener.getGroups(gi_list);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected static void mJsonDisG(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            String RetCode = obj.getString("RetCode");
            if (RetCode.equals(AppConfig.RIGHT_RETCODE)) {
                AppConfig.isDeleteGroup = true;
                ConversationActivity.instance.finish();
                ((Activity) mContext).finish();
            } else {
                String ErrorMessage = obj.getString("ErrorMessage");
                Toast.makeText(mContext, ErrorMessage, Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected static void mJsonMyFavor(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            String RetCode = obj.getString("RetCode");
            ArrayList<Document> mf_list = new ArrayList<Document>();
            if (RetCode.equals(AppConfig.RIGHT_RETCODE)) {
                JSONObject RetDatax = obj.getJSONObject("RetData");
                JSONArray RetDatas = RetDatax.getJSONArray("List");
                for (int i = 0; i < RetDatas.length(); i++) {
                    JSONObject RetData = RetDatas.getJSONObject(i);
                    String Title = RetData.getString("Title");
                    String Description = RetData.getString("Description");
                    String FileName = RetData.getString("FileName");
                    String FileDownloadURL = RetData.getString("AttachmentUrl");
                    String CreatedDate = RetData.getString("CreatedDate");
                    String VideoSize = RetData.getString("VideoSize");
                    String VideoDuration = RetData.getString("VideoDuration");
                    String SourceFileUrl = RetData.getString("SourceFileUrl");
                    String AttachmentUrl = RetData.getString("AttachmentUrl");
//                    int ProjectID = RetData.getInt("ProjectID");
//                    int LinkedKWProjectID = RetData.getInt("LinkedKWProjectID");
                    int AttachmentID = RetData.getInt("AttachmentID");
//                    int IncidentID = RetData.getInt("IncidentID");
                    int FileID = RetData.getInt("FileID");
//                    int AttachmentTypeID = RetData.getInt("AttachmentTypeID");
                    int Status = RetData.getInt("Status");
                    int ItemID = RetData.getInt("ItemID");
                    int SyncCount = RetData.getInt("SyncCount");
                    int FileType = RetData.getInt("FileType");

                    Document mf = new Document();
                    mf.setAttachmentID(AttachmentID + "");
//                    mf.setAttachmentTypeID(AttachmentTypeID);
                    mf.setCreatedDate(CreatedDate);
                    mf.setDescription(Description);
                    mf.setFileDownloadURL(FileDownloadURL);
                    mf.setFileID(FileID + "");
                    mf.setFileName(FileName);
                    mf.setDuration(VideoDuration);
                    mf.setSize(VideoSize);
                    mf.setSourceFileUrl(SourceFileUrl);
                    mf.setAttachmentUrl(AttachmentUrl);
//                    mf.setIncidentID(IncidentID);
//                    mf.setLinkedKWProjectID(LinkedKWProjectID);
//                    mf.setProjectID(ProjectID);
                    mf.setStatus(Status);
                    mf.setTitle(Title);
                    mf.setItemID(ItemID + "");
                    mf.setSyncCount(SyncCount);
                    mf.setJsonObject(RetData);
                    mf.setFileType(FileType);

                    mf_list.add(mf);
                }
            } else {
                String ErrorMessage = obj.getString("ErrorMessage");
                Toast.makeText(mContext, ErrorMessage, Toast.LENGTH_LONG).show();
            }
            myFavoritesGetListener.getFavorite(mf_list);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private static void mJsonPU(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            Boolean Success = obj.getBoolean("Success");
            if (Success) {
                JSONObject Data = obj.getJSONObject("Data");
                int ServiceProviderId = Data.getInt("ServiceProviderId");
                String RegionName = Data.getString("RegionName");
                String BucketName = Data.getString("BucketName");
                String AccessKeyId = Data.getString("AccessKeyId");
                String AccessKeySecret = Data.getString("AccessKeySecret");
                String SecurityToken = Data.getString("SecurityToken");

                Uploadao ud = new Uploadao();
                ud.setAccessKeyId(AccessKeyId);
                ud.setRegionName(RegionName);
                ud.setAccessKeySecret(AccessKeySecret);
                ud.setBucketName(BucketName);
                ud.setServiceProviderId(ServiceProviderId);
                ud.setSecurityToken(SecurityToken);
                ud.setData(Data.toString());

                prepareUploadingGetListener.getUD(ud);

            } else {
                /*String ErrorMessage = obj.getString("ErrorMessage");
                String DetailMessage = obj.getString("DetailMessage");
                Toast.makeText(mContext, ErrorMessage, Toast.LENGTH_SHORT).show();*/
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
        }
    }

}
