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

public class Query {

    private String text;

    public Query (String text) {
        this.text = text;
    }

    public static Query makeQuery(String captions, int size) {
        LanguageServiceClient languageService;
        try {
            languageService = LanguageServiceClient.create();
        } catch (IOException e) {
            return null;
        }

        Document doc =
            Document.newBuilder().setContent(captions).setType(Document.Type.PLAIN_TEXT).build();
        AnalyzeEntitiesRequest request = AnalyzeEntitiesRequest.newBuilder()
            .setDocument(doc)
            .build();
        AnalyzeEntitiesResponse response = languageService.analyzeEntities(request);

        String text = response.getEntitiesList().stream()
            .sorted(Comparator.comparingDouble(Entity::getSalience).reversed())
            .map(e -> e.getName())
            .limit(size)
            .collect(Collectors.joining(" "));
        
        return new Query(text);
    }

    public String toString() {
        return text;
    }
}