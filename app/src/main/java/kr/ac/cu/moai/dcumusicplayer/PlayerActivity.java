package kr.ac.cu.moai.dcumusicplayer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.util.Objects;

public class PlayerActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private Button playstopButton;
    private Button skipButton;
    private SeekBar seekBar;
    private TextView musicDurations;

    int playDurations;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ImageView ivCover = findViewById(R.id.ivCover);
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvDuration = findViewById(R.id.tvDuration);
        TextView tvArtist = findViewById(R.id.tvArtist);
        playstopButton = findViewById(R.id.playstopButton);
        skipButton = findViewById(R.id.skipButton);
        seekBar = findViewById(R.id.seekbar);
        musicDurations = findViewById(R.id.musicDurations);

        Intent intent = getIntent();
        String mp3file = intent.getStringExtra("mp3");

        try (MediaMetadataRetriever retriever = new MediaMetadataRetriever()) {
            retriever.setDataSource(mp3file);
            byte[] b = retriever.getEmbeddedPicture();
            if (b != null) {
                Bitmap cover = BitmapFactory.decodeByteArray(b, 0, b.length);
                ivCover.setImageBitmap(cover);
            }

            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            tvTitle.setText(title);

            tvDuration.setText(ListViewMP3Adapter.getDuration(retriever));

            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            tvArtist.setText(artist);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(mp3file);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        playDurations = mediaPlayer.getDuration();
        seekBar.setMax(playDurations);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playstopButton.setText("▶");
                seekBar.setProgress(0);
            }
        });

        playstopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playstopButton.setText("▶");
                } else {
                    mediaPlayer.start();
                    playstopButton.setText("II");
                    updateSeekBar();
                }
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
                musicDurations.setText(convertMillisecondsToTime(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void updateSeekBar() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        if (mediaPlayer.isPlaying()) {
            Runnable updater = new Runnable() {
                @Override
                public void run() {
                    updateSeekBar();
                    musicDurations.setText(convertMillisecondsToTime(mediaPlayer.getCurrentPosition()));
                }
            };
            seekBar.postDelayed(updater, 1000);
        }
    }

    private String convertMillisecondsToTime(int milliseconds) {
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}