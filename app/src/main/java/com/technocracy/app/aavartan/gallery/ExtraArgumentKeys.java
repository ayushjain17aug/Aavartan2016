package com.technocracy.app.aavartan.gallery;

/**
 * Created by Mohit on 12-10-2015.
 */

public enum ExtraArgumentKeys {
    OPEN_ACTIVITES("OPEN_ACTIVITES");

    private String text;

    private ExtraArgumentKeys(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}

