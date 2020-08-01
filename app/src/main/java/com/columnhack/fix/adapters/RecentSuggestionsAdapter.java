package com.columnhack.fix.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.columnhack.fix.R;
import com.columnhack.fix.interfaces.QueryTextSetters;

import java.util.List;

public class RecentSuggestionsAdapter extends Adapter<RecentSuggestionsAdapter.SuggestionsHolder> {

    Context mContext;
    List<String> mRecentQueries;

    public RecentSuggestionsAdapter(Context context, List<String> recentQueries) {
        mContext = context;
        mRecentQueries = recentQueries;
    }

    public void changeRecentQueries(List<String> newQueries){
        mRecentQueries = newQueries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SuggestionsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.recent_suggestion_item, parent, false);
        return new SuggestionsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionsHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mRecentQueries.size();
    }

    class SuggestionsHolder extends RecyclerView.ViewHolder {

        ImageView reloadSearchIcon;
        ImageView setQueryTextIcon;
        TextView suggestionText;

        public SuggestionsHolder(@NonNull View itemView) {
            super(itemView);

            reloadSearchIcon = itemView.findViewById(R.id.reload_search_icon);
            setQueryTextIcon = itemView.findViewById(R.id.set_query_text_icon);
            suggestionText = itemView.findViewById(R.id.recent_suggestion_text);

            setQueryTextIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof QueryTextSetters){
                        ((QueryTextSetters) mContext).setQueryText(mRecentQueries.get(getAdapterPosition()));
                    }
                }
            });

            suggestionText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof QueryTextSetters){
                        ((QueryTextSetters) mContext).setAndQueryText(mRecentQueries.get(getAdapterPosition()));
                    }
                }
            });
            reloadSearchIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof QueryTextSetters){
                        ((QueryTextSetters) mContext).setAndQueryText(mRecentQueries.get(getAdapterPosition()));
                    }
                }
            });

        }

        public void bind(int position) {
            suggestionText.setText(mRecentQueries.get(position));
        }
    }

}
