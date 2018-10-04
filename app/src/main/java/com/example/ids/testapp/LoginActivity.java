package com.example.ids.testapp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.ids.testapp.Fragment.LoginFragment;

import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {
    public   String TAG = "LoginActivity";
    public static final String REQUEST_METHOD_POST = "POST";
    AppCompatActivity mActivity;
    public static ProgressDialog loadinprogress;
    EditText e1, e2;
    Button b1;

    JSONObject loginjs;
    public static final String USER_TYPE = "2";
    public static final boolean DEBUG = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        initialMethod();



    }

    private void initialMethod() {
        LoginFragment loginFragment= new LoginFragment();
        CommonUtil.switchFragment(mActivity,loginFragment,R.id.rl_logincontainer,TAG);

    }


}
