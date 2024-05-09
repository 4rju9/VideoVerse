package app.netlify.dev4rju9.videoVerse.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import app.netlify.dev4rju9.videoVerse.MainActivity;
import app.netlify.dev4rju9.videoVerse.PlayerActivity;
import app.netlify.dev4rju9.videoVerse.R;
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

}
