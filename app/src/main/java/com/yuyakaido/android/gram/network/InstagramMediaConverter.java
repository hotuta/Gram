package com.yuyakaido.android.gram.network;

import com.yuyakaido.android.gram.model.InstagramMedia;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yuyakaido on 2/27/16.
 */
public class InstagramMediaConverter {

    private InstagramMediaConverter() {}

    public static List<InstagramMedia> convert(RecentMediaResponse recentMediaResponse) {
        List<InstagramMedia> instagramMedias = new ArrayList<>();
        for (InstagramMediaResponse response : recentMediaResponse.data) {
            InstagramMedia instagramMedia = new InstagramMedia();
            instagramMedia.instagramMediaId = response.id;
            Map<String, InstagramMediaResponse.InstagramImageResponse> images = response.images;
            instagramMedia.thumbnailUrl = images.get("thumbnail").url;
            instagramMedia.lowResolutionUrl = images.get("low_resolution").url;
            instagramMedia.standardResolutionUrl = images.get("standard_resolution").url;
            instagramMedias.add(instagramMedia);
        }
        return instagramMedias;
    }

}
