package com.google.sps.data;

import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.AnalyzeEntitiesRequest;
import com.google.cloud.language.v1.AnalyzeEntitiesResponse;
import com.google.cloud.language.v1.Entity;
import java.io.IOException;

import java.util.stream.Collectors;
import java.util.List;
import java.util.Comparator;

// A class for text queries.
public class Query {

    private String queryText;

    // Query constructor that directly sets text.
    public Query (String queryText) {
        this.queryText = queryText;
    }

    // Make a query based on salience scores given text and a query size.
    public static Query makeQuery(Video video, int querySize) throws IOException {
        // Using only title and description seems to work well.
        String metadata = video.getTitle() + " " + video.getDescription();

        // Set up language service.
        LanguageServiceClient languageService = LanguageServiceClient.create();

        // Analyze the text.
        Document doc =
            Document.newBuilder().setContent(metadata).setType(Document.Type.PLAIN_TEXT).build();
        AnalyzeEntitiesRequest request = AnalyzeEntitiesRequest.newBuilder()
            .setDocument(doc)
            .build();
        AnalyzeEntitiesResponse response = languageService.analyzeEntities(request);

        // Sort words in decreasing order of salience and take the top few.
        String queryText = response.getEntitiesList().stream()
            .sorted(Comparator.comparingDouble(Entity::getSalience).reversed())
            .map(e -> e.getName())
            .limit(querySize)
            .collect(Collectors.joining(" "));

        // Construct and return a query from these words.
        return new Query(queryText);
    }

    public String toString() {
        return queryText;
    }
}