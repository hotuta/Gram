package com.yuyakaido.android.gram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by yuyakaido on 2/27/16.
 */
public class InstagramMediaGridAdapter extends ArrayAdapter<InstagramMedia> {

    private LayoutInflater layoutInflater;
    private List<InstagramMedia> instagramMedias;

    public InstagramMediaGridAdapter(Context context, List<InstagramMedia> instagramMedias) {
        super(context, 0, instagramMedias);
        this.layoutInflater = LayoutInflater.from(context);
        this.instagramMedias = instagramMedias;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_instagram_media_grid, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        InstagramMedia instagramMedia = getItem(position);

        Glide.with(getContext())
                .load(instagramMedia.thumbnailUrl)
                .into(holder.image);

        return convertView;
    }

    @Override
    public InstagramMedia getItem(int position) {
        return instagramMedias.get(position);
    }

    @Override
    public int getCount() {
        return instagramMedias.size();
    }

    public void setInstagramMedias(List<InstagramMedia> instagramMedias) {
        this.instagramMedias = instagramMedias;
    }

    public static class ViewHolder {
        public ImageView image;

        public ViewHolder(View view) {
            this.image = (ImageView) view.findViewById(R.id.item_instagram_media_grid_image);
        }
    }

}
