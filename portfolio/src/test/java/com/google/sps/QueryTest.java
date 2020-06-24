package com.google.sps;

import com.google.sps.data.Query;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.JUnit4;
import java.io.IOException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class QueryTest {

    private static String[] samples = new String[1];

    @Before
    public void setUp() throws IOException{
        System.out.println(System.getProperty("user.dir"));
        samples[0] = fileToString("src/test/java/com/google/sps/samples/body_language_metadata.txt");
    }

    @Test
    public void testQuery() {
        Query.makeQuery(samples[0], 7);
    }

    private static String fileToString(String path) throws IOException{
        String text = "";
        text = new String(Files.readAllBytes(Paths.get(path).toRealPath()));
        return text;
    }
}