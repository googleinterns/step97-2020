package com.google.sps.servlets;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Text;
import com.google.sps.data.AUX;
import com.google.sps.data.ErrorConsts;
import com.google.sps.data.PropertyNames;
import com.google.sps.data.Video;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.GeneralSecurityException;

@WebServlet("/data")

public class DataServlet extends HttpServlet {
    private static DatastoreService datastore;
    public DataServlet() {
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    public DataServlet(DatastoreService datastore_){
        datastore = datastore_;
    }
    private static final boolean DEBUG = false;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        //Check if we get a valid video ID as a parameter 
        String videoId = request.getParameter(PropertyNames.VIDEO_ID);
        if(videoId == null || videoId.isEmpty()){
            sendErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, ErrorConsts.EMPTY_VIDEO_ID);
            return;
        }
        //Create custom key using video id.
        Key videoKey = KeyFactory.createKey("Video", videoId); 
        //grab our video using the key
        Entity video;
        try {
            video = datastore.get(videoKey);
        } catch (EntityNotFoundException e){
            sendErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, ErrorConsts.VIDEO_NOT_FOUND_IN_DB);
            return;
        }
        String videoObjectJson = ((Text) video.getProperty(PropertyNames.VIDEO_OBJECT_AS_JSON)).getValue();
        response.setContentType("application/json");
        //Try to send JSON back to the server
        try {
            response.getWriter().println(videoObjectJson);
        } catch(IOException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        //Run a query to see if we get any results from the video ID of the POST request
        String videoId = request.getParameter(PropertyNames.VIDEO_ID);
        if (videoId == null || videoId.isEmpty()) {
            sendErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, ErrorConsts.EMPTY_VIDEO_ID);
            return;
        }
        Query query = new Query("Video")
            .setFilter(new FilterPredicate(PropertyNames.VIDEO_ID, FilterOperator.EQUAL, videoId));
        PreparedQuery preparedQuery = datastore.prepare(query);
        Entity queryVideoEntity = preparedQuery.asSingleEntity();
        // If video id doesn't exist in database, convert to video entity and add to database.
        if(queryVideoEntity == null){
            Video video;
            try {
                video = AUX.VideoIdToObject(videoId);
            } catch (IOException e) {
                sendErrorMessage(response, HttpServletResponse.SC_NOT_FOUND, ErrorConsts.UNABLE_TO_RETRIEVE);
                return;
            } catch (IndexOutOfBoundsException e) {
                sendErrorMessage(response, HttpServletResponse.SC_NOT_FOUND, ErrorConsts.NO_VIDEO_QUERY_MATCHES);
                return;
            } catch (GeneralSecurityException e) {
                sendErrorMessage(response, HttpServletResponse.SC_NOT_FOUND, ErrorConsts.SECURITY_EXCEPTION);
                return;
            }
            Entity videoEntity = Video.videoToDatastoreEntity(video);
            datastore.put(videoEntity);
            // Create a new analysis entry using custom keys.
            Entity videoAnalysis = new Entity(/* Entity type=*/ "Analysis",
                /* Child id =*/ 1L,
                /* Key of parent =*/videoEntity.getKey());
            datastore.put(videoAnalysis);
        }
    }

    //Send an error message to the client.
    public static void sendErrorMessage(HttpServletResponse response, int status, String message) {
        response.setStatus(status);
        response.setContentType("text/plain");
        try {
            response.getWriter().println(message);
        } catch (IOException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
    }
}
