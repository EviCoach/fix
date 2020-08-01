package com.columnhack.fix.database;

import android.provider.BaseColumns;

public class QueryContractClass {
    public QueryContractClass() {
    }

    public static final class RecentQuery implements BaseColumns {
        public static final String TABLE_NAME = "recent_query";
        public static final String COLUMN_QUERY_STRING = "query_string";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " ( " +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_QUERY_STRING + " TEXT NOT NULL UNIQUE)";
    }
}
