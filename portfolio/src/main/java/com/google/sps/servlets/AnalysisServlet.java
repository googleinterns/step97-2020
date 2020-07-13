package com.google.sps.servlets;
import com.google.sps.data.PropertyNames;
import com.google.sps.data.SearchQuery;
import com.google.sps.data.SentimentTools;
import com.google.sps.data.Video;
import com.google.sps.data.VideoAnalysis;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;

// Takes in a video and sends back the corresponding search query for that video.
@WebServlet("/analysis")
public class AnalysisServlet extends HttpServlet {
    private static int SEARCH_QUERY_SIZE = 7;
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        // If videoId is malformed, send error status code.
        String videoKeyAsString = request.getParameter(PropertyNames.VIDEO_KEY);
        if (videoKeyAsString == null || videoKeyAsString.isEmpty()) {
            sendErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, "Video Key cannot be empty.");
            return;
        }
        Key videoKey = KeyFactory.stringToKey(videoKeyAsString); 

        Entity videoEntity;
        try {
            videoEntity = datastore.get(videoKey);
        } catch (EntityNotFoundException e){
            sendErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, "Video not found in database.");
            return;
        }
        // Get video entity from JSON and load captions.
        Gson gson = new Gson();
        Video video = gson.fromJson(
            (String) videoEntity.getProperty(PropertyNames.VIDEO_OBJECT_AS_JSON),
            Video.class);
        video.loadCaptions();
        
        // Calculate the search query and sentiment.
        VideoAnalysis videoAnalysis;
        try {
            videoAnalysis  = new VideoAnalysis(video);
        } catch (IOException e) {
            sendErrorMessage(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Language service unavailable.");
            return;
        }
        Entity analysisEntity = new Entity("Analysis", videoKey);
        analysisEntity.setProperty(PropertyNames.ANALYSIS_OBJECT_AS_JSON, new Gson().toJson(videoAnalysis));
        datastore.put(analysisEntity);
    }

    //Send an error message to the client.
    public static void sendErrorMessage(HttpServletResponse response, int status, String message) {
        response.setStatus(status);
        response.setContentType("text/plain");
        try {
            response.getWriter().println(message);
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
