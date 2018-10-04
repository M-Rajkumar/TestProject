package com.example.ids.testapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.ids.testapp.LoginActivity.DEBUG;

public class CommonUtil {

    private static final String TAG = "CommonUtil";

    public static boolean isHavingValue(String response) {
        if (response != null && !response.equalsIgnoreCase("")) {
            return true;
        }
        return false;
    }

    public static boolean isValidPassword(String password) {
        boolean sReturnValue = false;
        try {
            String special = "!@#$%^&*()_";
            String pattern = ".*[" + Pattern.quote(special) + "].*";
            if (password.matches(pattern)) {
                sReturnValue = true;
            } else {
                sReturnValue = false;
            }

        } catch (Exception e) {

        }
        return sReturnValue;
    }

    public static boolean isValidEmail(String emailID) {
        try {
            Pattern pattern;
            Matcher matcher;
            String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            pattern = Pattern.compile(EMAIL_PATTERN);
            matcher = pattern.matcher(emailID);
            return matcher.matches();
        } catch (Exception e) {

        }
        return false;
    }


    public static boolean isConnectedToInternet(Context _context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) _context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }

            }
        } catch (Exception e) {

        }
        return false;
    }

    public static AlertDialog.Builder showNoNetworkDialog(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog
                .setMessage("Please check your network connection!!!")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close
                                ((Activity) context)
                                        .startActivity(new Intent(
                                                Settings.ACTION_SETTINGS));

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // show it
        alertDialog.show();

        return alertDialog;

    }

    public static void switchFragment(FragmentActivity mActivity, Fragment fr,
                                      int containerViewID, String tagName) {
        if (fr == null && mActivity == null) {
            return;
        }

        FragmentManager fm = mActivity.getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        transaction.replace(containerViewID, fr, tagName);
        transaction.addToBackStack(null);
        transaction.commit();

    }


    /**
     * Post a Log.
     *
     * @param tag
     * @param string
     */
    public static void log(String tag, String string) {
        if (Constants.DEBUG) {
            Log.d(tag, string);
        }
    }


    /**
     * Shows a Toast Message in the Activity.
     *
     * @param mActivity
     * @param message
     */
    public static void showToastMessage(final Activity mActivity,
                                        final String message) {
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast toast = Toast.makeText(mActivity.getApplicationContext(), message,
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            });
        }
    }


    public static String postDataToServer(String stringURL, String contentType, String requestMethod,
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


    /**
     * Show alert dialog To for Error Message.
     *
     * @param message
     * @param context
     * @return
     */
    public static AlertDialog.Builder showDialog(String neutralButtonText, String message,
                                                 final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog
                .setMessage(message)
                .setCancelable(false)
                .setNeutralButton(neutralButtonText,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        alertDialog.show();
        return alertDialog;
    }
}
