package app.netlify.dev4rju9.videoVerse.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.netlify.dev4rju9.videoVerse.databinding.VideoViewBinding;
import app.netlify.dev4rju9.videoVerse.models.Video;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    Context context;
    ArrayList<Video> videoList;

    public VideoAdapter (Context context, ArrayList<Video> videoList) {

        this.context =  context;
        this.videoList = videoList;

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

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {

        TextView title, folder, duration;

        public VideoViewHolder(VideoViewBinding binding) {
            super(binding.getRoot());
            title = binding.videoTitleName;
            folder = binding.videoFolderName;
            duration = binding.videoDuration;
        }
    }

}
