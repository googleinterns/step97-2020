package com.google.sps.servlets;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.sps.data.PropertyNames;
import com.google.sps.data.Video;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// Servlet for testing queries.
@WebServlet("/data")
private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
public class DataServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        //Check if we get a valid video ID as a parameter 
        String id = request.getParameter(PropertyNames.VIDEO_ID);
        if(id == null || id.isEmpty()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        //if our ID is valid we go ahead and query our database for the video object pertaining to it
        Query query = new Query("Video")
            .setFilter(new FilterPredicate("videoId", FilterOperator.EQUAL, id));
        PreparedQuery preparedQuery = datastore.prepare(query);
        Entity video = preparedQuery.asSingleEntity();
        String videoObjectJson = video.getProperty(PropertyNames.VIDEO_OBJECT_AS_JSON).toString();
        response.setContentType("application/json");
        response.getWriter().println(videoObjectJson);
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //Run a query to see if we get any results from the video ID of the POST request
        Query query = new Query("Video")
            .setFilter(new FilterPredicate("videoId", FilterOperator.EQUAL, id));
        PreparedQuery preparedQuery = datastore.prepare(query);
        Entity queryVideoEntity = preparedQuery.asSingleEntity();
        //if the video Id doesnt exist in our database, we convert the request to a video entity, add it to the database, and redirect. 
        if(queryVideoEntity == null){
            video = Video.httpRequestToVideo(request);
            Entity videoEntity = Video.videoToDatastoreEntity(video);
            datastore.put(videoEntity);
            response.sendRedirect("/?videoId=" + video.getVideoId());
        }

        //if we have the video in our database already we query for the ID property and redirect using that.
        response.sendRedirect("/?videoId=" + queryVideoEntity.getProperty(PropertyNames.VIDEO_ID).toString());
        
        
    }
}