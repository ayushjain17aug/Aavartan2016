package com.technocracy.app.aavartan.gallery;

/**
 * Created by Mohit on 12-10-2015.
 */

public enum FragmentTags {
    LIST_BUDDIES("AavartanGalleryFragment"),
    CUSTOMIZE("CustomizeFragment");

    private String text;

    private FragmentTags(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}

