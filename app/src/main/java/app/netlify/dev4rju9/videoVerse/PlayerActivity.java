package app.netlify.dev4rju9.videoVerse;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import app.netlify.dev4rju9.videoVerse.databinding.ActivityPlayerBinding;
import app.netlify.dev4rju9.videoVerse.databinding.MoreFeaturesBinding;
import app.netlify.dev4rju9.videoVerse.models.Video;

public class PlayerActivity extends AppCompatActivity {

    public static int POS = -1;
    public static boolean IS_FOLDER = false;
    private ActivityPlayerBinding binding;
    public static ArrayList<Video> PLAYER_LIST;
    private static ExoPlayer exoPlayer;
    private boolean repeat = false, isFullscreen = false, isLocked = false;
    private Runnable runnable;

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
        setRepeatIcon(repeat);

    }

    @SuppressLint("PrivateResource")
    private void initializeBinding () {

        binding.playerBackButton.setOnClickListener( v -> finish());
        binding.playPauseButton.setOnClickListener( v -> {
            if (exoPlayer.isPlaying()) pauseVideo();
            else playVideo();
        });
        binding.prevButton.setOnClickListener( v -> nextPrevVideo(false));
        binding.nextButton.setOnClickListener( v -> nextPrevVideo(true));
        binding.repeatButton.setOnClickListener( v -> {
            if (repeat) {
                repeat = false;
                exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
                setRepeatIcon(repeat);
            } else {
                repeat = true;
                exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
                setRepeatIcon(repeat);
            }
        });
        binding.fullscreenButton.setOnClickListener( v -> {
            if (isFullscreen) {
                isFullscreen = false;
                playInFullscreen(false);
            } else {
                isFullscreen = true;
                playInFullscreen(true);
            }
        });

        binding.lockButton.setOnClickListener( v -> {
            if (isLocked) {
                isLocked = false;
                binding.playerView.setUseController(true);
                binding.playerView.showController();
                binding.lockButton.setImageResource(R.drawable.lock_open_icon);
            } else {
                isLocked = true;
                binding.playerView.hideController();
                binding.playerView.setUseController(false);
                binding.lockButton.setImageResource(R.drawable.lock_close_icon);
            }
        });

        binding.menuButton.setOnClickListener( v -> {
            pauseVideo();
            View customDialog = LayoutInflater.from(this).inflate(R.layout.more_features, binding.getRoot(), false);
            MoreFeaturesBinding binding = MoreFeaturesBinding.bind(customDialog);
            AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                    .setView(customDialog)
                    .setOnCancelListener( d -> playVideo())
                    .setBackground(new ColorDrawable(0xCC00BEF7)).create();
            dialog.show();
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

        playInFullscreen(isFullscreen);
        setVisibility();

    }

    private void setRepeatIcon (boolean repeat) {

        if (repeat) binding.repeatButton.setImageResource(com.google
                .android.exoplayer2.ui.R.drawable.exo_controls_repeat_all);
        else binding.repeatButton.setImageResource(com.google
                .android.exoplayer2.ui.R.drawable.exo_controls_repeat_off);

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
        if (repeat) return;
        if (isIncreament) {
            if (PLAYER_LIST.size()-1 == POS) POS = 0;
            else ++POS;
        } else {
            if (POS == 0) POS = PLAYER_LIST.size() - 1;
            else --POS;
        }
    }

    private void playInFullscreen (boolean enable) {
        if (enable) {
            binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            exoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            binding.fullscreenButton.setImageResource(R.drawable.fullscreen_exit_icon);
        } else {
            binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            exoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            binding.fullscreenButton.setImageResource(R.drawable.fullscreen_icon);
        }
    }

    private void setVisibility () {
        runnable = () -> {
            if (binding.playerView.isControllerVisible()) changeVisibility(View.VISIBLE);
            else changeVisibility(View.INVISIBLE);
            new Handler(Looper.getMainLooper()).postDelayed(runnable, 280);
        };
        new Handler().postDelayed(runnable, 0);
    }

    private void changeVisibility (int visibility) {
        binding.topController.setVisibility(visibility);
        binding.bottomController.setVisibility(visibility);
        binding.playPauseButton.setVisibility(visibility);
        if (!isLocked) binding.lockButton.setVisibility(visibility);
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