package com.ub.techexcel.tools;

import android.text.TextUtils;
import android.util.Log;

import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.service.ConnectService;
import com.kloudsync.techexcel2.config.AppConfig;
import com.kloudsync.techexcel2.info.Customer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/8/3.
 */

public class ServiceTool implements Runnable {

    private List<ServiceBean> mList = new ArrayList<>();
    private int isPublish, roleId;

    private ArrayList<Customer> custometList = new ArrayList<Customer>();

    public ServiceTool(int i, List<ServiceBean> mList,  ArrayList<Customer> custometList) {
        this.mList = mList;

        this.custometList = custometList;
        switch (i) {
            case 0:
                isPublish = 1;
                roleId = 1;
                break;
            case 1:
                isPublish = 1;
                roleId = 2;
                break;
            case 2:
                isPublish = 1;
                roleId = 1;
                break;
            case 3:
                isPublish = 1;
                roleId = 2;
                break;
            case 4:  // 全部课程
                isPublish = 0;
                roleId = 2;
                break;
        }
    }


    @Override
    public void run() {
        JSONObject returnJson = ConnectService
                .getIncidentbyHttpGet(AppConfig.URL_PUBLIC
                        + "Lesson/List?PageIndex=0&PageSize=20&isPublish=" + isPublish + "&roleID=" + roleId);
        Log.e("--------------uuuuuuuuu", returnJson.toString() + "");
        formatServiceData(returnJson);


    }

    private void formatServiceData(JSONObject returnJson) {
        try {
            int retCode = returnJson.getInt("RetCode");
            switch (retCode) {
                case AppConfig.RETCODE_SUCCESS:
                    JSONArray retdata = returnJson.getJSONArray("RetData");
                    for (int i = 0; i < retdata.length(); i++) {
                        JSONObject service = retdata.getJSONObject(i);
                        ServiceBean bean = new ServiceBean();

                        String name = service.getString("Name");
                        bean.setName((name == null || name.equals("null")) ? ""
                                : name);
                        int concernid = service.getInt("ConcernID");
                        bean.setConcernID(concernid);

                        bean.setCategoryID(service.getInt("CategoryID"));
                        bean.setSubCategoryID(service.getInt("SubCategoryID"));
                        bean.setCustomerRongCloudId(service.getString("CustomerRongCloudID"));
                        int statusID = service.getInt("StatusID");
                        bean.setStatusID(statusID);
                        bean.setId(service.getInt("ID"));
                        bean.setIfClose(service.getInt("IfClosed"));
                        bean.setRoleinlesson(service.getInt("RoleInLesson"));

                        bean.setPlanedEndDate(service.getString("PlanedEndDate"));
                        bean.setPlanedStartDate(service.getString("PlanedStartDate"));
                        bean.setCourseName(service.getString("CourseName"));


                        Customer customer = new Customer();
                        customer.setUserID(service.getInt("UserID") + "");
                        customer.setName(service.getString("UserName"));
                        if (custometList.size() > 0) {
                            for (Customer customer2 : custometList) {
                                if (customer.getUserID().equals(
                                        customer2.getUserID())) {
                                    customer.setUrl(customer2.getUrl());
                                    customer.setUBAOUserID(customer2
                                            .getUBAOUserID());
                                    customer.setName(customer2.getName());
                                }
                            }
                        }
                        bean.setCustomer(customer);

                        bean.setUserId(service.getInt("UserID") + "");
                        bean.setUserName(service.getString("UserName").trim());
                        bean.setUserUrl(customer.getUrl());

                        bean.setTeacherId(service.getInt("TeacherID") + "");
                        bean.setTeacherName(service.getString("TeacherName").trim());
                        if (custometList.size() > 0) {
                            for (Customer customer2 : custometList) {
                                if (bean.getTeacherId().equals(
                                        customer2.getUserID())) {
                                    bean.setTeacherUrl(customer2.getUrl());
                                }
                            }
                        }

//                        Log.e("tttttttttttttttttt  学生", bean.getCustomer().getName() + "   " + bean.getCustomer().getUrl());
//                        Log.e("tttttttttttttttttt  老师", bean.getTeacherName() + "   " + bean.getTeacherUrl());

                        // lineitems
                        JSONArray lineitems = service.getJSONArray("LineItems");
                        List<LineItem> items = new ArrayList<LineItem>();
                        for (int j = 0; j < lineitems.length(); j++) {
                            JSONObject lineitem = lineitems.getJSONObject(j);
                            LineItem item = new LineItem();
                            item.setIncidentID(lineitem.getInt("IncidentID"));
                            item.setEventID(lineitem.getInt("EventID"));

                            String linename = lineitem.getString("EventName");
                            item.setEventName((linename == null || linename
                                    .equals("null")) ? "" : linename);

                            item.setFileName(lineitem.getString("FileName"));
                            if (TextUtils.isEmpty(lineitem.getString("AttachmentH5Url"))) {
                                item.setUrl(lineitem.getString("AttachmentUrl"));
                                item.setHtml5(false);
                            } else {
                                item.setUrl(lineitem.getString("AttachmentH5Url"));
                                item.setHtml5(true);
                            }
                            item.setAttachmentID(lineitem.getString("AttachmentID"));
                            items.add(item);
                        }
                        bean.setLineItems(items);
                        mList.add(bean);
                    }
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
