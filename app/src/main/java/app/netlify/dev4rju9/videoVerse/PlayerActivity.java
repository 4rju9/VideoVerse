package app.netlify.dev4rju9.videoVerse;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.media.audiofx.LoudnessEnhancer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import app.netlify.dev4rju9.videoVerse.databinding.ActivityPlayerBinding;
import app.netlify.dev4rju9.videoVerse.databinding.BoosterBinding;
import app.netlify.dev4rju9.videoVerse.databinding.MoreFeaturesBinding;
import app.netlify.dev4rju9.videoVerse.databinding.PlaybackSpeedDialogBinding;
import app.netlify.dev4rju9.videoVerse.models.Video;

public class PlayerActivity extends AppCompatActivity {

    public static int POS = -1;
    public static boolean IS_FOLDER = false;
    private ActivityPlayerBinding binding;
    public static ArrayList<Video> PLAYER_LIST;
    private static ExoPlayer exoPlayer;
    private boolean repeat = false, isFullscreen = false, isLocked = false;
    private Runnable runnable;
    private DefaultTrackSelector trackSelector;
    private static LoudnessEnhancer loudnessEnhancer;
    private static float speed = 1.0f;
    private static Timer timer;

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

    @SuppressLint({"PrivateResource", "SetTextI18n"})
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
            View moreFeaturesView = LayoutInflater.from(this).inflate(R.layout.more_features, binding.getRoot(), false);
            MoreFeaturesBinding featuresBinding = MoreFeaturesBinding.bind(moreFeaturesView);
            AlertDialog mainDialog = new MaterialAlertDialogBuilder(this)
                    .setView(moreFeaturesView)
                    .setOnCancelListener( d -> playVideo())
                    .setBackground(new ColorDrawable(0xB300BEF7)).create();
            mainDialog.show();

            featuresBinding.audioTrack.setOnClickListener( view -> {
                mainDialog.dismiss();
                pauseVideo();

                ArrayList<String> audioTrack = new ArrayList<>();
                int length = exoPlayer.getCurrentTrackGroups().length;
                for (int i=0; i<length; i++) {
                    if (exoPlayer.getCurrentTrackGroups().get(i).getFormat(0)
                            .selectionFlags == C.SELECTION_FLAG_DEFAULT) {
                        String lang = new Locale(exoPlayer.getCurrentTrackGroups()
                                        .get(i).getFormat(0).language).getDisplayLanguage();
                        if (!lang.equals("und")) audioTrack.add(lang);
                    }
                }

                new MaterialAlertDialogBuilder(this, R.style.alertDialog)
                        .setTitle("Select Language")
                        .setOnCancelListener( d -> playVideo())
                        .setBackground(new ColorDrawable(0xB300BEF7))
                        .setItems(audioTrack.toArray(new String[0]),
                                (dial, pos) -> trackSelector.setParameters(
                                        trackSelector.buildUponParameters()
                                                .setPreferredAudioLanguage(audioTrack.get(pos))
                                ))
                        .create().show();
            });

            featuresBinding.subtitles.setOnClickListener( view -> {
                boolean isSubtitles = trackSelector.getParameters()
                        .getRendererDisabled(C.TRACK_TYPE_VIDEO);
                trackSelector.setParameters(
                            new DefaultTrackSelector.Parameters.Builder(this)
                                    .setRendererDisabled(C.TRACK_TYPE_VIDEO, !isSubtitles).build());
                String message = "Subtitles on";
                if (!isSubtitles) message = "Subtitles off";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                mainDialog.dismiss();
                playVideo();
            });

