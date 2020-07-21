package com.google.sps.data;
import com.google.sps.data.SearchQuery;
import com.google.sps.data.SentimentTools;
import com.google.sps.data.Video;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;

// Represents a video analysis, holds the results from an analysis and can also analyze a video that is passed in.
public class VideoAnalysis {
    private final String videoId;
    private final String videoTitle;
    private final float sentimentScore;
    private final float sentimentMagnitude;
    private final String searchQueryString;
    private final LocalDate timestamp;
    private static int SEARCH_QUERY_SIZE = 7;
    
    //If given a video we will run analysis on it !!THIS IS SLOWER THAN JUST PASSING IN THE VALUES IF YOU HAVE THEM!!
    public VideoAnalysis(Video video) throws IOException {
        this.videoId = video.getVideoId();
        this.videoTitle = video.getTitle();
        SentimentTools sentimentAnalysis = new SentimentTools(video);
        SearchQuery searchQuery = new SearchQuery(video, SEARCH_QUERY_SIZE);
        this.sentimentScore = sentimentAnalysis.getScore();
        this.sentimentMagnitude = sentimentAnalysis.getMagnitude();
        this.searchQueryString = searchQuery.toString();
        this.timestamp = LocalDate.now();
    }

    //Pass in the results from a Video analysis to create this object
    public VideoAnalysis(String videoId, String videoTitle, float _sentimentScore,
        float _sentimentMagnitude, String _searchQueryString) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.sentimentScore = _sentimentScore;
        this.sentimentMagnitude = _sentimentMagnitude;
        this.searchQueryString = _searchQueryString;
        this.timestamp = LocalDate.now();
    }

    public float getSentimentScore(){
        return this.sentimentScore;
    }

    public float getSentimentMagnitude() {
        return this.sentimentMagnitude;
    }

    public String getSearchQueryString(){
        return this.searchQueryString;
    }

    public LocalDate getTimestamp(){
        return this.timestamp;
    }

    public String getTimestampAsString(){
        return this.timestamp.toString();
    }

    public boolean isExpiredAnalysis(){
        LocalDate now = LocalDate.now();
        Period diff = Period.between(this.timestamp, now);
        return (diff.getDays() >= 7);
    }
}
