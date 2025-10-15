package ArenaGame.ui;

import ArenaGame.*;
import ArenaGame.database.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

/**
 *
 * BattleFrame is a GUI implementation of the combat loop from BattleManager and
 * PlayerGladiator in v1
 */
public class BattleFrame extends JFrame {

    private final PlayerDAO playerDAO;
    private final GladiatorDAO gladiatorDAO;
    private final BattleLogDAO battleLogDAO;

    private PlayerGladiator playerGladiator;
    private Gladiator enemyGladiator;
    private final JTextArea battleLogArea;
    private final JLabel playerStats;
    private final JLabel enemyStats;
    private final JButton buttonAttack, buttonGuard, buttonUseItem, buttonTaunt, buttonExit;

    /**
     * Create a new BattleFrame fetching data from the DAOs
     */
    public BattleFrame(String playerName) {
        this.playerDAO = new PlayerDAO();
        this.gladiatorDAO = new GladiatorDAO();
        this.battleLogDAO = new BattleLogDAO();

        this.playerGladiator = new PlayerGladiator(playerName, 100, 100, 30, 5, new java.util.Scanner(System.in));
        this.enemyGladiator = gladiatorDAO.getRandomGladiator();

        setTitle("ARENAv2 - Battle");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Stats panel
        JPanel topPanel = new JPanel(new GridLayout(1, 2));
        playerStats = new JLabel(formatStats(playerGladiator), SwingConstants.CENTER);
        enemyStats = new JLabel(formatStats(enemyGladiator), SwingConstants.CENTER);

        playerStats.setFont(new Font("Courier", Font.BOLD, 14));
        enemyStats.setFont(new Font("Courier", Font.BOLD, 14));

        topPanel.add(playerStats);
        topPanel.add(enemyStats);

        // Log Display Centering
        battleLogArea = new JTextArea();
        battleLogArea.setEditable(false);
        battleLogArea.setLineWrap(true);
        battleLogArea.setFont(new Font("Serif", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(battleLogArea);

        // Player Interactable Elements
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 5, 5));
        buttonAttack = new JButton("Attack");
        buttonGuard = new JButton("Guard");
        buttonUseItem = new JButton("Use Item");
        buttonTaunt = new JButton("Taunt");
        buttonExit = new JButton("Exit");

        buttonAttack.addActionListener(this::attackAction);
        buttonGuard.addActionListener(this::guardAction);
        buttonUseItem.addActionListener(this::itemAction);
        buttonTaunt.addActionListener(this::tauntAction);
        buttonExit.addActionListener(event -> dispose());

        buttonPanel.add(buttonAttack);
        buttonPanel.add(buttonGuard);
        buttonPanel.add(buttonUseItem);
        buttonPanel.add(buttonTaunt);
        buttonPanel.add(buttonExit);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        appendLog(enemyGladiator.getName() + " stands to face you in the ARENA!\n");

        setVisible(true);
    }

    // ========= BATTLE ACTIONS ==========
    private void attackAction(ActionEvent event) {
        int damage = enemyGladiator.getDefense() < playerGladiator.getAttack()
                ? playerGladiator.getAttack() - enemyGladiator.getDefense() : 1;
        enemyGladiator.setHealth(Math.max(0, enemyGladiator.getHealth() - damage));
        appendLog("You hit " + enemyGladiator.getName() + " for " + damage + " damage.\n");
        battleLogDAO.addBattleLog(playerGladiator.getName(), enemyGladiator.getName(), "ATTACK");
        
        playerGladiator.setBlocking(false);
        updateStats();
        checkBattleOutcome();
        enemyTurn();
    }

    private void guardAction(ActionEvent event) {
        playerGladiator.setBlocking(true);
        appendLog("You brace yourself for the next attack.\n");
        battleLogDAO.addBattleLog(enemyGladiator.getName(), playerGladiator.getName(), "GUARD");
        enemyTurn();
    }

    private void itemAction(ActionEvent event) {
        if (playerGladiator.getInventory().isEmpty()) {
            appendLog("Your inventory is empty!\n");
            enemyTurn();
            return;
        }

        var inventory = playerGladiator.getInventory();
        String[] itemNames = inventory.getItems().stream().map(Item::getName).toArray(String[]::new);
        String selectedItem = (String) JOptionPane.showInputDialog(this,
                "Select and item to use:", "Inventory",
                JOptionPane.PLAIN_MESSAGE, null, itemNames, itemNames[0]);

        if (selectedItem != null) {
            Item item = inventory.getItemByName(selectedItem);
            if (item != null) {
                playerGladiator.useItem(item);
                appendLog("Successfully used " + selectedItem + ".\n");
            }
        }
        updateStats();
        enemyTurn();
    }

