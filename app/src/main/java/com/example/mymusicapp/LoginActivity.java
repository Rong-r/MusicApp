package com.example.mymusicapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class LoginActivity extends Activity {
    private Button buttonRegister;
    private Button buttonLogin;
    private TextView textViewAgreeUser;
    private TextView textViewAgreePrivacy;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private EditText editTextUserName;
    private EditText editTextPassword;
    private CheckBox checkBoxRememberPassword;
    private RadioButton radioButtonAgree;
    private DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        buttonRegister=(Button) findViewById(R.id.bt_enroll);
        buttonLogin=(Button) findViewById(R.id.bt_login);
        textViewAgreeUser=(TextView)findViewById(R.id.tv_login_agree_user);
        textViewAgreePrivacy=(TextView)findViewById(R.id.tv_login_agree_privacy);
        editTextUserName=(EditText)findViewById(R.id.et_user_name);
        editTextPassword=(EditText)findViewById(R.id.et_password);
        checkBoxRememberPassword=(CheckBox)findViewById(R.id.cb_remember_password);
        radioButtonAgree=(RadioButton) findViewById(R.id.rb_login_agree);
        boolean isRemember=sharedPreferences.getBoolean("remember_password",false);
        if(isRemember){
            //自动填写信息
            String userName=sharedPreferences.getString("userName","");
            String password=sharedPreferences.getString("password","");
            editTextUserName.setText(userName);
            editTextPassword.setText(password);
            checkBoxRememberPassword.setChecked(true);
        }
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        textViewAgreeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this,"查看《用户协议》",Toast.LENGTH_SHORT).show();
            }
        });
        textViewAgreePrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this,"查看《隐私协议》",Toast.LENGTH_SHORT).show();
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName=editTextUserName.getText().toString();
                String password=editTextPassword.getText().toString();
                if(radioButtonAgree.isChecked()){
                    if(userName.isEmpty()||password.isEmpty()){
                        Toast.makeText(LoginActivity.this,"用户名或密码为空",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //核对登录信息
                        databaseHelper=new DatabaseHelper(LoginActivity.this,"SongsApp.db",null,1);
                        SQLiteDatabase db=databaseHelper.getWritableDatabase();
                        Boolean isTrue=new Boolean(false);
                        String selection="userName = ?";
                        String[] selectionArgs={userName};
                        Cursor cursor=db.query("Users",null,selection,selectionArgs,null,null,null);
                        //判断根据用户名筛选出的密码中是否有符合的
                        if(cursor.moveToFirst()){
                            do{
                                String passwordInDB=cursor.getString(cursor.getColumnIndexOrThrow("password"));
                                if(password.equals(passwordInDB)){
                                    isTrue=true;
                                }
                            }while(cursor.moveToNext()&&isTrue.equals(false));
                            if(isTrue.equals(true)){
                                //信息准确且选择记住密码
                                editor=sharedPreferences.edit();
                                if(checkBoxRememberPassword.isChecked()){
                                    editor.putBoolean("remember_password",true);
                                    editor.putString("userName",userName);
                                    editor.putString("password",password);
                                }else{
                                    editor.clear();
                                }
                                editor.apply();
                                //成功登录系统
                                Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }else {
                                //遍历数据库中账户密码后没有符合的
                                Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            //数据库里没有该用户
                            Toast.makeText(LoginActivity.this,"账户信息不存在，请先注册",Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    Toast.makeText(LoginActivity.this,"请勾选同意协议",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
