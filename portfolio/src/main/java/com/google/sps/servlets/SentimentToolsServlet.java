package com.google.sps;
import com.google.sps.data.SentimentTools;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

// Servlet for testing queries.
@WebServlet("/sentiment")
public class SentimentToolsServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String text = "I simply love dogs!";
        SentimentTools st = new SentimentTools(text);
        response.setContentType("text/html");
        response.getWriter().println(st.getScore());
    }
}