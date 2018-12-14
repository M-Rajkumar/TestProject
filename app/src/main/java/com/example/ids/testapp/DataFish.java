package com.example.ids.testapp;

public class DataFish {

    public String fishImage;
    public String fishName;
    public String catName;
    public String sizeName;
    public int price;
    
    
    
    // neee
    
    
    package us.adminsys.recharge;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import AsynTasks.DoAcitvateSim;
import AsynTasks.DoRechargeNowAndSendDataToServer;
import AsynTasks.GetCarrierInforByMobileNumberInterNational;
import AsynTasks.GetCarrierInforByMobileNumberUSRecharge;
import AsynTasks.GetCarrierListByCountryCodeFromSKUs;
import AsynTasks.GetSKUList;
import beans.CarriersData;
import beans.Countrybean;
import beans.FinalTransactionbean;
import beans.Loginbean;
import beans.SKUProductbean;
import beans.SaveData;
import customviews.CustomTextview;
import database.CellphoneDatabasehelper;
import utility.CellphoneCommonMethods;
import utility.CommonKeywords;
import utility.ConnectionDetector;
import utility.MyAlarmReceiver;
import utility.MyCommonSession;
import utility.MyJSONParser;
import utility.Urls;

import static com.paypal.android.sdk.bC.k;
import static utility.Urls.GETPRODUCTLIST;
import static utility.Urls.GETZIPCODE;

public class CellphoneHomeactivity extends BaseActivity {
    // PayPalPayment thingToBuy;
    private static final String PREF_NAME = "sample_twitter_pref";
    private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    private static final String PREF_USER_NAME = "twitter_user_name";
    /**
     * - Set to PaymentActivity.ENVIRONMENT_PRODUCTION to move real money.
     * <p/>
     * - Set to PaymentActivity.ENVIRONMENT_SANDBOX to use your test credentials
     * from https://developer.paypal.com
     * <p/>
     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
     * without communicating to PayPal's servers.
     */
    //private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;
    private static final String TAG = "paymentExample";
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;
    // note that these credentials will differ between live & sandbox environments.
    //private static final String CONFIG_CLIENT_ID = "AWPhFbDj3HE0-bm49Uom9vTtEPq4odnA_ZfF6ylPti4sX8copBKjaykqYiRtu5f1FFYyC6YJTjtpOOj_";
    private static final String PAYPAL_LIVE_CLEINT_ID = "ASW9kM4DqVj7N2bd3SClMfFoRezZghM615kHi6um_24Sv_o-nNUyl9gi3Qk2Cf-5kpmNlpOdaxy-9s8j";
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;
    public static EditText editText_amount = null;
    public static TextView tvActivationPlan;
    public static String recharge_type = CommonKeywords.RECHARGE_ONCE;
    public static SKUProductbean skuProductbean_selected = null;
    public static int lenghtofInternationCountyPhoneNumber = 0;
    public static CellphoneHomeactivity cellphoneHomeactivity = null;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(PAYPAL_LIVE_CLEINT_ID)
            // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("Example Merchant")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal")).acceptCreditCards(false);
    Button button_rechargeNew = null;
    EditText editText_us_phonenumber = null, editText_international_phonenumber = null;
    AlertDialog.Builder builder;

    ArrayList<Countrybean> arrayListCountrybeanArrayList = new ArrayList<>();

    Button button_logout = null;
    RadioGroup radioGroup_recharge = null;
    RadioButton radioButton_once = null, radioButton_repetive = null;
    MyCommonSession myCommonSession = null;
    ProgressBar progressBar_carrier = null, progressBar_carrier_international_recharge = null, progressBar_activate = null;
    EditText editText_country_US_code = null, editText_country_international_code = null;

    ///Button button_home = null, button_myaccount = null, button_commissions = null;
    Button button_us_recharge = null, button_internation_recharge = null;
    int recharge_flag = 1;
    TextView textView_no_order_found;
    View view_recharge = null;
    LayoutInflater layoutInflater = null;
    CellphoneCommonMethods cellphoneCommonMethods = null;
    LinearLayout linearLayout_us_recharge_phonenumber = null, linearLayout_international_recharge_number = null;
    String phonenumber = null, amount = null, selected_carrier = null, country_code = null;
    LinearLayout linearLayout_home = null, linearLayout_myaccount = null, linearLayout_commissions = null;
    LinearLayout linearLayout_coutries = null, linearLayout_carriers = null, linearLayout_plans = null;
    //9537667073
    RelativeLayout relativeLayout_countries = null, relativeLayout_carriers = null, relativeLayout_plans = null;
    ImageView imageview_countries = null, imageview_carriers = null, imageview_plans = null;
    TextView textview_countries = null, textview_carriers = null, textview_plans = null;
    AlertDialog alertDialog_countries = null, alertDialog_carriers = null, alertDialog_plans = null;
    boolean firsttimeClickedInternationRecharge = false;
    ConnectionDetector cd;
    CellphoneDatabasehelper cellphoneDatabasehelper = null;
    ConnectionDetector connectionDetector = null;
    String extraFees = "0";
    boolean isRechargeAmonuntValid = false;
    Button button_home = null, button_myaccount = null, button_commissions = null;
    Button btnSimActivation, btnActivate, btnMore;
    LinearLayout linSimActivation;
    TextView textView, tvNumberPlan, tvAmount;
    RelativeLayout relActivationPlan, relNumberPlan;
    EditText edtSimNumberOne, edtSimNumberTwo, edtSimNumberThree, edtZipCode;
    public static String skuId, carrierName;
    private Bundle bundle;

    public String Store_Zipcode;

    public static String[] carriers_desc, carriers_id, product_info;
    public static ArrayList<String> Description;

    public static MyJSONParser jparse = new MyJSONParser();
    public static String myCarrierID;

    public String response1, response2;

    ArrayList<SaveData> allistdata;
    SaveData saveData;

    ArrayList<CarriersData> alcarriersData;

