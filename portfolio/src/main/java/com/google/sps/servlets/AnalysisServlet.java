package com.google.sps.servlets;
import com.google.sps.data.ErrorConsts;
import com.google.sps.data.PropertyNames;
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
        // If video id is malformed, send error status code.
        String videoId = request.getParameter(PropertyNames.VIDEO_ID);
        if (videoId == null || videoId.isEmpty()) {
            sendErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, ErrorConsts.EMPTY_VIDEO_ID);
            return;
        }
        // Check if video is in database and retrieve it if it is.
        Key videoKey = KeyFactory.createKey("Video", videoId);
        Entity videoEntity;
        Entity videoAnalysisEntity;
        try {
            videoEntity = datastore.get(videoKey);
            // Try to retrieve the Analysis child with id 0.
            Key analysisKey = KeyFactory.createKey(videoKey, "Analysis", 1L);
            videoAnalysisEntity = datastore.get(analysisKey);
        } catch(EntityNotFoundException e) {
            sendErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, ErrorConsts.ANALYSIS_NOT_FOUND_IN_DB);
            return;
        }
        // If there is currently no analysis, generate a new one.
        if (videoAnalysisEntity.getProperty(PropertyNames.ANALYSIS_OBJECT_AS_JSON) == null) {
            Video video = Video.videoFromDatastoreEntity(videoEntity);
            video.loadCaptions();
            // Create the video analysis, may throw an IOException.
            VideoAnalysis analysis;
            try {
                analysis = new VideoAnalysis(video);

            } catch (IOException e) {
                sendErrorMessage(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorConsts.ANALYSIS_FAILED);
                return;
            }
            // Store video analysis Json as property (for now).
            Gson gson = new Gson();
            videoAnalysisEntity.setProperty(PropertyNames.ANALYSIS_OBJECT_AS_JSON, gson.toJson(analysis));
            datastore.put(videoAnalysisEntity);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        // Check if datastore key for video is valid.
        String videoId = request.getParameter(PropertyNames.VIDEO_ID);
        if (videoId == null || videoId.isEmpty()) {
            sendErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, ErrorConsts.EMPTY_VIDEO_ID);
            return;
        }
        Key videoKey = KeyFactory.createKey("Video", videoId);
        // Get video analysis if it exists.
        Entity videoEntity;
        Entity videoAnalysisEntity;
        try {
            // Create custom key for child with id 0.
            Key analysisKey = KeyFactory.createKey(videoKey, "Analysis", 1L);
            videoAnalysisEntity = datastore.get(analysisKey);
        } catch (EntityNotFoundException e) {
            sendErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, ErrorConsts.ANALYSIS_NOT_FOUND_IN_DB);
            return;
        }
        if (videoAnalysisEntity.getProperty(PropertyNames.ANALYSIS_OBJECT_AS_JSON) == null) {
            sendErrorMessage(response, HttpServletResponse.SC_BAD_REQUEST, ErrorConsts.ANALYSIS_NOT_FOUND_IN_DB);
            return;
        }
        // Attempt to respond with the Json.
        response.setContentType("application/json");
        try {
            response.getWriter().println(videoAnalysisEntity.getProperty(PropertyNames.ANALYSIS_OBJECT_AS_JSON));
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
