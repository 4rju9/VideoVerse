package app.netlify.dev4rju9.videoVerse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import app.netlify.dev4rju9.videoVerse.databinding.ActivityMainBinding;
import app.netlify.dev4rju9.videoVerse.models.Folder;
import app.netlify.dev4rju9.videoVerse.models.Video;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ActionBarDrawerToggle toggle;
    public static ArrayList<Video> VIDEO_LIST;
    public static ArrayList<Folder> FOLDER_LIST;
    public static ArrayList<Video> SEARCHED_LIST;
    public static boolean isSearched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // For View Binding.
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setTheme(R.style.DeepSkyBlueNav);
        setContentView(binding.getRoot());

        // TODO: Code Goes Here.
        if (checkRuntimePermission()) {
            FOLDER_LIST = new ArrayList<>();
            VIDEO_LIST = getAllVideos();
            setFragment(new VideoFragment());
        }

        toggle = new ActionBarDrawerToggle(this, binding.getRoot(), R.string.tv_open, R.string.tv_close);
        binding.getRoot().addDrawerListener(toggle);
        toggle.syncState();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        binding.bottomNav.setOnItemSelectedListener( item -> {

            int itemId = item.getItemId(); // item id of clicked item from bottom navigation bar.

            if (itemId == R.id.videoView) {
                setFragment(new VideoFragment());
                return true;
            } else if (itemId == R.id.folderView) {
                setFragment(new FolderFragment());
                return true;
            }
            return false;
        });

        binding.navView.setNavigationItemSelectedListener( item -> {
            int id = item.getItemId();

            if (id == R.id.feedbackNav) {
                Toast.makeText(MainActivity.this, "Feedback", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.themesNav) {
                Toast.makeText(MainActivity.this, "Themes", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.sortOrderNav) {
                Toast.makeText(MainActivity.this, "Sort Order", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.aboutNav) {
                Toast.makeText(MainActivity.this, "About", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.exitNav) {
                System.exit(1);
            }
            return false;
        });

    }

    private void setFragment (Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentFL, fragment);
        transaction.disallowAddToBackStack();
        transaction.commit();

    }

    private void requestRuntimePermission () {
        if (Build.VERSION.SDK_INT <= 32) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 7);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_VIDEO}, 7);
        }
    }

    private boolean checkRuntimePermission () {

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestRuntimePermission();
            return false;
        }

        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 7) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
            } else {
                requestRuntimePermission();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("Range")
    private ArrayList<Video> getAllVideos () {
        ArrayList<Video> tempList = new ArrayList<>();
        ArrayList<String> tempFolder = new ArrayList<>();

        String[] projection = {
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.BUCKET_ID,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DURATION
        };

        Cursor cursor = this.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, MediaStore.Video.Media.DATE_ADDED + " DESC");

        if (cursor != null) {
            if (cursor.moveToNext()) {
                do {

                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                    String size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                    String id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                    String folderName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
                    String folderId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID));
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));

                    try {

                        File file = new File(path);
                        if (file.exists()) {

                            tempList.add(new Video(
                                    id,
                                    title,
                                    folderName,
                                    size,
                                    path,
                                    duration,
                                    Uri.fromFile(file)
                            ));

                            if (!tempFolder.contains(folderName)) {
                                tempFolder.add(folderName);
                                FOLDER_LIST.add(new Folder(
                                        folderId,
                                        folderName
                                ));
                            }

                        }

                    } catch (Exception ignore) {}

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return tempList;
    }

}