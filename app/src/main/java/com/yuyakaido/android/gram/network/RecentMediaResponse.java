package com.yuyakaido.android.gram.network;

import com.google.gson.annotations.SerializedName;
import com.yuyakaido.android.gram.network.InstagramMediaResponse;

import java.util.List;

/**
 * Created by yuyakaido on 2/27/16.
 */
public class RecentMediaResponse {

    @SerializedName("data")
    public List<InstagramMediaResponse> data;

}
