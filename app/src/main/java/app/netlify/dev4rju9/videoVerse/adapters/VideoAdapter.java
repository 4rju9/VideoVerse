package app.netlify.dev4rju9.videoVerse.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.util.ArrayList;

import app.netlify.dev4rju9.videoVerse.FoldersActivity;
import app.netlify.dev4rju9.videoVerse.MainActivity;
import app.netlify.dev4rju9.videoVerse.PlayerActivity;
import app.netlify.dev4rju9.videoVerse.R;
import app.netlify.dev4rju9.videoVerse.databinding.RenameFieldBinding;
import app.netlify.dev4rju9.videoVerse.databinding.VideoMoreFeaturesBinding;
import app.netlify.dev4rju9.videoVerse.databinding.VideoViewBinding;
import app.netlify.dev4rju9.videoVerse.models.Video;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    Context context;
    ArrayList<Video> videoList;
    int CODE;

    public VideoAdapter (Context context, ArrayList<Video> videoList, int CODE) {

        this.context =  context;
        this.videoList = videoList;
        this.CODE = CODE;

    }

    @NonNull
    @Override
    public VideoAdapter.VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoViewHolder(VideoViewBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.VideoViewHolder holder, int position) {

        holder.title.setText(videoList.get(position).getTitle());
        holder.folder.setText(videoList.get(position).getFoderName());
        holder.duration.setText(DateUtils.formatElapsedTime(videoList.get(position).getDuration()/1000));

        Glide.with(context)
                .asBitmap()
                .load(videoList.get(position).getVideoUri())
                .apply(new RequestOptions().placeholder(R.drawable.play).centerCrop())
                .into(holder.image);

        holder.root.setOnClickListener( v -> {
            if (MainActivity.isSearched) CODE = 3;
            if (CODE == 1) PlayerActivity.isFolder = true;
            PlayerActivity.pipStatus = CODE;
            sendIntent(position, CODE);
        });

        holder.root.setOnLongClickListener( v -> {
            View customView = LayoutInflater.from(context).inflate(R.layout.video_more_features, holder.root, false);
            VideoMoreFeaturesBinding binding = VideoMoreFeaturesBinding.bind(customView);
            AlertDialog dialog = new MaterialAlertDialogBuilder(context).setView(customView)
                    .create();
            dialog.show();

            binding.renameButton.setOnClickListener( mv -> {
                requestPermissionR();
                dialog.dismiss();
                View view = LayoutInflater.from(context).inflate(R.layout.rename_field, holder.root, false);
                RenameFieldBinding rename_binding = RenameFieldBinding.bind(view);
                AlertDialog rename_dialog = new MaterialAlertDialogBuilder(context).setView(view)
                        .setCancelable(false)
                        .setPositiveButton("Rename", (self, which) -> {

                            File currentFile = new File(videoList.get(position).getPath());
                            String new_name = rename_binding.renameField.getText().toString();

                            if (new_name != null && currentFile.exists() && (!new_name.isEmpty())) {
                                File new_file = new File(currentFile.getParent(),
                                        new_name + currentFile.getName().substring(currentFile.getName().lastIndexOf(".")));
                                if (currentFile.renameTo(new_file)) {
                                    MediaScannerConnection.scanFile(context, new String[] {new_file.toString()},
                                            new String[] {"video/*"}, null);

                                    if (MainActivity.isSearched) {
                                        MainActivity.SEARCHED_LIST.get(position).setTitle(new_name);
                                        MainActivity.SEARCHED_LIST.get(position).setPath(new_file.getPath());
                                        MainActivity.SEARCHED_LIST.get(position).setVideoUri(Uri.fromFile(new_file));
                                        notifyItemChanged(position);
                                    } else if (PlayerActivity.isFolder) {
                                        FoldersActivity.LIST.get(position).setTitle(new_name);
                                        FoldersActivity.LIST.get(position).setPath(new_file.getPath());
                                        FoldersActivity.LIST.get(position).setVideoUri(Uri.fromFile(new_file));
                                        notifyItemChanged(position);
                                        MainActivity.dataChanged = true;
                                    } else {
                                        MainActivity.VIDEO_LIST.get(position).setTitle(new_name);
                                        MainActivity.VIDEO_LIST.get(position).setPath(new_file.getPath());
                                        MainActivity.VIDEO_LIST.get(position).setVideoUri(Uri.fromFile(new_file));
                                        notifyItemChanged(position);
                                    }

                                } else Toast.makeText(context, "Permission Denied!!", Toast.LENGTH_SHORT).show();
                            }

                            self.dismiss();
                        })
                        .setNegativeButton("Cancel", (self, which) -> self.dismiss())
                        .create();
                rename_dialog.show();
                rename_binding.renameField.setText(new SpannableStringBuilder(videoList.get(position).getTitle()));
                rename_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(MaterialColors.getColor(context, R.attr.ThemePrimary, Color.RED));
                rename_dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(MaterialColors.getColor(context, R.attr.ThemePrimary, Color.RED));
                rename_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(MaterialColors.getColor(context, R.attr.ThemeTextColor, Color.WHITE));
                rename_dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(MaterialColors.getColor(context, R.attr.ThemeTextColor, Color.WHITE));
            });

            return true;
        });

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    private void sendIntent (int pos, int CODE) {

        PlayerActivity.POS = pos;
        PlayerActivity.LIST_CODE = CODE;
        Intent intent = new Intent(context, PlayerActivity.class);
        ContextCompat.startActivity(context, intent, null);

    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList (ArrayList<Video> newList) {
        videoList = new ArrayList<>();
        videoList.addAll(newList);
        notifyDataSetChanged();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {

        TextView title, folder, duration;
        ImageView image;
        LinearLayoutCompat root;

        public VideoViewHolder(VideoViewBinding binding) {
            super(binding.getRoot());
            title = binding.videoTitleName;
            folder = binding.videoFolderName;
            duration = binding.videoDuration;
            image = binding.videoImage;
            root = binding.getRoot();
        }
    }

    private void requestPermissionR () {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:"+context.getApplicationContext().getPackageName()));
                ContextCompat.startActivity(context, intent, null);
            }
        }

    }

}
