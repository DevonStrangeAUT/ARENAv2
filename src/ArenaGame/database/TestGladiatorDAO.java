package ArenaGame.database;

import ArenaGame.Gladiator;

public class TestGladiatorDAO {
    public static void main(String[] args) {
        GladiatorDAO dao = new GladiatorDAO();

        System.out.println("\nAll Gladiators:");
        for (Gladiator g : dao.getAllGladiators()) {
            System.out.printf("%s -> HP:%d/%d, ATK:%d, DEF:%d%n",
                    g.getName(), g.getHealth(), g.getMaxHealth(), g.getAttack(), g.getDefense());
        }

        System.out.println("\nRandom Enemy:");
        Gladiator enemy = dao.getRandomGladiator();
        if (enemy != null) {
            System.out.println("You encounter " + enemy.getName() + "!");
        }

        // Uncomment to test reset
        // dao.resetGladiators();
    }
}
