package ArenaGame.database;

import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class BattleLogDAOTest {

    private BattleLogDAO BLdao;

    @Before
    public void setUp() {
        BLdao = new BattleLogDAO();
        BLdao.clearLogs();
    }

    @After
    public void tearDown() {
        BLdao.clearLogs();
        BLdao = null;
    }

    /**
     * Test of addBattleLog method, of class BattleLogDAO.
     */
    @Test
    public void testAddBattleLog() {
        BLdao.addBattleLog("Fynn", "Spartacus", "WIN");
        List<String> logs = BLdao.getLogs();
        assertFalse("Logs list shound not be empty after added to", logs.isEmpty());
        assertTrue("Inserted log should contain player name in form String", logs.get(0).contains("Fynn"));
    }

    /**
     * Test of addBattleLog method, of class BattleLogDAO.
     */
    @Test
    public void testAddMultipleBattleLogs() {
        BLdao.addBattleLog("Fynn", "Spartacus", "WIN");
        BLdao.addBattleLog("Devon", "Commodus", "LOSS");
        List<String> logs = BLdao.getLogs();
        assertTrue("Logs list should contain at least 2 logs", logs.size() >= 2);
    }

    /**
     * Test of getLogs method, of class BattleLogDAO.
     */
    @Test
    public void testGetLogsEmptyWhenCleared() {
        BLdao.addBattleLog("Fynn", "Spartacus", "WIN");
        BLdao.clearLogs();
        List<String> logs = BLdao.getLogs();
        assertTrue("When cleared, list should return empty", logs.isEmpty());
    }

    /**
     * Test of numLogs method, of class BattleLogDAO.
     */
    @Test
    public void testNumLogsAfterInsert() {
        BLdao.addBattleLog("Fynn", "Spartacus", "WIN");
        BLdao.addBattleLog("Devon", "Commodus", "LOSS");
        int num = BLdao.numLogs();
        assertEquals("Log count should be at least 2 after added to", 2, num);
    }

    /**
     * Test of numLogs method, of class BattleLogDAO.
     */
    @Test
    public void testNumLogsAfterCleared() {
        BLdao.addBattleLog("Fynn", "Spartacus", "WIN");
        BLdao.addBattleLog("Devon", "Commodus", "LOSS");
        BLdao.clearLogs();
        int num = BLdao.numLogs();
        assertEquals("Log count should be 0 after clearing", 0, num);
    }
}
