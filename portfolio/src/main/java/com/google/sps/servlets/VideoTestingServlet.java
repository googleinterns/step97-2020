package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.StringWriter;
import java.io.PrintWriter;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;

import com.google.sps.data.AUX;
import com.google.sps.data.Video;

@WebServlet("/VideoTesting")
public class VideoTestingServlet extends HttpServlet {
    private static String videoId = "-KqjH7mWggg";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try{
            Video testVideo = AUX.VideoIdToObject(videoId);
            ArrayList<String> videoIdList = AUX.SearchQueryToListOfVideoID("Snake");
            //String videoObject = AUX.videoObject(videoId);

            response.getWriter().println("Test video Title: " + testVideo.getTitle());
            //response.getWriter().println("\nFirst vid ID: " + videoIdList.get(0));
            //response.getWriter().println(videoObject);
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