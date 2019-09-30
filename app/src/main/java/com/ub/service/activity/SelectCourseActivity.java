package com.ub.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ub.techexcel.adapter.LessonAdapter;
import com.ub.techexcel.bean.CourseLesson;
import com.ub.techexcel.bean.Lesson;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.tools.CalListviewHeight;
import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.config.AppConfig;
import com.kloudsync.techexcel2.service.ConnectService;
import com.umeng.analytics.MobclickAgent;

import org.feezu.liuli.timeselector.TimeSelector;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wang on 2017/6/19.
 */
public class SelectCourseActivity extends Activity implements View.OnClickListener {

    private RelativeLayout selectuser, selectcourse, lessonType;
    private TextView cancel;
    private TextView cti, sl;
    private ListView listview;
    private LinearLayout ll2;
    private TextView addlesson;
    private TextView coursetempatename, coursename, studentname;
    private ServiceBean student = null;
    private CourseLesson courseLesson = new CourseLesson();
    private LessonAdapter lessonAdapter;
    private ScrollView scrollView;
    private List<Lesson> list = new ArrayList<>();
    public static SelectCourseActivity instance;
    private Lesson mLesson;
    private TextView submit;
    private TextView lessontypevalue;
    private int lessontype = -1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x1001:
                    AppConfig.newlesson = true;
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectcourse);

        EventBus.getDefault().register(this);
        initView();
        lessonAdapter = new LessonAdapter(SelectCourseActivity.this, list);
        lessonAdapter.setOnLessonListenering(new LessonAdapter.LessonListenering() {
            @Override
            public void delete(int position) {
                list.remove(position);
                lessonAdapter.notifyDataSetChanged();
                CalListviewHeight.setListViewHeightBasedOnChildren(listview);
            }

            @Override
            public void selectLectures(Lesson lesson) {
                mLesson = lesson;
                Intent intent = new Intent(SelectCourseActivity.this,
                        LectureListActivity.class);
                intent.putExtra("courseLessonid", courseLesson.getCourseID());
                intent.putExtra("mLectures", (Serializable) mLesson.getLectures());
                startActivity(intent);
            }

            @Override
            public void selectTime(final Lesson lesson) {
                final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date curDate = new Date(System.currentTimeMillis());
                String str = formatter.format(curDate);
                TimeSelector timeSelector = new TimeSelector(SelectCourseActivity.this, new TimeSelector.ResultHandler() {
                    @Override
                    public void handle(String time) {
//                        Log.e("sssssssss", time + "");
                        try {
                            Long second = formatter.parse(time).getTime();
//                            Log.e("sssssssss", second + "");
                            lesson.setStartData(second);
                            lesson.setEndData(second + 2 * 60 * 60 * 1000);
                            lessonAdapter.notifyDataSetChanged();
                            CalListviewHeight.setListViewHeightBasedOnChildren(listview);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, str, "2030-12-31 00:00");
                timeSelector.show();
            }
        });
        listview.setAdapter(lessonAdapter);
        instance = this;
    }


    @Override
    protected void onResume() {
        if (AppConfig.ISONRESUME) {
            AppConfig.ISONRESUME = false;
            student = AppConfig.tempServiceBean;
            studentname.setText(student.getCustomer().getName());
        }
        if (AppConfig.ISCOURSE) {
            AppConfig.ISCOURSE = false;
            courseLesson = AppConfig.tempCourse;
            coursename.setText(courseLesson.getTitle());
            coursetempatename.setText(courseLesson.getTitle());

            cti.setVisibility(View.VISIBLE);
            ll2.setVisibility(View.VISIBLE);
            sl.setVisibility(View.VISIBLE);
            listview.setVisibility(View.GONE);
            addlesson.setVisibility(View.VISIBLE);

            // 清除所有的lessons
            list.clear();
            lessonAdapter.notifyDataSetChanged();
            CalListviewHeight.setListViewHeightBasedOnChildren(listview);

        }
        if (AppConfig.ISLECTURE) {
            AppConfig.ISLECTURE = false;

            List<CourseLesson> lectures = mLesson.getLectures();
            lectures.clear();
            lectures.addAll(AppConfig.templectures);

            lessonAdapter.notifyDataSetChanged();
            CalListviewHeight.setListViewHeightBasedOnChildren(listview);
        }

        MobclickAgent.onPageStart("AddServiceFirst");
        MobclickAgent.onResume(this);
        super.onResume();
    }

    private void initView() {
        selectuser = (RelativeLayout) findViewById(R.id.selectuser);
        selectuser.setOnClickListener(this);
        selectcourse = (RelativeLayout) findViewById(R.id.selectcourse);
        selectcourse.setOnClickListener(this);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        lessonType = (RelativeLayout) findViewById(R.id.lessonType);
        lessontypevalue = (TextView) findViewById(R.id.lessontypetv);
        lessonType.setOnClickListener(this);

        cti = (TextView) findViewById(R.id.cti);
        sl = (TextView) findViewById(R.id.sl);
        ll2 = (LinearLayout) findViewById(R.id.ll2);
        listview = (ListView) findViewById(R.id.listview);
        addlesson = (TextView) findViewById(R.id.addlesson);

        cti.setVisibility(View.GONE);
        ll2.setVisibility(View.GONE);
        sl.setVisibility(View.GONE);
        listview.setVisibility(View.GONE);
        addlesson.setVisibility(View.GONE);


        addlesson.setOnClickListener(this);
        coursetempatename = (TextView) findViewById(R.id.coursetempatename);
        coursename = (TextView) findViewById(R.id.coursename);
        studentname = (TextView) findViewById(R.id.studentname);

        cancel = (TextView) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        submit = (TextView) findViewById(R.id.submit);
        submit.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.selectuser:
                Intent intent3 = new Intent(SelectCourseActivity.this,
                        SelectUserActivity.class);
                startActivity(intent3);
                break;
            case R.id.selectcourse:
                if (student == null) {
                    Toast.makeText(SelectCourseActivity.this, getString(R.string.selectstudent), Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(SelectCourseActivity.this,
                        CourseListActivity.class);
                startActivity(intent);
                break;
            case R.id.addlesson:
                listview.setVisibility(View.VISIBLE);
                Lesson c = new Lesson();
                c.setTitle("LESSON" + (list.size() + 1));
                c.setLessonId(0);

                List<CourseLesson> LL = new ArrayList<>();
                c.setLectures(LL);
                list.add(c);
                lessonAdapter.notifyDataSetChanged();
                CalListviewHeight.setListViewHeightBasedOnChildren(listview);
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
                break;
            case R.id.lessonType:
                Intent intent1 = new Intent(SelectCourseActivity.this, SelectLessonTypeActivity.class);
                intent1.putExtra("lessontype", lessontype);
                startActivity(intent1);
                break;
            case R.id.cancel:
                finish();
                overridePendingTransition(R.anim.tran_in7, R.anim.tran_out7);
                break;
            case R.id.submit:
                if (courseLesson == null || courseLesson.getCourseID() <= 0) {
                    Toast.makeText(SelectCourseActivity.this, getString(R.string.selectcoursefirst), Toast.LENGTH_LONG).show();
                } else {
                    submitLesson();
                }
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void received(Integer event) {
        lessontype = event;
        switch (lessontype) {
            case 1:
                lessontypevalue.setText("Public");
                break;
            case 2:
                lessontypevalue.setText("One to One");
                break;
            case 3:
                lessontypevalue.setText("One to Many");
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void submitLesson() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final JSONArray array = new JSONArray();
                    for (int i = 0; i < list.size(); i++) {
                        Lesson lesson = list.get(i);
                        List<CourseLesson> lectures = lesson.getLectures();
                        String lecs = "";
                        for (int j = 0; j < lectures.size(); j++) {
                            CourseLesson l = lectures.get(j);
                            if (j == 0) {
                                lecs += l.getLectureID() + "";
                            } else {
                                lecs += "," + l.getLectureID();
                            }
                        }
                        JSONObject js = new JSONObject();
                        js.put("LessonID", lesson.getLessonId());
                        js.put("CourseID", courseLesson.getCourseID());
                        js.put("StudentID", student.getCustomer().getUserID() + "");
                        js.put("Title", lesson.getLessonId());
                        js.put("Description", lesson.getDescription());
                        js.put("LectureIDs", lecs);
                        js.put("StartDate", lesson.getStartData());
                        js.put("EndDate", lesson.getEndData());
                        array.put(js);
                    }
                    JSONObject json = ConnectService.submitDataByJsonArray(AppConfig.URL_PUBLIC + "Service/CreateOrUpdateLessons", array);
                    int retCode = json.getInt("RetCode");
                    switch (retCode) {
                        case 0:
                            Message msg = Message.obtain();
                            msg.what = 0x1001;
                            handler.sendMessage(msg);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            overridePendingTransition(R.anim.tran_in7, R.anim.tran_out7);
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }
}
