package com.notonepine.lunchroulette;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

public class LunchRouletteFragmentActivity extends FragmentActivity {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String USER_ID = "user_id";
    public static final String FACEBOOK_USER_ID = "facebook_user_id";
    public static final String FACEBOOK_FULL_NAME = "user_full_name";

    private static final String SENDER_ID = "930480945207";

    public static Typeface tf;

    static final String TAG = "LunchRoulette";

    GoogleCloudMessaging gcm;

    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;

    String regid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tf = Typeface.createFromAsset(getAssets(), "fonts/opensans.ttf");
        Parse.initialize(this, "QCYQYAaLANlJtBoohLfBhdg7C9HtFdRpCE3aVFNh", "UOaeCJzOQRwV2T9TIOVtLh3NiFVoCAMXS001yM5W");
        ParseFacebookUtils.initialize("1456402971254781");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
        ImageLoader.getInstance().init(config);

        gcm = GoogleCloudMessaging.getInstance(this);
        regid = getRegistrationId(context);

        if (regid.equals("")) {
            registerInBackground();
        }

    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     * 
     * @return registration ID, or empty string if there is no existing registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.equals(null) || registrationId.equals("")) {
            Log.i(TAG, "Registration not found.");
            return "";
        }

        return registrationId;
    }

    /**
     * Register with GCM
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    Log.d("GCM ERROR", "Error :" + ex.getMessage());
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                // TODO: Do stuff with our message? I dont think this is necessary.
            }
        }.execute(null, null, null);
    }

    /**
     * Store the registration id in our preferences to recall later, when we send facebook login info.
     * 
     * @param context
     * @param regId
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.commit();
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {

        return getSharedPreferences("com.notonepine.lunchroulette", Context.MODE_PRIVATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }
}
