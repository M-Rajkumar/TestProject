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
import android.widget.TextView;

import com.example.ids.testapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationFragment extends Fragment {

    FragmentActivity mActivity;
    String TAG ="RegistrationFragment";
    EditText edt_Email,edt_Password,edt_RepeatPassword,edt_UserName,edt_countrycode,edt_MobileNumber,edt_ReferalCode;
    TextView tv_termsconditions;
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
       View rootView= inflater.inflate(R.layout.fragment_registration, container, false);
       mActivity=getActivity();

       intiMethod(rootView);

       return rootView;
    }

    private void intiMethod(View view) {

        edt_Email=(EditText)view.findViewById(R.id.edt_Email);
        edt_Password=(EditText)view.findViewById(R.id.edt_Password);
        edt_RepeatPassword=(EditText)view.findViewById(R.id.edt_RepeatPassword);
        edt_UserName=(EditText)view.findViewById(R.id.edt_UserName);
        edt_countrycode=(EditText)view.findViewById(R.id.edt_countrycode);
        edt_MobileNumber=(EditText)view.findViewById(R.id.edt_MobileNumber);
        edt_ReferalCode=(EditText)view.findViewById(R.id.edt_ReferalCode);
        tv_termsconditions=(TextView) view.findViewById(R.id.tv_termsconditions);
        btn_SignUp=(Button) view.findViewById(R.id.btn_SignUp);
        sp_countrylist=(Spinner) view.findViewById(R.id.sp_countrylist);
        CheckBoxResponse=(CheckBox) view.findViewById(R.id.CheckBoxResponse);
    }

}
