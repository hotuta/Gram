package com.yuyakaido.android.gram;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by yuyakaido on 2/28/16.
 */
public class PreviewMediaActivity extends AppCompatActivity {

    private static final String ARGS_INSTAGRAM_MEDIA = "ARGS_INSTAGRAM_MEDIA";

    public static Intent createIntent(Context context, InstagramMedia instagramMedia) {
        Intent intent = new Intent(context, PreviewMediaActivity.class);
        intent.putExtra(ARGS_INSTAGRAM_MEDIA, instagramMedia);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_media);

        Intent intent = getIntent();
        InstagramMedia instagramMedia = (InstagramMedia) intent.getSerializableExtra(ARGS_INSTAGRAM_MEDIA);

        ImageView imageView = (ImageView) findViewById(R.id.activity_preview_media_image);
        Glide.with(this)
                .load(instagramMedia.standardResolutionUrl)
                .into(imageView);
    }

}
