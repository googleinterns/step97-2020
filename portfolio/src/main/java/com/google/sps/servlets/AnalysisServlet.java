package com.google.sps.servlets;

import com.google.sps.data.PropertyNames;
import com.google.sps.data.SearchQuery;
import com.google.sps.data.SentimentTools;
import com.google.sps.data.Video;

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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;

// Takes in a video and sends back the corresponding search query for that video.
@WebServlet("/video")
public class AnalysisServlet extends HttpServlet {

    private static int SEARCH_QUERY_SIZE = 7;
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // If videoId is malformed, send error status code.
        String videoId = request.getParameter(PropertyNames.VIDEO_ID);
        if (videoId == null || videoId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Query for video in datastore
        Query query = new Query("Video")
            .setFilter(
            new FilterPredicate(PropertyNames.VIDEO_ID, FilterOperator.EQUAL, videoId));

        // Get video entity from JSON and load captions.
        Entity videoEntity = datastore.prepare(query).asSingleEntity();
        Gson gson = new Gson();
        Video video = gson.fromJson(
            (String) videoEntity.getProperty(PropertyNames.VIDEO_OBJECT_AS_JSON),
            Video.class);
        video.loadCaptions();
        
        // Calculate the search query and sentiment.
        float sentimentScore = new SentimentTools(video).getScore();
        SearchQuery searchQuery = new SearchQuery(video, SEARCH_QUERY_SIZE);

        // Make the param string and redirect the user to the results page.
        String paramString = "?q=" + searchQuery + "&score=" + sentimentScore + "&title=" + video.getTitle();
        response.sendRedirect("results.html" + paramString);
    }
}
