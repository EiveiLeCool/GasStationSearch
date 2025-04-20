package org.example.persoonlijke_opdr;

import java.util.prefs.Preferences;

public class StorageService {
    private static final String SORT_KEY = "lastSortOption";

    public static void saveLastSortOption(String option) {
        Preferences.userRoot().put(SORT_KEY, option);
    }

    public static String loadLastSortOption() {
        return Preferences.userRoot().get(SORT_KEY, "afstand");
    }
}
