import static org.junit.Assert.assertEquals;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
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

public class FiltersTest {
    @Test
    public void FilterTest1() throws MalformedURLException{
        String title = "Taylor Swift - Blank Space";
        String description = "New single ME! (feat. Brendon Urie of Panic! At The Disco) available now. Download here: https://TaylorSwift.lnk.to/MeYD\n \n►Exclusive Merch: https://store.taylorswift.com\n \n►Follow Taylor Swift Online\nInstagram: http://www.instagram.com/taylorswift\nFacebook";
        List<String> keywordsList = Arrays.asList((title + description).toLowerCase().split(" "));
        String query = SearchQuery.filterKeywords(keywordsList, 7);
        // Filtering again shouldn't change the query.
        String filteredQuery = refilter(query);
        assertEquals(query, filteredQuery);
    }
    @Test
    public void FilterTest2() throws MalformedURLException{
        String title = "US surpasses 4 million coronavirus cases";
        String description = "The surging cases are causing more uncertainty about whether schools will reopen. ABC’s Mona Kosar Abdi reports.";
        List<String> keywordsList = Arrays.asList((title + description).toLowerCase().split(" "));
        String query = SearchQuery.filterKeywords(keywordsList, 10);
        // Filtering again shouldn't change the query.
        String filteredQuery = refilter(query);
        assertEquals(query, filteredQuery);
    }

    public static String refilter(String query) {
        return Arrays.asList(query.split(" ")).stream()
        // Make sure entities are distinct
        .distinct()
        // Remove excessively long entities
        .filter(e -> e.length() < 30)
        // Only allow phrases with letters and numbers (no special chars)
        .filter(e -> e.matches("^[a-zA-Z0-9 ]+$"))
        // Remove URLs
        .filter(e -> !e.matches("^http[s]?:[^ ]*$"))
        .collect(Collectors.joining(" "));
    }
}