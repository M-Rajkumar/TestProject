package com.example.ids.testapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtill {

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
}