            featuresBinding.booster.setOnClickListener( view -> {
                mainDialog.dismiss();
                View boosterView = LayoutInflater.from(this).inflate(R.layout.booster, binding.getRoot(), false);
                BoosterBinding boosterBinding = BoosterBinding.bind(boosterView);
                AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                        .setView(boosterView)
                        .setOnCancelListener( d -> playVideo())
                        .setPositiveButton("OK", (self, pos) -> {
                            loudnessEnhancer.setTargetGain(
                                    boosterBinding.boosterBar.getProgress() * 100);
                            playVideo();
                            self.dismiss();
                        })
                        .setBackground(new ColorDrawable(0xB300BEF7)).create();
                dialog.show();

                int loudness = (int)loudnessEnhancer.getTargetGain();
                boosterBinding.boosterBar.setProgress(loudness/100);
                boosterBinding.boosterText.setText("Audio Boost\n\n" + (loudness/10)  + "%");
                boosterBinding.boosterBar.setOnProgressChangeListener( integer -> {
                    boosterBinding.boosterText.setText("Audio Boost\n\n" + (integer*10) + "%");
                    return null;
                });

            });

            featuresBinding.playBackSpeed.setOnClickListener( view -> {
                mainDialog.dismiss();
                playVideo();
                View speedView = LayoutInflater.from(this).inflate(R.layout.playback_speed_dialog, binding.getRoot(), false);
                PlaybackSpeedDialogBinding speedBinding = PlaybackSpeedDialogBinding.bind(speedView);
                AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                        .setView(speedView)
                        .setCancelable(false)
                        .setPositiveButton("OK", (self, pos) -> {
                            self.dismiss();
                        })
                        .setBackground(new ColorDrawable(0xB300BEF7)).create();
                dialog.show();

                speedBinding.speedText.setText(speed + "X");

                speedBinding.speedMinus.setOnClickListener( minusView -> {
                    changeSpeed(false);
                    speedBinding.speedText.setText(speed + "X");
                });

                speedBinding.speedPlus.setOnClickListener( plusView -> {
                    changeSpeed(true);
                    speedBinding.speedText.setText(speed + "X");
                });

            });

            featuresBinding.sleepTimer.setOnClickListener( view -> {
                mainDialog.dismiss();
                if (timer != null) {
                    Toast.makeText(this, "Timer is already running!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                AtomicInteger sleep = new AtomicInteger(15);
                View sleepView = LayoutInflater.from(this).inflate(R.layout.playback_speed_dialog, binding.getRoot(), false);
                PlaybackSpeedDialogBinding sleepBinding = PlaybackSpeedDialogBinding.bind(sleepView);
                AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                        .setView(sleepView)
                        .setCancelable(false)
                        .setPositiveButton("OK", (self, pos) -> {
                            timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    moveTaskToBack(true);
                                    System.exit(1);
                                }
                            }, (long)(sleep.get() * 60 * 1000L));
                            self.dismiss();
                        })
                        .setBackground(new ColorDrawable(0xB300BEF7)).create();
                dialog.show();

                sleepBinding.speedText.setText(sleep.get() + " Min");

                sleepBinding.speedMinus.setOnClickListener( minusView -> {
                    if (sleep.get() <= 15) return;
                    sleep.set(sleep.get() - 15);
                    sleepBinding.speedText.setText(sleep.get() + " Min");
                });

                sleepBinding.speedPlus.setOnClickListener( plusView -> {
                    if (sleep.get() >= 120) return;
                    sleep.set(sleep.get() + 15);
                    sleepBinding.speedText.setText(sleep.get() + " Min");
                });

            });

        });

    }

    private void createPlayer () {

        // make video title movable.
        binding.videoTitle.setText(PLAYER_LIST.get(POS).getTitle());
        binding.videoTitle.setSelected(true);

        release();
        speed = 1.0f;
        trackSelector = new DefaultTrackSelector(this);
        exoPlayer = new ExoPlayer.Builder(this)
                .setTrackSelector(trackSelector)
                .build();
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
        loudnessEnhancer = new LoudnessEnhancer(exoPlayer.getAudioSessionId());
        loudnessEnhancer.setEnabled(true);

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
        setPOS(isNext);
        createPlayer();
    }

    private void setPOS (boolean isIncrement) {
        if (repeat) return;
        if (isIncrement) {
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

    private void changeSpeed (boolean isIncrement) {
        if (isIncrement) {
            if (speed >= 3.0f) return;
            speed += 0.25f;
        } else {
            if (speed <= 0.25f) return;
            speed -= 0.25f;
        }
        exoPlayer.setPlaybackSpeed(speed);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }
}