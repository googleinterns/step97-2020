package com.google.sps.data;
import com.google.sps.data.PropertyNames;
import com.google.sps.data.Query;
import com.google.sps.data.SentimentTools;
import com.google.sps.data.Video;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.net.MalformedURLException;

// Represents a video, including its ID, title, description, and captions.
public class VideoAnalysis {
    private static float sentimentScore;
    private static float sentimentMagnitude;
    private static String query;
    private static int QUERY_SIZE = 7;
    
    public VideoAnalysis(Video video) {
        SentimentTools sentimentAnalysis = new SentimentTools(video);
        Query queryAnalysis = new Query(video, QUERY_SIZE);
        this.sentimentScore = SentimentTools.getScore();
        this.sentimentMagnitude = SentimentTools.getMagnitude();
        this.query = queryAnalysis.toString();
    }

    public VideoAnalysis(String _sentimentScore, String _sentimentMagnitude, String _query) {
        this.sentimentScore = _sentimentScore;
        this.sentimentMagnitude = _sentimentMagnitude;
        this.query = _query;
    }

    public void setSentimentScore(float score){
        this.sentimentScore = score;
    }

    public float getSentimentScore(){
        return this.sentimentScore;
    }

    public String setSentimentMagnitude(float magnitude) {
        this.sentimentMagnitude = score;
    }

    public float getSentimentMagnitude() {
        return this.sentimentMagnitude;
    }

    public void setQuery(String query){
        this.query = query;
    }

    public void getQuery(){
        return this.query;
    }
