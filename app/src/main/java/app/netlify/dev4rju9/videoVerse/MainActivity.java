package app.netlify.dev4rju9.videoVerse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationBarView;

import app.netlify.dev4rju9.videoVerse.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // For View Binding.
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // TODO: Code Goes Here.

        binding.bottomNav.setOnItemSelectedListener( item -> {
            Toast.makeText(MainActivity.this, "Item Clicked", Toast.LENGTH_SHORT).show();
            return true;
        });

    }

}