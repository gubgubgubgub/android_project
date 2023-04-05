package com.example.shared_calendar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class Signup_page extends AppCompatActivity {
    TextView back, pw_check_text, id_check_text, check_error_text;
    EditText name, id, pw, pw2, email;
    Button pwcheck, submit, idcheck;
    SharedPreferences preferences;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        //뒤로가기
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // 가입 항목
        name = findViewById(R.id.sign_name);
        id = findViewById(R.id.sign_ID);
        pw = findViewById(R.id.sign_password);
        pw2 = findViewById(R.id.sign_password_check);
        email = findViewById(R.id.sign_email);
        pw_check_text = findViewById(R.id.sign_pw_check_text);
        id_check_text = findViewById(R.id.sign_id_check_text);
        check_error_text = findViewById(R.id.check_error);

        //비밀번호 확인
        pwcheck = findViewById(R.id.sign_password_check_button);
        pwcheck.setOnClickListener(v -> {
            if (pw.getText().toString().equals(pw2.getText().toString())) {
                pw_check_text.setVisibility(View.VISIBLE);
                pw_check_text.setText("일치합니다.");
                pw_check_text.setTextColor(Color.parseColor("#00FF00")); // 초록

            } else {
                pw_check_text.setVisibility(View.VISIBLE);
                pw_check_text.setText("일치 하지 않습니다.");
                pw_check_text.setTextColor(Color.parseColor("#FF0000")); // 빨강
            }
        });

        idcheck = findViewById(R.id.sign_id_check_button);
        idcheck.setOnClickListener(v ->{
            String get_ID;
            String write_ID;
            String[] get_split_ID;
            int i;

            SharedPreferences sharedPreferences = getSharedPreferences("Signup_info",MODE_PRIVATE);
            get_ID = sharedPreferences.getString("Signup_ID","default");
            get_split_ID = get_ID.split("@#@");
            write_ID = id.getText().toString();

            for(i=0; i<get_split_ID.length; i++){

                if(get_split_ID[i].equals(write_ID)){  ////////////////////////////////////이해가 안감
                    id_check_text.setVisibility(View.VISIBLE);
                    id_check_text.setText("중복된 아이디가 있습니다.");
                    id_check_text.setTextColor(Color.parseColor("#FF0000")); // 빨강
                    break;
                }else {
                    id_check_text.setVisibility(View.VISIBLE);
                    id_check_text.setText("사용가능한 아이디 입니다.");
                    id_check_text.setTextColor(Color.parseColor("#00FF00")); // 초록
                }
            }


        });

        //회원가입 완료 버튼
        submit = findViewById(R.id.sign_up_finish_button);
        submit.setOnClickListener(v -> {
            String d_ID = id.getText().toString();
            String d_PW = pw.getText().toString();
            String d_NAME = name.getText().toString();
            String d_Email = email.getText().toString();
            Log.e("01_30","d_ID"+ d_ID);
            if(d_ID.length()==0){ // 이것도 이해안감
                check_error_text.setVisibility(View.VISIBLE);
                check_error_text.setText("ID를 입력해주세요");
                check_error_text.setTextColor(Color.parseColor("#FF0000")); // 빨강
            }else if(d_PW.length()==0){
                check_error_text.setVisibility(View.VISIBLE);
                check_error_text.setText("PW를 입력해주세요");
                check_error_text.setTextColor(Color.parseColor("#FF0000")); // 빨강
            }else if(d_NAME.length()==0){
                check_error_text.setVisibility(View.VISIBLE);
                check_error_text.setText("이름을 입력해주세요");
                check_error_text.setTextColor(Color.parseColor("#FF0000")); // 빨강
            }else if(d_Email.length()==0){
                check_error_text.setVisibility(View.VISIBLE);
                check_error_text.setText("Email를 입력해주세요");
                check_error_text.setTextColor(Color.parseColor("#FF0000")); // 빨강
            }else{
                //Editor를 preferences에 쓰겠다고 연결
                //sharedpreference
                String get_ID;
                String write_ID;
                String[] get_split_ID;
                int check_id = 0;
                int check_pw = 0;

                SharedPreferences sharedPreferences = getSharedPreferences("Signup_info",MODE_PRIVATE);
                get_ID = sharedPreferences.getString("Signup_ID","default");
                get_split_ID = get_ID.split("@#@");
                write_ID = id.getText().toString();

                for(int i=0; i<get_split_ID.length; i++){
                    if(get_split_ID[i].equals(write_ID)){
                        check_id = 1;
                        break;
                    }else{
                        check_id =0;
                    }
                }

                if(pw.getText().toString().equals(pw2.getText().toString())==false){
                    check_pw =1;
                }else{
                    check_pw =0;
                }


                if(check_id == 1){
                    check_error_text.setVisibility(View.VISIBLE);
                    check_error_text.setText("ID확인을 다시 해주세요");
                    check_error_text.setTextColor(Color.parseColor("#FF0000")); // 빨강
                }else if(check_pw == 1){
                    check_error_text.setVisibility(View.VISIBLE);
                    check_error_text.setText("PW확인을 다시 해주세요");
                    check_error_text.setTextColor(Color.parseColor("#FF0000")); // 빨강
                }else{
                    try {
                        add_signup(d_NAME ,d_ID, d_PW, d_Email );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }

            }

        });

    }

    void add_signup(String name, String ID, String PW, String Email) throws JSONException {
        ////////////////////////////회원 목록 정보
        JSONObject signup_list = new JSONObject();
        signup_list.put("NAME", name); //(key="NAME" : value="m_name")
        signup_list.put("ID", ID);
        signup_list.put("PW", PW);
        signup_list.put("Email", Email);

        String info = signup_list.toString();
        //넣어줄 위치
        SharedPreferences.Editor editor = getSharedPreferences("Signup_info", MODE_PRIVATE).edit();
        editor.putString(ID+"_info",info);
        editor.apply();
        Log.d("json", info);

        ////////////////////////////회원 ID목록
        String save_id;
        String check = "@#@"; //구분자

        SharedPreferences sharedPreferences = getSharedPreferences("Signup_info",MODE_PRIVATE);
        save_id = sharedPreferences.getString("Signup_ID","None");

        StringBuilder ID_list = new StringBuilder(); //구분자랑 memo랑 이어줄 메서

        SharedPreferences.Editor editor1 = getSharedPreferences("Signup_info", MODE_PRIVATE).edit();


        if(save_id != "None"){ // 내용있을때
            ID_list.append(save_id);
            ID_list.append(ID);
            ID_list.append(check);
        }else{ // 처음일때
            ID_list.append(ID);
            ID_list.append(check);
        }
        editor1.putString("Signup_ID",ID_list.toString());

        editor1.apply();

    }
}