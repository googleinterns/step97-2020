package com.google.sps.data;

import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.AnalyzeEntitiesRequest;
import com.google.cloud.language.v1.AnalyzeSentimentResponse;
import com.google.cloud.language.v1.Entity;
import java.io.IOException;

import java.util.stream.Collectors;
import java.util.List;
import java.util.Comparator;


public class SentimentTools {
    //how to interpret these values: https://cloud.google.com/natural-language/docs/basics#interpreting_sentiment_analysis_values
    private float score;
    private float magnitude;
    /*
    public Sentiment (Video video) {
        
    }
    */
    //Text constructor for pre-integration testing purposes
    public SentimentTools (String caption) {
        LanguageServiceClient language = LanguageServiceClient.create()) 
        Document doc = Document.newBuilder().setContent(caption).setType(Type.PLAIN_TEXT).build();
        AnalyzeSentimentResponse response = language.analyzeSentiment(doc);
        Sentiment sentiment = response.getDocumentSentiment();
        if (sentiment == null) {
            System.out.println("No sentiment found");
        } else {
            score = sentiment.getScore();
            magnitude = sentiment.getMagnitude();
        }
    }
    public float getScore(){
        return score;
    }
    public float getMagnitude(){
        return score;
    }
}