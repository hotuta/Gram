package com.yuyakaido.android.gram;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by yuyakaido on 2/27/16.
 */
public class InstagramMediaResponse {

    @SerializedName("id")
    public String id;

    @SerializedName("images")
    public Map<String, InstagramImageResponse> images;

    public static class InstagramImageResponse {
        @SerializedName("url")
        public String url;
    }

}
