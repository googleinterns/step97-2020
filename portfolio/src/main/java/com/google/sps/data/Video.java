package com.google.sps.data;

// Represents a video, including its ID, title, description, and captions.
public class Video {
    private String videoId;
    private String title;
    private String description;
    private String captions;

    public Video(String videoId, String title, String description, String captions) {
        this.videoId = videoId;
        this.title = title;
        this.description = description;
        this.captions = captions;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}