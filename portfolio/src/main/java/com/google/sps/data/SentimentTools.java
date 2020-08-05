package com.google.sps.data;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.AnalyzeEntitiesRequest;
import com.google.cloud.language.v1.AnalyzeSentimentResponse;
import com.google.cloud.language.v1.Sentiment;
import com.google.cloud.language.v1.Entity;
import com.google.sps.data.Video;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.List;

public class SentimentTools {
    //how to interpret these values: https://cloud.google.com/natural-language/docs/basics#interpreting_sentiment_analysis_values
    Sentiment sentiment;
    private static LanguageServiceClient language;
    //Static initialization for efficiency.
    static {
        try {
            language = LanguageServiceClient.create();
        } catch (IOException e) {
            language = null;
        }
    }

    //If the user passes a video in, we grab captions from the video.
    public SentimentTools (Video video) throws IOException{
        this(video.getDescription() + " " + video.getCaptions());
    }

    public SentimentTools (String captions) throws IOException {
        //If static initialization failed, try again.
        if (language == null) {
            language = LanguageServiceClient.create();
        }
        //Build a text document using captions from the video.
        Document doc = Document.newBuilder().setContent(captions).setType(Type.PLAIN_TEXT).build();
        AnalyzeSentimentResponse response = language.analyzeSentiment(doc);
        this.sentiment = response.getDocumentSentiment();
        if (sentiment == null) {
            System.out.println("No sentiment found");
        }
    }

    public float getScore(){
        return sentiment.getScore();
    }

    public float getMagnitude(){
        return sentiment.getMagnitude();
    }
}