package app.netlify.dev4rju9.videoVerse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import app.netlify.dev4rju9.videoVerse.adapters.VideoAdapter;
import app.netlify.dev4rju9.videoVerse.databinding.ActivityFoldersBinding;

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

        binding.videoRecyclerViewFolder.setHasFixedSize(true);
        binding.videoRecyclerViewFolder.setItemViewCacheSize(10);
        binding.videoRecyclerViewFolder.setLayoutManager(new LinearLayoutManager(this));
        binding.videoRecyclerViewFolder.setAdapter(new VideoAdapter(this, MainActivity.VIDEO_LIST));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}