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
public class DataServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String id = request.getParameter(PropertyNames.VIDEO_ID);
        if(id == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
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
        Video video = Video.httpRequestToVideo(request);
        Entity videoEntity = Video.videoToDatastoreEntity(video);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(videoEntity);
        response.sendRedirect("/?videoId=" + video.getVideoId());
    }
}