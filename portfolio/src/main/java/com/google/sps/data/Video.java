package com.google.sps.data;
import com.google.sps.data.PropertyNames;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.ThumbnailDetails;
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

    // URL for when thumbnail is unavailable.
    public static String UNAVAILABLE_THUMBNAIL_URL = "http://i.ytimg.com/vi/D5_RLA5NJAY/hqdefault.jpg";
    
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
        // Pass in video id as argument to make a custom key.
        Entity videoEntity = new Entity("Video", video.getVideoId());
        videoEntity.setProperty(PropertyNames.VIDEO_ID, video.getVideoId());
        // Use datastore Text object since Strings can exceed maximum property length.
        videoEntity.setProperty(PropertyNames.VIDEO_OBJECT_AS_JSON, new Text(new Gson().toJson(video)));
        return videoEntity;
    }

    public static Video videoFromDatastoreEntity(Entity videoEntity) {
        return new Gson().fromJson(
        ((Text) videoEntity.getProperty(PropertyNames.VIDEO_OBJECT_AS_JSON)).getValue(),
         Video.class);
    }

    public static Video videoFromPlaylistItem(PlaylistItem playlistItem) throws MalformedURLException {
        PlaylistItemSnippet snippet = playlistItem.getSnippet();
        String videoId = snippet.getResourceId().getVideoId();
        String videoTitle = snippet.getTitle();
        String videoDescription = snippet.getDescription();
        boolean isPublic = !playlistItem.getStatus().getPrivacyStatus().equals("private");
        String thumbnailUrl;
        if (!isPublic) {
            // Set url to unavailable thumbnail.
            thumbnailUrl = UNAVAILABLE_THUMBNAIL_URL;
        } else {
            thumbnailUrl = maxResThumbnailUrl(snippet.getThumbnails());
        }
        return new Video(videoId, videoTitle, videoDescription, thumbnailUrl, isPublic);
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
        this.captions = AUX.getVideoCaptions(videoId);
    }

    public String getCaptions() {
        return captions;
    }

    public String toJson(){
        String result = "{";
        result += "\"videoId\":\"" + videoId + "\"";
        result += ",\"title\":\"" + title + "\"";
        //result += ",\"description\":\"" + description + "\"";
        result += ",\"thumbnailUrl\":\"" + thumbnailUrl + "\"";
        result += ",\"isPublic\":\"" + isPublic + "\"";
        //result += ",\"captions\":\"" + captions + "\"";
        result += "}";
        return result;
    }

    public void setThumbnailUrl(String thumbnailUrl) throws MalformedURLException{
        this.thumbnailUrl = new URL(thumbnailUrl);
    }

    public URL getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public boolean isPublic() {
        return isPublic;
    }

    // Given thumbnails, get the highest quality one.
    public static String maxResThumbnailUrl(ThumbnailDetails details) {
        // Thumbnails sorted from highest to lowest quality.
        Thumbnail[] thumbnails = new Thumbnail[]{
            details.getMaxres(),
            details.getStandard(),
            details.getHigh(),
            details.getMedium(),
            details.getDefault()
        };
        Thumbnail highestRes = null;
        for (int thumbnailInd = 0; highestRes == null; thumbnailInd ++) {
            highestRes = thumbnails[thumbnailInd];
        }
        return highestRes.getUrl();
    }
}