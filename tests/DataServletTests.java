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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DataServletTests {

  // Maximum eventual consistency.
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
        assertEquals(1, ds.prepare(new Query("Dummy")).countEntities(withLimit(10)));
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
        } catch(EntityNotFoundException e){
            fail("Entity not found in Database")
        }
        assertEquals(true, queriedDummyParent == dummyParent);
    }
    
    @Test
    public void EntityPropertiesArePutInDatastore(){
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        dummyString = "Bananas";
        Entity dummy = new Entity("Dummy");
        dummy.setProperty("Fruit", dummyString);
        ds.put(dummy);
        Key dummyKey = dummy.getKey();
        Entity queriedDummy;
        try {
            queriedDummy = ds.get(dummyKey);
        } catch(EntityNotFoundException e){
            fail("Entity not found in Database")
        }
        assertEquals(true, queriedDummy.getProperty("Fruit").toString().equals(dummyString));
    }
}