package org.wit.placemark.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import org.wit.placemark.views.login.LoginView;

public class SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        startActivity(new Intent(SplashScreen.this, LoginView.class));
        finish();
    }
}