    CarriersData carriersData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            bundle = getIntent().getExtras();
            if (bundle == null) {
                bundle = new Bundle();
            }

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            cellphoneHomeactivity = this;
            cellphoneDatabasehelper = new CellphoneDatabasehelper(CellphoneHomeactivity.this);
            layoutInflater = getLayoutInflater();
            //FinalTransactionbean finalTransactionbean=cellphoneDatabasehelper.getTransactionByRawID(3);
            connectionDetector = new ConnectionDetector(CellphoneHomeactivity.this);
            instantiation();
            findViewByIDs();
            setUpEvents();
            setUpContriesList();
            if (bundle.containsKey("FromMenu")) {
                if (bundle.getBoolean("FromMenu")) {
                    imageView_back.setVisibility(View.VISIBLE);
                    textView_header_title.setText(getResources().getString(R.string.INTERNATIONAL_REFILL));
                    button_internation_recharge.setVisibility(View.GONE);
                    btnMore.setVisibility(View.GONE);
                    btnSimActivation.setVisibility(View.GONE);
                    button_us_recharge.setVisibility(View.GONE);

                    if (connectionDetector.isConnectingToInternet()) {
                        linearLayout_international_recharge_number.setVisibility(View.VISIBLE);
                        linearLayout_us_recharge_phonenumber.setVisibility(View.GONE);
                        linSimActivation.setVisibility(View.GONE);
                        recharge_flag = 2;
                        linearLayout_carriers.setVisibility(View.GONE);
                        linearLayout_plans.setVisibility(View.GONE);
                        linearLayout_coutries.setVisibility(View.VISIBLE);
                        editText_amount.setVisibility(View.VISIBLE);
                        editText_amount.setHint(getResources().getString(R.string.enter_amount));
                        editText_international_phonenumber.setText("");
                        editText_country_international_code.setText("");
                        editText_country_international_code.setVisibility(View.GONE);
                        button_internation_recharge.setBackgroundColor(getResources().getColor(R.color.new_orgage));
                        button_internation_recharge.setTextColor(getResources().getColor(R.color.white));
                        btnMore.setBackground(getResources().getDrawable(R.drawable.button_bg));
                        btnMore.setTextColor(getResources().getColor(R.color.new_orgage));
                        btnSimActivation.setBackground(getResources().getDrawable(R.drawable.button_bg));
                        btnSimActivation.setTextColor(getResources().getColor(R.color.new_orgage));

                        button_us_recharge.setBackground(getResources().getDrawable(R.drawable.button_bg));
                        button_us_recharge.setTextColor(getResources().getColor(R.color.new_orgage));
                        button_rechargeNew.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.VISIBLE);

                        Countrybean countrybean = null;

                        for (int i = 1; i < arrayListCountrybeanArrayList.size(); i++) {
                            countrybean = null;
                            countrybean = arrayListCountrybeanArrayList.get(i);
                            if (countrybean.getCountryName().equals(textview_countries.getText().toString())) {
                                CellphoneHomeactivity.lenghtofInternationCountyPhoneNumber = countrybean.getPhonenumberlenght();
                                break;
                            }
                        }

//                        new GetCarrierListByCountryCodeFromSKUs(relativeLayout_carriers, relativeLayout_plans,
//                                null, CellphoneHomeactivity.this, textview_carriers, textview_plans,
//                                linearLayout_carriers, linearLayout_plans,
//                                editText_country_international_code).execute(countrybean.getCountryCode());


                    } else {
                        cellphoneCommonMethods.customToastMessage(R.string.no_internet_connection, CellphoneHomeactivity.this);
                    }
                }
            }
            if (cd.isConnectingToInternet()) {
                if (myCommonSession.getSKULocalStorageFlag()) {
//                    new GetSKUList(2, editText_country_international_code, CellphoneHomeactivity.this,
//                            linearLayout_carriers, linearLayout_plans, relativeLayout_countries,
//                            relativeLayout_carriers, relativeLayout_plans, textview_countries,
//                            textview_carriers, textview_plans, arrayListCountrybeanArrayList,
//                            relActivationPlan, tvActivationPlan, tvAmount).execute();
                } else {
//                    new GetSKUList(1, editText_country_international_code, CellphoneHomeactivity.this,
//                            linearLayout_carriers, linearLayout_plans, relativeLayout_countries,
//                            relativeLayout_carriers, relativeLayout_plans, textview_countries,
//                            textview_carriers, textview_plans, arrayListCountrybeanArrayList,
//                            relActivationPlan, tvActivationPlan, tvAmount).execute();
                }
                scheduleAlarm();
            } else {
                //new PomodroidException(getResources().getString(R.string.no_internet_connection), CellphoneHomeactivity.this, false, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Setup a recurring alarm every half hour
    public void scheduleAlarm() {
        try {
            // Construct an intent that will execute the AlarmReceiver
            Intent intent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
            // Create a PendingIntent to be triggered when the alarm goes off
            final PendingIntent pIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            // Setup periodic alarm every 5 seconds
            long firstMillis = System.currentTimeMillis(); // alarm is set right away
            AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
            // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, 600000, AlarmManager.INTERVAL_DAY * 7, pIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try {
            phonenumber = savedInstanceState.getString("mobile");
            amount = savedInstanceState.getString("amount");
            selected_carrier = savedInstanceState.getString("carrier");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            if (recharge_flag == 1) {
                phonenumber = editText_us_phonenumber.getText().toString();
                country_code = editText_country_US_code.getText().toString();
                country_code = country_code.substring(1);
                phonenumber = country_code + phonenumber;
            } else if (recharge_flag == 2) {
                phonenumber = editText_international_phonenumber.getText().toString();
                country_code = editText_country_international_code.getText().toString();
                country_code = country_code.substring(1);
                phonenumber = country_code + phonenumber;
            }

            outState.putString("mobile", phonenumber);
            outState.putString("amount", editText_amount.getText().toString());
            outState.putString("carrier", textview_carriers.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void instantiation() {
        try {
            cellphoneCommonMethods = new CellphoneCommonMethods(CellphoneHomeactivity.this);
            myCommonSession = new MyCommonSession(CellphoneHomeactivity.this);
            cd = new ConnectionDetector(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findViewByIDs() {
        try {
            imageView_back.setVisibility(View.GONE);
            view_recharge = layoutInflater.inflate(R.layout.recharge, null);
            //  imageButton_home.setVisibility(View.GONE);
            btnMore = (Button) view_recharge.findViewById(R.id.btnMore);
            btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(linearLayout_left_menu_parent);
                }
            });
            button_home = (Button) view_recharge.findViewById(R.id.button_home);
            button_myaccount = (Button) view_recharge.findViewById(R.id.button_my_account);
            button_commissions = (Button) view_recharge.findViewById(R.id.button_commissions);

            linearLayout_home = (LinearLayout) view_recharge.findViewById(R.id.linerlayout_recharge_home);
            linearLayout_commissions = (LinearLayout) view_recharge.findViewById(R.id.linerlayout_commissions);
            linearLayout_myaccount = (LinearLayout) view_recharge.findViewById(R.id.linerlayout_my_account);

            linearLayout_coutries = (LinearLayout) view_recharge.findViewById(R.id.linerlayout_for_countries_spinner);
            linearLayout_carriers = (LinearLayout) view_recharge.findViewById(R.id.linerlayout_for_carrier_spinner);
            linearLayout_plans = (LinearLayout) view_recharge.findViewById(R.id.linerlayout_for_plans_spinner);

            linearLayout_international_recharge_number = (LinearLayout) view_recharge.findViewById(R.id.linerlayout_internation_phone_number);
            linearLayout_us_recharge_phonenumber = (LinearLayout) view_recharge.findViewById(R.id.linerlayout_fo_us_recharge);
            linSimActivation = (LinearLayout) view_recharge.findViewById(R.id.linSimActivation);

            button_us_recharge = (Button) view_recharge.findViewById(R.id.button_us_recharge);
            button_internation_recharge = (Button) view_recharge.findViewById(R.id.button_internation_recharge);
            btnSimActivation = (Button) view_recharge.findViewById(R.id.btnSimActivation);

            button_rechargeNew = (Button) view_recharge.findViewById(R.id.btn_recharge);
            editText_amount = (EditText) view_recharge.findViewById(R.id.edittext_amount);
            button_logout = (Button) view_recharge.findViewById(R.id.btn_logout);
            radioGroup_recharge = (RadioGroup) view_recharge.findViewById(R.id.radioGroup_recharge);
            radioButton_once = (RadioButton) view_recharge.findViewById(R.id.radiobutton_once);
            radioButton_repetive = (RadioButton) view_recharge.findViewById(R.id.radioButton_repetive);

            progressBar_carrier = (ProgressBar) view_recharge.findViewById(R.id.progressbar_spinner_carrier);
            progressBar_carrier_international_recharge = (ProgressBar) view_recharge.findViewById(R.id.progressbar_spinner_carrier_internation_recharge);

            editText_country_US_code = (EditText) view_recharge.findViewById(R.id.edittext_contry_code);
            editText_country_international_code = (EditText) view_recharge.findViewById(R.id.edittext_internation_phone_number_contry_code);

            editText_us_phonenumber = (EditText) view_recharge.findViewById(R.id.edittext_us_phone_number);
            editText_international_phonenumber = (EditText) view_recharge.findViewById(R.id.edittext_internation_phone_number);

            button_us_recharge.setBackgroundColor(getResources().getColor(R.color.new_orgage));
            button_us_recharge.setTextColor(getResources().getColor(R.color.white));

            relativeLayout_countries = (RelativeLayout) view_recharge.findViewById(R.id.relativelayout_button_countries);
            relativeLayout_carriers = (RelativeLayout) view_recharge.findViewById(R.id.relativelayout_button_select_carriers);
            relActivationPlan = (RelativeLayout) view_recharge.findViewById(R.id.relActivationPlan);
            relativeLayout_plans = (RelativeLayout) view_recharge.findViewById(R.id.relativelayout_button_select_plans);

            tvActivationPlan = (TextView) view_recharge.findViewById(R.id.tvActivationPlan);
            tvNumberPlan = (TextView) view_recharge.findViewById(R.id.tvNumberPlan);
            tvAmount = (TextView) view_recharge.findViewById(R.id.tvAmount);
            btnActivate = (Button) view_recharge.findViewById(R.id.btnActivate);

            builder = new AlertDialog.Builder(this);
            ArrayList<String> arrayListMonths = new ArrayList<>();
            arrayListMonths.add("1 month");
            final CharSequence[] items = arrayListMonths.toArray(new String[arrayListMonths.size()]);
            tvNumberPlan.setText(items[0]);
            builder.setTitle(getResources().getString(R.string.make_your_selection));
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    dialog.dismiss();
                    tvNumberPlan.setText(items[item]);
                }
            });

            relNumberPlan = (RelativeLayout) view_recharge.findViewById(R.id.relNumberPlan);
            relNumberPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.show();
                }
            });

            textview_countries = (TextView) view_recharge.findViewById(R.id.textview_select_countries);
            textview_carriers = (TextView) view_recharge.findViewById(R.id.textview_select_carriers);
            textview_plans = (TextView) view_recharge.findViewById(R.id.textview_select_plans);
            textView = (TextView) view_recharge.findViewById(R.id.textView);

            imageview_countries = (ImageView) view_recharge.findViewById(R.id.imageview_select_countries);
            imageview_carriers = (ImageView) view_recharge.findViewById(R.id.imageview_select_carriers);
            imageview_plans = (ImageView) view_recharge.findViewById(R.id.imageview_select_plans);

            button_internation_recharge.setBackground(getResources().getDrawable(R.drawable.button_bg));
            button_internation_recharge.setTextColor(getResources().getColor(R.color.new_orgage));

            btnSimActivation.setBackground(getResources().getDrawable(R.drawable.button_bg));
            btnSimActivation.setTextColor(getResources().getColor(R.color.new_orgage));

            edtZipCode = (EditText) view_recharge.findViewById(R.id.edtZipCode);


