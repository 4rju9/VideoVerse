package app.netlify.dev4rju9.videoVerse.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import app.netlify.dev4rju9.videoVerse.databinding.FolderViewBinding;
import app.netlify.dev4rju9.videoVerse.models.Folder;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    Context context;
    ArrayList<Folder> folderList;

    public FolderAdapter (Context context, ArrayList<Folder> folderList) {

        this.context =  context;
        this.folderList = folderList;

    }

    @NonNull
    @Override
    public FolderAdapter.FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FolderViewHolder(FolderViewBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FolderAdapter.FolderViewHolder holder, int position) {

        holder.title.setText(folderList.get(position).getFolderName());

    }

    @Override
    public int getItemCount() {
        return folderList.size();
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder {

        TextView title;

        public FolderViewHolder (FolderViewBinding binding) {
            super(binding.getRoot());
            title = binding.folderName;
        }
    }

}
