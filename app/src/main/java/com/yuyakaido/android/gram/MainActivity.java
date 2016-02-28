package com.yuyakaido.android.gram;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements InstagramAuthenticationDialog.InstagramAuthenticationCallback {

    private static final String SP_KEY_INSTAGRAM_ACCESS_TOKEN = "SP_KEY_INSTAGRAM_ACCESS_TOKEN";

    private View authenticateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authenticateButton = findViewById(R.id.activity_main_authentication_button);

        String accessToken = SharedPreferencesUtil.getString(this, SP_KEY_INSTAGRAM_ACCESS_TOKEN);
        if (accessToken == null) {
            authenticateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAuthenticationDialog();
                }
            });
        } else {
            authenticateButton.setVisibility(View.GONE);
            fetchInstagramMedias(accessToken);
        }
    }

    @Override
    public void onInstagramAuthenticationCompleted(String accessToken) {
        SharedPreferencesUtil.saveString(this, SP_KEY_INSTAGRAM_ACCESS_TOKEN, accessToken);
        Toast.makeText(MainActivity.this, accessToken, Toast.LENGTH_SHORT).show();
        authenticateButton.setVisibility(View.GONE);
    }

    private void fetchInstagramMedias(String accessToken) {}

    private void showAuthenticationDialog() {
        DialogFragment dialog = InstagramAuthenticationDialog.newInstance();
        dialog.show(getSupportFragmentManager(), InstagramAuthenticationDialog.class.getSimpleName());
    }

}
