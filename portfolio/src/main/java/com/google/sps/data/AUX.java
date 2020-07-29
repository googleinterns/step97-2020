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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AUX{
    private static String DEVELOPER_KEY = null;
    private static final String APPLICATION_NAME = "Watch Wisely";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String GET_URL_BASE_URL = "http://video.google.com/timedtext?lang=en&v=";
	private static String GET_URL;
    private static final boolean DEBUG = false;

    /*
    * Sets the DEVELOPER_KEY variable
    */
    private static void getDevKey(){
        if(DEVELOPER_KEY == null){
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            DEVELOPER_KEY = (String) datastore.prepare(new Query("YoutubeAPIKey"))
                .asSingleEntity()
                .getProperty("Key");
        }
    }

    public static Video videoIdToObject(String videoId) throws VideoException{
        getDevKey();

        VideoListResponse videoResponse = new VideoListResponse();
        try{
            // Define and execute the API request
            videoResponse = getService().videos()
            .list("snippet")
            .setKey(DEVELOPER_KEY)
            .setId(videoId)
            .execute();

            com.google.api.services.youtube.model.Video video = videoResponse.getItems().get(0);
            String videoTitle = video.getSnippet().getTitle();
            String videoDescription = video.getSnippet().getLocalized().getDescription();
            String thumbnailUrl = video.getSnippet().getThumbnails().getDefault().getUrl();
            String videoCaptions = getVideoCaptions(videoId);
            Video result = new Video(videoId, videoTitle, videoDescription, thumbnailUrl, videoCaptions, /*isPublic*/ true);
            return result;
        }
        catch (IOException e) {
            throw new VideoException("Unable to retrieve video");
        } 
        catch (IndexOutOfBoundsException e) {
            throw new VideoException("No such video found.");
        } 
        catch (GeneralSecurityException e) {
            throw new VideoException("A security exception occurred");
        }
        catch(Exception e){
            //Catch any other error that has been potentially overlooked
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            if(DEBUG){
                e.printStackTrace(pw);
            }
            throw new VideoException(sw.toString());
        }
        
        
    }

    /*
    *Returns a list of video ID's based on a search query
    */
    public static ArrayList<String> searchQueryToListOfVideoID(String query) throws IOException, GeneralSecurityException{
        getDevKey();
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
            if(DEBUG){
                //Print error to console
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                System.out.println(sw.toString());
            }
        }
        
        ArrayList<String> videoIdList = new ArrayList<String>();
        for(int i = 0; i < searchResponse.getItems().size(); i++){
            videoIdList.add(searchResponse.getItems().get(i).getId().getVideoId());
        }
        return videoIdList;
    }

    /*
    *Retrieve video id from youtube url
    *@return a video id as string
    */
    public static String youtubeUrlToId(String youtubeUrl) throws IllegalArgumentException{
        String pattern = "(https://youtu.be/|https://www.youtube.com/watch\\?v=)(\\w+).*";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(youtubeUrl);

        if(m.find()){
            return m.group(2);
        }else{
            return "";
        }
    }

    private static YouTube getService() throws IOException, GeneralSecurityException{
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new YouTube.Builder(httpTransport, JSON_FACTORY, null)
            .setApplicationName(APPLICATION_NAME)
            .build();
    }

    /*
    * Returns the captions of a video if any are available
    * @param
    * @returns
    */
    private static String getVideoCaptions(String videoId){
        try{
            GET_URL = GET_URL_BASE_URL + videoId;
            URL obj = new URL(GET_URL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();

            //If the GET request worked, read it from the buffer and put it into a string. It should come as an xml file
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer stringResponse = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    stringResponse.append(inputLine);
                }
                in.close();
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