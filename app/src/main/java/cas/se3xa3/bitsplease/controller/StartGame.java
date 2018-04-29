package cas.se3xa3.bitsplease.controller;

import cas.se3xa3.bitsplease.view.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StartGame {

    private JFrame frame;

    public static void main(String[] args) {
        new StartGame();
    }

    public StartGame() {
        frame = new JFrame("0hh1");

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                final List<FadingLabel> labels = new ArrayList<>(25);
                labels.add(new FadingLabel("0 "));
                labels.add(new FadingLabel("h "));
                labels.add(new FadingLabel("h "));
                labels.add(new FadingLabel("1"));

                JPanel panel = new JPanel();
                panel.setLayout(new GridBagLayout());
                panel.setBackground(Color.GRAY.darker().darker());

                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Dimension dim = toolkit.getScreenSize();
                frame.setSize(dim.width, dim.height);
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                frame.setLayout(new GridBagLayout());

                for (FadingLabel label : labels) { panel.add(label); }

                frame.setContentPane(panel);
                frame.setLocationRelativeTo(null);
                frame.setResizable(false);
                frame.setVisible(true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (FadingLabel label : labels) {
                            label.fadeIn();
                            label.waitFor();
                        }
                        try { new Game(frame); }
                        catch(IOException ioe){}
                    }
                }).start();
            }
        });
    }

    public class FadingLabel extends JLabel {

        protected static final int TIME = 1000;
        protected final Object fadeLock = new Object();

        private float targetAlpha;
        private float alpha = 0;
        private Timer timer;
        private long startTime;
        private float fromAlpha;

        public FadingLabel(String text) {
            super(text);
            setFont(new Font("Arial", Font.BOLD, 40));
            setBackground(Color.GRAY.darker().darker());
            setForeground(Color.WHITE);
            init();
        }

        protected void init() {
            timer = new Timer(40, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (alpha < 1f) {
                        long now = System.currentTimeMillis();
                        long diff = now - startTime;
                        float progress = (float) diff / (float) TIME;

                        float distance = targetAlpha - fromAlpha;
                        alpha = (float) (distance * progress);
                        alpha += fromAlpha;

                        if (alpha > 1f) {
                            timer.stop();
                            alpha = 1f;
                        }

                    } else {
                        alpha = 1f;
                        timer.stop();
                    }
                    repaint();
                    if (!timer.isRunning()) {
                        synchronized (fadeLock) {
                            fadeLock.notifyAll();
                        }
                    }
                }
            });
            timer.setInitialDelay(0);
        }

        protected void fadeTo(float target) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    timer.stop();
                    fromAlpha = alpha;
                    targetAlpha = target;
                    if (targetAlpha != alpha) {
                        startTime = System.currentTimeMillis();
                        timer.start();
                    } else {
                        repaint();
                    }
                }
            };
            if (EventQueue.isDispatchThread()) {
                run.run();
            } else {
                EventQueue.invokeLater(run);
            }
        }

        public void fadeIn() {
            fadeTo(1f);
        }

        public void waitFor() {
            if (EventQueue.isDispatchThread()) {
                throw new IllegalStateException("Calling waitFor while within the EDT!");
            }
            synchronized (fadeLock) {
                try {
                    fadeLock.wait();
                } catch (InterruptedException ex) {
                }
            }
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
            super.paint(g2d);
            g2d.dispose();
        }
    }
}
