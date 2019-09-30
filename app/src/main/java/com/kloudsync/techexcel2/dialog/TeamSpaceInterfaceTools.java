package com.kloudsync.techexcel2.dialog;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.kloudsync.techexcel2.help.ApiTask;
import com.kloudsync.techexcel2.pc.bean.TeamSpaceBean;
import com.ub.service.activity.ThreadManager;
import com.ub.techexcel.bean.SyncRoomMember;
import com.ub.techexcel.service.ConnectService;

import org.greenrobot.eventbus.EventBus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TeamSpaceInterfaceTools {

    public static final int ERRORMESSAGE = 0x1205;
    public static final int CREATETEAMSPACE = 0x1101;
    public static final int GETTEAMSPACELIST = 0x1102;
    public static final int GETTEAMITEM = 0x1103;
    public static final int GETSPACEDOCUMENTLIST = 0x1104;
    public static final int GETALLDOCUMENTLIST = 0x1105;
    public static final int UPLOADFROMSPACE = 0x1106;
    public static final int GETSYNCROOMLIST = 0x1107;
    public static final int TOPICLIST = 0x1108;
    public static final int CREATESYNCROOM = 0x1109;
    public static final int SWITCHSPACE = 0x1110;
    public static final int GETMEMBERLIST = 0x1111;
    public static final int UPDATETEAMTOPIC = 0x1112;
    public static final int GETAUDIENCELIST = 0x1113;
    public static final int GET_SPACE_LIST_IN_HOME_PAGE = 0x1120;

    private ConcurrentHashMap<Integer, TeamSpaceInterfaceListener> hashMap = new ConcurrentHashMap<>();

    private static TeamSpaceInterfaceTools serviceInterfaceTools;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int code = msg.what;
            if (code != ERRORMESSAGE) {
                TeamSpaceInterfaceListener serviceInterfaceListener = hashMap.get(code);
                if (serviceInterfaceListener != null) {
                    serviceInterfaceListener.getServiceReturnData(msg.obj);
                    hashMap.remove(code);
                }
            } else {
                String message = "";
                try {
                    message = (String) msg.obj;
                } catch (Exception e) {
                    message = "";
                }

                EventBus.getDefault().post(message);

            }
        }
    };


    public static TeamSpaceInterfaceTools getinstance() {
        if (serviceInterfaceTools == null) {
            synchronized (TeamSpaceInterfaceTools.class) {
                if (serviceInterfaceTools == null) {
                    serviceInterfaceTools = new TeamSpaceInterfaceTools();
                }
            }
        }
        return serviceInterfaceTools;
    }

    public void getMemberList(final String url, final int code, TeamSpaceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentData(url);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        JSONArray jsonArray = returnjson.getJSONArray("RetData");
                        List<SyncRoomMember> list = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            SyncRoomMember syncRoomBean = new SyncRoomMember();
                            syncRoomBean.setSyncroomId(jsonObject.getInt("SyncRoomID"));
                            syncRoomBean.setMemberID(jsonObject.getInt("MemberID"));
                            syncRoomBean.setMemberName(jsonObject.getString("MemberName"));
                            syncRoomBean.setMemberAvatar(jsonObject.getString("MemberAvatar"));
                            syncRoomBean.setJoinDate(jsonObject.getString("JoinDate"));
                            syncRoomBean.setMemberType(jsonObject.getInt("MemberType"));
                            list.add(syncRoomBean);
                        }
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = list;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());

    }

    public void getTeamSpaceList(final String url, final int code, TeamSpaceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentData(url);
                    Log.e("getTeamSpaceList", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        List<TeamSpaceBean> list = new ArrayList<>();
                        JSONArray jsonArray = returnjson.getJSONArray("RetData");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            TeamSpaceBean teamSpaceBean = new TeamSpaceBean();
                            teamSpaceBean.setItemID(jsonObject.getInt("ItemID"));
                            teamSpaceBean.setName(jsonObject.getString("Name"));
                            teamSpaceBean.setCompanyID(jsonObject.getInt("CompanyID"));
                            teamSpaceBean.setType(jsonObject.getInt("Type"));
                            teamSpaceBean.setParentID(jsonObject.getInt("ParentID"));
                            teamSpaceBean.setCreatedDate(jsonObject.getString("CreatedDate"));
                            teamSpaceBean.setCreatedByName(jsonObject.getString("CreatedByName"));
                            teamSpaceBean.setAttachmentCount(jsonObject.getInt("AttachmentCount"));
                            teamSpaceBean.setMemberCount(jsonObject.getInt("MemberCount"));
                            teamSpaceBean.setSyncRoomCount(jsonObject.getInt("SyncRoomCount"));

                            list.add(teamSpaceBean);
                        }
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = list;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    public void getSpaceDocumentList(final String url, final int code, TeamSpaceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentData(url);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {

                        JSONObject ss = returnjson.getJSONObject("RetData");
                        JSONArray jsonArray = ss.getJSONArray("DocumentList");

                        List<Document> list = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Document teamSpaceBeanFile = new Document();
                            teamSpaceBeanFile.setSpaceID(jsonObject.getInt("SpaceID") + "");
                            teamSpaceBeanFile.setItemID(jsonObject.getInt("ItemID") + "");
                            teamSpaceBeanFile.setAttachmentID(jsonObject.getInt("AttachmentID") + "");
                            teamSpaceBeanFile.setCreatedDate(jsonObject.getString("CreatedDate"));
                            teamSpaceBeanFile.setFileType(jsonObject.getInt("FileType"));
                            teamSpaceBeanFile.setSourceFileUrl(jsonObject.getString("SourceFileUrl"));
                            teamSpaceBeanFile.setSyncCount(jsonObject.getInt("SyncCount"));
                            teamSpaceBeanFile.setTitle(jsonObject.getString("Title"));
                            teamSpaceBeanFile.setFileName(jsonObject.getString("FileName"));
                            teamSpaceBeanFile.setAttachmentUrl(jsonObject.getString("AttachmentUrl"));
                            list.add(teamSpaceBeanFile);
                        }
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = list;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }





    /**
     * 创建Team或者Space
     */
    public void uploadFromSpace(final String url, final int code, TeamSpaceInterfaceListener teamSpaceInterfaceListener) {
        putInterface(code, teamSpaceInterfaceListener);
        new  Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.submitDataByJson(url, null);
                    Log.e("hhh", url + "  " + "     " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = "";
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    private void putInterface(int code, TeamSpaceInterfaceListener serviceInterfaceListener) {
        if (hashMap.get(code) != null) {
            hashMap.remove(code);
        }
        hashMap.put(code, serviceInterfaceListener);
    }


}
