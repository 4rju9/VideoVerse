package app.netlify.dev4rju9.videoVerse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;

import java.io.File;
import java.util.ArrayList;

import app.netlify.dev4rju9.videoVerse.adapters.VideoAdapter;
import app.netlify.dev4rju9.videoVerse.databinding.ActivityFoldersBinding;
import app.netlify.dev4rju9.videoVerse.models.Folder;
import app.netlify.dev4rju9.videoVerse.models.Video;

public class FoldersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityFoldersBinding binding = ActivityFoldersBinding.inflate(getLayoutInflater());
        setTheme(R.style.DeepSkyBlueNav);
        setContentView(binding.getRoot());

        Intent result = getIntent();
        int position = 0;
        if (result != null) {
            position = result.getIntExtra("pos", 0);
        }

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(MainActivity.FOLDER_LIST.get(position).getFolderName());
        }

        ArrayList<Video> list = getAllVideos(MainActivity.FOLDER_LIST.get(position).getId());

        binding.videoRecyclerViewFolder.setHasFixedSize(true);
        binding.videoRecyclerViewFolder.setItemViewCacheSize(10);
        binding.videoRecyclerViewFolder.setLayoutManager(new LinearLayoutManager(this));
        binding.videoRecyclerViewFolder.setAdapter(new VideoAdapter(this, list));
        String size = getResources().getString(R.string.tv_total_videos) + " " + list.size();
        binding.totalVideosFolder.setText(size);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("Range")
    private ArrayList<Video> getAllVideos (String ID) {
        ArrayList<Video> tempList = new ArrayList<>();

        String selection = MediaStore.Video.Media.BUCKET_ID + " Like? ";

        String[] projection = {
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DURATION
        };

        Cursor cursor = this.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection, selection, new String[] {ID}, MediaStore.Video.Media.DATE_ADDED + " DESC");

        if (cursor != null) {
            if (cursor.moveToNext()) {
                do {

                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                    String size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                    String id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                    String folderName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));

                    try {

                        File file = new File(path);
                        if (file.exists()) tempList.add(new Video(
                                id,
                                title,
                                folderName,
                                size,
                                path,
                                duration,
                                Uri.fromFile(file)
                        ));

                    } catch (Exception ignore) {}

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return tempList;
    }

}