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
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        initElements();
    }

    /**
     * Initializes GUI elements.
     */
    private void initElements() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

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
        
        PlayerDAO dao = new PlayerDAO();
        dao.addPlayer(playerName);
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
        playerDAO.resetScores();
        JOptionPane.showMessageDialog(this, "Scores successfully cleared.");
    }

    private void resetLogs(ActionEvent event) {
        battleLogDAO.clearLogs();
        JOptionPane.showMessageDialog(this, "Logs successfully cleared.");
    }

    private void resetGladiators(ActionEvent event) {
        gladiatorDAO.resetGladiators();
        JOptionPane.showMessageDialog(this, "Gladiators successfully reset.");
    }

    // ========== JFRAME LAUNCHER ==========
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainMenuFrame().setVisible(true);
        });
    }
    

}
