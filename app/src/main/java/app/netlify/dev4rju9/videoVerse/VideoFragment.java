package app.netlify.dev4rju9.videoVerse;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import app.netlify.dev4rju9.videoVerse.adapters.VideoAdapter;
import app.netlify.dev4rju9.videoVerse.databinding.FragmentVideoBinding;

public class VideoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        FragmentVideoBinding binding = FragmentVideoBinding.bind(view);

        binding.videoRecyclerView.setHasFixedSize(true);
        binding.videoRecyclerView.setItemViewCacheSize(10);
        binding.videoRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.videoRecyclerView.setAdapter(new VideoAdapter(requireContext(), MainActivity.VIDEO_LIST));

        return view;
    }

}