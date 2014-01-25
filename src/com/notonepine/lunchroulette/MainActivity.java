package com.notonepine.lunchroulette;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Parse.initialize(this, "QCYQYAaLANlJtBoohLfBhdg7C9HtFdRpCE3aVFNh", "UOaeCJzOQRwV2T9TIOVtLh3NiFVoCAMXS001yM5W");
        ParseFacebookUtils.initialize("1456402971254781");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }
}
