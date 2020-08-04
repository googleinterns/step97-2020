package com.google.sps.servlets;
import com.google.sps.data.AUX;
import com.google.sps.data.Video;
import java.net.MalformedURLException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.io.IOException;
import com.google.gson.*;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {
    private static final boolean DEBUG = false;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try{
            String searchQuery = request.getParameter("searchQuery");
            if (searchQuery == null || searchQuery.isEmpty()) {
                return;
            }
            ArrayList<Video> videos = AUX.searchQueryToListOfVideos(searchQuery);
            String videosJson = "{\"videos\":[";
            for(int i = 0; i < videos.size(); i++){
                videosJson = videosJson + videos.get(i).toJson();
                if(i != videos.size()-1){
                    videosJson = videosJson + ",";
                }
            }
            videosJson = videosJson + "]}";
            response.getWriter().println(videosJson);
        }catch(Exception e){
            if(DEBUG){
                e.printStackTrace();
            }
        }
    }
}