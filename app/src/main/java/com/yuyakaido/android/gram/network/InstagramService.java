package com.yuyakaido.android.gram.network;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by yuyakaido on 2/28/16.
 */
public interface InstagramService {
    @GET("users/self/media/recent")
    Observable<RecentMediaResponse> getRecentMedia(@Query("access_token") String accessToken);
}
