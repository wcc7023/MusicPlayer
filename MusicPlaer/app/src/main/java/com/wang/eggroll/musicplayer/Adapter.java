package com.wang.eggroll.musicplayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by eggroll on 17/02/2017.
 */

public class Adapter extends BaseAdapter{
    private List<MusicItem> musicItemList;

    public Adapter(List<MusicItem> musicItemList) {
        this.musicItemList = musicItemList;
    }

    @Override
    public int getCount() {
        return musicItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return musicItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.music_item, null);

            TextView name = (TextView) convertView.findViewById(R.id.music_name);
            TextView album = (TextView) convertView.findViewById(R.id.music_album);
            TextView artist = (TextView) convertView.findViewById(R.id.music_artist);
            TextView duration = (TextView) convertView.findViewById(R.id.music_duration);

            viewHolder = new ViewHolder(name, album, artist, duration);
            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MusicItem musicItem = musicItemList.get(position);

        viewHolder.getMusicName().setText(musicItem.getMusicName());
        viewHolder.getMusicAlbum().setText(musicItem.getMusicAlbum());
        viewHolder.getMusicArtist().setText(musicItem.getMusicArtist());
        viewHolder.getMusicDuration().setText(musicItem.getDuration());

        return convertView;
    }

    private static class ViewHolder{

        TextView musicName;
        TextView musicAlbum;
        TextView musicArtist;
        TextView musicDuration;

        public ViewHolder(TextView musicName, TextView musicAlbum, TextView musicArtist, TextView musicDuration) {
            this.musicName = musicName;
            this.musicAlbum = musicAlbum;
            this.musicArtist = musicArtist;
            this.musicDuration = musicDuration;
        }

        public TextView getMusicName() {
            return musicName;
        }

        public TextView getMusicAlbum() {
            return musicAlbum;
        }

        public TextView getMusicArtist() {
            return musicArtist;
        }

        public TextView getMusicDuration() {
            return musicDuration;
        }
    }
}
