package ArenaGame.database;

import java.sql.*;
import org.junit.*;
import static org.junit.Assert.*;

public class DatabaseManagerTest {

    private DatabaseManager dbManager;

    @Before
    public void setUp() {
        dbManager = DatabaseManager.getInstance();
    }

    @After
    public void tearDown() {
        dbManager = null;
    }

    /**
     * Test of getInstance method, of class DatabaseManager.
     */
    @Test
    public void testGetInstance() {
        DatabaseManager db1 = DatabaseManager.getInstance();
        DatabaseManager db2 = DatabaseManager.getInstance();
        assertSame("getInstance() should return the same singletion object when there are multiple databases", db1, db2);
    }

    /**
     * Test of getConnection method, of class DatabaseManager.
     */
    @Test
    public void testGetConnection() {
        Connection connection = dbManager.getConnection();
        assertNotNull("Database connection should not return null", connection);
    }

    /**
     * Test of getConnection method, of class DatabaseManager.
     */
    @Test
    public void testConnectionValid() {
        try {
            Connection connection = dbManager.getConnection();
            assertTrue("Connection should be valid", connection.isValid(2));
        } catch (Exception exception) {
            fail("Connection validation failed: " + exception.getMessage());
        }
    }

    @Test
    public void testDriver() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException exception) {
            fail("Derby Embedded driver not available in the classpath");
        }
    }

    /**
     * Test of getConnection method, of class DatabaseManager.
     */
    @Test
    public void testConnectionAccess() {
        try {
            dbManager.getConnection();
        } catch (Exception exception) {
            fail("getConnection() threw an exception: " + exception.getMessage());
        }
    }
    
    /**
     * Test of getConnection method & getInstance method, of class DatabaseManager.
     */
    @Test
    public void testConnectionInstancePersistance() {
        Connection connection1 = DatabaseManager.getInstance().getConnection();
        Connection connection2 = DatabaseManager.getInstance().getConnection();
        assertSame("Both connection calls should return the same instance confirming singletion behaviour", connection1, connection2 );
    }
}
