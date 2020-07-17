import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.assertEquals;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/*TEST CLASSES AND THERE FILES HAVE TO FOLLOW ANY OF THE FOLLOWING NAMING CONVENTIONS DUE TO MAVEN
[rest of name]Test.java -> BlahBlahBlahTest.java
Test[rest of name].java -> TestBlahBlahBlah.java
*/

public class DatastoreTest {

  // Maximum eventual consistency. [https://en.wikipedia.org/wiki/Eventual_consistency] TL;DR, some writes will commit but will always fail to apply, meaning we can't see them with global queries, strong consistency queries like ancestor queries will work
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
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
    public void EntitiesArePutInDatastore(){
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Entity dummy = new Entity("Dummy");
        ds.put(dummy);
        try {
            assertEquals(true, ds.get(dummy.getKey()) != null);
        } catch(EntityNotFoundException e) {
            Assert.fail("Entity not found in Database");
        }
  }

    @Test
    public void EntitiesCanHaveParentsUsingKeys(){
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Entity dummyParent = new Entity("DummyParent");
        ds.put(dummyParent);
        Key dummyParentKey = dummyParent.getKey();
        Entity dummy = new Entity("Dummy", dummyParentKey);
        ds.put(dummy);
        Key queriedDummyParentKey = dummy.getParent();
        Entity queriedDummyParent; 
        try {
            queriedDummyParent = ds.get(queriedDummyParentKey);
            assertEquals(true, queriedDummyParent == dummyParent);
        } catch(EntityNotFoundException e){
            Assert.fail("Entity not found in Database");
        }
    }
    
    @Test
    public void EntityPropertiesArePutInDatastore(){
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        String dummyString = "Bananas";
        Entity dummy = new Entity("Dummy");
        dummy.setProperty("Fruit", dummyString);
        ds.put(dummy);
        Key dummyKey = dummy.getKey();
        Entity queriedDummy;
        try {
            queriedDummy = ds.get(dummyKey);
            assertEquals(true, queriedDummy.getProperty("Fruit").toString().equals(dummyString));
        } catch(EntityNotFoundException e){
            Assert.fail("Entity not found in Database");
        }
    }
}