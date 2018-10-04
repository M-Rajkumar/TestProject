package com.example.ids.testapp.Fragment;


import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.Toast;

import com.example.ids.testapp.CommonUtil;
import com.example.ids.testapp.Constants;
import com.example.ids.testapp.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "LoginActivity";
    public static final String REQUEST_METHOD_POST = "POST";
    String mUsernameTxt, mPasswordTxt;
    FragmentActivity mActivity;
    public static ProgressDialog loadinprogress;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;

    EditText e1, e2;
    Button b1, b2;
    JSONObject loginjs;
    public static final boolean DEBUG = true;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, null);
        firstView(rootView);
        mActivity = getActivity();
        return rootView;
    }

    private void firstView(View rootView) {
        e1 = (EditText) rootView.findViewById(R.id.edt_UserName);
        e2 = (EditText) rootView.findViewById(R.id.edt_Password);
        b1 = (Button) rootView.findViewById(R.id.btn_Login);
        b2 = (Button) rootView.findViewById(R.id.btn_DontHaveAccount);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        mUsernameTxt = e1.getText().toString().trim();
        mPasswordTxt = e2.getText().toString().trim();

        switch (v.getId()) {
            case R.id.btn_Login:

                if (!CommonUtil.isHavingValue(e1.getText().toString())) {
                    Toast.makeText(mActivity, "Please fill the email address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!CommonUtil.isValidEmail(e1.getText().toString())) {
                    Toast.makeText(mActivity, "Please fill the valid email address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!CommonUtil.isHavingValue(e2.getText().toString())) {
                    Toast.makeText(mActivity, "Please enter the password", Toast.LENGTH_SHORT).show();
                    return;
                }


                Logintoserver login = new Logintoserver();
                login.execute(" ");
                break;
            case R.id.btn_DontHaveAccount: {
                RegistrationFragment registrationFragment = new RegistrationFragment();
                CommonUtil.switchFragment(mActivity, registrationFragment, R.id.rl_logincontainer, "RegistrationFragment");

                break;
            }
            default:
                break;

        }


    }

    public class Logintoserver extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadinprogress = new ProgressDialog(mActivity);
            loadinprogress.setMessage("Please Wait");
            loadinprogress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loadinprogress.setIndeterminate(true);
            loadinprogress.setProgress(0);
            //loadinprogress.setCanceledOnTouchOutside(getRetainInstance());
            loadinprogress.setCancelable(false);
            loadinprogress.show();

        }

        @Override
        protected String doInBackground(String... params) {
            loginjs = new JSONObject();
            String serverresponse = null;
            try {
                loginjs.put("email", mUsernameTxt.trim());
                loginjs.put("password", mPasswordTxt.trim());
                loginjs.put("user_type", "" + Constants.USER_TYPE);
                //loginjs.put("device_token", "" + subscribeToPushService());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, ">>>>>>>>>>>>>>>>log" + loginjs.toString());
            serverresponse = postDataToServer(Constants.LOGIN_URL, "", REQUEST_METHOD_POST, null, loginjs.toString());
            Log.d(TAG, ">>>>>>>>>>>>>>>>log" + serverresponse);

            return serverresponse;
        }

        /*@Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate();
            loadinprogress = new ProgressDialog(mActivity);
            loadinprogress.setMessage("Please Wait");
            loadinprogress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loadinprogress.setIndeterminate(true);
            loadinprogress.setProgress(0);
            //loadinprogress.setCanceledOnTouchOutside(getRetainInstance());
            loadinprogress.setCancelable(false);
            loadinprogress.show();
        }*/

        @Override
        protected void onPostExecute(String result) {
            if (isHavingValue(result)) {
                Log.d(TAG, ":::::::::::::::::: result::::::::" + result);
                try {
                    JSONObject serverobj = new JSONObject(result);

                    if (serverobj != null && serverobj.length() > 0) {
                        if (!result.contains("error")) {

                            if (serverobj.has("token") == true) {


                            }
                            if (result.contains("message")) {
                                Toast.makeText(mActivity, "" + serverobj.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        }
                    }


                    if (loadinprogress != null && loadinprogress.isShowing()) {
                        loadinprogress.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }

            } else {
                if (loadinprogress != null && loadinprogress.isShowing()) {
                    loadinprogress.dismiss();
                }
            }
        }


    /*private String subscribeToPushService() {
        FirebaseApp.initializeApp(mActivity.getApplicationContext());
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        Log.d("AndroidBash", "Subscribed");
        String token = FirebaseInstanceId.getInstance().getToken();
        // Log and toast
        Log.d("AndroidBash", "" + token);
        return token;
    }*/

        public String postDataToServer(String stringURL, String contentType, String requestMethod,
                                       HashMap<String, String> headerValueMap,
                                       String json) {

            InputStream inputStream = null;
            String response = "";
            if (stringURL == null || stringURL.equalsIgnoreCase("")) {
                return response;
            }
            try {
                log(TAG, "URL is  : " + stringURL);
                log(TAG, "json is  : " + json);
                // create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // make POST request to the given URL
                HttpPost httpPost = new HttpPost(stringURL);

                // Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                String mHeaderKey = null;
                String mHeaderVal = null;
                if (headerValueMap != null && headerValueMap.size() > 0) {
                    Iterator it = headerValueMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pairs = (Map.Entry) it.next();
                        mHeaderKey = pairs.getKey().toString().trim();
                        mHeaderVal = pairs.getValue().toString().trim();
                        log(TAG, pairs.getKey() + " = "
                                + pairs.getValue());
                        if (isHavingValue(mHeaderKey) && isHavingValue(mHeaderVal)) {
                            httpPost.setHeader(mHeaderKey, mHeaderVal);
                        }
                    }
                }
                StringEntity entity = new StringEntity(json);
                httpPost.setEntity(entity);


                // Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);

                // receive response as inputStream
                //			inputStream = httpResponse.getEntity().getContent();
                DataInputStream dataIn = new DataInputStream(
                        httpResponse.getEntity().getContent());
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(dataIn));
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    response += inputLine;
                }
                if (DEBUG)
                    log(TAG, "final response is  : " + response);
                br.close();
                dataIn.close();

            } catch (Exception e) {

            }
            return response;
        }

        public boolean isHavingValue(String response) {
            if (response != null && !response.equalsIgnoreCase("")) {
                return true;
            }
            return false;
        }

        public void log(String tag, String string) {
            if (DEBUG) {
                Log.d(tag, string);
            }
        }
    }

}
