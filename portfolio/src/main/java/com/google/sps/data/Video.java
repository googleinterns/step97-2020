package com.google.sps.data;
import com.google.sps.data.PropertyNames;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Key;
import com.google.gson.Gson;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.net.MalformedURLException;
import com.google.sps.data.VideoException;

// Represents a video, including its ID, title, description, and captions.
public class Video {
    // Fetch via the youtube API before previewing.
    private String videoId;
    private String title;
    private String description;
    private URL thumbnailUrl;
    private boolean isPublic;
    // Fetch when analyzing.
    private String captions;
    
    public Video(String videoId, String title, String description, String thumbnailUrl, boolean isPublic) 
      throws MalformedURLException{
        this.videoId = videoId;
        this.title = title;
        this.description = description;
        this.thumbnailUrl = new URL(thumbnailUrl);
        this.isPublic = isPublic;
        // Captions not yet initialized.
        this.captions = null;
    }

    public Video(String videoId, String title, String description, String thumbnailUrl, String captions, boolean isPublic) 
      throws MalformedURLException{
        this.videoId = videoId;
        this.title = title;
        this.description = description;
        this.thumbnailUrl = new URL(thumbnailUrl);
        this.isPublic = isPublic;
        this.captions = captions;
    }

    // Create video from videoId.
    public Video(String videoId) throws MalformedURLException{
        // For Olabode to fill in.
        this(null, null, null, null, true);
    }

    public static Video httpRequestToVideo(HttpServletRequest request) throws MalformedURLException{
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
        // Use datastore Text object since Strings can exceed maximum property length.
        videoEntity.setProperty(PropertyNames.VIDEO_OBJECT_AS_JSON, new Text(new Gson().toJson(video)));
        return videoEntity;
    }
    public static Entity videoToDatastoreEntity(Video video, Key key){
        Entity videoEntity = new Entity("Video", key);
        videoEntity.setProperty(PropertyNames.VIDEO_ID, video.getVideoId());
        videoEntity.setProperty(PropertyNames.VIDEO_OBJECT_AS_JSON, new Text(new Gson().toJson(video)));
        return videoEntity;
    }

    public static Video videoFromDatastoreEntity(Entity videoEntity) {
        return new Gson().fromJson(
        ((Text) videoEntity.getProperty(PropertyNames.VIDEO_OBJECT_AS_JSON)).getValue(),
         Video.class);
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