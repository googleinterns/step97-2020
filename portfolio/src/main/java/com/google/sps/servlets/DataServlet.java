
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;
package com.google.sps;
import com.google.sps.data.Fields;
import com.google.sps.data.Video;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

// Servlet for testing queries.
@WebServlet("/data")
public class DataServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        return;
    }

      @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
       
        Video video = requestToVideoObject(request);
        Entity videoEntity = videoToEntity(video);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(videoEntity);
        response.sendRedirect("/");
    }

    public Video requestToVideoObject(HttpServletRequest request){
        // Get the input from the form.
        String videoId = request.getParameter(Fields.VIDEO_ID);
        // Extra fields in form for metadata until we can fetch them.
        String title = request.getParameter(Fields.TITLE);
        String description = request.getParameter(Fields.DESCRIPTION);
        String captions = request.getParameter(Fields.CAPTIONS);
        Video video = new Video(videoId, title, description, captions);
        return video;
    }

    public Entity videoToEntity(Video video){
        Entity videoEntity = new Entity("Video");
        videoEntity.setProperty("videoId", video.getVideoId());
        videoEntity.setProperty("videoObjectAsJSON", new Gson().toJson(video));
        return videoEntity;
    }
}