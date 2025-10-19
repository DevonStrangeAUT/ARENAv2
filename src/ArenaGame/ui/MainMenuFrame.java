package ArenaGame.ui;

import ArenaGame.database.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * MainMenuFrame is the J Frame based main GUI - this class replaces the CUI
 * handling "GameMenu" class from v1
 *
 * Responsibilities: - Display main menu selections - Handle user inputs via GUI
 * elements - Connect to DAO (data access object) classes when data operations
 * needed
 */
public class MainMenuFrame extends JFrame {

    private final PlayerDAO playerDAO;
    private final GladiatorDAO gladiatorDAO;
    private final BattleLogDAO battleLogDAO;

    public MainMenuFrame() {
        // default constructor initialization of DAOs
        this.playerDAO = new PlayerDAO();
        this.gladiatorDAO = new GladiatorDAO();
        this.battleLogDAO = new BattleLogDAO();

        setTitle("ARENAv2 - Main Menu Frame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 800);
        setLocationRelativeTo(null);
        setResizable(false);

        initElements();
    }

    /**
     * Initializes GUI elements.
     */
    private void initElements() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(9, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel(" ARENAv2 ", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 24));

        JButton buttonBattle = new JButton("Enter the ARENA");
        JButton buttonScores = new JButton("View Scores");
        JButton buttonLogs = new JButton("View Logs");
        JButton buttonResetScores = new JButton("Reset Scores");
        JButton buttonResetLogs = new JButton("Reset Logs");
        JButton buttonResetGladiators = new JButton("Reset Gladiators");
        JButton buttonExit = new JButton("Exit to Desktop");

        // Button event listeners (button pressed)
        buttonBattle.addActionListener(this::enterBattle);
        buttonScores.addActionListener(this::viewScores);
        buttonLogs.addActionListener(this::viewLogs);
        buttonResetScores.addActionListener(this::resetScores);
        buttonResetLogs.addActionListener(this::resetLogs);
        buttonResetGladiators.addActionListener(this::resetGladiators);
        buttonExit.addActionListener(e -> System.exit(0));

        // Add buttons to panel
        panel.add(title);
        panel.add(buttonBattle);
        panel.add(buttonScores);
        panel.add(buttonLogs);
        panel.add(buttonResetScores);
        panel.add(buttonResetLogs);
        panel.add(buttonResetGladiators);
        panel.add(buttonExit);

        add(panel);
    }

    // ========= LISTENER METHODS =========
    private void enterBattle(ActionEvent event) {
        String playerName = JOptionPane.showInputDialog(this, "Enter the name of your gladiator: ");
        if (playerName == null || playerName.isBlank()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid name!");
            return;
        }

        if (playerDAO.playerExists(playerName)) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "A gladiator named '" + playerName + "' already exists. Continue?",
                    "Existing Player",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        } else {
            playerDAO.addPlayer(playerName);
        }

        dispose(); // close the menu
        new BattleFrame(playerName);
    }

    private void viewScores(ActionEvent event) {
        var scores = playerDAO.getAllPlayers();
        if (scores.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Failed to find scores.");
        } else {
            StringBuilder stringBuilder = new StringBuilder("===== Player Scores =====\n");
            scores.forEach((name, score)
                    -> stringBuilder.append(String.format("%s → %d%n", name, score)));
            JOptionPane.showMessageDialog(this, stringBuilder.toString(), "Scores", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewLogs(ActionEvent event) {
        var logs = battleLogDAO.getLogs();
        if (logs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Failed to find logs.");
        } else {
            JTextArea textArea = new JTextArea();
            logs.forEach(log -> textArea.append(log + "\n"));
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(1000, 500));
            JOptionPane.showMessageDialog(this, scrollPane, "Battle Logs", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void resetScores(ActionEvent event) {
        int userChoice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reset all player scores?",
                "Warning: This action cannot be undone!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (userChoice == JOptionPane.YES_OPTION) {
            playerDAO.resetScores();
            JOptionPane.showMessageDialog(this, "Successfully cleared stored scores.");
        }
    }

    private void resetLogs(ActionEvent event) {
        int userChoice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reset all database logs?",
                "Warning: This action cannot be undone!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (userChoice == JOptionPane.YES_OPTION) {
            battleLogDAO.clearLogs();
            JOptionPane.showMessageDialog(this, "Successfully cleared stored logs.");
        }
    }

    private void resetGladiators(ActionEvent event) {
        int userChoice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reset gladiators to default?",
                "Warning: This action cannot be undone!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (userChoice == JOptionPane.YES_OPTION) {
            gladiatorDAO.resetGladiators();
            JOptionPane.showMessageDialog(this, "Successfully reset gladiators to default.");
        }
    }

    // ========== JFRAME LAUNCHER ==========
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainMenuFrame().setVisible(true);
        });
    }

}
