package com.example.mymusicapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class RegisterActivity extends Activity {
    private EditText editTextUserName;
    private EditText editTextPassword;
    private EditText editTextPasswordConfirm;
    private Button buttonRegister;
    private DatabaseHelper databaseHelper;
    private TextView textViewTourist;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editTextUserName=(EditText) findViewById(R.id.et_register_user_name);
        editTextPassword=(EditText) findViewById(R.id.et_register_password);
        editTextPasswordConfirm=(EditText) findViewById(R.id.et_register_password_confirm);
        buttonRegister=(Button) findViewById(R.id.bt_register);
        textViewTourist=(TextView)findViewById(R.id.tv_tourist);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=sharedPreferences.edit();
        String userName=sharedPreferences.getString("nowUser","");
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName=editTextUserName.getText().toString();
                String password=editTextPassword.getText().toString();
                String passwordConfirm=editTextPasswordConfirm.getText().toString();
                if(userName.isEmpty()||password.isEmpty()||passwordConfirm.isEmpty()){
                    Toast.makeText(RegisterActivity.this,"请输入完整信息",Toast.LENGTH_SHORT).show();
                }else if(passwordConfirm.equals(password)){
                    //注册用户
                    databaseHelper=new DatabaseHelper(RegisterActivity.this,"SongsApp.db",null,1);
                    SQLiteDatabase db=databaseHelper.getWritableDatabase();
                    ContentValues values=new ContentValues();
                    values.put("userName",userName);
                    values.put("password",password);
                    db.insert("Users",null,values);
                    values.clear();
                    Toast.makeText(RegisterActivity.this,"用户注册成功",Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(RegisterActivity.this,"两次密码不一致",Toast.LENGTH_SHORT).show();
                }
            }
        });
        textViewTourist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!userName.equals("")){
                    editor.putString("nowUser","");
                    editor.putBoolean("remember_password",false);
                    editor.putString("userName","");
                    editor.putString("password","");
                    editor.apply();
                }
                Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
