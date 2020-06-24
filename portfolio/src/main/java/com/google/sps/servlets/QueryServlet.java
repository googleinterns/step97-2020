package com.google.sps;

import com.google.sps.data.Query;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@WebServlet("/query")
public class QueryServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        loadSamples();
        response.setContentType("text/plain");
        response.getWriter().println(Query.makeQuery(samples[0], 7));
    }

    private static String[] samples = new String[1];

    public static void loadSamples() throws IOException{
        samples[0] = fileToString("body_language_metadata.txt");
    }

    private static String fileToString(String path) throws IOException{
        String text = "";
        text = new String(Files.readAllBytes(Paths.get(path).toRealPath()));
        return text;
    }
}