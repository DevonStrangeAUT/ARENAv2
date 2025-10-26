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
 *
 * Responsibilities:
 * - Handles turn-based combat loop.
 * - Display player/enemy stats and log messages.
 * - Write battle data to the database via DAO classes
 * 
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
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // == Base Gradient Background ==
        JPanel basePanel = new GradientPanel();
        basePanel.setLayout(new BorderLayout(20, 20));
        basePanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        basePanel.setOpaque(false);
        
        // == Stats Panel ==
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        topPanel.setOpaque(false);
        
        //Player and enemy stats font and colour
        playerStats = new JLabel(formatStats(playerGladiator), SwingConstants.CENTER);
        enemyStats = new JLabel(formatStats(enemyGladiator), SwingConstants.CENTER);
        
        playerStats.setFont(new Font("Courier New", Font.BOLD, 22));
        enemyStats.setFont(new Font("Courier New", Font.BOLD, 22));
        playerStats.setForeground(new Color(240, 240, 255));
        enemyStats.setForeground(new Color(255, 210, 210));
        
        // add soft dark translucent background so they stand out    
        playerStats.setOpaque(true);
        enemyStats.setOpaque(true);
        playerStats.setBackground(new Color(40, 40, 60));
        enemyStats.setBackground(new Color(80, 30, 30));
        playerStats.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        enemyStats.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        
        topPanel.add(playerStats);
        topPanel.add(enemyStats);

        // Log Display Centering
        battleLogArea = new JTextArea();
        battleLogArea.setEditable(false);
        battleLogArea.setLineWrap(true);
        battleLogArea.setWrapStyleWord(true);
        battleLogArea.setFont(new Font("Consolas", Font.PLAIN, 16));
        battleLogArea.setForeground(Color.WHITE);
        battleLogArea.setBackground(new Color(10, 10, 10));
        battleLogArea.setMargin(new Insets(14, 14, 14, 14));
        battleLogArea.setCaretColor(Color.WHITE);
        battleLogArea.setSelectionColor(new Color(255, 255, 255, 60));
        battleLogArea.setOpaque(true);
        
        JScrollPane scrollPane = new JScrollPane(battleLogArea);
        scrollPane.setOpaque(true);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 60, 60), 3, true));
        scrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);

        // == Bottom Panel (Action Buttons) ==
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        
        buttonAttack = makeButton("Attack");
        buttonGuard = makeButton("Guard");
        buttonUseItem = makeButton("Use Item");
        buttonTaunt = makeButton("Taunt");
        buttonExit = makeButton("Exit");
        
        Dimension buttonSize = new Dimension(200, 55);
        for (JButton b : new JButton[]{buttonAttack, buttonGuard, buttonUseItem, buttonTaunt, buttonExit}) {
            b.setPreferredSize(buttonSize);
            buttonPanel.add(b);
        }
        
        // Listen for user to click button
        // call the given function on click
        buttonAttack.addActionListener(this::attackAction);
        buttonGuard.addActionListener(this::guardAction);
        buttonUseItem.addActionListener(this::itemAction);
        buttonTaunt.addActionListener(this::tauntAction);
        buttonExit.addActionListener(event -> exitBattlePrompt());
        
        // Merge all panels onto base frame
        basePanel.add(topPanel, BorderLayout.NORTH);
        basePanel.add(scrollPane, BorderLayout.CENTER);
        basePanel.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(basePanel);

        appendLog(enemyGladiator.getName() + " stands to face you in the ARENA!\n");
        setVisible(true);
    }
    
    // ===== Custom Components ======
    /*
    *gradient background for battle ambience.
    */
    private static class GradientPanel extends JPanel {
        public GradientPanel() {
            setDoubleBuffered(true);
            setOpaque(true);
        }
    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintChildren(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        int w = getWidth(), h = getHeight();
        GradientPaint gp = new GradientPaint(
                0, 0, new Color(50, 30, 40),
                0, h, new Color(100, 20, 20)
        );
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);        

        }
    }

    /**
     * Creates a themed button consistent with the ARENA visual style.
     */
    private JButton makeButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(new Color(100, 25, 25));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 16));
        b.setBorder(BorderFactory.createLineBorder(new Color(180, 50, 50), 3, true));

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(160, 40, 40));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(100, 25, 25));
            }
        });
        return b;
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
    
    private void exitBattlePrompt() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to leave the ARENA?",
                "Exit Battle", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new MainMenuFrame().setVisible(true);
        }
    }    

}
