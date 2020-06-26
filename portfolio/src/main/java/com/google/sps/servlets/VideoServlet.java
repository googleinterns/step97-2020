package com.google.sps.servlets;

import com.google.sps.data.Video;
import com.google.sps.data.Query;
import com.google.sps.data.Fields;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

// Takes in a video and sends back the corresponding query for that video.
@WebServlet("/video")
public class VideoServlet extends HttpServlet {

    private static int QUERY_SIZE = 7;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String videoId = request.getParameter(Fields.VIDEO_ID);

        // Have the user enter video metadata until we can fetch it.
        String title = request.getParameter(Fields.TITLE);
        String description = request.getParameter(Fields.DESCRIPTION);
        String captions = request.getParameter(Fields.CAPTIONS);
        Video dummyVideo = new Video(videoId, title, description, captions);
        
        // Calculate the query.
        Query query = Query.makeQuery(dummyVideo, QUERY_SIZE);

        // Send the query text back to the user.
        response.setContentType("text/plain");
        response.getWriter().println(query);
    }
}
