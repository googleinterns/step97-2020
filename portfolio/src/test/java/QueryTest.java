import static org.junit.Assert.assertEquals;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.Video;
import com.google.sps.data.SearchQuery;
import java.util.Arrays;
import java.util.List;
import java.net.MalformedURLException;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

public class QueryTest {
    @Test
    public void UnitTest1() throws MalformedURLException{
        String title = "Taylor Swift - Blank Space";
        String description = "New single ME! (feat. Brendon Urie of Panic! At The Disco) available now. Download here: https://TaylorSwift.lnk.to/MeYD\n \n►Exclusive Merch: https://store.taylorswift.com\n \n►Follow Taylor Swift Online\nInstagram: http://www.instagram.com/taylorswift\nFacebook";
        Video v = new Video(null, title, description, "https://www.youtube.com", true);
        String query = new SearchQuery(v, 10).toString();
        String filteredQuery = Arrays.asList(query.split(" ")).stream()
        // Make sure entities are distinct
        .distinct()
        // Remove excessively long entities
        .filter(e -> e.length() < 30)
        // Only allow phrases with letters and numbers (no special chars)
        .filter(e -> e.matches("^[a-zA-Z0-9 ]+$"))
        // Remove URLs
        .filter(e -> !e.matches("^http[s]?:[^ ]*$"))
        .collect(Collectors.joining(" "));
        assertEquals(query, filteredQuery);
    }
}