            edtZipCode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 3) {
                        Store_Zipcode = edtZipCode.getText().toString();

                        getZipcode();
                        Log.d(" checking zip >", "" + Store_Zipcode);
                        Log.d(" checking zip >", "" + s);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            edtSimNumberOne = (EditText) view_recharge.findViewById(R.id.edtSimNumberOne);
            edtSimNumberOne.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 5) {
                        edtSimNumberTwo.requestFocus();


                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            edtSimNumberTwo = (EditText) view_recharge.findViewById(R.id.edtSimNumberTwo);
            edtSimNumberTwo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 5) {
                        edtSimNumberThree.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            edtSimNumberThree = (EditText) view_recharge.findViewById(R.id.edtSimNumberThree);


            linearLayout_home.setVisibility(View.VISIBLE);

            button_us_recharge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    linearLayout_international_recharge_number.setVisibility(View.GONE);
                    linearLayout_us_recharge_phonenumber.setVisibility(View.VISIBLE);
                    linSimActivation.setVisibility(View.GONE);
                    recharge_flag = 1;
                    editText_amount.setVisibility(View.VISIBLE);
                    editText_amount.setHint(getResources().getString(R.string.enter_amount));
                    editText_us_phonenumber.setText("");
                    editText_country_US_code.setText("+1");
                    btnMore.setBackground(getResources().getDrawable(R.drawable.button_bg));
                    btnMore.setTextColor(getResources().getColor(R.color.new_orgage));
                    editText_country_US_code.setVisibility(View.VISIBLE);
                    button_us_recharge.setBackgroundColor(getResources().getColor(R.color.new_orgage));
                    button_us_recharge.setTextColor(getResources().getColor(R.color.white));
                    button_internation_recharge.setBackground(getResources().getDrawable(R.drawable.button_bg));
                    button_internation_recharge.setTextColor(getResources().getColor(R.color.new_orgage));
                    btnSimActivation.setBackground(getResources().getDrawable(R.drawable.button_bg));
                    btnSimActivation.setTextColor(getResources().getColor(R.color.new_orgage));
//                    new GetCarrierListByCountryCodeFromSKUs(relativeLayout_carriers, relativeLayout_plans, null,
//                            CellphoneHomeactivity.this, textview_carriers, textview_plans, linearLayout_carriers,
//                            linearLayout_plans, editText_country_international_code).execute("US");
                    linearLayout_carriers.setVisibility(View.VISIBLE);
                    linearLayout_plans.setVisibility(View.VISIBLE);
                    linearLayout_coutries.setVisibility(View.GONE);
                    button_rechargeNew.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                }
            });

            button_internation_recharge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (connectionDetector.isConnectingToInternet()) {
                        linearLayout_international_recharge_number.setVisibility(View.VISIBLE);
                        linearLayout_us_recharge_phonenumber.setVisibility(View.GONE);
                        linSimActivation.setVisibility(View.GONE);
                        recharge_flag = 2;
                        linearLayout_carriers.setVisibility(View.VISIBLE);
                        linearLayout_plans.setVisibility(View.GONE);
                        linearLayout_coutries.setVisibility(View.VISIBLE);
                        editText_amount.setVisibility(View.VISIBLE);
                        editText_amount.setHint(getResources().getString(R.string.enter_amount));
                        editText_international_phonenumber.setText("");
                        editText_country_international_code.setText("");
                        editText_country_international_code.setVisibility(View.GONE);
                        button_internation_recharge.setBackgroundColor(getResources().getColor(R.color.new_orgage));
                        button_internation_recharge.setTextColor(getResources().getColor(R.color.white));
                        btnMore.setBackground(getResources().getDrawable(R.drawable.button_bg));
                        btnMore.setTextColor(getResources().getColor(R.color.new_orgage));
                        btnSimActivation.setBackground(getResources().getDrawable(R.drawable.button_bg));
                        btnSimActivation.setTextColor(getResources().getColor(R.color.new_orgage));

                        button_us_recharge.setBackground(getResources().getDrawable(R.drawable.button_bg));
                        button_us_recharge.setTextColor(getResources().getColor(R.color.new_orgage));
                        button_rechargeNew.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.VISIBLE);

                        Countrybean countrybean = null;

                        for (int i = 1; i < arrayListCountrybeanArrayList.size(); i++) {
                            countrybean = null;
                            countrybean = arrayListCountrybeanArrayList.get(i);
                            if (countrybean.getCountryName().equals(textview_countries.getText().toString())) {
                                CellphoneHomeactivity.lenghtofInternationCountyPhoneNumber = countrybean.getPhonenumberlenght();
                                break;
                            }
                        }

//                        new GetCarrierListByCountryCodeFromSKUs(relativeLayout_carriers, relativeLayout_plans,
//                                null, CellphoneHomeactivity.this, textview_carriers, textview_plans,
//                                linearLayout_carriers, linearLayout_plans,
//                                editText_country_international_code).execute(countrybean.getCountryCode());

                    } else {
                        cellphoneCommonMethods.customToastMessage(R.string.no_internet_connection, CellphoneHomeactivity.this);
                    }
                }
            });

            btnSimActivation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recharge_flag = 3;
                    button_internation_recharge.setBackground(getResources().getDrawable(R.drawable.button_bg));
                    button_internation_recharge.setTextColor(getResources().getColor(R.color.new_orgage));
                    btnSimActivation.setBackgroundColor(getResources().getColor(R.color.new_orgage));
                    btnSimActivation.setTextColor(getResources().getColor(R.color.white));
                    button_us_recharge.setBackground(getResources().getDrawable(R.drawable.button_bg));
                    button_us_recharge.setTextColor(getResources().getColor(R.color.new_orgage));
                    btnMore.setBackground(getResources().getDrawable(R.drawable.button_bg));
                    btnMore.setTextColor(getResources().getColor(R.color.new_orgage));
                    linearLayout_international_recharge_number.setVisibility(View.GONE);
                    linearLayout_us_recharge_phonenumber.setVisibility(View.GONE);

                    linearLayout_coutries.setVisibility(View.GONE);
                    linearLayout_carriers.setVisibility(View.GONE);
                    linearLayout_plans.setVisibility(View.GONE);
                    editText_country_international_code.setVisibility(View.GONE);
                    editText_amount.setVisibility(View.GONE);
                    button_rechargeNew.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);

                    linSimActivation.setVisibility(View.VISIBLE);
                }
            });

            //    textView_header_title.setText(getResources().getString(R.string.easy_refill));
            //   imageButton_save_and_edit_my_account.setVisibility(View.VISIBLE);
            //   imageButton_save_and_edit_my_account.setImageResource(R.drawable.transactionlist);

            textView_header_title.setText(getResources().getString(R.string.title_activity_home));

            imageButton_save_and_edit_my_account.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CellphoneHomeactivity.this, MyTransactionsActivity.class);
                    startActivity(intent);
                }
            });
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) frameLayout_main_container.getLayoutParams();
            //  params.setMargins(8, 8, 8, 8);
            frameLayout_main_container.setLayoutParams(params);
            frameLayout_main_container.addView(view_recharge);
            button_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textView_header_title.setText(getResources().getString(R.string.title_activity_home));
                    /*linearLayout_home.setVisibility(View.VISIBLE);
                    linearLayout_commissions.setVisibility(View.GONE);
                    linearLayout_myaccount.setVisibility(View.GONE);*/
                    button_home.setBackgroundColor(getResources().getColor(R.color.main_sky_blue));
                    button_home.setTextColor(getResources().getColor(R.color.white));
                    button_commissions.setBackgroundColor(getResources().getColor(R.color.gray_color));
                    button_commissions.setTextColor(getResources().getColor(R.color.black));
                    button_myaccount.setBackgroundColor(getResources().getColor(R.color.gray_color));
                    button_myaccount.setTextColor(getResources().getColor(R.color.black));
                }
            });


            button_myaccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textView_header_title.setText(getResources().getString(R.string.title_activity_home));
                    button_home.setBackgroundColor(getResources().getColor(R.color.main_sky_blue));
                    button_home.setTextColor(getResources().getColor(R.color.white));
                    button_commissions.setBackgroundColor(getResources().getColor(R.color.gray_color));
                    button_commissions.setTextColor(getResources().getColor(R.color.black));
                    button_myaccount.setBackgroundColor(getResources().getColor(R.color.gray_color));
                    button_myaccount.setTextColor(getResources().getColor(R.color.black));

                    Intent intent = new Intent(CellphoneHomeactivity.this, MyAccountActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id", myCommonSession.getLoggedUserID());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            button_commissions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textView_header_title.setText(getResources().getString(R.string.title_activity_home));
                    button_home.setBackgroundColor(getResources().getColor(R.color.main_sky_blue));
                    button_home.setTextColor(getResources().getColor(R.color.white));

                    button_commissions.setBackgroundColor(getResources().getColor(R.color.gray_color));
                    button_commissions.setTextColor(getResources().getColor(R.color.black));

                    button_myaccount.setBackgroundColor(getResources().getColor(R.color.gray_color));
                    button_myaccount.setTextColor(getResources().getColor(R.color.black));
                }
            });

            getCarriers();


            relativeLayout_carriers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("DESP carriers_desc", "" + carriers_desc);
                    AlertDialog.Builder b = new AlertDialog.Builder(CellphoneHomeactivity.this);
                    b.setTitle("Select Carrier");
                    //carriers_desc = new String[carriers_desc.length];
                    Log.d("Find carriers_desc >", "" + carriers_desc);

                    //carriers_id = new String[carriers_id.length];
                    b.setItems(carriers_desc, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            textview_carriers.setText(carriers_desc[which].toString());
                            String carrierID = carriers_id[which].toString();

                            myCarrierID = carrierID;

                            Log.d("Find my carrier id", "" + carrierID);
                            String response1 = jparse.makeServiceCall(GETPRODUCTLIST + "carreir_id=" + myCarrierID);

                            Log.d("Find product responce", "" + response1);

                            JSONObject jsonObject1 = null;
                            String sym, amnt;
                            try {
                                jsonObject1 = new JSONObject(response1);
                                JSONArray jsonArray1 = jsonObject1.getJSONArray("Product");

                                Log.d("DESP", "" + jsonArray1);
                                jsonObject1 = jsonArray1.getJSONObject(0);
                                textview_plans.setText(jsonObject1.getString("Description").toString());
                                amnt = jsonObject1.getString("Amount");
                                sym = jsonObject1.getString("CurrencySymbol");
                                editText_amount.setText(amnt + " " + sym);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                    b.show();
                }
            });

            // relActivationPlan click listner


