package app.netlify.dev4rju9.videoVerse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.audiofx.LoudnessEnhancer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.io.File;
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

public class PlayerActivity extends AppCompatActivity implements AudioManager.OnAudioFocusChangeListener {

    public static int POS = -1;
    public static int LIST_CODE = 0;
    public static boolean isFolder = false;
    private ActivityPlayerBinding binding;
    public static ArrayList<Video> PLAYER_LIST;
    private static ExoPlayer exoPlayer;
    private boolean repeat = false, isFullscreen = false, isLocked = false;
    private DefaultTrackSelector trackSelector;
    private static LoudnessEnhancer loudnessEnhancer;
    private static float speed = 1.0f;
    private static Timer timer;
    public static int pipStatus = 0;
    private long currentPosition = 0L;
    private AudioManager audioManager;
    private ImageButton playPauseBtn, fullScreenBtn, repeatButton;
    private TextView videoTitle;

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
        makeImmersive();

        
        initializeUIButtons();

        // For handling video file intent;
        try {
            Intent myIntent = getIntent();
            if ((myIntent != null) && (myIntent.getData() != null) && (myIntent.getData().getScheme().contentEquals("content"))) {

                PLAYER_LIST = new ArrayList<>();
                POS = 0;

                Cursor cursor = getContentResolver().query(
                        myIntent.getData(),
                        new String[] {MediaStore.Video.Media.DATA},
                        null, null, null
                );

                if (cursor != null) {
                    cursor.moveToFirst();
                    @SuppressLint("Range")
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    File file = new File(path);
                    Video video = new Video("", file.getName(), "", "", path, 0L, Uri.fromFile(file));
                    PLAYER_LIST.add(video);
                    cursor.close();
                }
                createPlayer();
                initializeBinding();

            } else {
                initializePlayer();
                initializeBinding();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    
    private void initializeUIButtons () {
        playPauseBtn = findViewById(R.id.play_pause_button);
        fullScreenBtn = findViewById(R.id.fullscreen_button);
        videoTitle = findViewById(R.id.video_title);
        repeatButton = findViewById(R.id.repeat_button);
    }

    private void initializePlayer () {

        if (LIST_CODE == 1) {
            PLAYER_LIST = FoldersActivity.LIST;
        } else if (LIST_CODE == 2) {
            PLAYER_LIST = MainActivity.VIDEO_LIST;
        } else if (LIST_CODE == 3) {
            PLAYER_LIST = MainActivity.SEARCHED_LIST;
        } else if (LIST_CODE == 4) {
            if (isFolder) {
                PLAYER_LIST = FoldersActivity.LIST;
            } else {
                PLAYER_LIST = MainActivity.VIDEO_LIST;
            }
        }
        createPlayer();
        setRepeatIcon(repeat);

    }

    private void initStatus (int mode) {
        String ID = PLAYER_LIST.get(POS).getId();
        SharedPreferences preferences = getSharedPreferences(ID, Context.MODE_PRIVATE);

        if (mode == 1) {
            currentPosition = preferences.getLong("currentPosition", 0L);
        } else if (mode == 2) {
            currentPosition = exoPlayer.getCurrentPosition();
            if (currentPosition >= exoPlayer.getDuration() - 10000) return;
            preferences.edit()
                    .putLong("currentPosition", currentPosition).apply();
        } else {
            preferences.edit().remove("currentPosition").apply();
        }
    }

    @SuppressLint({"PrivateResource", "SetTextI18n", "ServiceCast"})
    private void initializeBinding () {

        binding.playerView.setOnClickListener( v -> {
            makeImmersive();
        });

        findViewById(R.id.rotation_button).setOnClickListener( v -> {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }
        });

        findViewById(R.id.rewind_frame).setOnClickListener(new DoubleClickListener(() -> {
            binding.playerView.showController();
            findViewById(R.id.rewind_button).setVisibility(View.VISIBLE);
            long position = exoPlayer.getCurrentPosition() - 10000;
            exoPlayer.seekTo(Math.max(0, position));
        }));
        findViewById(R.id.forward_frame).setOnClickListener(new DoubleClickListener(() -> {
            binding.playerView.showController();
            findViewById(R.id.forward_button).setVisibility(View.VISIBLE);
            long position = exoPlayer.getCurrentPosition() + 10000;
            long max = exoPlayer.getDuration();
            exoPlayer.seekTo(Math.min(position, max));
        }));

        findViewById(R.id.player_back_button).setOnClickListener( v -> finish());
        playPauseBtn.setOnClickListener( v -> {
            if (exoPlayer.isPlaying()) pauseVideo();
            else playVideo();
        });
        findViewById(R.id.prev_button).setOnClickListener( v -> nextPrevVideo(false));
        findViewById(R.id.next_button).setOnClickListener( v -> nextPrevVideo(true));
        repeatButton.setOnClickListener( v -> {
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
        fullScreenBtn.setOnClickListener( v -> {
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

        findViewById(R.id.menu_button).setOnClickListener( v -> {
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
                ArrayList<String> audioList = new ArrayList<>();

                for (Tracks.Group group : exoPlayer.getCurrentTracks().getGroups()) {
                    if (group.getType() == C.TRACK_TYPE_AUDIO) {
                        TrackGroup groupInfo = group.getMediaTrackGroup();
                        int length = groupInfo.length;
                        for (int i=0; i<length; i++) {
                            audioTrack.add(groupInfo.getFormat(i).language);
                            String text = "";
                            text += audioList.size() + 1 + " ";
                            text += new Locale(groupInfo.getFormat(i).language).getDisplayLanguage();
                            text += " (" + groupInfo.getFormat(i).label + ")";
                            audioList.add(text);
                        }
                    }
                }

                if (audioList.get(0).contains("null")) {
                    audioList.remove(0);
                    audioList.add(0, "1. Default Track");
                }

                new MaterialAlertDialogBuilder(this, R.style.alertDialog)
                        .setTitle("Select Language")
                        .setOnCancelListener( d -> playVideo())
                        .setPositiveButton("Disable", (self, pos) -> {
                            trackSelector.setParameters(trackSelector.buildUponParameters()
                                    .setRendererDisabled(C.TRACK_TYPE_AUDIO, true));
                            self.dismiss();
                        })
                        .setBackground(new ColorDrawable(0xB300BEF7))
                        .setItems(audioList.toArray(new String[0]),
                                (dial, pos) -> trackSelector.setParameters(
                                        trackSelector.buildUponParameters()
                                                .setRendererDisabled(C.TRACK_TYPE_AUDIO, false)
                                                .setPreferredAudioLanguage(audioTrack.get(pos))
                                ))
                        .create().show();
            });

            featuresBinding.subtitles.setOnClickListener( view -> {
                mainDialog.dismiss();
                pauseVideo();

                ArrayList<String> subtitles = new ArrayList<>();
                ArrayList<String> subtitlesList = new ArrayList<>();

                for (Tracks.Group group : exoPlayer.getCurrentTracks().getGroups()) {
                    if (group.getType() == C.TRACK_TYPE_TEXT) {
                        TrackGroup groupInfo = group.getMediaTrackGroup();
                        int length = groupInfo.length;
                        for (int i=0; i<length; i++) {
                            subtitles.add(groupInfo.getFormat(i).language);
                            String text = "";
                            text += subtitlesList.size() + 1 + " ";
                            text += new Locale(groupInfo.getFormat(i).language).getDisplayLanguage();
                            text += " (" + groupInfo.getFormat(i).label + ")";
                            subtitlesList.add(text);
                        }
                    }
                }

                new MaterialAlertDialogBuilder(this, R.style.alertDialog)
                        .setTitle("Select Subtitles")
                        .setOnCancelListener( d -> playVideo())
                        .setPositiveButton("Disable", (self, pos) -> {
                            trackSelector.setParameters(trackSelector.buildUponParameters()
                                    .setRendererDisabled(C.TRACK_TYPE_VIDEO, true));
                            self.dismiss();
                        })
                        .setBackground(new ColorDrawable(0xB300BEF7))
                        .setItems(subtitlesList.toArray(new String[0]),
                                (dial, pos) -> trackSelector.setParameters(
                                        trackSelector.buildUponParameters()
                                                .setRendererDisabled(C.TRACK_TYPE_VIDEO, false)
                                                .setPreferredTextLanguage(subtitles.get(pos))
                                ))
                        .create().show();
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
                            }, sleep.get() * 60 * 1000L);
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

            featuresBinding.pipMode.setOnClickListener( view -> {
                AppOpsManager appOps = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
                boolean status;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    status = appOps.checkOpNoThrow(AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
                            Process.myUid(), getPackageName()) == AppOpsManager.MODE_ALLOWED;
                    if (status) {
                        this.enterPictureInPictureMode(new PictureInPictureParams.Builder().build());
                        mainDialog.dismiss();
                        binding.playerView.hideController();
                        playVideo();
                        pipStatus = 0;
                    } else {
                        Intent intent = new Intent("android.settings.PICTURE_IN_PICTURE_SETTINGS",
                                Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "Feature not supported!", Toast.LENGTH_SHORT).show();
                    mainDialog.dismiss();
                    playVideo();
                }
            });

        });

    }

    private void createPlayer () {

        initStatus(1);

        // make video title movable.
        videoTitle.setText(PLAYER_LIST.get(POS).getTitle());
        videoTitle.setSelected(true);

        release();
        speed = 1.0f;
        trackSelector = new DefaultTrackSelector(this);
        exoPlayer = new ExoPlayer.Builder(this)
                .setTrackSelector(trackSelector)
                .build();
        binding.playerView.setPlayer(exoPlayer);

        MediaItem mediaItem = MediaItem.fromUri(PlayerActivity.PLAYER_LIST.get(POS).getVideoUri());
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.seekTo(currentPosition);
        exoPlayer.prepare();
        playVideo();

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                if (playbackState == Player.STATE_ENDED) {
                    initStatus(3);
                    nextPrevVideo(true);
                }
            }
        });

        playInFullscreen(isFullscreen);
        loudnessEnhancer = new LoudnessEnhancer(exoPlayer.getAudioSessionId());
        loudnessEnhancer.setEnabled(true);
        
        binding.playerView.setControllerVisibilityListener( it -> {
            if (isLocked) binding.lockButton.setVisibility(View.VISIBLE);
            else if (binding.playerView.isControllerVisible()) binding.lockButton.setVisibility(View.VISIBLE);
            else binding.lockButton.setVisibility(View.GONE);
            findViewById(R.id.forward_button).setVisibility(View.GONE);
            findViewById(R.id.rewind_button).setVisibility(View.GONE);
        });

    }

