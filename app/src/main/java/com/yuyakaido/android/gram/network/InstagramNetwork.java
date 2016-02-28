package com.yuyakaido.android.gram.network;

import com.yuyakaido.android.gram.model.InstagramMedia;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by yuyakaido on 2/27/16.
 */
public class InstagramNetwork {

    public static Observable<List<InstagramMedia>> getRecentMedia(String accessToken) {
        InstagramService service = new Retrofit.Builder()
                .baseUrl("https://api.instagram.com/v1/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(InstagramService.class);
        return service.getRecentMedia(accessToken)
                .map(new Func1<RecentMediaResponse, List<InstagramMedia>>() {
                    @Override
                    public List<InstagramMedia> call(RecentMediaResponse recentMediaResponse) {
                        return InstagramMediaConverter.convert(recentMediaResponse);
                    }
                });
    }

}
