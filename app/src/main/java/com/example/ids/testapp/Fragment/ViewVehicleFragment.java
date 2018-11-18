package com.example.ids.testapp.Fragment;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ids.testapp.CommonUtil;
import com.example.ids.testapp.Constants;
import com.example.ids.testapp.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.ids.testapp.CommonUtil.isHavingValue;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewVehicleFragment extends Fragment {

    public SharedPreferences.Editor editor;
    public SharedPreferences pref;
    FragmentActivity mActivity;
    public static final String APP_PREFS_ACCOUNT = "TestApp";
    TextView tv_username;
    ArrayAdapter<String> yearspinner;
    EditText edt_drivername, edt_vechicleno, edt_carname, edt_carbrandname, edt_carowenedby;
    Button btn_update;
    Spinner sp_Model;
    RadioGroup rg_question;
    TextView tv_errormessage;
    ImageView iv_carphoto;
    ArrayList<String> yearofmodel, totalyear, cartypeid, carmasterid;
    String yearname;
    String TAG = "ViewVehicleFragment";
    AlertDialog dialog;
    Bitmap imagebitmap, carImage_bitmap;
    public Dialog customDialogObj;
    boolean bIsCamera;
    private int permissioncode_read = 150, camerapermissioncode = 100;
    public JSONObject profileobj;
    int cartype = 1;
    int carid = -1;
    public static ProgressDialog loadinprogress;
    Bundle bundle;
    BitmapDrawable dr;
    FrameLayout fl_pictuercontainer;
    ImageView iv_ProfileImage, iv_Edit;
    boolean iscarphoto = false;
    ImageView btn_browse;
    int yearselected;
    ImageView iv_back;
    protected LocationManager locationManager;
    boolean isNetworkEnabled = false;
    boolean isGPSEnabled = false;
    List<Address> addresses;
    Geocoder geocoder;
   // GPSTracker gps;
    String currentcity;
    int currentcityid;
    int cartypescount;
    ArrayList<String> cartypedata;
    String oldcarid, oldcartype;




    public ViewVehicleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mActivity = getActivity();
        pref = mActivity.getApplicationContext().getSharedPreferences(APP_PREFS_ACCOUNT, Context.MODE_PRIVATE);
        editor = pref.edit();
        View rootView = inflater.inflate(R.layout.fragment_view_vehicle, null);
        bundle = new Bundle();
        bundle = this.getArguments();

       /* yearofmodel = new ArrayList<String>();
        yearofmodel.add("--- Select Year ---");
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1990; i <= thisYear; i++) {
            yearofmodel.add(Integer.toString(i));
        }*/
        if (totalyear == null) {
            totalyear = new ArrayList<String>();
        } else {
            totalyear.clear();
        }

        if (yearofmodel == null) {
            yearofmodel = new ArrayList<String>();
        } else {
            yearofmodel.clear();
        }
        //yearofmodel = new ArrayList<String>();

        yearofmodel.add("---Select Year---");
        totalyear.add("---Select Year---");

        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1990; i <= thisYear; i++) {
            //  CommonUtil.log(TAG,i+" yrear testing "+Integer.toString(i));
            yearofmodel.add(Integer.toString(i));
            totalyear.add("----" + Integer.toString(i) + "----");
        }
        /*yearofmodel.add("2010");
        yearofmodel.add("2011");
        yearofmodel.add("2012");
        yearofmodel.add("2013");
        yearofmodel.add("2014");
        yearofmodel.add("2015");
        yearofmodel.add("2016");
        yearofmodel.add("2017");*/
        initViews(rootView);
        /*if (DataStore.getUserimagebitmap() != null) {
            iv_ProfileImage.setImageBitmap(DataStore.getUserimagebitmap());
            BitmapDrawable dr = new BitmapDrawable(DataStore.getUserimagebitmap());
            fl_pictuercontainer.setBackground(dr);
        }  */      /*fetcholdprofieldata fetchdata=new fetcholdprofieldata();
        fetchdata.execute("");*/
        /*CityLoader cityload = new CityLoader();
        cityload.execute("");*/

        return rootView;
    }

    private void initViews(final View rootView) {

        tv_errormessage = (TextView) rootView.findViewById(R.id.tv_errormessage);
        btn_browse = (ImageView) rootView.findViewById(R.id.btn_browse);
        iv_Edit = (ImageView) rootView.findViewById(R.id.iv_Edit);
        edt_carowenedby = (EditText) rootView.findViewById(R.id.edt_carowenedby);
        edt_drivername = (EditText) rootView.findViewById(R.id.edt_drivername);
        edt_vechicleno = (EditText) rootView.findViewById(R.id.edt_vechicleno);
        edt_carname = (EditText) rootView.findViewById(R.id.edt_carname);
        edt_carbrandname = (EditText) rootView.findViewById(R.id.edt_carbrandname);
        tv_username = (TextView) rootView.findViewById(R.id.tv_username);
        btn_update = (Button) rootView.findViewById(R.id.btn_update);
        rg_question = (RadioGroup) rootView.findViewById(R.id.rg_Gender);
        iv_carphoto = (ImageView) rootView.findViewById(R.id.iv_carphoto);
        sp_Model = (Spinner) rootView.findViewById(R.id.sp_Model);
        iv_ProfileImage = (ImageView) rootView.findViewById(R.id.iv_ProfileImage);
        fl_pictuercontainer = (FrameLayout) rootView.findViewById(R.id.fl_pictuercontainer);
        iv_back = (ImageView) rootView.findViewById(R.id.iv_back);
        iv_back.setVisibility(View.VISIBLE);
        if (bundle != null) {
            if (bundle.containsKey("Name")) {
                String username = "" + bundle.getString("Name");
                tv_username.setText("" + bundle.getString("Name"));
                edt_drivername.setText("" + bundle.getString("Name"));
            }

        } else {
            Log.d(TAG, "The BUndel is nulll>>>>>>>>>>>>>>");
        }

        /*if (DataStore.getUserimagebitmap() != null) {
            iv_ProfileImage.setImageBitmap(DataStore.getUserimagebitmap());
            BitmapDrawable dr = new BitmapDrawable(DataStore.getUserimagebitmap());
            fl_pictuercontainer.setBackground(dr);
        } else {
            Log.d(TAG, "The Image BUndel is nulll>>>>>>>>>>>>>>");
        }*/

        yearspinner = new ArrayAdapter<String>(mActivity, R.layout.widget_spinner_item, totalyear) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 1) {
                    // Disable the second item from Spinner
                    return true;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                //tv.setTypeface(CommonUtil.getfont(mActivity, Constants.Helvetica_Neue_Medium));
                tv.setTextColor(Color.BLACK);
                return view;
            }

            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                // tv.setTypeface(CommonUtil.getfont(mActivity, Constants.Helvetica_Neue_Medium));
                tv.setTextColor(Color.BLACK);
                return view;
            }
        };
        yearspinner.setDropDownViewResource(R.layout.widget_spinner_item);
        sp_Model.setAdapter(yearspinner);
        sp_Model.setOnItemSelectedListener(new StateSpinnerItemClicked());

        btn_browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iscarphoto = true;
               // selectImage();
            }
        });


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*LauncherFragment fragment = new LauncherFragment();
                CommonUtil.switchFragment(mActivity, fragment, R.id.rl_MainContainer, TAG);*/
            }
        });
        rg_question.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radiobutton = (RadioButton) rootView.findViewById(checkedId);
                if (radiobutton != null) {
                    carid = radiobutton.getId();
                    cartype = (int) radiobutton.getTag();
                    // cartype=radiobutton.getId();
                    Log.d(TAG, cartype + ">>>>>>>>>>>>>>>>>" + radiobutton.getId());
                    /*if (radiobutton.getText().equals("Yiiri O")) {
                        cartype = 1;
                        Log.d(TAG, ">>>>>>>>>>>>first" + cartype);
                    } else if (radiobutton.getText().equals("Yiiri Suv")) {
                        cartype = 2;
                        Log.d(TAG, ">>>>>>>>>>>>scond" + cartype);
                    } else if (radiobutton.getText().equals("Yiiri Og")) {
                        cartype = 3;
                        Log.d(TAG, ">>>>>>>>>>>>thid" + cartype);
                    } else if (radiobutton.getText().equals("Yiiri Share")) {
                        cartype = 4;
                        Log.d(TAG, ">>>>>>>>>>>>thid" + cartype);
                    } else {
                        cartype = 1;
                        Log.d(TAG, ">>>>>>>>>>>>else" + cartype);
                    }*/
                }
            }
        });


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /*if (!CommonUtil.isHavingValue(edt_drivername.getText().toString())) {
                    Toast.makeText(mActivity, "Please enter driver name", Toast.LENGTH_SHORT).show();
                    return;
                }*/
                if (!isHavingValue(edt_vechicleno.getText().toString())) {
                    Toast.makeText(mActivity, "Please enter vechicle no", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cartype == -1) {
                    Toast.makeText(mActivity, "Please select car type", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isHavingValue(edt_carname.getText().toString())) {
                    Toast.makeText(mActivity, "Please enter car name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isHavingValue(edt_carbrandname.getText().toString())) {
                    Toast.makeText(mActivity, "Please enter car brand", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (yearselected == 0) {
                    Toast.makeText(mActivity, "Please select year of model", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!isHavingValue(edt_carowenedby.getText().toString())) {
                    Toast.makeText(mActivity, "Please enter owner name", Toast.LENGTH_SHORT).show();
                    return;
                }


                try {
                  //  CommonUtil.hideKeyBoard(mActivity);
                    profileobj = new JSONObject();

                    profileobj.put("user", "" + pref.getString("" + Constants.USER_ID, null));
                    profileobj.put("car_owner_name", "" + edt_carowenedby.getText().toString());
                    profileobj.put("car_number", "" + edt_vechicleno.getText().toString());
                    profileobj.put("car_name", "" + edt_carname.getText().toString());
                    profileobj.put("car_brand", "" + edt_carbrandname.getText().toString());
                    profileobj.put("year_of_mfg", "" + yearname);
                    if (carid != -1) {
                        profileobj.put("car_type", carid);
                    } else {
                        Log.d(TAG, ">>>>>>>>>>>>>oldcartype id>>>>>>>>>>>>>>" + oldcarid);
                        profileobj.put("car_type", oldcarid);
                    }

                    upddatedetailtoserver imageupload = new upddatedetailtoserver();
                    imageupload.execute("");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


        });
    }




    public class StateSpinnerItemClicked implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            if (pos == 0) {
                yearname = yearofmodel.get(pos + 1);
            } else {
                yearname = yearofmodel.get(pos);
            }
            yearselected = pos;


        }

        @Override
        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }



    public class upddatedetailtoserver extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
        protected String doInBackground(String... params) {
            String serverresponse = null;

            try {

                serverresponse = putDataToServer(Constants.UPdate_Cardeatails + "" + pref.getString("" + Constants.USER_CAR_ID, null), "", Constants.REQUEST_METHOD_POST, null, profileobj.toString(), pref.getString("" + Constants.ACCESS_TOKEN, null));
            } catch (Exception e) {
                e.printStackTrace();
                if (loadinprogress != null && loadinprogress.isShowing()) {
                    loadinprogress.dismiss();
                }
            }
            return serverresponse;
        }

        @Override
        protected void onPostExecute(String result) {

            if (isHavingValue(result)) {

                try {
                    editor.putString("" + Constants.USER_CAR_TYPE, "" + cartype);
                    editor.putString("" + Constants.CAR_TYPE, "" + cartype);
                    editor.commit();

                    /*imageuploadLoader upload = new imageuploadLoader();
                    upload.execute("" + pref.getString("" + Constants.USER_CAR_ID, ""));*/
                } catch (Exception e) {
                    if (loadinprogress != null && loadinprogress.isShowing()) {
                        loadinprogress.dismiss();
                    }
                    e.printStackTrace();
                }

            }
            if (loadinprogress != null && loadinprogress.isShowing()) {
                loadinprogress.dismiss();
            }
        }
    }






    /**
     * Webservice Calling PUT Method
     *
     * @param stringURL
     * @param contentType
     * @param requestMethod
     * @param headerValueMap -
     *                       returns response from given Webservice URL.
     */
    public static String putDataToServer(String stringURL, String contentType,
                                         String requestMethod, HashMap<String, String> headerValueMap,
                                         String json, String accesstoken) {

        InputStream inputStream = null;
        String response = "";
        if (stringURL == null || stringURL.equalsIgnoreCase("")) {
            return response;
        }
        try {
            //CommonUtil.log(TAG, "URL is  : " + stringURL);


            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // make POST request to the given URL
            HttpPut httpPut = new HttpPut(stringURL);

            // Set some headers to inform server about the type of the content
            httpPut.setHeader("Accept", "application/json");
            httpPut.setHeader("Content-type", "application/json");
            httpPut.setHeader("Authorization", "Bearer " + accesstoken);
            String mHeaderKey = null;
            String mHeaderVal = null;
            if (headerValueMap != null && headerValueMap.size() > 0) {
                Iterator it = headerValueMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry) it.next();
                    mHeaderKey = pairs.getKey().toString().trim();
                    mHeaderVal = pairs.getValue().toString().trim();
                    /*CommonUtil.log(TAG, pairs.getKey() + " = "
                            + pairs.getValue());*/
                    if (isHavingValue(mHeaderKey) && isHavingValue(mHeaderVal)) {
                        httpPut.setHeader(mHeaderKey, mHeaderVal);
                    }
                }
            }
            if(json!=null) {
              //  CommonUtil.log(TAG, "json is  : " + json);
                StringEntity entity = new StringEntity(json);
                httpPut.setEntity(entity);
            }



            // Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPut);

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
            if (Constants.DEBUG)
               // CommonUtil.log(TAG, "final response is  : " + response);
            br.close();
            dataIn.close();

        } catch (Exception e) {

        }
        return response;
    }




   /* private class fetchcartypes extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;

        @Override
        protected String doInBackground(final String... params) {

            String serverresponse = null;
            try {

                //    serverresponse = CommonUtil.getDataFromServer(Constants.Fetch_cartypes, Constants.REQUEST_METHOD_GET, null);
                if (currentcity != null && lcityid != null && lcityid.size() > 0) {

                    currentcityid = getindex(lcity, currentcity);
                    if (currentcityid != -1) {
                        //    serverresponse = CommonUtil.getDataFromServer(Constants.Fetch_cartypes + "?city_id=" + lcityid.get(currentcityid), Constants.REQUEST_METHOD_GET, null);
                    }

                }
                serverresponse = getDataFromServer(Constants.Fetch_cartypes , Constants.REQUEST_METHOD_GET, null);
                // serverresponse = CommonUtil.getDataFromServer(Constants.Fetch_cartypes + "?city_name=" , Constants.REQUEST_METHOD_GET, null);
                //serverresponse = CommonUtil.getDataFromServer(Constants.MASTERCARLIST, Constants.REQUEST_METHOD_GET, null);
            } catch (Exception e) {
                e.printStackTrace();
            }


            return serverresponse;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        @Override
        protected void onPostExecute(String result) {

            if (CommonUtil.isHavingValue(result)) {
                try {
                    Log.d(TAG, ">>>>>server response " + result);
                    JSONArray serverarrobj = new JSONArray(result.toString());
                    if (serverarrobj != null && serverarrobj.length() > 0) {
                        tv_errormessage.setVisibility(View.GONE);
                        rg_question.setVisibility(View.VISIBLE);
                        rg_question.removeAllViews();
                        if (cartypedata == null) {
                            cartypedata = new ArrayList<String>();
                        } else {
                            cartypedata.clear();
                        }
                        RadioButton radioButton;
                        RadioGroup.LayoutParams rprms;
                        for (int index = 0; index < serverarrobj.length(); index++) {
                            cartypescount = serverarrobj.length();
                            JSONObject serverobj = new JSONObject("" + serverarrobj.getString(index));
                            radioButton = new RadioButton(mActivity);

                            *//*radioButton.setText(serverobj.getString("car_type_name"));
                            radioButton.setId(serverobj.getInt("id"));
                            radioButton.setTag(serverobj.getInt("id"));
                            if (oldcarid != null && oldcarid.equals("" + serverobj.getInt("id"))) {
                                Log.d(TAG, serverobj.getInt("id") + ">>>>>>>>>>>>Matcher>>>>>>>>>>>>>>" + oldcarid + " >>>>carytpe" + serverobj.getString("id"));

                                radioButton.setChecked(true);
                            }*//*

                         *//*   if (serverobj.getString("car_type").equals("1")) {
                                radioButton.setText("Yiiri O");
                                radioButton.setId(serverobj.getInt("id"));
                                radioButton.setTag(serverobj.getInt("car_type"));
                                if (oldcarid != null && oldcarid.equals("" + serverobj.getInt("id"))) {
                                    Log.d(TAG, serverobj.getInt("id") + ">>>>>>>>>>>>Matcher>>>>>>>>>>>>>>" + oldcarid + " >>>>carytpe" + serverobj.getString("car_type"));

                                    radioButton.setChecked(true);
                                }

                            }
                            if (serverobj.getString("car_type").equalsIgnoreCase("2")) {
                                radioButton.setText("Yiiri Suv");
                                radioButton.setId(serverobj.getInt("id"));
                                radioButton.setTag(serverobj.getInt("car_type"));
                                if (oldcarid != null && oldcarid.equals("" + serverobj.getInt("id")))

                                {
                                    Log.d(TAG, serverobj.getInt("id") + ">>>>>>>>>>>>Matcher>>>>>>>>>>>>>>" + oldcarid + " >>>>carytpe" + serverobj.getString("car_type"));

                                    radioButton.setChecked(true);
                                }
                            }
                            if (serverobj.getString("car_type").equalsIgnoreCase("3")) {
                                radioButton.setText("Yiiri Og");
                                radioButton.setId(serverobj.getInt("id"));
                                radioButton.setTag(serverobj.getInt("car_type"));
                                if (oldcarid != null && oldcarid.equalsIgnoreCase("" + serverobj.getInt("id")))

                                {
                                    Log.d(TAG, serverobj.getInt("id") + ">>>>>>>>>>>>Matcher>>>>>>>>>>>>>>" + oldcarid + " >>>>carytpe" + serverobj.getString("car_type"));

                                    //oldcarid=""+(int)serverobj.getInt("id");
                                    radioButton.setChecked(true);
                                }

                            }*//*


                            //JSONObject cartypeobj = new JSONObject(serverobj.getString("car_type").toString());
                            //if (cartypeobj.getString("car_type").equals("1")) {
                            radioButton.setText("" + serverobj.getString("car_type_name"));
                            radioButton.setId(serverobj.getInt("id"));
                            radioButton.setTag(serverobj.getInt("id"));
                            if (oldcarid != null && oldcarid.equals("" + serverobj.getInt("id"))) {
                                Log.d(TAG, serverobj.getInt("id") + ">>>>>>>>>>>>Matcher>>>>>>>>>>>>>>" + oldcarid + " >>>>carytpe" + serverobj.getString("id"));
                                radioButton.setChecked(true);
                            }
                            // }


                            rprms = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            rg_question.addView(radioButton, rprms);

                        }


                       *//* Getcabdetails getcabdetails = new Getcabdetails();
                        getcabdetails.execute("" + DataStore.getCurrentpickupCity());*//*
                        // if(cabstypeid!=null&&cabstypeid.size()>0) {

                        // }


                    } else {
                        tv_errormessage.setVisibility(View.VISIBLE);
                        rg_question.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }*/




    /**
     * Webservice Calling GET Method
     *
     * @param stringURL
     * @param requestMethod
     * @param headerValueMap -
     *                       returns response from given Webservice URL.
     */
    public static String getDataFromServer(String stringURL,
                                           String requestMethod, HashMap<String, String> headerValueMap) {
        InputStream inputStream = null;
        String response = "";
        if (stringURL == null || stringURL.equalsIgnoreCase("")) {
            return response;
        }
        try {

           // CommonUtil.log(TAG, " <URL> " + stringURL);
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // make POST request to the given URL
            HttpGet httpGet = new HttpGet(stringURL);

            // Set some headers to inform server about the type of the content
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Content-type", "application/json");
            String mHeaderKey = null;
            String mHeaderVal = null;
            if (headerValueMap != null && headerValueMap.size() > 0) {
                Iterator it = headerValueMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry) it.next();
                    mHeaderKey = pairs.getKey().toString().trim();
                    mHeaderVal = pairs.getValue().toString().trim();
                    //CommonUtil.log(TAG, pairs.getKey() + " == " + pairs.getValue());
                    if (isHavingValue(mHeaderKey) && isHavingValue(mHeaderVal)) {
                        httpGet.setHeader(mHeaderKey, mHeaderVal);
                    }
                    // it.remove(); // to avoids a
                    // ConcurrentModificationException
                }
            }
            // Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpGet);

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            String res = null;
            // convert inputstream to string
            if (inputStream != null) {
                res = convertInputStreamToString(inputStream);
            } else {
                res = "";
            }
            response = res;
            inputStream.close();

        } catch (Exception e) {

        }
        return response;
    }




    /**
     * Convert InputStream To String
     *
     * @param inputStream -
     *                    returns response string.
     */
    public static String convertInputStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(
                    inputStream));
            String line = "";
            String result = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;

           // CommonUtil.log(TAG, "Input Stream to String : " + result);
            return result;
        } catch (IllegalStateException e) {

        } catch (IOException e) {

        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {

            }
        }

        return null;

    }
}
