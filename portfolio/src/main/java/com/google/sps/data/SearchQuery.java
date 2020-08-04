package com.google.sps.data;

import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.AnalyzeEntitiesRequest;
import com.google.cloud.language.v1.AnalyzeEntitiesResponse;
import com.google.cloud.language.v1.Entity;
import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// A class for text queries.
public class SearchQuery {

    private static LanguageServiceClient languageService;
    // Static initialization as per: https://stackoverflow.com/questions/1028661/unhandled-exceptions-in-field-initializations.
    static {
        try {
            languageService = LanguageServiceClient.create();
        } catch (IOException e) {
            languageService = null;
        }
    }
    private String queryText;

    // Make a query based on salience scores given text and a query size.
    public SearchQuery(Video video, int querySize) {
        String queryText;
        List<String> keywordList;
        if (languageService == null) {
            keywordList = Arrays.asList(video.getTitle().split("\\s+"));
        } else {
            // Using only title and description seems to work well.
            String metadata = (video.getTitle() + " " + video.getDescription()).toLowerCase();
            // A request with a few hundred words takes around 10 seconds to process.
            // Quotas are 600 requests/sec and 800k requests/day.
            Document doc =
                Document.newBuilder().setContent(metadata).setType(Document.Type.PLAIN_TEXT).build();
            AnalyzeEntitiesRequest request = AnalyzeEntitiesRequest.newBuilder()
                .setDocument(doc)
                .build();
            AnalyzeEntitiesResponse response = languageService.analyzeEntities(request);

            // Take top few distinct words (they are autosorted by decreasing salience).
            keywordList = response.getEntitiesList().stream()
                // Require a basic amount of salience (importance)
                // Generally important words seemed to be above 0.15 and unimportant words seemed to be less than 0.10.
                .filter(e -> e.getSalience() > 0.12)
                .map(e -> e.getName())
                .collect(Collectors.toList());
        }
        queryText = filterKeywords(keywordList, querySize);
        this.queryText = queryText;
    }

    // Return a query string with filtered keywords
    public static String filterKeywords(List<String> keywordList, int querySize) {
        return keywordList.stream()
        // Make sure entities are distinct
        .distinct()
        // Remove excessively long entities
        .filter(e -> e.length() < 30)
        // Only allow phrases with letters and numbers (no special chars)
        .filter(e -> e.matches("^[a-zA-Z0-9 ]+$"))
        // Remove URLs
        .filter(e -> !e.matches("^http[s]?:[^ ]*$"))
        .limit(querySize)
        .collect(Collectors.joining(" "));
    }

    public String toString() {
        return queryText;
    }
}