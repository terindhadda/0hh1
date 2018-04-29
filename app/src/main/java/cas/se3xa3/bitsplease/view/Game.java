package cas.se3xa3.bitsplease.view;

import cas.se3xa3.bitsplease.controller.GameSession;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Game {

    private static final long serialVersionUID = 1L;

    private JFrame window;
    private GameSession session;
    private JPanel mainScreen;

    public Game(JFrame window) throws IOException {
        this.window = window;
        this.session = new GameSession();
        initUI();
        setContents(mainScreen);
    }

    private void initUI() throws IOException {
        //window.setTitle("0hh1");
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dim = toolkit.getScreenSize();
        //window.setSize(dim.width, dim.height);
        //window.setLocationRelativeTo(null);
        //window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //window.setExtendedState(JFrame.MAXIMIZED_BOTH);

        mainScreen = new JPanel(new GridLayout(5, 1, 10, 10));
        mainScreen.setBackground(Color.DARK_GRAY.darker().darker());

        BufferedImage image = ImageIO.read(Game.class.getClassLoader().getResource("logo.png"));

        JLabel logo = new JLabel(new ImageIcon(image), SwingConstants.CENTER);
        logo.setVerticalAlignment(JLabel.TOP);

        JLabel author = new JLabel("By bitsplease", SwingConstants.CENTER);
        author.setVerticalAlignment(JLabel.BOTTOM);
        author.setFont(new Font("Sans Serif", Font.ITALIC, 50));
        author.setForeground(Color.WHITE);

        JButton about = new JButton("About");
        JButton rules = new JButton("How To Play");
        JButton play = new JButton("Play");

        JButton[] buttons = { about, rules, play };

        for (JButton button : buttons) {
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setFont(new Font("Arial", Font.PLAIN, 40));
            button.setForeground(Color.WHITE);
        }

        mainScreen.add(logo);
        mainScreen.add(play);
        mainScreen.add(about);
        mainScreen.add(rules);
        mainScreen.add(author);

        about.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JLabel aboutTitle = new JLabel("About");
                aboutTitle.setFont(new Font("Sans Serif", Font.BOLD, 50));
                aboutTitle.setForeground(Color.WHITE);
                aboutTitle.setHorizontalAlignment(SwingConstants.CENTER);

                JPanel aboutWindow = new JPanel(new GridLayout(3, 1, 10, 10));
                aboutWindow.setBackground(Color.DARK_GRAY.darker().darker());
                aboutWindow.setSize(dim.width, dim.height);

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
                back.addActionListener(ae -> {
                    setContents(mainScreen);
                });

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

                setContents(aboutWindow);
            }
        });

        rules.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JLabel rulesTitle = new JLabel("How To Play");
                rulesTitle.setFont(new Font("Sans Serif", Font.BOLD, 50));
                rulesTitle.setForeground(Color.WHITE);
                rulesTitle.setHorizontalAlignment(SwingConstants.CENTER);

                JPanel rulesWindow = new JPanel(new GridLayout(3, 1, 10, 10));
                rulesWindow.setBackground(Color.DARK_GRAY.darker().darker());
                rulesWindow.setSize(dim.width, dim.height);

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
                back.addActionListener(ae -> {
                    setContents(mainScreen);
                });

                String spaces = "                                                                                "; //80
                JTextArea rulesContents = new JTextArea(
                        spaces + "• Click a tile once to make it red. Click it twice to make it blue.\n\n"
                                + spaces + "• Three of the same colour tiles in a row and column is not allowed.\n\n"
                                + spaces + "• A full row and column must have as many red tiles as it has blue tiles.\n\n"
                                + spaces + "• No two rows and no two columns can look the same.\n\n"
                                + spaces + "• Most importantly, have fun!");
                rulesContents.setEditable(false);
                rulesContents.setFont(new Font("Serif", Font.BOLD, 20));
                rulesContents.setColumns(60);
                rulesContents.setRows(3);
                rulesContents.setLineWrap(false);
                rulesContents.setBackground(Color.DARK_GRAY.darker().darker());
                rulesContents.setForeground(Color.WHITE);

                rulesWindow.add(rulesTitle);
                rulesWindow.add(rulesContents);
                rulesWindow.add(back);

                setContents(rulesWindow);
            }
        });

        play.addActionListener(pressEvent -> {
            int selection = JOptionPane.showOptionDialog(
                    window,
                    "Select the board size.",
                    "Select a size",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new Integer[]{4, 6, 8, 10, 12},
                    null
            );
            if (selection == JOptionPane.CLOSED_OPTION) return;
            int size = (selection + 2) * 2;
            session.startNewGame(size);
            setContents(session.getView());
        });
    }

    public void setContents(Container contents) {
        window.setContentPane(contents);
        window.revalidate();
    }
}
