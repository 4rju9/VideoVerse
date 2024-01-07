package app.netlify.dev4rju9.videoVerse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import app.netlify.dev4rju9.videoVerse.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // For View Binding.
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setTheme(R.style.DeepSkyBlueNav);
        setContentView(binding.getRoot());

        // TODO: Code Goes Here.
        checkRuntimePermission();

        toggle = new ActionBarDrawerToggle(this, binding.getRoot(), R.string.tv_open, R.string.tv_close);
        binding.getRoot().addDrawerListener(toggle);
        toggle.syncState();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setFragment(new VideoFragment());

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
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 7);
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
}