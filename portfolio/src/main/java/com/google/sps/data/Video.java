package com.google.sps.data;
import com.google.sps.data.PropertyNames;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

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

    public static Video httpRequestToVideo(HttpServletRequest request) {
        // Get the input from the form.
        String videoId = request.getParameter(PropertyNames.VIDEO_ID);
        // Extra fields in form for metadata until we can fetch them.
        String title = request.getParameter(PropertyNames.TITLE);
        String description = request.getParameter(PropertyNames.DESCRIPTION);
        String captions = request.getParameter(PropertyNames.CAPTIONS);
        Video video = new Video(videoId, title, description, captions);
        return video;
    }

    public static Entity videoToDatastoreEntity(Video video){
        Entity videoEntity = new Entity("Video");
        videoEntity.setProperty(PropertyNames.VIDEO_ID, video.getVideoId());
        videoEntity.setProperty(PropertyNames.VIDEO_OBJECT_AS_JSON, new Gson().toJson(video));
        return videoEntity;
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

    public String getCaptions() {
        return captions;
    }

    public void setCaptions(String captions) {
        this.captions = captions;
    }
}