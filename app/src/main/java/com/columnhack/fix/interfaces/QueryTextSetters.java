package com.columnhack.fix.interfaces;

public interface QueryTextSetters {
    public void setQueryText(String text); // sets the query text, but doesn't submit the query
    public void setAndQueryText(String text); // sets and submit the query
}
