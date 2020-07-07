package com.google.sps.servlets;

import com.google.sps.data.PropertyNames;
import com.google.sps.data.Query;
import com.google.sps.data.SentimentTools;
import com.google.sps.data.Video;

import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Takes in a video and sends back the corresponding query for that video.
@WebServlet("/video")
public class VideoServlet extends HttpServlet {
    private static int QUERY_SIZE = 7;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String videoId = request.getParameter(PropertyNames.VIDEO_ID);

        // Have the user enter video metadata until we can fetch it.
        String title = request.getParameter(PropertyNames.TITLE);
        String description = request.getParameter(PropertyNames.DESCRIPTION);
        String captions = request.getParameter(PropertyNames.CAPTIONS);
        Video dummyVideo = new Video(videoId, title, description, captions);
        
        // Calculate the query and sentiment.
        Query query = new Query(dummyVideo, QUERY_SIZE);
        float sentimentScore = new SentimentTools(dummyVideo).getScore();

        // Make the param string and redirect the user to the results page.
        String paramString = "?q=" + query + "&score=" + sentimentScore + "&title=" + title;
        response.sendRedirect("results.html" + paramString);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        //Run a query to see if we get any results from the video ID of the POST request
        String id = request.getParameter(PropertyNames.VIDEO_ID);
        Query query = new Query("Video")
            .setFilter(new FilterPredicate("videoId", FilterOperator.EQUAL, id));
        PreparedQuery preparedQuery = datastore.prepare(query);
        Entity queryVideoEntity = preparedQuery.asSingleEntity();
        //if the video Id doesnt exist in our database, we convert the request to a video entity, add it to the database, and redirect. 
        if(queryVideoEntity == null){
            Video video = Video.httpRequestToVideo(request);
            Entity videoEntity = Video.videoToDatastoreEntity(video);
            datastore.put(videoEntity);
            response.sendRedirect("/?videoId=" + video.getVideoId());
        } else {
            response.sendRedirect("/?videoId=" + queryVideoEntity.getProperty(PropertyNames.VIDEO_ID).toString());
        }
    }   
}
