package app.netlify.dev4rju9.videoVerse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

import java.util.ArrayList;

import app.netlify.dev4rju9.videoVerse.databinding.ActivityPlayerBinding;
import app.netlify.dev4rju9.videoVerse.models.Video;

public class PlayerActivity extends AppCompatActivity {

    public static int POS = -1;
    public static boolean IS_FOLDER = false, REPEAT = false;
    private ActivityPlayerBinding binding;
    public static ArrayList<Video> PLAYER_LIST;
    private static ExoPlayer exoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setTheme(R.style.PlayerActivityTheme);
        setContentView(binding.getRoot());

        // For Immersive Mode.
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), binding.getRoot());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        initializePlayer();
        initializeBinding();

    }

    private void initializePlayer () {

        if (IS_FOLDER) {
            PLAYER_LIST = FoldersActivity.LIST;
        } else {
            PLAYER_LIST = MainActivity.VIDEO_LIST;
        }
        createPlayer();
    }

    @SuppressLint("PrivateResource")
    private void initializeBinding () {

        binding.playerBackButton.setOnClickListener( v -> finish());
        binding.playPauseButton.setOnClickListener( v -> {
            if (exoPlayer.isPlaying()) pauseVideo();
            else playVideo();
        });
        binding.prevButton.setOnClickListener( v -> nextPrevVideo(false));
        binding.pauseButton.setOnClickListener( v -> nextPrevVideo(true));
        binding.repeatButton.setOnClickListener( v -> {
            if (REPEAT) {
                REPEAT = false;
                exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
                binding.repeatButton.setImageResource(com.google.android.exoplayer2.ui.R.drawable.exo_controls_repeat_off);
            } else {
                REPEAT = true;
                exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
                binding.repeatButton.setImageResource(com.google.android.exoplayer2.ui.R.drawable.exo_controls_repeat_all);
            }
        });

    }

    private void createPlayer () {

        // make video title movable.
        binding.videoTitle.setText(PLAYER_LIST.get(POS).getTitle());
        binding.videoTitle.setSelected(true);

        release();
        exoPlayer = new ExoPlayer.Builder(this).build();
        binding.playerView.setPlayer(exoPlayer);

        MediaItem mediaItem = MediaItem.fromUri(PlayerActivity.PLAYER_LIST.get(POS).getVideoUri());
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        playVideo();

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                if (playbackState == Player.STATE_ENDED) nextPrevVideo(true);
            }
        });

    }

    private void playVideo () {
        binding.playPauseButton.setImageResource(R.drawable.pause_icon);
        exoPlayer.play();
    }

    private void pauseVideo () {
        binding.playPauseButton.setImageResource(R.drawable.play_icon);
        exoPlayer.pause();
    }

    private void nextPrevVideo (boolean isNext) {
        if (isNext) setPOS(true);
        else setPOS(false);
        createPlayer();
    }

    private void setPOS (boolean isIncreament) {
        if (!REPEAT) {
            if (isIncreament) {
                if (PLAYER_LIST.size()-1 == POS) POS = 0;
                else ++POS;
            } else {
                if (POS == 0) POS = PLAYER_LIST.size() - 1;
                else --POS;
            }
        }
    }

    private void release () {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }
}