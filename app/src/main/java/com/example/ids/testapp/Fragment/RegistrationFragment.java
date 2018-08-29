package com.example.ids.testapp.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ids.testapp.CommonUtill;
import com.example.ids.testapp.Constants;
import com.example.ids.testapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationFragment extends Fragment implements View.OnClickListener {

    FragmentActivity mActivity;
    String TAG = "RegistrationFragment";
    EditText edt_Email, edt_Password, edt_RepeatPassword, edt_UserName, edt_countrycode, edt_MobileNumber, edt_ReferalCode;
    TextView tv_termsconditions;
    String mPasswrdTxt,mUsernameTxt,mEmailTxt,mReferalCodeTxt;
    Button btn_SignUp;
    Spinner sp_countrylist;
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
        View rootView = inflater.inflate(R.layout.fragment_registration, container, false);
        mActivity = getActivity();

        intiMethod(rootView);

        return rootView;
    }

    private void intiMethod(View view) {

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

        mPasswrdTxt=edt_Password.getText().toString().trim();
        mUsernameTxt=edt_UserName.getText().toString().trim();
        mEmailTxt=edt_Email.getText().toString().trim();
        mReferalCodeTxt=edt_ReferalCode.getText().toString().trim();


        switch (v.getId()) {

            case R.id.btn_SignUp:
                if (!CommonUtill.isConnectedToInternet(mActivity)) {
                    CommonUtill.showNoNetworkDialog(mActivity);
                    return;
                }

                if (!CommonUtill.isHavingValue(edt_Email.getText().toString().trim())) {
                    edt_Email.requestFocus();
                    Toast.makeText(mActivity, "Please enter email id", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!CommonUtill.isValidEmail(edt_Email.getText().toString().trim())) {
                    Toast.makeText(mActivity, "Please enter valid email id", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!CommonUtill.isHavingValue(edt_Password.getText().toString().trim())) {
                    edt_Password.requestFocus();
                    Toast.makeText(mActivity, "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edt_Password.getText().toString().length() < 8) {
                    Toast.makeText(mActivity, "Password minimum 8 character", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!CommonUtill.isHavingValue(edt_RepeatPassword.getText().toString().trim())) {
                    Toast.makeText(mActivity, "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edt_RepeatPassword.getText().toString().equals(edt_Password.getText().toString())) {
                    Toast.makeText(mActivity, "Please enter same password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!CommonUtill.isHavingValue(edt_UserName.getText().toString().trim())) {
                    edt_UserName.requestFocus();
                    Toast.makeText(mActivity, "Please enter driver name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!CommonUtill.isHavingValue(edt_MobileNumber.getText().toString().trim())) {
                    edt_MobileNumber.requestFocus();
                    Toast.makeText(mActivity, "Please enter mobile number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!CommonUtill.isHavingValue(edt_ReferalCode.getText().toString())) {

                    Toast.makeText(mActivity, "Please enter Referral Code", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!CheckBoxResponse.isChecked()){
                    Toast.makeText(mActivity, "Please Accept Terms and Conditions", Toast.LENGTH_SHORT).show();
                }
                try {
                    JSONObject userobj = new JSONObject();
                    JSONArray passwrdarray = new JSONArray();
                    JSONObject passwrobj = new JSONObject();
                   passwrobj.put("password", mPasswrdTxt);
                   passwrdarray.put(passwrobj);
                   userobj.put("userPassword",passwrdarray);
                   userobj.put("user_type", Constants.USER_TYPE);
                   userobj.put("user_name",mUsernameTxt.trim());
                   userobj.put("email",mEmailTxt.trim());
                   userobj.put("referral_code",mReferalCodeTxt.trim());
                   userobj.put("login_type","app");




                } catch (Exception e) {
                    e.printStackTrace();
                }
        }

    }
}
