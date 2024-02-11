package app.netlify.dev4rju9.videoVerse;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import app.netlify.dev4rju9.videoVerse.adapters.VideoAdapter;
import app.netlify.dev4rju9.videoVerse.databinding.FragmentVideoBinding;

public class VideoFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        FragmentVideoBinding binding = FragmentVideoBinding.bind(view);

        binding.videoRecyclerView.setHasFixedSize(true);
        binding.videoRecyclerView.setItemViewCacheSize(10);
        binding.videoRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.videoRecyclerView.setAdapter(new VideoAdapter(requireContext(), MainActivity.VIDEO_LIST, false));
        String size = getResources().getString(R.string.tv_total_videos) + " " + MainActivity.VIDEO_LIST.size();
        binding.totalVideos.setText(size);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.video_option_menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.searchView);
        SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return true; }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(requireContext(), newText, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }
}