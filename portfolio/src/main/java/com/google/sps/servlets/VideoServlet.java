package com.google.sps.servlets;

import com.google.sps.data.PropertyNames;
import com.google.sps.data.Query;
import com.google.sps.data.SentimentTools;
import com.google.sps.data.Video;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Takes in a video and sends back the corresponding query for that video.
@WebServlet("/video")
public class VideoServlet extends HttpServlet {

    private static int QUERY_SIZE = 7;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{

        // Create the video and load its captions.
        Video video;
        try {
            video = Video.httpRequestToVideo(request);
        } catch (MalformedURLException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        video.loadCaptions();
        // Calculate the query and sentiment.
        Query query = new Query(video, QUERY_SIZE);
        float sentimentScore = new SentimentTools(video).getScore();
        // Make the param string and redirect the user to the results page.
        String paramString = "?q=" + query + "&score=" + sentimentScore + "&title=" + video.getTitle();
        response.sendRedirect("results.html" + paramString);
    }
}
