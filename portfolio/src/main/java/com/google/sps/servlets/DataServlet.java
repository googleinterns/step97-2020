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
import com.google.sps.data.VideoException;
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
    private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static final boolean DEBUG = false;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        //Check if we get a valid video ID as a parameter 
        String videoKeyAsString = request.getParameter(PropertyNames.VIDEO_KEY);
        if(videoKeyAsString == null || videoKeyAsString.isEmpty()){
            sendErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, "Video key cannot be empty.");
            return;
        }
        Key videoKey = KeyFactory.stringToKey(videoKeyAsString); 
        //grab our video using the key
        Entity video;
        try {
            video = datastore.get(videoKey);
        } catch (EntityNotFoundException e){
            sendErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, "Video not found in database.");
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
        String id = request.getParameter(PropertyNames.VIDEO_ID);
        if (id == null || id.isEmpty()) {
            sendErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, "Video ID cannot be empty.");
            return;
        }
        Query query = new Query("Video")
            .setFilter(new FilterPredicate(PropertyNames.VIDEO_ID, FilterOperator.EQUAL, id));
        PreparedQuery preparedQuery = datastore.prepare(query);
        Entity queryVideoEntity = preparedQuery.asSingleEntity();
        //if the video Id doesnt exist in our database, we convert the request to a video entity, add it to the database, and redirect. 
        if(queryVideoEntity == null){
            Video video;
            video = AUX.videoIdToObject(id);
            try{
                video.getException();
            }
            catch(VideoException e){
                sendErrorMessage(response, HttpServletResponse.SC_NOT_FOUND, e.toString());
                return;
            }
            Entity emptyAnalysisEntity = new Entity("Analysis");
            datastore.put(emptyAnalysisEntity);
            Entity videoEntity = Video.videoToDatastoreEntity(video, emptyAnalysisEntity.getKey());
            datastore.put(videoEntity);
            //Get the Datastore key of the the entity we just created as a string 
            String videoEntityKey = KeyFactory.keyToString(videoEntity.getKey());
            //Send key back to the client.
            response.setContentType("text/plain");
            try {
                response.getWriter().println(videoEntityKey);
            } catch (IOException e) {
                if (DEBUG) {
                    e.printStackTrace();
                }
            }
        } else {
            //Get entity key and send back to the client.
            String queryVideoEntityKey = KeyFactory.keyToString(queryVideoEntity.getKey());
            response.setContentType("text/plain");
            try {
                response.getWriter().println(queryVideoEntityKey);
            } catch (IOException e) {
                if (DEBUG) {
                    e.printStackTrace();
                }
            }
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
