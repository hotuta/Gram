package com.yuyakaido.android.gram;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements InstagramAuthenticationDialog.InstagramAuthenticationCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.activity_main_authentication_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAuthenticationDialog();
                    }
                });
    }

    @Override
    public void onInstagramAuthenticationCompleted(String accessToken) {
        Toast.makeText(MainActivity.this, accessToken, Toast.LENGTH_SHORT).show();
    }

    private void showAuthenticationDialog() {
        DialogFragment dialog = InstagramAuthenticationDialog.newInstance();
        dialog.show(getSupportFragmentManager(), InstagramAuthenticationDialog.class.getSimpleName());
    }

}
