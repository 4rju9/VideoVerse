package app.netlify.dev4rju9.videoVerse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;

import app.netlify.dev4rju9.videoVerse.databinding.ActivityPlayerBinding;

public class PlayerActivity extends AppCompatActivity {

    ActivityPlayerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();

        if (intent != null) {

            ExoPlayer exoPlayer = new ExoPlayer.Builder(this).build();
            binding.playerView.setPlayer(exoPlayer);

            MediaItem mediaItem = MediaItem.fromUri(MainActivity.VIDEO_LIST.get(intent.getIntExtra("pos", 0)).getVideoUri());
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.play();

        }

    }
}