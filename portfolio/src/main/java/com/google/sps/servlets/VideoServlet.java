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
}
