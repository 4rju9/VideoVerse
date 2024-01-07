package app.netlify.dev4rju9.videoVerse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Toast;
import app.netlify.dev4rju9.videoVerse.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // For View Binding.
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setTheme(R.style.DeepSkyBlueNav);
        setContentView(binding.getRoot());

        // TODO: Code Goes Here.
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

    }

    private void setFragment (Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentFL, fragment);
        transaction.disallowAddToBackStack();
        transaction.commit();

    }

}