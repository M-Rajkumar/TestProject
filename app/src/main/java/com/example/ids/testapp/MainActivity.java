package com.example.ids.testapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    public static final String REQUEST_METHOD_POST = "POST";
    AppCompatActivity mActivity;
    public static ProgressDialog loadinprogress;
    EditText e1, e2;
    Button b1;
    JSONObject loginjs;
    public static final String USER_TYPE = "2";
    public static final boolean DEBUG = true;

    // Z-Developemnt url
    public static final String LOGIN_URL = "http://18.218.60.154:2030/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        e1 = (EditText) findViewById(R.id.edt_UserName);
        e2 = (EditText) findViewById(R.id.edt_Password);
        b1 = (Button) findViewById(R.id.btn_Login);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!CommonUtill.isHavingValue(e1.getText().toString())) {
                    Toast.makeText(mActivity, "Please fill the email address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!CommonUtill.isValidEmail(e1.getText().toString())) {
                    Toast.makeText(mActivity, "Please fill the valid email address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!CommonUtill.isHavingValue(e2.getText().toString())) {
                    Toast.makeText(mActivity, "Please enter the password", Toast.LENGTH_SHORT).show();
                    return;
                }


                Logintoserver login = new Logintoserver();
                login.execute(" " + e1.getText().toString(), " " + e2.getText().toString());

            }
        });
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
                loginjs.put("email", "" + params[0]);
                loginjs.put("password", "" + params[1]);
                loginjs.put("user_type", "" + USER_TYPE);
                //loginjs.put("device_token", "" + subscribeToPushService());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, ">>>>>>>>>>>>>>>>log" + loginjs.toString());
            serverresponse = postDataToServer(LOGIN_URL, "", REQUEST_METHOD_POST, null, loginjs.toString());
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

                            if (serverobj.has("token")== true){

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
