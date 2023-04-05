package com.example.shared_calendar;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class Calendar_page extends AppCompatActivity {
    public TextView day_now;
    public TextView day_now_friends;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_page);


        Button friends_button = findViewById(R.id.calendar_page_friends_button);
        Button calendar_button = findViewById(R.id.calendar_page_calendar_button);
        Button buttonAdditem = findViewById(R.id.calender_add_button);
        Button my_memo_button = findViewById(R.id.btn_my_memo);
        Button friends_memo_button = findViewById(R.id.btn_friend_memo);

        LinearLayout my_calendar = findViewById(R.id.my_calendar);
        LinearLayout friends_calendar = findViewById(R.id.friends_calendar);

        EditText add_memo_text = findViewById(R.id.add_memo_text);

        day_now = findViewById(R.id.calenadr_day);
        day_now_friends = findViewById(R.id.calenadr_day1);




        //////////////////////////////////////////////////////// 리사이클 뷰
        ArrayList dataList = new ArrayList<>();
        ArrayList<Sign_up_item> friends_dataList = new ArrayList<>();

        Adapter_memo adapter = new Adapter_memo(dataList);
        Adapter_friends_list friends_list = new Adapter_friends_list(friends_dataList);

        RecyclerView recyclerView = findViewById(R.id.recycle);
        RecyclerView friends_recyclerView = findViewById(R.id.friends_recycle);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, recyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new ItemDecoration());

        friends_recyclerView.setAdapter(friends_list);
        friends_recyclerView.setLayoutManager(new LinearLayoutManager(this, friends_recyclerView.VERTICAL, false));
        friends_recyclerView.addItemDecoration(new ItemDecoration());

        day_now = findViewById(R.id.calenadr_day);

        Log.e("CalanderView", "onCreate");

        ////////////////달력 정보 들고오기
       // CalendarView calendarView = findViewById(R.id.calendarview);
        MaterialCalendarView materialCalendarView =  findViewById(R.id.materialCalendarView);
        materialCalendarView.setSelectedDate(CalendarDay.today());
        Calendar cal = new GregorianCalendar();
        String date;
        date = cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DAY_OF_MONTH);
        day_now.setText(date); // 첫 화면 달력 표시
        String check = "@#@"; //구분자


        List<CalendarDay> my_reservedDates = null; // memo 있는 날짜 리스트
        List<CalendarDay> friends_reservedDates = null; // memo 있는 날짜 리스트

        my_reservedDates = get_my_ReservedDates();
        friends_reservedDates = get_friends_ReservedDates();

        EventDecorator my_Decorator = new EventDecorator(Color.RED, my_reservedDates);
        EventDecorator friends_Decorator = new EventDecorator(Color.GREEN, friends_reservedDates);
        materialCalendarView.addDecorators(
                my_Decorator,
                friends_Decorator,
                new SundayDecorator(),
                new SaturdayDecorator(),
                new onDayDecorator());



        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {

            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                int day = date.getDay();
                int year = date.getYear();
                int month = date.getMonth()+1;
                String memo_unsplit;
                String friend_unsplit;
                String[] memo_split;

                String ID_unsplit;
                String[] ID_split;
                int length;
                int length1;
                String now_ID;
                String now_date = year+"/"+month+"/"+day;

                day_now.setText(now_date);
                day_now_friends.setText(now_date);
                try {
                    memo_unsplit = get_memo(day_now.getText().toString());
                    memo_split = memo_unsplit.split("@#@");
                    length = memo_split.length;
                    dataList.clear();
                    for (int i = 0; i < length; i++) {
                        dataList.add(memo_split[i]);  //리사이클러뷰 바로 출력
                    }

                } catch (JSONException e) {
                    dataList.clear();
                    e.printStackTrace();
                }
                /////////////////////////////////////////////////////////////// 친구 메모 표시
                ID_unsplit = get_signup_ID();

                ID_split = ID_unsplit.split("@#@");
                length1 = ID_split.length;
                friends_dataList.clear();

                try {

                    now_ID = get_now_ID();

                    for (int i = 0; i < length1; i++) {
                        friend_unsplit = get_friends_memo(day_now_friends.getText().toString(),ID_split[i]);
                        String img_path = get_image_path(ID_split[i]);
                        if(ID_split[i].equals(now_ID)){ // 왜들어감????
                        }else{
                            if(friend_unsplit != "default"){
                                friends_dataList.add(new Sign_up_item(ID_split[i], get_signup_name(ID_split[i]), get_signup_Email(ID_split[i]),img_path));  //리사이클러뷰 바로 출력
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                adapter.notifyDataSetChanged();
                friends_list.notifyDataSetChanged();

            }
        });

        //////////////////데이터 추가/////////////////////////////

        buttonAdditem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                day_now = findViewById(R.id.calenadr_day);
                String day = day_now.getText().toString();

                String memo_text = add_memo_text.getText().toString();
                Log.d("my tag", memo_text);
                add_memo_text.setText("");
                dataList.add(memo_text);  //리사이클러뷰 바로 출력
                adapter.notifyDataSetChanged();

///////////////////////////
                StringBuilder memo_list = new StringBuilder(); //구분자랑 memo랑 이어줄 메서드

                for (int i = 0; i < dataList.size(); i++) {
                    memo_list.append(adapter.get_list(i));
                    memo_list.append(check);
                }
                String str = memo_list.toString();

                try {
                    save_memo(str, day);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
/////////////////////////////

            }
        });

        //리사이클 뷰 아이템 클릭 이벤트

        adapter.setOnitemClickListener(new Adapter_memo.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) { // 메모 클릭시
                String memo_text = dataList.get(position).toString();
                Toast toast = Toast.makeText(Calendar_page.this, "내용:" + memo_text, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

            @Override
            public void onChangeClick(View v, int position) { // 변경 버튼
                String memo_text = dataList.get(position).toString();
                editItem(memo_text, position); // 변경 할 메모 데이터와 포지션 값 보낸다
            }

            @Override
            public void onDeleteClick(View v, int position) { // 삭제 버튼
                dataList.remove(position);
                adapter.notifyItemRemoved(position);
                ////////////////
                day_now = findViewById(R.id.calenadr_day);
                String day = day_now.getText().toString();

                StringBuilder memo_list = new StringBuilder(); //구분자랑 memo랑 이어줄 메서드
                Log.d("to check datalist", "dataList.size()="+ dataList.size());
                for (int i = 0; i < dataList.size(); i++) {
                    memo_list.append(adapter.get_list(i));
                    memo_list.append(check);
                }
                String str = memo_list.toString();
                try {
                    Log.d("log_for_save_memo", "save_memo(str, day) 들어감?");
                    save_memo(str, day);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                /////////////////

            }

            /////////////dialog를 사용해서 데이터 수정
            public void editItem(String memo_text, int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Calendar_page.this);
                View view = LayoutInflater.from(Calendar_page.this).inflate(R.layout.memo_change_dialog, null, false);
                builder.setView(view);

                final Button btn_edit = view.findViewById(R.id.btn_edit);
                final Button btn_cancel = view.findViewById(R.id.btn_cancel);
                final EditText edit_text = view.findViewById(R.id.edit_editmemo);
                final AlertDialog dialog = builder.create();

                edit_text.setText(memo_text);

                btn_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String edittext = edit_text.getText().toString();
                        //   dataList.get(position).setMemo(edittext);
                        dataList.remove(position);
                        dataList.add(position,edittext);
                        adapter.notifyItemChanged(position);

                        ////////////////
                        day_now = findViewById(R.id.calenadr_day);
                        String day = day_now.getText().toString();

                        StringBuilder memo_list = new StringBuilder(); //구분자랑 memo랑 이어줄 메서드

                        for (int i = 0; i < dataList.size(); i++) {
                            memo_list.append(adapter.get_list(i));
                            memo_list.append(check);
                        }
                        String str = memo_list.toString();

                        try {
                            save_memo(str, day);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        /////////////////

                        dialog.dismiss();


                    }
                });
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }


        });
        ////////////친구 이사이클러뷰
        friends_list.setOnitemClickListener(new Adapter_friends_list.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d("ffffffffff","into");
                AlertDialog.Builder builder = new AlertDialog.Builder(Calendar_page.this);
                View view = LayoutInflater.from(Calendar_page.this).inflate(R.layout.memo_friends_dialog, null, false);
                builder.setView(view);

                final TextView memo_friend_text;
                final AlertDialog dialog = builder.create();
                memo_friend_text = view.findViewById(R.id.friends_memo_text);
                memo_friend_text.setMovementMethod(new ScrollingMovementMethod());


                String memo_unsplit;
                String memo_split[];
                String memo_split_space = "";
                int length;
                String memo_text = friends_dataList.get(position).getID(); // ID


                try {
                    memo_unsplit = get_friends_memo(day_now_friends.getText().toString(),memo_text);
                    memo_split = memo_unsplit.split("@#@");
                    length = memo_split.length;

                    Log.d("qweasd","memo_unsplit="+ memo_unsplit);
                    for(int i =0; i<length; i++){
                        memo_split_space = memo_split_space + (i+1) +". " + memo_split[i]+"\n\n";
                    }
                    memo_friend_text.setText(memo_split_space);
                    dialog.show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });



        friends_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Calendar_page.this, Friends_page.class);
                startActivity(intent);
                //  onStop();

            }
        });

        calendar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Calendar_page.this, Calendar_page.class);
                startActivity(intent);
                //  onStop();
            }
        });

        my_memo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                my_calendar.setVisibility(View.VISIBLE);
                friends_calendar.setVisibility(View.GONE);
            }
        });

        friends_memo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                my_calendar.setVisibility(View.GONE);
                friends_calendar.setVisibility(View.VISIBLE);
            }
        });


    }

    private List<CalendarDay> get_my_ReservedDates() {
        List<CalendarDay> reservedDates = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("Memo", MODE_PRIVATE);
        String now_id = get_now_ID();

        String memo_data;
        String memo_data_inner;

        for(int i=1 ; i < 13; i++ ){
            for(int j=1; j < 32; j++){
                memo_data =  sharedPreferences.getString(now_id+"_2023/"+i+"/"+j, "default");

                if(memo_data != "default"){
                    JSONObject data = null;
                    Log.d("to check datalist", "들어옴?=["+memo_data+"]");
                    try {
                        data = new JSONObject(memo_data);
                        memo_data_inner = data.get("memo").toString();
                        Log.d("to check datalist", "들어옴2?=["+memo_data_inner+"]");
                        if(memo_data_inner.length()>0){
                            Log.d("to check datalist", "들어옴3?");
                            reservedDates.add(CalendarDay.from(2023, i-1, j));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return reservedDates;
    }
    private List<CalendarDay> get_friends_ReservedDates() {
        Log.d("to check datalist", "111111");
        List<CalendarDay> reservedDates = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("Memo", MODE_PRIVATE);
        String now_id = get_now_ID();
        String ID_unsplit = get_signup_ID();
        String ID_unsplit_delete_now_id = ID_unsplit.replace(now_id+"@#@","");
        Log.d("to check datalist", "222222");
        String ID_split[] = ID_unsplit_delete_now_id.split("@#@");
        int ID_string = ID_split.length;
        Log.d("to check datalist", "ID_string"+ID_string);

        String memo_data;
        String memo_data_inner;
        Log.d("to check datalist", "3333");
        for(int i=1 ; i < 13; i++ ){
            for(int j=1; j < 32; j++){
                for(int k=0; k<ID_string; k++){
                    memo_data =  sharedPreferences.getString(ID_split[k]+"_2023/"+i+"/"+j, "default");
                    if(memo_data != "default"){
                        JSONObject data = null;
                        Log.d("to check datalist", "들어옴?=["+memo_data+"]");
                        try {
                            data = new JSONObject(memo_data);
                            memo_data_inner = data.get("memo").toString();
                            Log.d("to check datalist", "들어옴2?=["+memo_data_inner+"]");
                            if(memo_data_inner.length()>0){
                                Log.d("to check datalist", "들어옴3?");
                                reservedDates.add(CalendarDay.from(2023, i-1, j));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
        Log.d("to check datalist", "4444");
        return reservedDates;
    }
/////////////////////////////////////////////////////////////////////////////////////////

    void save_memo(String memo, String now_day) throws JSONException { // 일정, 현재 날짜 String memo에는 구분자 @#@이 포함되어있는  string 값이다
        JSONObject inner = new JSONObject();
        inner.put("memo", memo);
        String info = inner.toString();
        String now_ID;
        SharedPreferences.Editor editor = getSharedPreferences("Memo", MODE_PRIVATE).edit();
        now_ID = get_now_ID();

        if(memo.length()>0){

            editor.putString(now_ID+"_"+ now_day, info);
            editor.apply();
            Log.d("log_for_save_memo", now_day);
            Log.d("log_for_save_memo", info);
        }else{
            Log.d("log_for_save_memo", "들어왔니?");
          editor.remove(now_ID+"_"+ now_day);
          editor.apply();
        }

    }

    String get_memo(String now_day) throws JSONException {
        String memo_data;
        String get_memo;
        String now_ID;

        SharedPreferences sharedPreferences = getSharedPreferences("Memo", MODE_PRIVATE);
        now_ID = get_now_ID();

        memo_data = sharedPreferences.getString(now_ID+"_"+ now_day, "default");
        JSONObject data = new JSONObject(memo_data);

        get_memo = data.get("memo").toString();

        return get_memo;
    }
    String get_friends_memo(String now_day, String id) throws JSONException {
        String get_memo;
        String memo_key;
        Log.d("qweasd","now_day="+ now_day);
        Log.d("qweasd","id="+ id);
        SharedPreferences sharedPreferences = getSharedPreferences("Memo", MODE_PRIVATE);
        memo_key = sharedPreferences.getString(id+"_"+ now_day, "default");
        if(memo_key == "default"){
            get_memo = memo_key;
        }else{
            JSONObject data = new JSONObject(memo_key);
            get_memo = data.get("memo").toString();
        }

        return get_memo;

    }
    String get_signup_ID(){
        String ID;
        SharedPreferences sharedPreferences = getSharedPreferences("Signup_info", MODE_PRIVATE);
        ID = sharedPreferences.getString("Signup_ID","default");
        return ID;
    }
    String get_now_ID()  {

        String now_ID;
        SharedPreferences signup_sharedPreferences = getSharedPreferences("Signup_info", MODE_PRIVATE);

        now_ID = signup_sharedPreferences.getString("login_ID", "default");

        return now_ID;
    }
    String get_signup_name(String ID) throws JSONException{
        String name;
        SharedPreferences sharedPreferences = getSharedPreferences("Signup_info", MODE_PRIVATE);
        ID = sharedPreferences.getString(ID+"_info","default");
        JSONObject data = new JSONObject(ID);
        name= data.get("NAME").toString();

        return name;
    }

    String get_signup_Email(String ID) throws JSONException{
        String Email;
        SharedPreferences sharedPreferences = getSharedPreferences("Signup_info", MODE_PRIVATE);
        ID = sharedPreferences.getString(ID+"_info","default");
        JSONObject data = new JSONObject(ID);
        Email= data.get("Email").toString();

        return Email;
    }

    private String get_image_path(String id){
        String filename = id + "프로필사진.jpg";
        File storageDir = new File(getFilesDir() + "/capture");

        try {
            String imgpath = storageDir + "/" + filename;   // 내부 저장소에 저장되어 있는 이미지 경로
            return imgpath;
        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), "파일 로드 실패", Toast.LENGTH_SHORT).show();
            return "none";
        }

    }
}