//            relActivationPlan.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    String response = jparse.makeServiceCall(Urls.GETCARRIERLIST);
//
//
//                    Log.d("CARRIERS", "" + response);
//                    JSONObject jsonObject = null;
//                    try {
//                        jsonObject = new JSONObject(response);
//                        JSONArray jsonArray = jsonObject.getJSONArray("Carrier");
//                        Log.d("DESP Checking", "" + jsonArray);
//                        String description, carrierid;
//                        JSONObject jsonObject1;
//                        final String[] desp = new String[jsonArray.length()];
//                        final String[] carrier = new String[jsonArray.length()];
//
//
//                        for (int k = 0; k < jsonArray.length(); k++) {
//                            jsonObject1 = jsonArray.getJSONObject(k);
//                            description = jsonObject1.getString("Description").toString();
//                            desp[k] = description.toString();
//                            carrierid = jsonObject1.getString("CarrierId").toString();
//
//
//                            if (carrierid.equals("592")) {
//
//                                carrier[k] = carrierid.toString();
//                                response1 = jparse.makeServiceCall(GETPRODUCTLIST + "carreir_id=" + carrier[k]);
//
//                            }
//
//
//                            carrier[k] = carrierid.toString();
//
//
//                        }
//
//
//                        JSONObject jsonObject_r1 = new JSONObject(response1);
//
//
//                        JSONArray jsonArray_r1 = jsonObject_r1.getJSONArray("Product");
//                        String pro_desc, pro_amnt, pro_sym;
//                        JSONObject jsonObject_r2;
//
//
//                        final String[] pro_amount = new String[jsonArray_r1.length()];
//                        final String[] pro_description = new String[jsonArray_r1.length()];
//                        final String[] pro_symbol = new String[jsonArray_r1.length()];
//                        for (int v = 0; v < jsonArray_r1.length(); v++) {
//                            jsonObject_r2 = jsonArray_r1.getJSONObject(v);
//                            pro_desc = jsonObject_r2.getString("Description").toString();
//                            pro_amnt = jsonObject_r2.getString("Amount").toString();
//                            pro_sym = jsonObject_r2.getString("CurrencySymbol").toString();
//                            pro_description[v] = pro_desc.toString();
//                            pro_amount[v] = pro_amnt.toString();
//                            pro_symbol[v] = pro_sym.toString();
//
//                        }
//
//
//                        AlertDialog.Builder b = new AlertDialog.Builder(CellphoneHomeactivity.this);
//                        b.setTitle("Select Carrier");
//                        b.setItems(product_info, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                tvActivationPlan.setText(product_info[which].toString());
//                                tvAmount.setText(pro_amount[which].toString() + " " + pro_symbol[which].toString());
//                                JSONObject jsonObject1 = null;
//                                try {
//                                    jsonObject1 = new JSONObject(response1);
//                                    JSONArray jsonArray1 = jsonObject1.getJSONArray("Product");
//                                    Log.d("DESP", "" + jsonArray1);
//                                    jsonObject1 = jsonArray1.getJSONObject(0);
//                                    textview_plans.setText(jsonObject1.getString("Description").toString());
//
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//                        });
//
//                        b.show();
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            });


            relActivationPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    try {

                        JSONObject jsonObject_r1 = new JSONObject(response1);
                        Log.d("dddd", "responce test" + response1);


                        JSONArray jsonArray_r1 = jsonObject_r1.getJSONArray("Product");
                        String pro_desc, pro_amnt, pro_sym;
                        JSONObject jsonObject_r2;


                        final String[] pro_amount = new String[jsonArray_r1.length()];
                        final String[] pro_description = new String[jsonArray_r1.length()];
                        final String[] pro_symbol = new String[jsonArray_r1.length()];
                        for (int v = 0; v < jsonArray_r1.length(); v++) {
                            jsonObject_r2 = jsonArray_r1.getJSONObject(v);
                            pro_desc = jsonObject_r2.getString("Description").toString();
                            pro_amnt = jsonObject_r2.getString("Amount").toString();
                            pro_sym = jsonObject_r2.getString("CurrencySymbol").toString();
                            pro_description[v] = pro_desc.toString();
                            pro_amount[v] = pro_amnt.toString();
                            pro_symbol[v] = pro_sym.toString();

                        }


                        AlertDialog.Builder b = new AlertDialog.Builder(CellphoneHomeactivity.this);
                        b.setTitle("Select Carrier");
                        b.setItems(product_info, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                tvActivationPlan.setText(product_info[which].toString());
                                tvAmount.setText(pro_amount[which].toString() + " " + pro_symbol[which].toString());


                            }
                        });

                        b.show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });


            relativeLayout_plans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("CARRIERSFirst>> ", "" + myCarrierID);

                    String response = jparse.makeServiceCall(GETPRODUCTLIST + "carreir_id=" + myCarrierID);

                    Log.d("CARRIERS>> ", "" + myCarrierID);

                    Log.d("CARRIERS", "" + response);

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Product");
                        Log.d("DESP Dummy", "" + jsonArray);
                        String description, carrierid, amount, symbol;
                        JSONObject jsonObject1;
                        final String[] desp = new String[jsonArray.length()];
                        final String[] carrier = new String[jsonArray.length()];
                        final String[] amnt = new String[jsonArray.length()];
                        final String[] sym = new String[jsonArray.length()];
                        for (int k = 0; k < jsonArray.length(); k++) {
                            jsonObject1 = jsonArray.getJSONObject(k);
                            description = jsonObject1.getString("Description").toString();
                            desp[k] = description.toString();
                            carrierid = jsonObject1.getString("CarrierId").toString();
                            carrier[k] = carrierid.toString();
                            amount = jsonObject1.getString("Amount").toString();
                            amnt[k] = amount.toString();
                            symbol = jsonObject1.getString("CurrencySymbol").toString();
                            sym[k] = symbol.toString();
                        }
                        Log.d("DESP", "" + desp);
                        AlertDialog.Builder b = new AlertDialog.Builder(CellphoneHomeactivity.this);
                        b.setTitle("Select Carrier");
                        b.setItems(desp, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                textview_plans.setText(desp[which].toString());
                                editText_amount.setText(amnt[which].toString() + " " + sym[which].toString());
                            }
                        });

                        b.show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpEvents() {
        try {
            button_rechargeNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String phoneNumberWithoutCountryCode = null;
                        if (cd.isConnectingToInternet()) {
                            if (recharge_flag == 1) {
                                phonenumber = editText_us_phonenumber.getText().toString();
                                phoneNumberWithoutCountryCode = phonenumber;
                                phonenumber = "1" + phonenumber;
                            } else if (recharge_flag == 2) {
                                phonenumber = editText_international_phonenumber.getText().toString();
                                phoneNumberWithoutCountryCode = phonenumber;

                                country_code = editText_country_international_code.getText().toString();
                                country_code = country_code.substring(1);
                                phonenumber = country_code + phonenumber;
                            }

                            if (phonenumber.isEmpty() || phonenumber.equals("")) {
                                CellphoneCommonMethods.customToastMessage(R.string.enter_valid_phone_number,
                                        CellphoneHomeactivity.this);
                            } else {
                                if (phonenumber.length() < lenghtofInternationCountyPhoneNumber || phonenumber.length() >= 15) {
                                    CellphoneCommonMethods.customToastMessage(R.string.enter_valid_phone_number,
                                            CellphoneHomeactivity.this);
                                } else if (TextUtils.isEmpty(editText_amount.getText().toString())) {
                                    CellphoneCommonMethods.customToastMessage(R.string.enter_valid_amount,
                                            CellphoneHomeactivity.this);
                                } else if (!isRechargeAmonuntValid) {
                                    CellphoneCommonMethods.customToastMessage(R.string.invalid_value, CellphoneHomeactivity.this);
                                } else {
                                    amount = editText_amount.getText().toString();
                                    selected_carrier = textview_carriers.getText().toString();
                                    StringBuffer stringBuffer = new StringBuffer();

                                    if (skuProductbean_selected != null) {
                                        stringBuffer.append("Mobile Recharge").append("\n");
                                        stringBuffer.append("Carrier Name :").append(textview_carriers.getText().toString()).append("\n");
                                        stringBuffer.append("Plan Name :").append(textview_plans.getText().toString()).append("\n");
                                        stringBuffer.append("Recharge Amount :").append(editText_amount.getText().toString()).append("\n");
                                        stringBuffer.append("Currency:").append(skuProductbean_selected.getCurrencyCode()).append("\n");
                                    }

                                    try {
                                        CellphoneCommonMethods.printLogs("before_price", editText_amount.getText().toString());
                                        String amount = "0.0";
                                        if (textview_carriers.getText().toString().contains("T-Mobile") || textview_carriers.getText().toString().contains("Ultra Mobile")) {
                                            double recharge_amount = Double.parseDouble(editText_amount.getText().toString());
                                            recharge_amount += 2.0;
                                            amount = String.valueOf(recharge_amount);
                                            extraFees = "2";
                                        } else {
                                            extraFees = "0";
                                            amount = editText_amount.getText().toString();
                                        }
                                        //private void confirmRechargeDialog(final String amount, String phonenumber, String carrierName, String countrycode){
                                        //selectPaymentOptionDialog(amount);
                                        confirmRechargeDialog(amount, phoneNumberWithoutCountryCode, textview_plans.getText().toString(), country_code);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } else {
                            //  new PomodroidException(getResources().getString(R.string.no_internet_connection), CellphoneHomeactivity.this, false, false);
                        }
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

            btnActivate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (cd.isConnectingToInternet()) {
                            if ((edtSimNumberOne.getText().length() + edtSimNumberTwo.getText().length()
                                    + edtSimNumberThree.getText().length()) != 15) {
                                CellphoneCommonMethods.customToastMessage(R.string.PLEASE_ENTER_15_DIGITS,
                                        CellphoneHomeactivity.this);
                            } else if (TextUtils.isEmpty(tvActivationPlan.getText().toString())) {
                                CellphoneCommonMethods.customToastMessage(R.string.KINDLY_SELECT_ACTIVATION_PLAN,
                                        CellphoneHomeactivity.this);
                            } else if (TextUtils.isEmpty(edtZipCode.getText().toString()) || (edtZipCode.getText().length() != 3)) {
                                CellphoneCommonMethods.customToastMessage(R.string.PLEASE_ENTER_A_VALID_ZIPCODE,
                                        CellphoneHomeactivity.this);
                            } else {
                                phonenumber = "89014" + edtSimNumberOne.getText().toString() + edtSimNumberTwo.getText().toString()
                                        + edtSimNumberThree.getText().toString();


                                Log.d("Phone dummy >> ", "" + phonenumber);
                                amount = tvAmount.getText().toString().substring(1);

                                extraFees = "0";
                                recharge_type = CommonKeywords.RECHARGE_ONCE;


//                                StringBuffer stringBuffer = new StringBuffer();
//
//                                if (skuProductbean_selected != null) {
//                                    stringBuffer.append("Mobile Recharge").append("\n");
//                                    stringBuffer.append("Carrier Name :").append(textview_carriers.getText().toString()).append("\n");
//                                    stringBuffer.append("Plan Name :").append(textview_plans.getText().toString()).append("\n");
//                                    stringBuffer.append("Recharge Amount :").append(editText_amount.getText().toString()).append("\n");
//                                    stringBuffer.append("Currency:").append(skuProductbean_selected.getCurrencyCode()).append("\n");
//                                }
//                                try {
//                                    CellphoneCommonMethods.printLogs("before_price", editText_amount.getText().toString());
//                                    String amount = "0.0";
//                                    if (textview_carriers.getText().toString().contains("T-Mobile") || textview_carriers.getText().toString().contains("Ultra Mobile")) {
//                                        double recharge_amount = Double.parseDouble(editText_amount.getText().toString());
//                                        recharge_amount += 2.0;
//                                        amount = String.valueOf(recharge_amount);
//                                        extraFees = "2";
//                                    } else {
//                                        extraFees = "0";
//                                        amount = editText_amount.getText().toString();
//                                    }
//
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }

                                selectPaymentOptionDialog(amount);

//                                new DoAcitvateSim(CellphoneHomeactivity.this, phonenumber, edtZipCode.getText().toString(),
//                                        amount, 1).execute();
                            }
                        } else {
                        }
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

            radioButton_once.setChecked(true);
            radioButton_repetive.setChecked(false);
            //spinner_carrierID.setEnabled(false);
            editText_us_phonenumber.setInputType(1234567890);
            editText_international_phonenumber.setInputType(1234567890);
            radioGroup_recharge
                    .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            try {
                                Log.d("chk", "id" + checkedId);

                                if (checkedId == R.id.radiobutton_once) {

                                    recharge_type = CommonKeywords.RECHARGE_ONCE;
                                    Log.d("recharge_type", "id" + recharge_type);

                                    //some code
                                } else if (checkedId == R.id.radioButton_repetive) {
                                    recharge_type = CommonKeywords.RECHARFE_REPETIVE;
                                    Log.d("recharge_type", "id" + recharge_type);
                                    //some code
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    });
            //  call_virtuemart();
            editText_us_phonenumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // carrierid.setEnabled(false);
                    if (editText_us_phonenumber.getText().toString().isEmpty()) {
                    }
                    // adapter.notifyDataSetChanged();

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (s.length() == 0)

                    {

                    } else {
                        if (skuProductbean_selected != null) {

                            editText_us_phonenumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(skuProductbean_selected.getLocalPhoneNumberLength())});

                            if (s.length() > skuProductbean_selected.getLocalPhoneNumberLength()) {
                                editText_us_phonenumber.setError("Please enter valid Number");

                            } else if (s.length() == skuProductbean_selected.getLocalPhoneNumberLength()) {
                                editText_us_phonenumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(skuProductbean_selected.getLocalPhoneNumberLength())});

                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                                if (cd.isConnectingToInternet()) {
//                                    new GetCarrierInforByMobileNumberUSRecharge(relativeLayout_carriers, relativeLayout_plans, CellphoneHomeactivity.this,
//                                            editText_us_phonenumber.getText().toString(), progressBar_carrier,
//                                            linearLayout_coutries,
//                                            linearLayout_carriers,
//                                            linearLayout_plans,
//                                            textview_countries, textview_carriers, textview_plans, editText_country_US_code).execute();

                                } else {
                                    //   new PomodroidException(getResources().getString(R.string.no_internet_connection), CellphoneHomeactivity.this, false, false);

                                }


                            }


                        } else {
                            if (s.length() == 10) {
                                editText_us_phonenumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                                if (cd.isConnectingToInternet()) {
//                                    new GetCarrierInforByMobileNumberUSRecharge(relativeLayout_carriers, relativeLayout_plans, CellphoneHomeactivity.this,
//                                            editText_us_phonenumber.getText().toString(), progressBar_carrier,
//                                            linearLayout_coutries,
//                                            linearLayout_carriers,
//                                            linearLayout_plans,
//                                            textview_countries, textview_carriers, textview_plans, editText_country_US_code).execute();

                                } else {
                                    // new PomodroidException(getResources().getString(R.string.no_internet_connection), CellphoneHomeactivity.this, false, false);

                                }


                            } else if (s.length() > 10) {
                                //Toast.makeText(getApplicationContext(), "Please enter valid Number/Amount", Toast.LENGTH_SHORT).show();
                                editText_us_phonenumber.setError("Please enter valid Number");

                            }
                        }

                    }


                }
            });
            editText_international_phonenumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // carrierid.setEnabled(false);
                    if (editText_us_phonenumber.getText().toString().isEmpty()) {
                    }
                    // adapter.notifyDataSetChanged();

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (s.length() == 0) {

                    } else {
                        if (lenghtofInternationCountyPhoneNumber == 0) {

                        } else {

                            if (s.length() > lenghtofInternationCountyPhoneNumber) {
                                progressBar_carrier_international_recharge.setVisibility(View.GONE);
                                editText_international_phonenumber.setError("Please enter valid Number");

                            } else if (s.length() == lenghtofInternationCountyPhoneNumber) {

                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


                                progressBar_carrier_international_recharge.setVisibility(View.VISIBLE);

                                if (cd.isConnectingToInternet()) {
//                                    new GetCarrierInforByMobileNumberInterNational(relativeLayout_carriers, relativeLayout_plans, CellphoneHomeactivity.this,
//                                            editText_international_phonenumber.getText().toString(), progressBar_carrier_international_recharge,
//                                            linearLayout_coutries,
//                                            linearLayout_carriers,
//                                            linearLayout_plans,
//                                            textview_countries, textview_carriers, textview_plans, editText_country_international_code, editText_amount).execute();

                                } else {
                                    //   new PomodroidException(getResources().getString(R.string.no_internet_connection), CellphoneHomeactivity.this, false, false);

                                }

                               /* if (skuProductbean_selected != null) {

                                    button_rechargeNew.setEnabled(true);

                                    new GetCarrierListByCountryCodeFromSKUs(relativeLayout_carriers, relativeLayout_plans, skuProductbean_selected, CellphoneHomeactivity.this, textview_carriers, textview_plans, linearLayout_carriers, linearLayout_plans, editText_country_international_code).execute(skuProductbean_selected.getCountryCode());

                                    new GetSKUFilterByCarrierName(relativeLayout_plans, skuProductbean_selected, CellphoneHomeactivity.this, textview_plans, linearLayout_plans).execute(skuProductbean_selected.getCarrierName());


                                } else {
                                }*/
                            }
                        }
                    }
                }
            });


            editText_amount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // carrierid.setEnabled(false);
                    // adapter.notifyDataSetChanged();
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    editText_international_phonenumber.setEnabled(true);
                    isRechargeAmonuntValid = false;
                }

                @Override
                public void afterTextChanged(Editable s) {

                    try {
                        if (editText_amount.getText().toString().startsWith("0")) {
                            editText_amount.setError(getResources().getString(R.string.invalid_value));
                            isRechargeAmonuntValid = false;

                        } else {
                            if (skuProductbean_selected != null) {
                                if (editText_amount.getText().toString().startsWith("0")) {
                                    editText_amount.setError(getResources().getString(R.string.invalid_value));
                                    isRechargeAmonuntValid = false;
                                } else if (s.length() > 0) {
                                    if (s.toString().endsWith(".") || s.toString().startsWith(".")) {
                                        editText_amount.setError(getResources().getString(R.string.invalid_value));
                                        isRechargeAmonuntValid = false;
                                    } else {
                                        Double enteredAmount = new Double(Double.parseDouble(editText_amount.getText().toString()));
                                        Double min_amount = new Double(skuProductbean_selected.getMinAmount());
                                        Double max_amount = new Double(skuProductbean_selected.getMaxAmount());
                                        int a = enteredAmount.compareTo(min_amount);
                                        int b = enteredAmount.compareTo(max_amount);

                                        boolean check_a = false;

                                        if (a == 0) {
                                            check_a = true;
                                        } else if (a < 0) {
                                            check_a = false;

                                        } else {
                                            check_a = true;

                                        }
                                        boolean check_b = false;


                                        if (b == 0) {
                                            check_b = true;
                                        } else if (b < 0) {
                                            check_b = true;

                                        } else {
                                            check_b = false;

                                        }


                                        if (check_a && check_b) {
                                            isRechargeAmonuntValid = true;

                                        } else {
                                            isRechargeAmonuntValid = false;

                                            editText_amount.setError(getResources().getString(R.string.invalid_value));
                                        }
                                    }
                                }
                            } else {

                            }
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        isRechargeAmonuntValid = false;
                    }
                }
            });


            button_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        //logout();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void setUpContriesList() {
        try {
            String[] locales = Locale.getISOCountries();


            for (String countryCode : locales) {

                Locale obj = new Locale("", countryCode);

                Currency a = Currency.getInstance(obj);
                Countrybean countrybean = new Countrybean();
                countrybean.setCountryCode(obj.getCountry());
                countrybean.setCountryName(obj.getDisplayCountry());

                if (a != null) {
                    countrybean.setCurrencyCode(a.getCurrencyCode());
                }


                arrayListCountrybeanArrayList.add(countrybean);


            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == REQUEST_CODE_PAYMENT) {
                if (resultCode == RESULT_OK) {
                    PaymentConfirmation confirm =
                            data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                    if (confirm != null) {
                        try {

                            String s = confirm.toJSONObject().toString(4);
                            Gson gson = new Gson();
                            String login_responce = myCommonSession.getStoreLoggedResponce();
                            Type type = new TypeToken<Loginbean>() {
                            }.getType();
                            final Loginbean loginbean = gson.fromJson(login_responce, type);

                            FinalTransactionbean finalTransactionbean = new FinalTransactionbean();
                            finalTransactionbean.setTransactionId("0");
                            finalTransactionbean.setUserId(myCommonSession.getLoggedUserID());
                            finalTransactionbean.setFirstName(loginbean.getFirstName());
                            finalTransactionbean.setLastName(loginbean.getLastName());
                            finalTransactionbean.setEmail(loginbean.getEmail());

                            finalTransactionbean.setMobileNumber(phonenumber.trim());
                            finalTransactionbean.setAmount(amount);
                            finalTransactionbean.setRecurring(recharge_type);
                            finalTransactionbean.setPaymentTypeMode(CommonKeywords.PAYMENT_TYPE_PAYPAL_ID);
                            finalTransactionbean.setExtrachargedCost(extraFees);

                            finalTransactionbean.setProduct_name(CellphoneHomeactivity.skuProductbean_selected.getCarrierName());
                            finalTransactionbean.setCurrencyCode(CellphoneHomeactivity.skuProductbean_selected.getCurrencyCode());
                            finalTransactionbean.setAmount(String.valueOf(amount));

                            JSONObject jsonObject_paypal = new JSONObject(s);
                            finalTransactionbean.setPaypalSuccessResponce(s);

                            JSONObject jsonObject_responce = jsonObject_paypal.getJSONObject("response");
                            finalTransactionbean.setState(jsonObject_responce.getString("state"));
                            finalTransactionbean.setId(jsonObject_responce.getString("id"));
                            finalTransactionbean.setPaypalcreate_time(jsonObject_responce.getString("create_time"));
                            finalTransactionbean.setIntent(jsonObject_responce.getString("intent"));

                            JSONObject jsonObject_client = jsonObject_paypal.getJSONObject("client");
                            finalTransactionbean.setPlatform(jsonObject_client.getString("platform"));
                            finalTransactionbean.setPaypal_sdk_version(jsonObject_client.getString("paypal_sdk_version"));
                            finalTransactionbean.setPaypal_product_name(jsonObject_client.getString("product_name"));
                            finalTransactionbean.setEnvironment(jsonObject_client.getString("environment"));
                            finalTransactionbean.setResponse_type(jsonObject_paypal.getString("response_type"));

                            int recordID = cellphoneDatabasehelper.getTotalCountTransactionRecords();
                            recordID++;
                            finalTransactionbean.setRecordID(recordID);

                            finalTransactionbean.setLocalPhoneNumberLength(String.valueOf(CellphoneHomeactivity.skuProductbean_selected.getLocalPhoneNumberLength()));
                            finalTransactionbean.setAllowDecimal(CellphoneHomeactivity.skuProductbean_selected.isAllowDecimal());
                            finalTransactionbean.setDiscount(String.valueOf(CellphoneHomeactivity.skuProductbean_selected.getDiscount()));
                            finalTransactionbean.setCarrierName(CellphoneHomeactivity.skuProductbean_selected.getCarrierName());
                            finalTransactionbean.setCurrencyCode(CellphoneHomeactivity.skuProductbean_selected.getCurrencyCode());
                            finalTransactionbean.setCategory(CellphoneHomeactivity.skuProductbean_selected.getCategory());
                            finalTransactionbean.setProductCode(CellphoneHomeactivity.skuProductbean_selected.getProductCode());
                            finalTransactionbean.setIsSalesTaxCharged(CellphoneHomeactivity.skuProductbean_selected.isSalesTaxCharged());
                            finalTransactionbean.setProduct_name(CellphoneHomeactivity.skuProductbean_selected.getProductName());
                            finalTransactionbean.setSkuId(String.valueOf(CellphoneHomeactivity.skuProductbean_selected.getSkuId()));
                            finalTransactionbean.setInternationalCodes(CellphoneHomeactivity.skuProductbean_selected.getInternationalCodes());
                            finalTransactionbean.setFee(String.valueOf(CellphoneHomeactivity.skuProductbean_selected.getFee()));
                            finalTransactionbean.setMinAmount(String.valueOf(CellphoneHomeactivity.skuProductbean_selected.getMinAmount()));
                            finalTransactionbean.setExchangeRate(String.valueOf(CellphoneHomeactivity.skuProductbean_selected.getExchangeRate()));
                            finalTransactionbean.setCountryCode(CellphoneHomeactivity.skuProductbean_selected.getCountryCode());
                            finalTransactionbean.setDenomination(String.valueOf(CellphoneHomeactivity.skuProductbean_selected.getDenomination()));
                            finalTransactionbean.setMaxAmount(String.valueOf(CellphoneHomeactivity.skuProductbean_selected.getMaxAmount()));
                            finalTransactionbean.setCarrierId(String.valueOf(CellphoneHomeactivity.skuProductbean_selected.getCarrierId()));
                            finalTransactionbean.setBonusAmount(String.valueOf(CellphoneHomeactivity.skuProductbean_selected.getBonusAmount()));

                            new DoRechargeNowAndSendDataToServer(CellphoneHomeactivity.this,
                                    finalTransactionbean, CommonKeywords.CREDIT_CARD_FOR_PAYPAL_TYPE).execute();
                        } catch (Exception e) {
                        }
                    }
                } else if (resultCode == RESULT_CANCELED) {
                } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                }

            } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
                if (resultCode == RESULT_OK) {
                    PayPalAuthorization auth =
                            data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                } else if (resultCode == RESULT_CANCELED) {
                } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        // Stop service when done
        super.onDestroy();
    }

    private void selectPaymentOptionDialog(final String amount) {
        try {
            final Dialog dialog = new Dialog(CellphoneHomeactivity.this, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setContentView(R.layout.payment_option_dialog_main);

            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);

            RelativeLayout relativeLayout_paypal = (RelativeLayout) dialog.findViewById(R.id.layout_paypal);
            relativeLayout_paypal.setVisibility(View.VISIBLE);
            RelativeLayout relativeLayout_credit_card = (RelativeLayout) dialog.findViewById(R.id.layout_credit_card);
            RelativeLayout relativeLayout_wallet = (RelativeLayout) dialog.findViewById(R.id.layout_wallet);
            relativeLayout_credit_card.setVisibility(View.GONE);

            if (myCommonSession.isAffiliateUserNew()) {
                relativeLayout_wallet.setVisibility(View.VISIBLE);
            } else {
                relativeLayout_wallet.setVisibility(View.GONE);
            }

            relativeLayout_paypal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        paypalPaymentInit(amount);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            relativeLayout_credit_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //for checking payment option called form left menu or defualt home
                    CommonKeywords.DEFUALT_PAYMENT_OPTION = CommonKeywords.FROM_DEFUALT_PAYMENT_OPTION;
                    if (StripeCreditCardPaymentActivity.stripeCreditCardPaymentActivity != null) {
                        StripeCreditCardPaymentActivity.stripeCreditCardPaymentActivity.finish();
                    }
                    Intent intent = new Intent(CellphoneHomeactivity.this, StripeCreditCardPaymentActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("amount", amount);
                    bundle.putString("phoneNumber", phonenumber);
                    bundle.putString("extraCost", extraFees);
                    bundle.putString("recurringStatus", recharge_type);
                    bundle.putString("ZipCode", "" + edtZipCode.getText().toString());
                    bundle.putInt("Flag", recharge_flag);
                    bundle.putInt("NumberPlan", Integer.parseInt(tvNumberPlan.getText().toString().split(" ")[0]));
                    intent.putExtras(bundle);
                    startActivity(intent);

                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });

            relativeLayout_wallet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        walletPaymentInit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            //The below code is EXTRA - to dim the parent view by 70%
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.7f;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            dialog.getWindow().setAttributes(lp);
            //Show the dialog
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void confirmRechargeDialog(final String amount, String phonenumber, String carrierName, String countrycode) {
        try {
            final Dialog dialog = new Dialog(CellphoneHomeactivity.this, android.R.style.Theme_Translucent_NoTitleBar);

            dialog.setContentView(R.layout.confirm_recharge_dialog);

            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);

            CustomTextview customTextview_recharge_message = (CustomTextview) dialog.findViewById(R.id.textview_confirm_recharge_message);
            CustomTextview customTextview_middle_message = (CustomTextview) dialog.findViewById(R.id.textview_message);

            CustomTextview customTextview_confirm = (CustomTextview) dialog.findViewById(R.id.textview_confirm);
            CustomTextview customTextview_cancel = (CustomTextview) dialog.findViewById(R.id.textview_cancel);

            customTextview_recharge_message.setText(getResources().getString(R.string.confirm_recharge) + " $"
                    + CellphoneCommonMethods.getDecimalvalueFromString(amount));
            customTextview_middle_message.setText(getResources().getString(R.string.are_you_sure_you_want_to_recharge) + "+" + countrycode
                    + " " + phonenumber + " " + getResources().getString(R.string.with) + " " + carrierName);

            customTextview_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        selectPaymentOptionDialog(amount);
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            customTextview_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });

            //The below code is EXTRA - to dim the parent view by 70%
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.7f;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            dialog.getWindow().setAttributes(lp);
            //Show the dialog
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void paypalPaymentInit(String amount) {
        try {
            PayPalPayment thingToBuy;
            thingToBuy = new PayPalPayment(new BigDecimal(amount), "USD", "Mobile Recharge", PayPalPayment.PAYMENT_INTENT_SALE);
            Intent intent = new Intent(CellphoneHomeactivity.this, PaymentActivity.class);
            // send the same configuration for restart resil
            // iency
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
            startActivityForResult(intent, REQUEST_CODE_PAYMENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void walletPaymentInit() {
        try {
            FinalTransactionbean finalTransactionbean = new FinalTransactionbean();
            finalTransactionbean.setTransactionId("0");
            finalTransactionbean.setUserId(myCommonSession.getLoggedUserID());
            finalTransactionbean.setMobileNumber(phonenumber.trim());
            finalTransactionbean.setAmount(amount);
            finalTransactionbean.setRecurring(recharge_type);
            finalTransactionbean.setPaymentTypeMode(CommonKeywords.PAYMENT_TYPE_WALLLET_ID);
            finalTransactionbean.setCurrencyCode(CellphoneHomeactivity.skuProductbean_selected.getCurrencyCode());
            finalTransactionbean.setProduct_name(CellphoneHomeactivity.skuProductbean_selected.getCarrierName());
            finalTransactionbean.setExtrachargedCost(extraFees);
            finalTransactionbean.setLocalPhoneNumberLength(String.valueOf(CellphoneHomeactivity.skuProductbean_selected.getLocalPhoneNumberLength()));
            finalTransactionbean.setAllowDecimal(CellphoneHomeactivity.skuProductbean_selected.isAllowDecimal());
            finalTransactionbean.setDiscount(String.valueOf(CellphoneHomeactivity.skuProductbean_selected.getDiscount()));
            finalTransactionbean.setCurrencyCode(CellphoneHomeactivity.skuProductbean_selected.getCurrencyCode());
            finalTransactionbean.setProductCode(CellphoneHomeactivity.skuProductbean_selected.getProductCode());
            finalTransactionbean.setIsSalesTaxCharged(CellphoneHomeactivity.skuProductbean_selected.isSalesTaxCharged());
            finalTransactionbean.setInternationalCodes(CellphoneHomeactivity.skuProductbean_selected.getInternationalCodes());
            finalTransactionbean.setFee(String.valueOf(CellphoneHomeactivity.skuProductbean_selected.getFee()));
            finalTransactionbean.setMinAmount(String.valueOf(CellphoneHomeactivity.skuProductbean_selected.getMinAmount()));
            finalTransactionbean.setExchangeRate(String.valueOf(CellphoneHomeactivity.skuProductbean_selected.getExchangeRate()));
            finalTransactionbean.setCountryCode(CellphoneHomeactivity.skuProductbean_selected.getCountryCode());
            finalTransactionbean.setDenomination(String.valueOf(CellphoneHomeactivity.skuProductbean_selected.getDenomination()));
            finalTransactionbean.setMaxAmount(String.valueOf(CellphoneHomeactivity.skuProductbean_selected.getMaxAmount()));
            finalTransactionbean.setCarrierId(String.valueOf(CellphoneHomeactivity.skuProductbean_selected.getCarrierId()));
            finalTransactionbean.setBonusAmount(String.valueOf(CellphoneHomeactivity.skuProductbean_selected.getBonusAmount()));
            if (recharge_flag == 3) {
                finalTransactionbean.setIsActivate("1");
                finalTransactionbean.setSkuId(String.valueOf(skuId));
                finalTransactionbean.setProduct_name(tvActivationPlan.getText().toString());
                finalTransactionbean.setZipCode(edtZipCode.getText().toString());
                finalTransactionbean.setNumberOfPlanMonths(Integer.parseInt(tvNumberPlan.getText().toString().split(" ")[0]));
                finalTransactionbean.setCategory("Sim");
                finalTransactionbean.setCarrierName(String.valueOf(carrierName));
                finalTransactionbean.setIsActivationSim("1");
            } else {
                finalTransactionbean.setIsActivate("0");
                finalTransactionbean.setSkuId(String.valueOf(CellphoneHomeactivity.skuProductbean_selected.getSkuId()));
                finalTransactionbean.setProduct_name(CellphoneHomeactivity.skuProductbean_selected.getProductName());
                finalTransactionbean.setCategory(CellphoneHomeactivity.skuProductbean_selected.getCategory());
                finalTransactionbean.setCarrierName(CellphoneHomeactivity.skuProductbean_selected.getCarrierName());
            }

            int recordID = cellphoneDatabasehelper.getTotalCountTransactionRecords();
            recordID++;
            finalTransactionbean.setRecordID(recordID);

            if (recharge_flag == 3) {
                new DoAcitvateSim(this, phonenumber, edtZipCode.getText().toString(), amount, 1,
                        finalTransactionbean, CommonKeywords.CREDIT_CARD_FOR_WALLETE_TYPE).execute();
            } else {
                new DoRechargeNowAndSendDataToServer(CellphoneHomeactivity.this,
                        finalTransactionbean, CommonKeywords.CREDIT_CARD_FOR_WALLETE_TYPE).execute();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* public void getCarriers() {
        linearLayout_carriers.setVisibility(View.VISIBLE);
        MyJSONParser jparse = new MyJSONParser();
        String response = jparse.makeServiceCall(Urls.GETCARRIERLIST);

        String pro_desc, pro_amnt;

        Log.d("CARRIERS", "" + response);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("Carrier");
            Log.d("DESP", "" + jsonArray);
            jsonObject = jsonArray.getJSONObject(0);
            textview_carriers.setText(jsonObject.getString("Description").toString());
            linearLayout_plans.setVisibility(View.VISIBLE);
            String activeCarrierID = "";
            for (int l = 0; l < jsonArray.length(); l++) {
                activeCarrierID = jsonArray.getJSONObject(l).getString("CarrierId").toString();
                Log.d("active592>>>>>>>", "CONTAINS 592 >>>>>>>>>>>> " + activeCarrierID);
                if (activeCarrierID == "592") {
                    Log.d("TEST", "CONTAINS 592");
                }
            }

            String carrierID = jsonObject.getString("CarrierId").toString();

            response2 = jparse.makeServiceCall(GETPRODUCTLIST + "carreir_id=" + carrierID);

            Log.d("CARRIERS123", "" + response2);
            JSONObject jsonObject1 = null;


            jsonObject1 = new JSONObject(response2);
            JSONArray jsonArray1 = jsonObject1.getJSONArray("Product");
            String rec_amnt, rec_description, amnt, sym;
            JSONObject jsonObject2;
            //  jsonObject3=jsonArray1.getJSONObject(0);

            // new change


            for(int t=0;t<jsonArray1.length();t++){
saveData= new SaveData();
jsonObject2= new JSONObject(jsonArray1.get(t).toString());

                jsonObject2 = jsonArray1.getJSONObject(0);

                textview_plans.setText(jsonObject2.getString("Description").toString());
                amnt = jsonObject2.getString("Amount");
                sym = jsonObject2.getString("CurrencySymbol");
                editText_amount.setText(amnt + " " + sym);

            }

            // new change end



           // old code 14-12-2018
            *//*jsonObject2 = jsonArray1.getJSONObject(0);
            textview_plans.setText(jsonObject2.getString("Description").toString());
            amnt = jsonObject2.getString("Amount");
            sym = jsonObject2.getString("CurrencySymbol");
            editText_amount.setText(amnt + " " + sym);*//*




        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/


//    public void getCarriers() {
//        linearLayout_carriers.setVisibility(View.VISIBLE);
//        MyJSONParser jparse = new MyJSONParser();
//        String response = jparse.makeServiceCall(Urls.GETCARRIERLIST);
//
//        String pro_desc, pro_amnt;
//
//        Log.d("CARRIERS", "" + response);
//        JSONObject jsonObject = null;
//        try {
//            jsonObject = new JSONObject(response);
//            JSONArray jsonArray = jsonObject.getJSONArray("Carrier");
//            Log.d("DESP", "" + jsonArray);
//            jsonObject = jsonArray.getJSONObject(0);
//            textview_carriers.setText(jsonObject.getString("Description").toString());
//            linearLayout_plans.setVisibility(View.VISIBLE);
//            String activeCarrierID = "";
//            for (int l = 0; l < jsonArray.length(); l++) {
//
//                carriersData=new CarriersData();
//
//                jsonObject= new JSONObject(jsonArray.get(l).toString());
//                carriersData.setCarrierdata_id(jsonObject.getString("CarrierId"));
//
//                alcarriersData.add(carriersData);
//               // activeCarrierID = jsonArray.getJSONObject(l).getString("CarrierId").toString();
//                activeCarrierID = carriersData.getCarrierdata_id().toString();
//
//                Log.d("active592>>>>>>>", "CONTAINS 592 >>>>>>>>>>>> " + activeCarrierID);
//                if (activeCarrierID == "592") {
//                    Log.d("TEST", "CONTAINS 592");
//                }
//            }
//
//            String carrierID = jsonObject.getString("CarrierId").toString();
//
//            response2 = jparse.makeServiceCall(GETPRODUCTLIST + "carreir_id=" + carrierID);
//
//            Log.d("CARRIERS123", "" + response2);
//            JSONObject jsonObject1 = null;
//
//
//            jsonObject1 = new JSONObject(response2);
//            JSONArray jsonArray1 = jsonObject1.getJSONArray("Product");
//            String rec_amnt, rec_description, amnt, sym;
//            JSONObject jsonObject2;
//
//            for(int s=0; s<jsonArray1.length();s++){
//
//                saveData= new SaveData();
//                jsonObject2 = new JSONObject(jsonArray1.get(s).toString());
//                saveData.setDescription_product(jsonObject2.getString("Description"));
//                saveData.setAmount(jsonObject2.getString("Amount"));
//                saveData.setSymbol(jsonObject2.getString("CurrencySymbol"));
//
//                allistdata.add(saveData);
//
//
//                textview_plans.setText(saveData.getAmount());
//             //   textview_plans.setText(jsonObject2.getString("Description").toString());
//                amnt = jsonObject2.getString("Amount");
//                sym = jsonObject2.getString("CurrencySymbol");
//                editText_amount.setText(amnt + " " + sym);
//            }
//             /*jsonObject2 = jsonArray1.getJSONObject(0);
//            textview_plans.setText(jsonObject2.getString("Description").toString());
//            amnt = jsonObject2.getString("Amount");
//            sym = jsonObject2.getString("CurrencySymbol");
//            editText_amount.setText(amnt + " " + sym);
//*/
//
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }


    public void getZipcode() {

        MyJSONParser jparse = new MyJSONParser();
        //  String response = jparse.makeServiceCall(GETZIPCODE);
        String response = jparse.makeServiceCall(GETZIPCODE + "" + Store_Zipcode);


        Log.d("ZIP Responce", "" + response);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);

            String zip_value = jsonObject.getString("ZIP");

            Log.d("ZIP value", "" + zip_value);

            if (zip_value.equalsIgnoreCase("Invaild NPA")) {

                // Toast.makeText(getApplicationContext(), "Sorry !!! Invalid NPA ", Toast.LENGTH_SHORT).show();

                showDialog("ok", "Sorry !!! Invalid NPA", CellphoneHomeactivity.this);
                edtZipCode.setText("");
                edtZipCode.requestFocus();

                // Toast.makeText(CellphoneHomeactivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            } else {

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
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


    //    MY EDITS
    public void getCarriers() {
        linearLayout_carriers.setVisibility(View.VISIBLE);
        MyJSONParser jparse = new MyJSONParser();
        String response = jparse.makeServiceCall(Urls.GETCARRIERLIST);

        String pro_desc, pro_amnt;

        Log.d("CARRIERS_GETCARRIES", "" + response);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("Carrier");
            Log.d("DESP", "" + jsonArray);
            jsonObject = jsonArray.getJSONObject(0);
            textview_carriers.setText(jsonObject.getString("Description").toString());
            linearLayout_plans.setVisibility(View.VISIBLE);
            String activeCarrierID = "";

            carriers_id = new String[jsonArray.length()];
            carriers_desc = new String[jsonArray.length()];
            String activeCarrierID1 = "", activeCarrierDesc1 = "";
            for (int k = 0; k < jsonArray.length(); k++) {

                activeCarrierID1 = jsonArray.getJSONObject(k).getString("CarrierId");
                Log.d("Find activecarrierID1 >", "ID1 >" + activeCarrierID1);
                carriers_id[k] = activeCarrierID1;

                activeCarrierDesc1 = jsonArray.getJSONObject(k).getString("Description").toString();
                carriers_desc[k] = activeCarrierDesc1;
            }


            for (int l = 0; l < jsonArray.length(); l++) {
                activeCarrierID = jsonArray.getJSONObject(l).getString("CarrierId").toString();
                Log.d("active592>>>>>>>", "CONTAINS 592 >>>>>>>>>>>> " + activeCarrierID);
                if (activeCarrierID == "592") {
                    Log.d("TEST", "CONTAINS 592");
                }
            }


            String carrierID = jsonObject.getString("CarrierId").toString();
            response2 = jparse.makeServiceCall(GETPRODUCTLIST + "carreir_id=" + carrierID);
            Log.d("CARRIERS123", "" + response2);
            JSONObject jsonObject1 = null;


            jsonObject1 = new JSONObject(response2);
            JSONArray jsonArray1 = jsonObject1.getJSONArray("Product");
            String rec_amnt, rec_description, amnt, sym;
            JSONObject jsonObject2;
            JSONObject jsonObject5;
            //  jsonObject3=jsonArray1.getJSONObject(0);

            // new change


            for (int t = 0; t < jsonArray1.length(); t++) {

                jsonObject2 = jsonArray1.getJSONObject(0);
                textview_plans.setText(jsonObject2.getString("Description").toString());
                amnt = jsonObject2.getString("Amount");
                sym = jsonObject2.getString("CurrencySymbol");
                editText_amount.setText(amnt + " " + sym);

            }


            String productDescription, carrierid;
            final String[] carrier = new String[jsonArray.length()];


            for (int k = 0; k < jsonArray.length(); k++) {
                jsonObject1 = jsonArray.getJSONObject(k);


                carrierid = jsonObject1.getString("CarrierId").toString();


                if (carrierid.equals("592")) {

                    carrier[k] = carrierid.toString();
                    response1 = jparse.makeServiceCall(GETPRODUCTLIST + "carreir_id=" + carrier[k]);

                    Log.d("TEST", "Needed Responce 592 > " + response1);


                }

                carrier[k] = carrierid.toString();


            }


            jsonObject5 = new JSONObject(response1);
            JSONArray jsonArray3 = jsonObject5.getJSONArray("Product");
            product_info = new String[jsonArray3.length()];


            for (int n = 0; n < jsonArray3.length(); n++) {
                productDescription = jsonArray3.getJSONObject(n).getString("Description");
                product_info[n] = productDescription;


                Log.d("TEST", "Needed prdescr592 > " + response1);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
}
