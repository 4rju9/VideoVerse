package app.netlify.dev4rju9.videoVerse;

import android.content.Intent;
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

import java.util.ArrayList;

import app.netlify.dev4rju9.videoVerse.adapters.VideoAdapter;
import app.netlify.dev4rju9.videoVerse.databinding.FragmentVideoBinding;
import app.netlify.dev4rju9.videoVerse.models.Video;

public class VideoFragment extends Fragment {

    private VideoAdapter adapter;
    private FragmentVideoBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        requireContext().getTheme().applyStyle(MainActivity.THEMES[MainActivity.THEME_INDEX], true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        binding = FragmentVideoBinding.bind(view);

        binding.videoRecyclerView.setHasFixedSize(true);
        binding.videoRecyclerView.setItemViewCacheSize(10);
        binding.videoRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new VideoAdapter(requireContext(), MainActivity.VIDEO_LIST, 2);
        binding.videoRecyclerView.setAdapter(adapter);
        String size = getResources().getString(R.string.tv_total_videos) + " " + MainActivity.VIDEO_LIST.size();
        binding.totalVideos.setText(size);

        binding.nowPlayingIcon.setOnClickListener( v -> {
            PlayerActivity.LIST_CODE = 4;
            Intent intent = new Intent(requireContext(), PlayerActivity.class);
            startActivity(intent);
        });

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

                if (newText != null) {
                    MainActivity.SEARCHED_LIST = new ArrayList<>();
                    for (Video video : MainActivity.VIDEO_LIST) {
                        if (video.getTitle().toLowerCase()
                                .contains(newText.toLowerCase())) MainActivity.SEARCHED_LIST
                                .add(video);
                    }
                    MainActivity.isSearched = true;
                    adapter.updateList(MainActivity.SEARCHED_LIST);
                }

                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PlayerActivity.POS != -1) binding.nowPlayingIcon.setVisibility(View.VISIBLE);
        if (MainActivity.dataChanged) adapter.notifyDataSetChanged();
        MainActivity.dataChanged = false;
    }
}