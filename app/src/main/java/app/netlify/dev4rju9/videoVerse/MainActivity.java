package app.netlify.dev4rju9.videoVerse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

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

        binding.tvMain.setOnClickListener( v -> binding.tvMain.setText("Hello 4rju9!"));

    }

}