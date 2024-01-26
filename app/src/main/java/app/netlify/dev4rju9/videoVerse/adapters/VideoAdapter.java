package app.netlify.dev4rju9.videoVerse.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;

import app.netlify.dev4rju9.videoVerse.PlayerActivity;
import app.netlify.dev4rju9.videoVerse.R;
import app.netlify.dev4rju9.videoVerse.databinding.VideoViewBinding;
import app.netlify.dev4rju9.videoVerse.models.Video;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    Context context;
    ArrayList<Video> videoList;
    boolean isFolder;

    public VideoAdapter (Context context, ArrayList<Video> videoList, boolean isFolder) {

        this.context =  context;
        this.videoList = videoList;
        this.isFolder = isFolder;

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
            if (isFolder) PlayerActivity.pipStatus = 1;
            else PlayerActivity.pipStatus = 2;
            sendIntent(position, isFolder);
        });

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    private void sendIntent (int pos, boolean isFolder) {

        PlayerActivity.POS = pos;
        PlayerActivity.IS_FOLDER = isFolder;
        Intent intent = new Intent(context, PlayerActivity.class);
        ContextCompat.startActivity(context, intent, null);

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
