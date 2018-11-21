package com.example.ids.testapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.example.ids.testapp.CommonUtil.isHavingValue;
import static com.example.ids.testapp.Fragment.ViewVehicleFragment.convertInputStreamToString;

public class ContactActivity extends Activity {

    Activity mActivity;
    public static ProgressDialog loadinprogress;
    public static String TAG = ContactActivity.class.getSimpleName();
    private static String url = "https://api.androidhive.info/contacts/";

    ArrayList<HashMap<String, String>> contactList;
    ListView lv;



    ArrayList<ContactsModel>alcontactsModels;

    ContactsModel contactsModel;

    ContactListAdapter contactListAdapter;

    RecyclerView rv_contact;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        mActivity = this;
        contactList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);
        rv_contact = (RecyclerView) findViewById(R.id.rv_contact);
        LinearLayoutManager linearLayoutManagr = new LinearLayoutManager(mActivity.getApplicationContext());
        linearLayoutManagr.setOrientation(LinearLayoutManager.VERTICAL);
        rv_contact.setLayoutManager(linearLayoutManagr);

        Fetchmydata fetchmydata = new Fetchmydata();
        fetchmydata.execute("");
    }


    public class Fetchdata extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            loadinprogress = new ProgressDialog(ContactActivity.this);
            loadinprogress.setMessage("Please wait...");
            loadinprogress.setCancelable(false);
            loadinprogress.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();

            String jsonstr = sh.makeServiceCall(url);

            Log.e(TAG, "Responce from url>>>>>>>>>> " + jsonstr);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            Log.d(TAG, ">>>>>>>>>>>>>>>>>>>Resulst Null " + result);
            /*if (CommonUtil.isHavingValue(result)) {
                try {
                    JSONArray jsarr = new JSONArray(result);
                    JSONObject fetchdataobj = new JSONObject(jsarr.get(0).toString());

                    if (fetchdataobj.has("bank_name") && !fetchdataobj.getString("bank_name").toString().equalsIgnoreCase("null"))
                        edt_BankName.setText("" + fetchdataobj.getString("bank_name").toString());

                    if (fetchdataobj.has("account_no") && !fetchdataobj.getString("account_no").toString().equalsIgnoreCase("null"))
                        edt_AccountNo.setText("" + fetchdataobj.getString("account_no").toString());

                    if (fetchdataobj.has("aba_routing_no") && !fetchdataobj.getString("aba_routing_no").toString().equalsIgnoreCase("null"))
                        edt_ABA.setText("" + fetchdataobj.getString("aba_routing_no").toString());


                } catch (JSONException e) {
                    if (loadinprogress != null && loadinprogress.isShowing()) {
                        loadinprogress.dismiss();
                    }
                    e.printStackTrace();
                }

                if (loadinprogress != null && loadinprogress.isShowing()) {
                    loadinprogress.dismiss();
                }


            }*/

        }
    }


    public class Fetchmydata extends AsyncTask<String, String, String> {
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
            String serverresponse = null;

            try {
                serverresponse = getDataFromServer(url, Constants.REQUEST_METHOD_GET, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return serverresponse;
        }

        @Override
        protected void onPostExecute(String result) {

            if(alcontactsModels ==null){
                alcontactsModels = new ArrayList<ContactsModel>();
            } else {
               //alcontactsModels.clear();
            }



            Log.d(TAG, ">>>>>>>>>>>>>>>>>>>Resulst Null " + result);

                JSONObject contactsModelobj;
                if (CommonUtil.isHavingValue(result)) {


                    try {

                        JSONObject jsobj = new JSONObject(result);

                        JSONArray conarray = jsobj.getJSONArray("contacts");


                        if (conarray != null && conarray.length() > 0) {

                            for (int i = 0; i < conarray.length(); i++) {

                                contactsModel = new ContactsModel();

                                contactsModelobj = new JSONObject(conarray.get(i).toString());

                                contactsModel.setContactid(contactsModelobj.getString("id"));
                                contactsModel.setContactname(contactsModelobj.getString("name"));
                                contactsModel.setContactemail(contactsModelobj.getString("email"));
                                contactsModel.setContactmobile(contactsModelobj.getString("address"));

                                // Add the Data's into the models.

                                alcontactsModels.add(contactsModel);
                            }

                            contactListAdapter = new ContactListAdapter(mActivity, alcontactsModels);
                            rv_contact.setAdapter(contactListAdapter);

                        } else {

                        }


                    } catch (JSONException e) {
                        if (loadinprogress != null && loadinprogress.isShowing()) {
                            loadinprogress.dismiss();
                        }
                        e.printStackTrace();
                    }

                    if (loadinprogress != null && loadinprogress.isShowing()) {
                        loadinprogress.dismiss();
                    }


                }

            }

    }


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


    /*Get Data from server with token*/
    public static String getDataFromServerwithtoken(String stringURL, HashMap<String, String> headerValueMap, String accesstoken) {
        InputStream inputStream = null;
        String response = "";
        if (stringURL == null || stringURL.equalsIgnoreCase("")) {
            return response;
        }
        try {

            CommonUtil.log(TAG, " <URL> " + stringURL);
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // make POST request to the given URL
            HttpGet httpGet = new HttpGet(stringURL);

            // Set some headers to inform server about the type of the content
            Log.d(TAG, "Authorization Bearer " + accesstoken);
            httpGet.setHeader("Authorization", "Bearer " + accesstoken);
            String mHeaderKey = null;
            String mHeaderVal = null;
            if (headerValueMap != null && headerValueMap.size() > 0) {
                Iterator it = headerValueMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry) it.next();
                    mHeaderKey = pairs.getKey().toString().trim();
                    mHeaderVal = pairs.getValue().toString().trim();
                    CommonUtil.log(TAG, pairs.getKey() + " == " + pairs.getValue());
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
            CommonUtil.log(TAG, " the fnal response " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

}
