package com.google.sps.servlets;

import com.google.sps.data.Fields;
import com.google.sps.data.Video;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

@WebServlet("/video")
public class VideoServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String videoId = request.getParameter(Fields.VIDEO_ID);
        String title = "";
        String description = "";
        String captions = "";
        Video dummyVideo = new Video(videoId, title, description, captions);
        response.setContentType("application/json");
        response.getWriter().println((new Gson()).toJson(dummyVideo));
    }
}
