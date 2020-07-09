package com.google.sps.data;
import com.google.sps.data.Query;
import com.google.sps.data.SentimentTools;
import com.google.sps.data.Video;

// Represents a video analysis, holds the results from an analysis and can also analyze a video that is passed in.
public class VideoAnalysis {
    private float sentimentScore;
    private float sentimentMagnitude;
    private String query;
    private static int QUERY_SIZE = 7;
    
    //If given a video we will run analysis on it !!THIS IS SLOWER THAN JUST PASSING IN THE VALUES IF YOU HAVE THEM!!
    public VideoAnalysis(Video video) {
        SentimentTools sentimentAnalysis = new SentimentTools(video);
        Query queryAnalysis = new Query(video, QUERY_SIZE);
        this.sentimentScore = sentimentAnalysis.getScore();
        this.sentimentMagnitude = sentimentAnalysis.getMagnitude();
        this.query = queryAnalysis.toString();
    }

    //Pass in the results from a Video analysis to create this object
    public VideoAnalysis(float _sentimentScore, float _sentimentMagnitude, String _query) {
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

    public void setSentimentMagnitude(float magnitude) {
        this.sentimentMagnitude = magnitude;
    }

    public float getSentimentMagnitude() {
        return this.sentimentMagnitude;
    }

    public void setQuery(String query){
        this.query = query;
    }

    public String getQuery(){
        return this.query;
    }
}