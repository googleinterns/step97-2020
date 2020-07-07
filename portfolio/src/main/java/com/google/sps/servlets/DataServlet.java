package com.google.sps.servlets;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
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

@WebServlet("/data")

public class DataServlet extends HttpServlet {
    private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        //Check if we get a valid video ID as a parameter 
        String videoKeyAsString = request.getParameter(PropertyNames.VIDEO_KEY);
        if(videoKeyAsString == null || videoKeyAsString.isEmpty()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Key videoKey = KeyFactory.stringToKey(videoKeyAsString); 
        //grab our video using the key
        try {
            Entity video = datastore.get(videoKey);
            String videoObjectJson = video.getProperty(PropertyNames.VIDEO_OBJECT_AS_JSON).toString();
            response.setContentType("application/json");
            response.getWriter().println(videoObjectJson);
        } catch (EntityNotFoundException e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
            String videoEntityKey = KeyFactory.keyToString(videoEntity.getKey());
            response.sendRedirect("/?videoKey=" + videoEntityKey);
        } else {
            String queryVideoEntityKey = KeyFactory.keyToString(queryVideoEntity.getKey());
            response.sendRedirect("/?videoKey=" + queryVideoEntityKey);
        }
    }
}
