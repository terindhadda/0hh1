package cas.se3xa3.bitsplease.view;

import cas.se3xa3.bitsplease.model.Board;
import cas.se3xa3.bitsplease.view.themes.DefaultTheme;
import cas.se3xa3.bitsplease.view.themes.Theme;
import cas.se3xa3.bitsplease.view.themes.WinterTheme;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GameBoard extends JPanel {

    private Board board;

    private JLabel message;

    private JPanel mainPanel;
    private BoardView boardPanel;
    private JPanel buttonPanel;

    private JButton exit;
    private JButton reset;
    private JButton hint;
    private JButton settings;
    private JButton question;

    private Theme theme;

	public GameBoard(Board board) {

        this.board = board;

        mainPanel = new JPanel(new BorderLayout(100, 50));
        theme = DefaultTheme.getInstance();
        boardPanel = new BoardView(board, theme);
        buttonPanel = new JPanel();

        message = new JLabel(board.getSize() + " x " + board.getSize());

        this.addPropertyChangeListener("theme", boardPanel);

        try {
            exit = new JButton(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResource("exit.png"))));
            reset = new JButton(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResource("reset.png"))));
            hint = new JButton(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResource("hint.png"))));
            settings = new JButton(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResource("settings.png"))));
            question = new JButton(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResource("question.png"))));
        } catch (IOException e) { e.printStackTrace(); }

		JButton[] buttons = { exit,reset,hint,settings,question };

		buttonPanel.setLayout(new GridLayout(1, 5, 5, 5));

		for (JButton button : buttons) {
			button.setBorder(BorderFactory.createEmptyBorder());
			button.setOpaque(false);
			button.setContentAreaFilled(false);
			button.setBorderPainted(false);
            button.setSelected(false);
            button.setFocusable(false);
        }

        exit.addActionListener(e -> { exitClicked(); });
        reset.addActionListener(e -> {
            resetClicked();
        });
        settings.addActionListener(e -> {
            settingsClicked();
        });
        question.addActionListener(e -> {
            questionClicked();
        });

		message.setForeground(Color.WHITE);
		resetMessage();
		message.setHorizontalAlignment(JLabel.CENTER);

		buttonPanel.add(exit);
		buttonPanel.add(reset);
		buttonPanel.add(hint);
		buttonPanel.add(settings);
		buttonPanel.add(question);

	    mainPanel.add(message, BorderLayout.NORTH);
		mainPanel.add(boardPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setBackground(Color.DARK_GRAY.darker().darker());
        mainPanel.setBackground(Color.DARK_GRAY.darker().darker());
        boardPanel.setBackground(Color.DARK_GRAY.darker().darker());
        buttonPanel.setBackground(Color.DARK_GRAY.darker().darker());

        this.add(mainPanel);
	}

    public BoardView boardView() { return this.boardPanel; }

    public void setMessage(String text) {
        this.message.setPreferredSize(this.message.getPreferredSize());
        this.message.setFont(this.getFont().deriveFont((float) 25));
        this.message.setText("<html><p style=\"text-align:center\">" + text + "</p></html>");
        this.revalidate();
    }

    public void resetMessage() {
        message.setFont(new Font("Arial", Font.BOLD, 80));
        message.setText("<html><p style=\"text-align:center\">" + board.getSize() + "x" + board.getSize() + "</p></html>");
        this.revalidate();
    }

    public void setTheme(Theme newTheme) {
        this.firePropertyChange("theme", this.theme, newTheme);
        this.theme = newTheme;
        repaint();
    }

    public void addHintButtonListener(ActionListener listener) {
        this.hint.addActionListener(listener);
    }

    private void exitClicked() {
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        try { new Game(currentFrame); } catch(IOException ioe) {}
    }

    private void resetClicked() {
        this.board.clearAllUnlockedTiles();
        boardPanel.clearHighlights();
        resetMessage();
        boardPanel.update();
    }

    private void settingsClicked() {
        String selection = (String) JOptionPane.showInputDialog(this,
                "Select a theme:",
                "Theme Selector",
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Default", "Winter"},
                "Default");
        if (selection == null) return;
        switch (selection) {
            case "Default":
                this.setTheme(DefaultTheme.getInstance());
                return;
            case "Winter":
                this.setTheme(WinterTheme.getInstance());
                return;
        }
    }

    private void questionClicked() {
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        JLabel aboutTitle = new JLabel("About");
        aboutTitle.setFont(new Font("Sans Serif", Font.BOLD, 50));
        aboutTitle.setForeground(Color.WHITE);
        aboutTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel aboutWindow = new JPanel(new GridLayout(3, 1, 10, 10));
        aboutWindow.setBackground(Color.DARK_GRAY.darker().darker());
        aboutWindow.setSize(Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height);

        JButton back;
        try {
            back = new JButton(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResource("exit.png"))));
        } catch (IOException ioe) {
            back = new JButton("Back");
        }
        back.setOpaque(false);
        back.setContentAreaFilled(false);
        back.setBorderPainted(false);
        back.setFont(new Font("Arial", Font.PLAIN, 40));
        back.setForeground(Color.WHITE);
        back.addActionListener(e -> {  currentFrame.setContentPane(this); });

        JTextArea aboutContents = new JTextArea(
                "0hh1 is a game that contains various puzzles that require logic to solve. As the\n"
              + "dimensions of the game board increases, so does the difficulty. BitsPlease is a\n"
              + "group consisting of Spencer Park, Terin Dhadda, and Shane DeSouza who are\n"
              + "students of McMaster University\n");
        aboutContents.setEditable(false);
        aboutContents.setFont(new Font("Serif", Font.BOLD, 40));
        aboutContents.setColumns(60);
        aboutContents.setRows(3);
        aboutContents.setLineWrap(true);
        aboutContents.setBackground(Color.DARK_GRAY.darker().darker());
        aboutContents.setForeground(Color.WHITE);

        aboutWindow.add(aboutTitle);
        aboutWindow.add(aboutContents);
        aboutWindow.add(back);

        currentFrame.setContentPane(aboutWindow);
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);

        graphics.drawImage(theme.screenOverlay(), 0, 0, getWidth(), getHeight(), this);
    }
}
