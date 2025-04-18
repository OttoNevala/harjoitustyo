package com.example.olioohjelmointiharjoitusty.history;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryStorage {

    private static SearchHistoryStorage instance;
    private final List<String> searchHistory = new ArrayList<>();

    private SearchHistoryStorage() {
    }

    public static SearchHistoryStorage getInstance() {
        if (instance == null) {
            instance = new SearchHistoryStorage();
        }
        return instance;
    }

    public void addSearchEntry(String entry) {
        if (!searchHistory.contains(entry)) {
            searchHistory.add(0, entry); // Uusin tulee listan alkuun
        }
    }

    public List<String> getSearchHistory() {
        return new ArrayList<>(searchHistory);
    }
}