    private void tauntAction(ActionEvent event) {
        appendLog("You taunt " + enemyGladiator.getName() + ".\n");
        if (new Random().nextInt(10) >= 3) { 
            int attackDebuff = (int) (enemyGladiator.getAttack() * 0.2);
            int defenseDebuff = (int) (enemyGladiator.getDefense() * 0.2);
            enemyGladiator.setAttack(Math.max(1, enemyGladiator.getAttack() - attackDebuff));
            enemyGladiator.setDefense(Math.max(0, enemyGladiator.getDefense() - defenseDebuff));
            appendLog(enemyGladiator.getName() + " loses -" + attackDebuff + " ATK and -" + defenseDebuff + " DEF permanently!\n");
            battleLogDAO.addBattleLog(playerGladiator.getName(), enemyGladiator.getName(), "TAUNT SUCCESS");
        } else {
            appendLog(enemyGladiator.getName() + " ignores your taunt!\n");
            battleLogDAO.addBattleLog(playerGladiator.getName(), enemyGladiator.getName(), "TAUNT FAIL");
        }
        updateStats();
        enemyTurn();
    }

    private void enemyTurn() {
        if (!enemyGladiator.isAlive()) return;

        int action = new Random().nextInt(3);
        switch (action) {
            case 0 -> { // Attack
                int damage = playerGladiator.isBlocking()
                        ? Math.max(1, enemyGladiator.getAttack() / 2)
                        : enemyGladiator.getAttack();

                playerGladiator.setHealth(Math.max(0, playerGladiator.getHealth() - damage));
                appendLog(enemyGladiator.getName() + " hits you for " + damage + " damage!\n");
                playerGladiator.setBlocking(false);
                battleLogDAO.addBattleLog(enemyGladiator.getName(), playerGladiator.getName(), "ATTACK");
            }

            case 1 -> { // Guard
                enemyGladiator.setBlocking(true);
                appendLog(enemyGladiator.getName() + " braces for the next incoming attack.\n");
                battleLogDAO.addBattleLog(enemyGladiator.getName(), playerGladiator.getName(), "GUARD");
            }

            case 2 -> { // Taunt (debuffs player)
                appendLog(enemyGladiator.getName() + " taunts you! \n");
                if (new Random().nextInt(10) >= 3) {
                    int atkLoss = (int) (playerGladiator.getAttack() * 0.2);
                    int defLoss = (int) (playerGladiator.getDefense() * 0.2);
                    playerGladiator.setAttack(Math.max(1, playerGladiator.getAttack() - atkLoss));
                    playerGladiator.setDefense(Math.max(0, playerGladiator.getDefense() - defLoss));
                    appendLog("You lose -" + atkLoss + " ATK and -" + defLoss + " DEF permanently!\n");
                    battleLogDAO.addBattleLog(enemyGladiator.getName(), playerGladiator.getName(), "TAUNT SUCCESS");
                } else {
                    appendLog("You shrug off the insult.\n");
                    battleLogDAO.addBattleLog(enemyGladiator.getName(), playerGladiator.getName(), "TAUNT FAIL");
                }
            }
        }

        updateStats();
        checkBattleOutcome();
    }

    private void checkBattleOutcome() {
        if (!enemyGladiator.isAlive()) {
            appendLog("\n You defeated " + enemyGladiator.getName() + " !\n");
            battleLogDAO.addBattleLog(playerGladiator.getName(), enemyGladiator.getName(), "WIN");

            int newScore = playerDAO.getScore(playerGladiator.getName()) + 1;
            playerDAO.updateScore(playerGladiator.getName(), newScore);
            JOptionPane.showMessageDialog(this, "You stand victorious! Your score is: " + newScore);
            dispose();
        } else if (!playerGladiator.isAlive()) {
            appendLog("\n You were defeated by " + enemyGladiator.getName() + "!\n");
            battleLogDAO.addBattleLog(playerGladiator.getName(), enemyGladiator.getName(), "LOSS");
            JOptionPane.showMessageDialog(this, "Defeat!");
            dispose();
        }
    }

    // =========== UTIL/HELPER METHODS ==========
    private void appendLog(String message) {
        battleLogArea.append(message);
        battleLogArea.setCaretPosition(battleLogArea.getDocument().getLength());
    }

    private void updateStats() {
        playerStats.setText(formatStats(playerGladiator));
        enemyStats.setText(formatStats(enemyGladiator));
    }

    private String formatStats(Gladiator gladiator) {
        return String.format("%s HP: %d/%d  ATK: %d  DEF: %d",
                gladiator.getName(), gladiator.getHealth(), gladiator.getMaxHealth(), gladiator.getAttack(), gladiator.getDefense());
    }

}
