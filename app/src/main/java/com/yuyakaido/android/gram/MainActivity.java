package com.yuyakaido.android.gram;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity
        implements InstagramAuthenticationDialog.InstagramAuthenticationCallback,
                   AdapterView.OnItemClickListener {

    private static final String SP_KEY_INSTAGRAM_ACCESS_TOKEN = "SP_KEY_INSTAGRAM_ACCESS_TOKEN";

    private View authenticateButton;
    private GridView gridView;
    private InstagramMediaGridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authenticateButton = findViewById(R.id.activity_main_authentication_button);

        adapter = new InstagramMediaGridAdapter(this, new ArrayList<InstagramMedia>());
        gridView = (GridView) findViewById(R.id.activity_main_grid_view);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);

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
        fetchInstagramMedias(accessToken);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        InstagramMedia instagramMedia = adapter.getItem(position);
        startActivity(PreviewMediaActivity.createIntent(this, instagramMedia));
    }

    private void fetchInstagramMedias(String accessToken) {
        InstagramNetwork.getRecentMedia(accessToken)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<InstagramMedia>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<InstagramMedia> instagramMedias) {
                        authenticateButton.setVisibility(View.GONE);
                        gridView.setVisibility(View.VISIBLE);
                        adapter.setInstagramMedias(instagramMedias);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void showAuthenticationDialog() {
        DialogFragment dialog = InstagramAuthenticationDialog.newInstance();
        dialog.show(getSupportFragmentManager(), InstagramAuthenticationDialog.class.getSimpleName());
    }

}
