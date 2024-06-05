package kr.ac.cu.moai.dcumusicplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class ListViewMP3Adapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<String> mp3s;

    ListViewMP3Adapter(Context context, ArrayList<String> mp3s) {
        this.context = context;
        this.mp3s = mp3s;
    }

    @Override
    public int getCount() {
        return mp3s.size();
    }

    @Override
    public String getItem(int position) {
        return mp3s.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try (MediaMetadataRetriever retriever = new MediaMetadataRetriever()) {
            @SuppressLint({"ViewHolder", "InflateParams"})
            View view = LayoutInflater.from(context).inflate(R.layout.list_view_mp3, null);

            ImageView ivCover = view.findViewById(R.id.ivCover);
            retriever.setDataSource(mp3s.get(position));
            byte[] b = retriever.getEmbeddedPicture();
            Bitmap cover = BitmapFactory.decodeByteArray(b, 0, b.length);
            ivCover.setImageBitmap(cover);

            TextView tvTitle = view.findViewById(R.id.tvTitle);
            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            tvTitle.setText(title);

            TextView tvDuration = view.findViewById(R.id.tvDuration);
            tvDuration.setText(getDuration(retriever));

            TextView tvArtist = view.findViewById(R.id.tvArtist);
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            tvArtist.setText(artist);

            return view;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getDuration(MediaMetadataRetriever retriever) {
        long d = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        String s = String.valueOf((d % 60000) / 1000), m = String.valueOf(d / 60000);
        if(s.length() == 1) s = "0" + s;
        if(m.length() == 1) m = "0" + m;

        return m + ":" + s;
    }

}