package com.example.shared_calendar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //로그인, 회원가입 버튼
        Button login_button = findViewById(R.id.login_button);
        Button sign_button = findViewById(R.id.sign_up_button);

        //전달받은 id.pw
        EditText Login_ID, Login_PW;
        TextView check_id_pw;

        Login_ID = findViewById(R.id.ID_edit);
        Login_PW = findViewById(R.id.Password_edit);
        check_id_pw = findViewById(R.id.ID_PW_check_text);

        login_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String get_ID;
                String get_PW;
                String Sign_up_data;


                SharedPreferences sharedPreferences = getSharedPreferences("Signup_info",MODE_PRIVATE);
                Sign_up_data = sharedPreferences.getString(Login_ID.getText().toString()+ "_info","default");
                try {
                    JSONObject data = new JSONObject(Sign_up_data);
                    get_ID = data.get("ID").toString();
                    get_PW = data.get("PW").toString();

                    Log.d("first_page","받은 ID :"+ get_ID);
                    Log.d("first_page","받은 PW :"+ get_PW);

                    if (Login_ID.getText().toString().equals(get_ID) && Login_PW.getText().toString().equals(get_PW)) {
                        // ID PW 일치할떄 로그인하는  ID를 저장
                        SharedPreferences.Editor editor = getSharedPreferences("Signup_info", MODE_PRIVATE).edit();
                        editor.putString("login_ID",get_ID);
                        editor.apply();
                        Intent intent = new Intent(MainActivity.this, Calendar_page.class);
                        startActivity(intent);
                        // onStop()
                    }else
                    {
                        check_id_pw.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        sign_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Signup_page.class);
                startActivity(intent);
            }
        });
    }
}