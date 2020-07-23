package com.google.sps.servlets;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.sps.data.AUX;
import com.google.sps.data.Video;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.GeneralSecurityException;

@WebServlet("/VideoTesting")
public class VideoTestingServlet extends HttpServlet {
    private final static String TEST_VIDEO_ID = "-KqjH7mWggg";
    private final static String TEST_VIDEO_ID_2 = "asdfhkials";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try{
            String whereIsV = AUX.youtubeUrlToId("yesv=maybe&x");
            response.getWriter().println(whereIsV);
        }
        catch(Exception e){
            //Print errors to console
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            System.out.println(sw.toString());
        }
    }

}