package com.google.sps.data;
import com.google.sps.data.Query;
import com.google.sps.data.SentimentTools;
import com.google.sps.data.Video;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;

// Represents a video analysis, holds the results from an analysis and can also analyze a video that is passed in.
public class VideoAnalysis {
    private final float sentimentScore;
    private final float sentimentMagnitude;
    private final String query;
    private final String timestamp;
    private static int QUERY_SIZE = 7;
    
    //If given a video we will run analysis on it !!THIS IS SLOWER THAN JUST PASSING IN THE VALUES IF YOU HAVE THEM!!
    public VideoAnalysis(Video video) throws IOException {
        SentimentTools sentimentAnalysis = new SentimentTools(video);
        Query queryAnalysis = new Query(video, QUERY_SIZE);
        this.sentimentScore = sentimentAnalysis.getScore();
        this.sentimentMagnitude = sentimentAnalysis.getMagnitude();
        this.query = queryAnalysis.toString();
        this.timestamp = LocalDate.now().toString();
    }

    //Pass in the results from a Video analysis to create this object
    public VideoAnalysis(float _sentimentScore, float _sentimentMagnitude, String _query) {
        this.sentimentScore = _sentimentScore;
        this.sentimentMagnitude = _sentimentMagnitude;
        this.query = _query;
    }

    public float getSentimentScore(){
        return this.sentimentScore;
    }

    public float getSentimentMagnitude() {
        return this.sentimentMagnitude;
    }

    public String getQuery(){
        return this.query;
    }

    public LocalDate getTimestamp(){
        return this.timestamp;
    }

    public boolean isExpiredAnalysis(){
        LocalDate now = LocalDate.now();
        Period diff = Period.between(LocalDate.parse(this.timestamp), now);
        if(diff.days() >= 7){
            return true;
        }else {
            return false;
        }
    }
}
