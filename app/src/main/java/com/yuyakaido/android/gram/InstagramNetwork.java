package com.yuyakaido.android.gram;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by yuyakaido on 2/27/16.
 */
public class InstagramNetwork {

    private static final String BASE_URL = "https://api.instagram.com/v1/";

    public static Observable<List<InstagramMedia>> getRecentMedia(String accessToken) {
        InstagramService service = new Retrofit.Builder()
                .client(new OkHttpClient())
                .baseUrl(BASE_URL)
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

    public interface InstagramService {

        @GET("users/self/media/recent")
        Observable<RecentMediaResponse> getRecentMedia(@Query("access_token") String accessToken);

    }

}
