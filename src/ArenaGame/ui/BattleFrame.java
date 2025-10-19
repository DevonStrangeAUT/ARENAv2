package ArenaGame.ui;

import ArenaGame.*;
import ArenaGame.database.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

/**
 *
 * BattleFrame is GUI implementation of the precursor classes battlemanager and
 * PlayerGladiator in v1. 
 * BattleFrame also handles turn-based combat and the enemy action algorithm.
 */
public class BattleFrame extends JFrame {

    // == Database Objects ==
    // imported from .database for usage during database read/writes
    private final PlayerDAO playerDAO;
    private final GladiatorDAO gladiatorDAO;
    private final BattleLogDAO battleLogDAO;

    // == Gladiator Objects ==
    // imported from base packages, contains functions used for combat back-end
    private final PlayerGladiator playerGladiator;
    private final Gladiator enemyGladiator;
    
    // == UI Elements ==
    private final JTextArea battleLogArea;
    private final JLabel playerStats;
    private final JLabel enemyStats;
    private final JButton buttonAttack, buttonGuard, buttonUseItem, buttonTaunt, buttonExit;

    /**
     * Create a new BattleFrame window fetching data from the DAOs
     *
     * @param playerName to use in battle and in DB writes
     */
    public BattleFrame(String playerName) {
        // == DAO setups ==
        this.playerDAO = new PlayerDAO();
        this.gladiatorDAO = new GladiatorDAO();
        this.battleLogDAO = new BattleLogDAO();

        // == Gladiator setups ==
        // Contains default stats, also select an enemy 
        // gladiator at random
        this.playerGladiator = new PlayerGladiator(playerName, 120, 120, 35, 10, new java.util.Scanner(System.in));
        this.enemyGladiator = gladiatorDAO.getRandomGladiator();
        
        // == Battle Frame setups ==
        setTitle("ARENAv2 - Battle");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // == Stats Panel ==
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

        // Listen for user to click button
        // call the given function on click
        buttonAttack.addActionListener(this::attackAction);
        buttonGuard.addActionListener(this::guardAction);
        buttonUseItem.addActionListener(this::itemAction);
        buttonTaunt.addActionListener(this::tauntAction);
        buttonExit.addActionListener(event -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to leave the ARENA?",
                    "Exit Battle",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new MainMenuFrame().setVisible(true); // battle frame -> menu
            }
        });
        
        // Add buttons to bottom of window
        buttonPanel.add(buttonAttack);
        buttonPanel.add(buttonGuard);
        buttonPanel.add(buttonUseItem);
        buttonPanel.add(buttonTaunt);
        buttonPanel.add(buttonExit);
        
        // Merge all panels onto base frame
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        appendLog(enemyGladiator.getName() + " stands to face you in the ARENA!\n");

        setVisible(true);
    }

    // ========= BATTLE ACTIONS ==========
    /**
     * Handles player attack event, calculates damage
     * check if enemy is blocking and update damage formula
     * 
     * @param event 
     */
    private void attackAction(ActionEvent event) {
        int damage = enemyGladiator.getDefense() < playerGladiator.getAttack() // if enemy defense < attack, damage = attack - defense
                ? playerGladiator.getAttack() - enemyGladiator.getDefense() : 1; // if attack < defense set damage = 1
        if (enemyGladiator.isBlocking()) {
            damage /= 2; // half damage if guarding is true
            enemyGladiator.setBlocking(false); // reset after damage
        }
        enemyGladiator.setHealth(Math.max(0, enemyGladiator.getHealth() - damage));
        appendLog("You hit " + enemyGladiator.getName() + " for " + damage + " damage.\n");
        battleLogDAO.addBattleLog(playerGladiator.getName(), enemyGladiator.getName(), "ATTACK");

        playerGladiator.setBlocking(false); // reset at end combat if no damage taken
        updateStats(); // update both gladiator values
        checkBattleOutcome(); // check if a gladiator has won
        enemyTurn(); // pass turn to enemy if no gladiator has won
    }

    /**
     * Half damage received if attacked following turn
     * uses ArenaV1 guard mechanic
     * 
     * @param event 
     */
    private void guardAction(ActionEvent event) {
        playerGladiator.setBlocking(true);
        appendLog("You brace yourself for the next attack.\n");
        battleLogDAO.addBattleLog(enemyGladiator.getName(), playerGladiator.getName(), "GUARD");
        enemyTurn();
    }
    
    /**
     * Handle player inventory interactions
     * uses ArenaV1 inventory system
     * 
     * @param event 
     */
    private void itemAction(ActionEvent event) {
        if (playerGladiator.getInventory().isEmpty()) { // check inventory has no zero elements inside
            appendLog("Your inventory is empty!\n");
            enemyTurn();
            return;
        }

        var inventory = playerGladiator.getInventory(); // get the list of items in inventory and display them
        String[] itemNames = inventory.getItems().stream().map(Item::getName).toArray(String[]::new);
        String selectedItem = (String) JOptionPane.showInputDialog(this,
                "Select and item to use:", "Inventory",
                JOptionPane.PLAIN_MESSAGE, null, itemNames, itemNames[0]);

        if (selectedItem != null) { // if a valid item is selected get the name and apply the effect
            Item item = inventory.getItemByName(selectedItem);
            if (item != null) {
                playerGladiator.useItem(item);
                appendLog("Successfully used " + selectedItem + ".\n"); // write action to log
            }
        }
        updateStats(); // update both gladiator values - needed to reflect stat updates from items
        enemyTurn();
    }

    /**
     * Try to taunt the enemy gladiator 
     * permanently lowering stats if successful
     * 70% chance of success
     * 
     * @param event 
     */
    private void tauntAction(ActionEvent event) {
        appendLog("You taunt " + enemyGladiator.getName() + "!\n");
        if (new Random().nextInt(10) >= 3) { // 70% effective chance
            int attackDebuff = (int) (enemyGladiator.getAttack() * 0.2); // lower values relative to total instead of fixed value
            int defenseDebuff = (int) (enemyGladiator.getDefense() * 0.2);
            enemyGladiator.setAttack(Math.max(1, enemyGladiator.getAttack() - attackDebuff));
            enemyGladiator.setDefense(Math.max(0, enemyGladiator.getDefense() - defenseDebuff));
            appendLog(enemyGladiator.getName() + " loses -" + attackDebuff + " ATK and -" + defenseDebuff + " DEF permanently!\n");
            battleLogDAO.addBattleLog(playerGladiator.getName(), enemyGladiator.getName(), "TAUNT SUCCESS"); // write action to log
        } else {
            appendLog(enemyGladiator.getName() + " shrugs off your taunt!\n");
            battleLogDAO.addBattleLog(playerGladiator.getName(), enemyGladiator.getName(), "TAUNT FAIL");
        }
        updateStats();
        enemyTurn();
    }

    /**
     * Randomly determine the enemy 'ai' action 
     * using a combat algorithm derived from ArenaV1
     * weighted so attack has priority over guarding or taunting
     * can change roll values to change difficulty
     */
    private void enemyTurn() {
        if (!enemyGladiator.isAlive()) {
            return;
        }

        int roll = new Random().nextInt(10); // roll between 0-9 (10)
        int action;
        if (roll <= 5) {
            action = 0;   // 60% pick attack
        } else if (roll <= 7) {
            action = 1;   // 20% pick guard
        } else {
            action = 2;   // 20% taunt
        } 

        switch (action) {
            case 0 -> { // attack
                int damage = playerGladiator.isBlocking()
                        ? Math.max(1, enemyGladiator.getAttack() / 2)
                        : enemyGladiator.getAttack();

                playerGladiator.setHealth(Math.max(0, playerGladiator.getHealth() - damage)); // subtract damage from health
                appendLog(enemyGladiator.getName() + " hits you for " + damage + " damage!\n");
                playerGladiator.setBlocking(false); // clear player guard status
                battleLogDAO.addBattleLog(enemyGladiator.getName(), playerGladiator.getName(), "ATTACK"); // write action to log
            }

            case 1 -> { // guard
                enemyGladiator.setBlocking(true); // set enemy guard status
                appendLog(enemyGladiator.getName() + " braces for the next incoming attack.\n");
                battleLogDAO.addBattleLog(enemyGladiator.getName(), playerGladiator.getName(), "GUARD");
            }

            case 2 -> { // taunt (debuffs player def and atk)
                appendLog(enemyGladiator.getName() + " taunts you! \n");
                if (new Random().nextInt(10) >= 3) { // same 70% as player 
                    int atkLoss = (int) (playerGladiator.getAttack() * 0.2); // relative to player stats not fixed value
                    int defLoss = (int) (playerGladiator.getDefense() * 0.2);
                    playerGladiator.setAttack(Math.max(1, playerGladiator.getAttack() - atkLoss));
                    playerGladiator.setDefense(Math.max(0, playerGladiator.getDefense() - defLoss));
                    appendLog("You lose -" + atkLoss + " ATK and -" + defLoss + " DEF permanently!\n");
                    battleLogDAO.addBattleLog(enemyGladiator.getName(), playerGladiator.getName(), "TAUNT SUCCESS"); // write action to log
                } else {
                    appendLog("You shrug off the taunt!\n");
                    battleLogDAO.addBattleLog(enemyGladiator.getName(), playerGladiator.getName(), "TAUNT FAIL");
                }
            }
        }

        updateStats();
        checkBattleOutcome(); // check if any actions made a gladiator win
    }

    /**
     * Determine if either party has one the match
     * write to DB log and display a message depending
     * on outcome
     * 
     */
    private void checkBattleOutcome() {
        if (!enemyGladiator.isAlive()) {
            appendLog("\n You defeated " + enemyGladiator.getName() + " !\n");
            battleLogDAO.addBattleLog(playerGladiator.getName(), enemyGladiator.getName(), "WIN");

            int newScore = playerDAO.getScore(playerGladiator.getName()) + 1;
            playerDAO.updateScore(playerGladiator.getName(), newScore);
            JOptionPane.showMessageDialog(this, "You stand victorious! Your score is: " + newScore);
            dispose();
            new MainMenuFrame().setVisible(true); // battle frame -> menu
        } else if (!playerGladiator.isAlive()) {
            appendLog("\n You were defeated by " + enemyGladiator.getName() + "!\n");
            battleLogDAO.addBattleLog(playerGladiator.getName(), enemyGladiator.getName(), "LOSS");
            JOptionPane.showMessageDialog(this, "Defeat!");
            dispose();
            new MainMenuFrame().setVisible(true); // battle frame -> menu
        }
    }

    // =========== UTIL/HELPER METHODS ==========
    /**
     * Adds text to the log and scrolls to next message
     * position
     * 
     * @param message contains the string of text to write to log
     */
    private void appendLog(String message) {
        battleLogArea.append(message); // write
        battleLogArea.setCaretPosition(battleLogArea.getDocument().getLength()); // move to next pos
    }

    /**
     * Refresh stat display and reflect changes
     * uses formatStats to write them in correct order
     * 
     */
    private void updateStats() {
        playerStats.setText(formatStats(playerGladiator));
        enemyStats.setText(formatStats(enemyGladiator));
    }

    /**
     * Format gladiator stats into frame compatible data
     * 
     * @param gladiator
     * @return 
     */
    private String formatStats(Gladiator gladiator) {
        return String.format("%s HP: %d/%d  ATK: %d  DEF: %d",
                gladiator.getName(), gladiator.getHealth(), gladiator.getMaxHealth(), gladiator.getAttack(), gladiator.getDefense());
    }

}
