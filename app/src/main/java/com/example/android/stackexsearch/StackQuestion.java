package com.example.android.stackexsearch;

public class StackQuestion {

    private String display_name;
    private String profile_image;
    private String title;
    private String link;

    public StackQuestion(String display_name, String profile_image, String title, String link) {
        this.display_name = display_name;
        this.profile_image = profile_image;
        this.title = title;
        this.link = link;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }
}
