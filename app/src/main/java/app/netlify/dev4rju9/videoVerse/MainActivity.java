package app.netlify.dev4rju9.videoVerse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.lukelorusso.verticalseekbar.BuildConfig;

import java.io.File;
import java.util.ArrayList;
import app.netlify.dev4rju9.videoVerse.databinding.ActivityMainBinding;
import app.netlify.dev4rju9.videoVerse.databinding.ThemeViewBinding;
import app.netlify.dev4rju9.videoVerse.models.Folder;
import app.netlify.dev4rju9.videoVerse.models.Video;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ActionBarDrawerToggle toggle;
    public static ArrayList<Video> VIDEO_LIST;
    public static ArrayList<Folder> FOLDER_LIST;
    public static ArrayList<Video> SEARCHED_LIST;
    public static boolean isSearched = false;
    public static int THEME_INDEX = 0;
    public static int[] THEMES = {
            R.style.DeepSkyBlueNav, R.style.BrownNav, R.style.YellowNav, R.style.PurpleNav,
            R.style.PinkNav, R.style.DarkBlackTheme, R.style.DarkPurpleNav, R.style.BlackNav,
            R.style.BlueBlackNav, R.style.BrownBlackNav, R.style.YellowBlackNav, R.style.PinkBlackNav
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        saveTheme(-1);
        setTheme(THEMES[THEME_INDEX]);

        // For View Binding.
        binding = ActivityMainBinding.inflate(getLayoutInflater());
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

            if (id == R.id.exitNav) {
                System.exit(1);
            } else if (id == R.id.aboutNav) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://www.4rju9.netlify.app/"));
                startActivity(intent);

            } else if (id == R.id.themesNav) {

                View customDialog = LayoutInflater.from(this).inflate(R.layout.theme_view, binding.getRoot(), false);
                ThemeViewBinding themeBinding = ThemeViewBinding.bind(customDialog);
                AlertDialog mainDialog = new MaterialAlertDialogBuilder(this)
                        .setView(customDialog)
                        .setTitle("App Theme")
                        .create();
                mainDialog.show();

                switch (THEME_INDEX) {
                    case 0: {
                        themeBinding.deepSkyBlueTheme.setBackgroundColor(Color.LTGRAY);
                        break;
                    }
                    case 1: {
                        themeBinding.brownTheme.setBackgroundColor(Color.LTGRAY);
                        break;
                    }
                    case 2: {
                        themeBinding.yellowTheme.setBackgroundColor(Color.LTGRAY);
                        break;
                    }
                    case 3: {
                        themeBinding.purpleTheme.setBackgroundColor(Color.LTGRAY);
                        break;
                    }
                    case 4: {
                        themeBinding.pinkTheme.setBackgroundColor(Color.LTGRAY);
                        break;
                    }
                    case 5: {
                        themeBinding.darkBlackTheme.setBackgroundColor(Color.LTGRAY);
                        break;
                    }
                    case 6: {
                        themeBinding.darkPurpleTheme.setBackgroundColor(Color.LTGRAY);
                        break;
                    }
                    case 7: {
                        themeBinding.blackTheme.setBackgroundColor(Color.LTGRAY);
                        break;
                    }
                    case 8: {
                        themeBinding.blueBlackTheme.setBackgroundColor(Color.LTGRAY);
                        break;
                    }
                    case 9: {
                        themeBinding.brownBlackTheme.setBackgroundColor(Color.LTGRAY);
                        break;
                    }
                    case 10: {
                        themeBinding.yellowBlackTheme.setBackgroundColor(Color.LTGRAY);
                        break;
                    }
                    case 11: {
                        themeBinding.pinkBlackTheme.setBackgroundColor(Color.LTGRAY);
                        break;
                    }
                }

                themeBinding.deepSkyBlueTheme.setOnClickListener(v -> saveTheme(0));
                themeBinding.brownTheme.setOnClickListener(v -> saveTheme(1));
                themeBinding.yellowTheme.setOnClickListener(v -> saveTheme(2));
                themeBinding.purpleTheme.setOnClickListener(v -> saveTheme(3));
                themeBinding.pinkTheme.setOnClickListener(v -> saveTheme(4));
                themeBinding.darkBlackTheme.setOnClickListener(v -> saveTheme(5));
                themeBinding.darkPurpleTheme.setOnClickListener(v -> saveTheme(6));
                themeBinding.blackTheme.setOnClickListener(v -> saveTheme(7));
                themeBinding.blueBlackTheme.setOnClickListener(v -> saveTheme(8));
                themeBinding.brownBlackTheme.setOnClickListener(v -> saveTheme(9));
                themeBinding.yellowBlackTheme.setOnClickListener(v -> saveTheme(10));
                themeBinding.pinkBlackTheme.setOnClickListener(v -> saveTheme(11));

            } else if (id == R.id.sortOrderNav) {

                String[] menuItems = {"Latest", "Oldest", "Name(A to Z)", "Name(Z to A)",
                "File Size(smallest)", "File Size(largest)"};

                AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                        .setTitle("Sort By")
                        .setPositiveButton("OK", (self, which) -> self.dismiss())
                        .setSingleChoiceItems(menuItems, 0, (self, position) -> {
                            Toast.makeText(this, menuItems[position], Toast.LENGTH_SHORT).show();
                        }).create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);

            } else {
                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
            }
            binding.getRoot().closeDrawer(GravityCompat.START);
            return true;
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
    
    private void saveTheme (int index) {

        SharedPreferences prefs = getSharedPreferences("AppThemes", Context.MODE_PRIVATE);
        
        if (index == -1) {
            THEME_INDEX = prefs.getInt("themeIndex", 0);
        } else {
            prefs.edit().putInt("themeIndex", index).apply();
            finish();
            startActivity(getIntent());
        }
        
    }

}