    private void setRepeatIcon (boolean repeat) {

        if (repeat) repeatButton.setImageResource(com.google
                .android.exoplayer2.ui.R.drawable.exo_controls_repeat_all);
        else repeatButton.setImageResource(com.google
                .android.exoplayer2.ui.R.drawable.exo_controls_repeat_off);

    }

    private void playVideo () {
        playPauseBtn.setImageResource(R.drawable.pause_icon);
        exoPlayer.play();
    }

    private void pauseVideo () {
        playPauseBtn.setImageResource(R.drawable.play_icon);
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
            fullScreenBtn.setImageResource(R.drawable.fullscreen_exit_icon);
        } else {
            binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            exoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            fullScreenBtn.setImageResource(R.drawable.fullscreen_icon);
        }
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
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, @NonNull Configuration newConfig) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
            if (pipStatus != 0) {
                finish();
                Intent intent = new Intent(this, PlayerActivity.class);
                PlayerActivity.LIST_CODE = pipStatus;
                startActivity(intent);
            }
            if (!isInPictureInPictureMode) pauseVideo();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (audioManager == null) audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioManager.abandonAudioFocus(this);
        initStatus(2);
        release();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange <= 0) pauseVideo();
        else playVideo();
    }

    private void makeImmersive () {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), binding.getRoot());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }

}