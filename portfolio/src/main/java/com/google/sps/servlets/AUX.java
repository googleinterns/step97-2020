package com.google.sps.servlets;

import java.io.IOException;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.SearchListResponse;

import java.io.StringWriter;
import java.io.PrintWriter;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class AUX{
    private static final String DEVELOPER_KEY = "AIzaSyBOUFTwKaYgeEpRNZvw9tt-T1QKufhbeoM";

    private static final String APPLICATION_NAME = "API code samples";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public static Video VideoIdToObject(String videoId) throws IOException, GeneralSecurityException{
        YouTube youtubeService = getService();
        // Define and execute the API request
        YouTube.Videos.List videoRequest = youtubeService.videos()
            .list("snippet");
        VideoListResponse videoResponse = videoRequest.setKey(DEVELOPER_KEY)
            .setId(videoId)
            .execute();
            
        String videoTitle = videoResponse.getItems().get(0).getSnippet().getTitle();
        String videoDescription = videoResponse.getItems().get(0).getSnippet().getLocalized().getDescription();
        String thumbnailUrl = videoResponse.getItems().get(0).getSnippet().getThumbnails().getDefault().getUrl();

        return Video.Builder(videoTitle, videoDescription);
    }

    public static ArrayList<String> SearchQueryToListOfVideoID(String query) throws IOException, GeneralSecurityException{
        YouTube youtubeService = getService();
        // Define and execute the API request
        YouTube.Search.List searchRequest = youtubeService.search()
            .list("snippet")
            .setType("video");
        SearchListResponse searchResponse = searchRequest.setQ(query)
            .setKey(DEVELOPER_KEY)
            .execute();
        
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
}