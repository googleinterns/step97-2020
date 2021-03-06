import com.google.sps.data.ErrorConsts;
import com.google.sps.servlets.AnalysisServlet;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.io.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.junit.Assert.assertEquals;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**

* TEST CLASSES AND THERE FILES HAVE TO FOLLOW ANY OF THE FOLLOWING NAMING CONVENTIONS DUE TO MAVEN
* [rest of name]Test.java -> BlahBlahBlahTest.java
* Test[rest of name].java -> TestBlahBlahBlah.java
*/
public class AnalysisServletUnitTest extends Mockito {
  String TEST_VIDEO_ID = "abcdefghijklmnop";
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
  .setDefaultHighRepJobPolicyUnappliedJobPercentage(100));

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testDoGetWithNoVideoId() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);    
        //Add an empty video id parameter to the get request
        when(request.getParameter("videoId")).thenReturn("");
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        //begin a get request with our parameter
        new AnalysisServlet().doGet(request, response);
        writer.flush(); // it may not have been flushed yet...
        assertEquals(true, stringWriter.toString().contains(ErrorConsts.EMPTY_VIDEO_ID));
    }

    @Test
    public void testDoGetWithUnusedVideoId() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);    
        //Add a video id parameter to the get request
        when(request.getParameter("videoId")).thenReturn(this.TEST_VIDEO_ID);
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        //begin a get request with our parameter
        new AnalysisServlet().doGet(request, response);
        writer.flush(); // it may not have been flushed yet...
        assertEquals(true, stringWriter.toString().contains(ErrorConsts.ANALYSIS_NOT_FOUND_IN_DB));
    }
}