package com.columnhack.fix.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.columnhack.fix.R;
import com.columnhack.fix.adapters.RecentSuggestionsAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RecentSuggestionsFragment extends Fragment {
    public static final String RECENT_SUGGESTIONS = "recent_suggestions";
    private RecyclerView suggestionsRecyclerView;
    private ArrayList<String> recentSuggestions = new ArrayList<>();
    private RecentSuggestionsAdapter mAdapter;

    public static RecentSuggestionsFragment newInstance(List<String> suggestions) {

        Bundle args = new Bundle();
        args.putSerializable(RECENT_SUGGESTIONS, (Serializable) suggestions);
        RecentSuggestionsFragment fragment = new RecentSuggestionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public RecentSuggestionsAdapter getAdapter(){
        return mAdapter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recentSuggestions = (ArrayList<String>) getArguments().getSerializable(RECENT_SUGGESTIONS);
        mAdapter = new RecentSuggestionsAdapter(getActivity(), recentSuggestions);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_suggestions, container, false);
        suggestionsRecyclerView = view.findViewById(R.id.suggestions_recycler_view);
        suggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        suggestionsRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
