package com.google.sps.servlets;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.YouTube;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import com.google.sps.data.PropertyNames;

@WebServlet("/playlist")
public class PlaylistServlet extends HttpServlet{

    private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // Build youtube service using dummy HttpRequestInitializer.
    private static YouTube youtubeService = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(),
        new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        })
        .setApplicationName("playlist-items").build();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{

        String playlistId = request.getParameter(PropertyNames.PLAYLIST_ID);
        String apiKey = (String) datastore.prepare(new Query("YoutubeAPIKey"))
            .asSingleEntity()
            .getProperty("Key");

        YouTube.PlaylistItems.List playlistRequest = youtubeService
            .playlistItems()
            .list("snippet,status")
            .setFields("items("+
                "snippet/title," +
                "snippet/description," +
                "snippet/thumbnails/default/url," +
                "snippet/resourceId/videoId," +
                "status/privacyStatus)");
        PlaylistItemListResponse playlistResponse = playlistRequest
            .setKey(apiKey)
            .setPlaylistId(playlistId)
            .setMaxResults(100L)
            .execute();
        response.getWriter().println(playlistResponse);
    }
}