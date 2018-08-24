package com.example.ids.testapp;

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
}
