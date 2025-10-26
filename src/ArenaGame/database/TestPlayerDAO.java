package ArenaGame.database;

public class TestPlayerDAO {

    public static void main(String[] args) {
        PlayerDAO dao = new PlayerDAO();

       // dao.addPlayer("Devon");
       // dao.addPlayer("Marcus");
       // dao.updateScore("Devon", 7);
      //  dao.updateScore("Marcus", 3);

       // System.out.println("Devon's score: " + dao.getScore("Devon"));
       // System.out.println("Marcus' score: " + dao.getScore("Marcus"));

        System.out.println("\nAll Players:");
        dao.getAllPlayers().forEach((name, score) ->
            System.out.println(name + " → " + score)
        );

         dao.resetScores();
    }
}
