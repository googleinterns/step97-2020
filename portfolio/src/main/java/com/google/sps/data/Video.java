package com.google.sps.data;
import com.google.sps.data.PropertyNames;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

// Represents a video, including its ID, title, description, and captions.
public class Video {

    // Fetch via the youtube API before previewing.
    private String videoId;
    private String title;
    private String description;
    private String thumbnailUrl;
    private boolean isPublic;

    // Fetch when analyzing.
    private String captions;
    
    public Video(String videoId, String title, String description, String thumbnailUrl, boolean isPublic) {
        this.videoId = videoId;
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.isPublic = isPublic;
        // Captions not yet initialized.
        this.captions = null;
    }

    // Create video from videoId.
    public Video(String videoId) {
        // For Olabode to fill in.
        this(null, null, null, null, true);
    }

    public static Video httpRequestToVideo(HttpServletRequest request) {
        // Get the input from the form.
        String videoId = request.getParameter(PropertyNames.VIDEO_ID);
        // Extra fields in form for metadata until we can fetch them.
        String title = request.getParameter(PropertyNames.TITLE);
        String description = request.getParameter(PropertyNames.DESCRIPTION);
        String thumbnailUrl = request.getParameter(PropertyNames.THUMBNAIL_URL);
        boolean isPublic = request.getParameter(PropertyNames.IS_PUBLIC) != null;
        Video video = new Video(videoId, title, description, thumbnailUrl, isPublic);
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

    // Fetch the captions and save them to the captions instance variable.
    public void loadCaptions() {
        // Placeholder for Olabode to fill in.
        this.captions = description;
    }

    public String getCaptions() {
        return captions;
    }
}