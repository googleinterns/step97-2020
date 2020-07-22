package com.google.sps.data;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.sps.data.Video;
import com.google.sps.data.VideoException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AUX{
    private static String DEVELOPER_KEY = null;
    private static final String APPLICATION_NAME = "Watch Wisely";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String GET_URL_BASE_URL = "http://video.google.com/timedtext?lang=en&v=";
	private static String GET_URL;

    private static void GetDevKey(){
        if(DEVELOPER_KEY == null){
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            DEVELOPER_KEY = (String) datastore.prepare(new Query("YoutubeAPIKey"))
                .asSingleEntity()
                .getProperty("Key");
        }
    }

    public static Video VideoIdToObject(String videoId){
        GetDevKey();

        VideoListResponse videoResponse = new VideoListResponse();
        try{
            //Get youtube service
            YouTube youtubeService = getService();

            // Define and execute the API request
            YouTube.Videos.List videoRequest = youtubeService.videos()
            .list("snippet");

            videoResponse = videoRequest.setKey(DEVELOPER_KEY)
            .setId(videoId)
            .execute();

            com.google.api.services.youtube.model.Video video = videoResponse.getItems().get(0);
            String videoTitle = video.getSnippet().getTitle();
            String videoDescription = video.getSnippet().getLocalized().getDescription();
            String thumbnailUrl = video.getSnippet().getThumbnails().getDefault().getUrl();
            String videoCaptions = getVideoCaptions(videoId);
            boolean isPublic = true;
            Video result = new Video(videoId, videoTitle, videoDescription, thumbnailUrl, videoCaptions, isPublic);
            return result;
        }
        catch (IOException e) {
            Video tempVideo =  new Video(new VideoException("Unable to retrieve video"));
            tempVideo.setTitle("Error!");
            return tempVideo;
        } 
        catch (IndexOutOfBoundsException e) {
            Video tempVideo =  new Video(new VideoException("No such video found."));
            tempVideo.setTitle("Error!");
            return tempVideo;
        } 
        catch (GeneralSecurityException e) {
            Video tempVideo =  new Video(new VideoException("A security exception occurred"));
            tempVideo.setTitle("Error!");
            return tempVideo;
        }
        catch(Exception e){
            //Catch any other error that has been potentially overlooked
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            Video tempVideo =  new Video(new VideoException(sw.toString()));
            tempVideo.setTitle("Error!");
            return tempVideo;
        }
        
        
    }

    public static ArrayList<String> SearchQueryToListOfVideoID(String query) throws IOException, GeneralSecurityException{
        GetDevKey();
        YouTube youtubeService = getService();
        // Define and execute the API request
        YouTube.Search.List searchRequest = youtubeService.search()
            .list("snippet")
            .setType("video");

        SearchListResponse searchResponse = new SearchListResponse();
        try{
            searchResponse = searchRequest.setQ(query)
                .setKey(DEVELOPER_KEY)
                .execute();
        }
        catch(Exception e){
            //Print errors to console
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            System.out.println(sw.toString());
        }
        
        ArrayList<String> videoIdList = new ArrayList<String>();
        for(int i = 0; i < searchResponse.getItems().size(); i++){
            videoIdList.add(searchResponse.getItems().get(i).getId().getVideoId());
        }
        return videoIdList;
    }

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    private static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new YouTube.Builder(httpTransport, JSON_FACTORY, null)
            .setApplicationName(APPLICATION_NAME)
            .build();
    }

    public static String getVideoCaptions(String videoId){
        try{
            GET_URL = GET_URL_BASE_URL + videoId;
            URL obj = new URL(GET_URL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();

            //If the get request worked, read it into a string. It should come as an xml file
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer stringResponse = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    stringResponse.append(inputLine);
                }
                in.close();

                // print result
                return stringResponse.toString();
            } 
            
            else {
                //Get request failed.
                return "";
            }
        }
        catch(Exception e){
            return "";
        }

    }
}