package ArenaGame.database;

import ArenaGame.database.BattleLogDAO;

public class TestBattleLogDAO {
    public static void main(String[] args) {
        BattleLogDAO dao = new BattleLogDAO();

        dao.addBattleLog("Devon", "Spartacus", "Win");
        dao.addBattleLog("Devon", "Commodus", "Loss");

        System.out.println("\n===== Battle Logs =====");
        dao.getLogs().forEach(System.out::println);

        System.out.println("\nTotal Logs: " + dao.numLogs());

        // Uncomment to test clear - clear working as of 12-11
        // dao.clearLogs();
    }
}
