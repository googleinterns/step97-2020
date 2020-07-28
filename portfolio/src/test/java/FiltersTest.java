import static org.junit.Assert.assertEquals;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.SearchQuery;
import java.util.Arrays;
import java.util.List;
import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

public class FiltersTest {
    @Test
    public void GeneralFilterTest1() throws MalformedURLException{
        String mockTitle = "MOCK_TITLE https:// hello hi how$ a4e/you";
        String mockDescription = "MOCK_DESCRIPTION this-% is A  \n^description";
        List<String> keywordsList = Arrays.asList((mockTitle + " " + mockDescription).toLowerCase().split("\\s+"));
        String query = SearchQuery.filterKeywords(keywordsList, 7);
        // No special characters.
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9 ]");
        Matcher matcher = pattern.matcher(query);
        assertEquals(matcher.find(), false);
        // URL removed.
        assertEquals(query.indexOf("https://"), -1);
        // Filtering again without a size limit shouldn't change the query.
        String filteredQuery = SearchQuery.filterKeywords(Arrays.asList(query.split("\\s+")), 1000);
        assertEquals(query, filteredQuery);
    }

    @Test
    public void GeneralFilterTest2() throws MalformedURLException{
        String mockTitle = "MOCK TITLE test/test$ test hello.";
        String mockDescription = "MOCK DESCRIPTION $$$ c4$h m0nEy/";
        List<String> keywordsList = Arrays.asList((mockTitle + mockDescription).toLowerCase().split("\\s+"));
        String query = SearchQuery.filterKeywords(keywordsList, 7);
        // No special characters.
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9 ]");
        Matcher matcher = pattern.matcher(query);
        assertEquals(matcher.find(), false);
        // Filtering again without a size limit shouldn't change the query.
        String filteredQuery = SearchQuery.filterKeywords(Arrays.asList(query.split("\\s+")), 1000);
        assertEquals(query, filteredQuery);
    }

    @Test
    public void LengthTest() {
        String mockTitle = "MOCK_asdfasdfasdfasdfjgjafjfjwefjsdfjsdfjasdfjdsfjsdfasdfdfasdfsadf";
        String mockDescription = "mock description";
        List<String> keywordsList = Arrays.asList((mockTitle + " " + mockDescription).toLowerCase().split("\\s+"));
        String query = SearchQuery.filterKeywords(keywordsList, 7);
        // Long string should be removed.
        assertEquals(query, "mock description");
    }

    @Test
    public void DuplicateTest() {
        String mockTitle = "mock mock mock mock mock mock";
        String mockDescription = "mock mock mock mock mock mock mock";
        List<String> keywordsList = Arrays.asList((mockTitle + " " + mockDescription).toLowerCase().split("\\s+"));
        String query = SearchQuery.filterKeywords(keywordsList, 7);
        // Duplicates should be removed.
        assertEquals(query, "mock");
    }
}