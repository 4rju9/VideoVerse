package app.netlify.dev4rju9.videoVerse;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import app.netlify.dev4rju9.videoVerse.adapters.FolderAdapter;
import app.netlify.dev4rju9.videoVerse.adapters.VideoAdapter;
import app.netlify.dev4rju9.videoVerse.databinding.FragmentFolderBinding;
import app.netlify.dev4rju9.videoVerse.databinding.FragmentVideoBinding;

public class FolderFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_folder, container, false);

        FragmentFolderBinding binding = FragmentFolderBinding.bind(view);

        binding.folderRecyclerView.setHasFixedSize(true);
        binding.folderRecyclerView.setItemViewCacheSize(10);
        binding.folderRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.folderRecyclerView.setAdapter(new FolderAdapter(requireContext(), MainActivity.FOLDER_LIST));

        return view;
    }

}