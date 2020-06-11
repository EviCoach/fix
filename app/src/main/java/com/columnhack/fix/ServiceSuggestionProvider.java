package com.columnhack.fix;

import android.content.SearchRecentSuggestionsProvider;

public class ServiceSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.columnhack.fix.ServiceSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public ServiceSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
