package app.netlify.dev4rju9.videoVerse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;

import java.util.ArrayList;

import app.netlify.dev4rju9.videoVerse.databinding.ActivityPlayerBinding;
import app.netlify.dev4rju9.videoVerse.models.Video;

public class PlayerActivity extends AppCompatActivity {

    public static int POS = -1;
    public static boolean IS_FOLDER = false;
    ActivityPlayerBinding binding;
    public static ArrayList<Video> PLAYER_LIST;
    public static ExoPlayer exoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializePlayer();

    }

    private void initializePlayer () {

        if (IS_FOLDER) {
            PLAYER_LIST = new ArrayList<>();
            PLAYER_LIST = FoldersActivity.LIST;
        } else {
            PLAYER_LIST = new ArrayList<>();
            PLAYER_LIST = MainActivity.VIDEO_LIST;
        }

        createPlayer();

    }

    private void createPlayer () {

        // make video title movable.
        binding.videoTitle.setText(PLAYER_LIST.get(POS).getTitle());
        binding.videoTitle.setSelected(true);

        exoPlayer = new ExoPlayer.Builder(this).build();
        binding.playerView.setPlayer(exoPlayer);

        MediaItem mediaItem = MediaItem.fromUri(PlayerActivity.PLAYER_LIST.get(POS).getVideoUri());
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.prepare();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exoPlayer.release();
    }
}