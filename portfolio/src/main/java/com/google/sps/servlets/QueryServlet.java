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

// Servlet for testing queries.
@WebServlet("/query")
public class QueryServlet extends HttpServlet {

    private static int QUERY_SIZE = 7;

    // Get a query based on one of the sample files.
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String fileName = request.getParameter("name");
        response.setContentType("text/plain");
        response.getWriter().println(Query.makeQuery(fileToString(fileName), QUERY_SIZE));
    }

    // Extract all the text from the selected sample file.
    private static String fileToString(String fileName) throws IOException{
        String text = "";
        text = new String(Files.readAllBytes(Paths.get(fileName)));
        return text;
    }
}