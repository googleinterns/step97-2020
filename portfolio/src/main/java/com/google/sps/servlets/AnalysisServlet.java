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

// Takes in a video and sends back the corresponding search query for that video.
@WebServlet("/analysis")
public class AnalysisServlet extends HttpServlet {
    private static int SEARCH_QUERY_SIZE = 7;
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static final boolean DEBUG = false;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        // If video key is malformed, send error status code.
        String keyString = request.getParameter(PropertyNames.VIDEO_KEY);
        if (keyString == null || keyString.isEmpty()) {
            sendErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, "Video key cannot be empty.");
            return;
        }
        // Check if video is in database and retrieve it if it is.
        Key videoKey = KeyFactory.stringToKey(keyString);
        Entity videoEntity;
        try {
            videoEntity = datastore.get(videoKey);
        } catch(EntityNotFoundException e) {
            sendErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, "Video not found in database.");
            return;
        }

        // Get video entity from JSON and load captions.
        Gson gson = new Gson();
        Video video = gson.fromJson(
            (String) videoEntity.getProperty(PropertyNames.VIDEO_OBJECT_AS_JSON),
            Video.class);
        video.loadCaptions();
        // Create the video analysis.
        VideoAnalysis analysis;
        try {
            analysis = new VideoAnalysis(video);

        } catch (IOException e) {
            sendErrorMessage(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Video analysis failed.");
            return;
        }
        // Store video analysis Json as property (for now).
        videoEntity.setProperty(PropertyNames.VIDEO_ANALYSIS_JSON, gson.toJson(analysis));
        datastore.put(videoEntity);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        // Check if datastore key for video is valid.
        String keyString = request.getParameter(PropertyNames.VIDEO_KEY);
        if (keyString == null || keyString.isEmpty()) {
            sendErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, "Video key cannot be empty.");
            return;
        }
        Key videoKey = KeyFactory.stringToKey(keyString);
        // Get video analysis if it exists.
        Entity videoEntity;
        try {
            videoEntity = datastore.get(videoKey);
        } catch (EntityNotFoundException e) {
            sendErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, "Video not found in database.");
            return;
        }
        if (videoEntity.getProperty(PropertyNames.VIDEO_ANALYSIS_JSON) == null) {
            sendErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, "The database does not contain analysis for this video.");
            return;
        }
        // Attempt to respond with the Json.
        response.setContentType("application/json");
        try {
            response.getWriter().println(videoEntity.getProperty(PropertyNames.VIDEO_ANALYSIS_JSON));
        } catch (IOException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
    }

    // Send an error message to the client.
    public static void sendErrorMessage(HttpServletResponse response, int status, String message) {
        response.setStatus(status);
        response.setContentType("text/plain");
        try {
            response.getWriter().println(message);
            return;
        } catch (IOException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
    }
}
