package com.example.ids.testapp.Fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ids.testapp.Activity.ProfileActivity;
import com.example.ids.testapp.CommonUtil;
import com.example.ids.testapp.Constants;
import com.example.ids.testapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationFragment extends Fragment implements View.OnClickListener {

    FragmentActivity mActivity;
    String TAG = "RegistrationFragment";
    EditText edt_Email, edt_Password, edt_RepeatPassword, edt_UserName, edt_countrycode, edt_MobileNumber, edt_ReferalCode;
    TextView tv_termsconditions;
    String mPasswrdTxt, mUsernameTxt, mEmailTxt, mReferalCodeTxt,mMobileNumberTxt;
    Button btn_SignUp;
    ImageView iv_Back;
    public static ProgressDialog loadinprogress;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    Spinner sp_countrylist;
    JSONObject userobj;
    CheckBox CheckBoxResponse;


    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_registration, null);
        mActivity = getActivity();

        intiMethod(rootView);

        return rootView;
    }

    private void intiMethod(View view) {
        iv_Back = (ImageView) view.findViewById(R.id.iv_Back);
        edt_Email = (EditText) view.findViewById(R.id.edt_Email);
        edt_Password = (EditText) view.findViewById(R.id.edt_Password);
        edt_RepeatPassword = (EditText) view.findViewById(R.id.edt_RepeatPassword);
        edt_UserName = (EditText) view.findViewById(R.id.edt_UserName);
        edt_countrycode = (EditText) view.findViewById(R.id.edt_countrycode);
        edt_MobileNumber = (EditText) view.findViewById(R.id.edt_MobileNumber);
        edt_ReferalCode = (EditText) view.findViewById(R.id.edt_ReferalCode);
        tv_termsconditions = (TextView) view.findViewById(R.id.tv_termsconditions);
        btn_SignUp = (Button) view.findViewById(R.id.btn_SignUp);
        sp_countrylist = (Spinner) view.findViewById(R.id.sp_countrylist);
        CheckBoxResponse = (CheckBox) view.findViewById(R.id.CheckBoxResponse);
    }

    @Override
    public void onClick(View v) {

        mPasswrdTxt = edt_Password.getText().toString().trim();
        mUsernameTxt = edt_UserName.getText().toString().trim();
        mEmailTxt = edt_Email.getText().toString().trim();
        mReferalCodeTxt = edt_ReferalCode.getText().toString().trim();
        mMobileNumberTxt= edt_MobileNumber.getText().toString().trim();


        switch (v.getId()) {

            case R.id.btn_SignUp:
                if (!CommonUtil.isConnectedToInternet(mActivity)) {
                    CommonUtil.showNoNetworkDialog(mActivity);
                    return;
                }

                if (!CommonUtil.isHavingValue(edt_Email.getText().toString().trim())) {
                    edt_Email.requestFocus();
                    Toast.makeText(mActivity, "Please enter email id", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!CommonUtil.isValidEmail(edt_Email.getText().toString().trim())) {
                    Toast.makeText(mActivity, "Please enter valid email id", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!CommonUtil.isHavingValue(edt_Password.getText().toString().trim())) {
                    edt_Password.requestFocus();
                    Toast.makeText(mActivity, "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edt_Password.getText().toString().length() < 8) {
                    Toast.makeText(mActivity, "Password minimum 8 character", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!CommonUtil.isHavingValue(edt_RepeatPassword.getText().toString().trim())) {
                    Toast.makeText(mActivity, "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edt_RepeatPassword.getText().toString().equals(edt_Password.getText().toString())) {
                    Toast.makeText(mActivity, "Please enter same password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!CommonUtil.isHavingValue(edt_UserName.getText().toString().trim())) {
                    edt_UserName.requestFocus();
                    Toast.makeText(mActivity, "Please enter driver name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!CommonUtil.isHavingValue(edt_MobileNumber.getText().toString().trim())) {
                    edt_MobileNumber.requestFocus();
                    Toast.makeText(mActivity, "Please enter mobile number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!CommonUtil.isHavingValue(edt_ReferalCode.getText().toString())) {

                    Toast.makeText(mActivity, "Please enter Referral Code", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!CheckBoxResponse.isChecked()) {
                    Toast.makeText(mActivity, "Please Accept Terms and Conditions", Toast.LENGTH_SHORT).show();
                }

                /* Reference*/

            /*{
                "userPassword":[{"password":"asdfasdf"}],
                "user_type":"2",
                "user_name":"Vinay",
                "email":"vinay@gmail.com",
                "mobile_number":"648978978977",
                "referral_code":"Ci4bzb7b",
                "login_type":"app",
                "device_token":"dcAmEpAbAQg:APA91bGzBySrSRo5yNuiAiFzx-AhLK-eKhwfOkAY8F2NhDWoEnKIUp0Hmd7ClJuAK3xbIm9106vxg7Eadd9T0vInqc7DrcNrQUfy97PEK2b8Qvs1y4tEULoQH_P0LhhV_m5zRu1rTR474WqgH9iw6visccBM3XIO1A"
            }
            */
                try {
                    userobj = new JSONObject();
                    JSONArray passwrdarray = new JSONArray();
                    JSONObject passwrobj = new JSONObject();
                    passwrobj.put("password", mPasswrdTxt);
                    passwrdarray.put(passwrobj);
                    userobj.put("userPassword", passwrdarray);
                    userobj.put("user_type", Constants.USER_TYPE);
                    userobj.put("user_name", mUsernameTxt.trim());
                    userobj.put("email", mEmailTxt.trim());
                    userobj.put("referral_code", mReferalCodeTxt.trim());
                    userobj.put("login_type", "app");
                    userobj.put("device_token", "");


                } catch (Exception e) {
                    e.printStackTrace();
                }

                PostDataToServer pdts = new PostDataToServer();
                pdts.execute("");
                break;

            case R.id.iv_Back: {
                LoginFragment loginFragment = new LoginFragment();
                CommonUtil.switchFragment(mActivity, loginFragment, R.id.rl_logincontainer, TAG);
                break;
            }
            default:
                break;
        }


    }


    public class PostDataToServer extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // HideError();
            loadinprogress = new ProgressDialog(mActivity);
            loadinprogress.setMessage("Please Wait");
            loadinprogress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loadinprogress.setIndeterminate(true);
            loadinprogress.setProgress(0);
            loadinprogress.setCanceledOnTouchOutside(getRetainInstance());
            loadinprogress.setCancelable(false);
            loadinprogress.show();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected String doInBackground(String... params) {
            // ProgressBarAnimation.showProgress();

            String url = Constants.REGISTRATION_URL;
            String serverResponse = null;
            try {


                serverResponse = CommonUtil.postDataToServer(url.trim(), "",
                        Constants.REQUEST_METHOD_POST, null, userobj.toString().trim());

            } catch (Exception e) {
                CommonUtil.log(TAG, " Main Exception  " + e.getMessage());
            }
            return serverResponse;
        }


        @Override
        protected void onPostExecute(String result) {
            JSONObject OtpData = null;
            JSONObject errormessage = null;
            JSONArray errorjsonarr;
            CommonUtil.log(TAG, " Main Result " + result);
            if (CommonUtil.isHavingValue(result)) {

                Log.d(TAG, ">>>>>>>>>>responsefrom server" + result);
                try {
                    JSONObject serverobj = new JSONObject(result);

                    if (serverobj != null && serverobj.length() > 0) {

                        if (result.contains("message")) {
                            if (serverobj.has("message")) {
                                Toast.makeText(mActivity, "" + serverobj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        }

                        if (result.contains("error")) {
                            OtpData = new JSONObject(serverobj.getString("err"));
                            //if(OtpData.getInt("status")==400)
                            errormessage = new JSONObject(OtpData.getString("invalidAttributes"));
                            if (errormessage.has("email")) {
                                errorjsonarr = new JSONArray(errormessage.getString("email"));
                                OtpData = new JSONObject(errorjsonarr.get(0).toString());
                                Toast.makeText(mActivity, "Email id already exists", Toast.LENGTH_SHORT).show();
                            }
                            if (errormessage.has("mobile_number")) {
                                errorjsonarr = new JSONArray(errormessage.getString("mobile_number"));
                                OtpData = new JSONObject(errorjsonarr.get(0).toString());
                                Toast.makeText(mActivity, "mobile number already exists", Toast.LENGTH_SHORT).show();
                            }

                        } else if (result.contains("apiStatus")) {
                            CommonUtil.showToastMessage(mActivity, "Sorry! Invalid Referral Code");
                        } else {
                            if (serverobj.has("token") == true) {
                                JSONObject userobj = new JSONObject(serverobj.getString("result"));
                                editor.putString(Constants.USER_ID, "" + userobj.getString("id"));
                                editor.putString(Constants.REFERRALCODE, "" + userobj.getString("referral_code"));
                                String strAccessToken = serverobj.getString("token");
                                CommonUtil.log(TAG, "user Details : " + strAccessToken);
                                editor.putString("" + Constants.ACCESS_TOKEN, strAccessToken);
                                editor.commit();
                                Intent intent=new Intent(mActivity,ProfileActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("Name", "" + mUsernameTxt.trim());
                                bundle.putString("Mobileno", "" + mMobileNumberTxt.trim());
                                bundle.putString("Countrycode", "" + edt_countrycode.getText().toString());
                                bundle.putString("Email", "" + mEmailTxt.toString().trim());
                                intent.putExtra("firstBundle",bundle);
                                startActivity(intent);
                            }
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (loadinprogress != null && loadinprogress.isShowing()) {
                        loadinprogress.dismiss();
                    }
                }
                if (loadinprogress != null && loadinprogress.isShowing()) {
                    loadinprogress.dismiss();
                }
            } else {
                CommonUtil.showDialog("Ok", "Registration Failed!", mActivity);
                if (loadinprogress != null && loadinprogress.isShowing()) {
                    loadinprogress.dismiss();
                }
            }
        }
    }
}
