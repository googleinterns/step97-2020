import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.google.sps.data.AUX;
import com.google.sps.data.VideoException;

/**
* TEST CLASSES AND THERE FILES HAVE TO FOLLOW ANY OF THE FOLLOWING NAMING CONVENTIONS DUE TO MAVEN
* [rest of name]Test.java -> BlahBlahBlahTest.java
* Test[rest of name].java -> TestBlahBlahBlah.java
*/

public class YoutubeUrlToIdTest{
    //There are 2 types of youtube links:
    private final static String type0 = "https://youtu.be/";
    private final static String type1 = "https://www.youtube.com/watch?v=";
    private final static String TEST_VIDEO_ID = "asdjfkasl";
    private final static String INVALID_LINK = "https://www.google.com/";

    //Test case when invalid link is provided
    @Test
    public void InvalidLinkProvided(){
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {AUX.youtubeUrlToId(INVALID_LINK);});
        String expectedMessage = "Link provided is not a valid youtube link";
        String actualMessage = thrown.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        
    }

    //Test case when no link is provided
    @Test
    public void NoLinkProvided(){
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {AUX.youtubeUrlToId("");});
        String expectedMessage = "Link provided is not a valid youtube link";
        String actualMessage = thrown.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    //Test case for type0 link
    @Test
    public void Type0LinkProvided(){
        String testUrl = type0 + TEST_VIDEO_ID;
        String result = AUX.youtubeUrlToId(testUrl);
        assertEquals(TEST_VIDEO_ID, result);
    }

    //Test case for type1 link
    @Test
    public void Type1LinkProvided(){
        String testUrl = type1 + TEST_VIDEO_ID;
        String result = AUX.youtubeUrlToId(testUrl);
        assertEquals(TEST_VIDEO_ID, result);
    }

    //Test case for when link is provided but it does not contain video ID
    @Test
    public void LinkDoesNotContainVideoId(){
        String testUrl = type1;
        String result = AUX.youtubeUrlToId(testUrl);
        assertEquals("", result);
    }
}